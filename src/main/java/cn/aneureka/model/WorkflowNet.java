package cn.aneureka.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:40
 * @description
 **/
public class WorkflowNet {

    private Set<Node> nodes;

    private Map<Node, List<Node>> graph;

    private Node in;

    private Node out;

    public WorkflowNet() {
        nodes = new HashSet<>();
        graph = new HashMap<>();
        in = null;
        out = null;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(String sourceId, String targetId) {
        Node source = findNode(sourceId);
        Node target = findNode(targetId);
        if (source == null || target == null) {
            throw new IllegalArgumentException("source or target ID not exists");
        }
        addEdge(source, target);
    }

    public void addEdge(Node source, Node target) {
        List<Node> neighbors = graph.getOrDefault(source, new ArrayList<>());
        if (!neighbors.contains(target)) {
            neighbors.add(target);
        }
        graph.put(source, neighbors);
    }

    public static void removeEdge(Map<Node, List<Node>> graph, Node source, Node target) {
        if (graph.containsKey(source)) {
            graph.get(source).remove(target);
        }
    }

    public void init() {
        // find the in and out place
        for (Node node : nodes) {
            if (node instanceof Place) {
                if (inDegreeOf(this.graph, node) == 0) {
                    this.in = node;
                }
                if (outDegreeOf(this.graph, node) == 0) {
                    this.out = node;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Place]").append("\n");
        for (Node node : nodes) {
            if (node instanceof Place) {
                sb.append(node.toString()).append(" ");
            }
        }
        sb.append("\n").append("[Transition]").append("\n");
        for (Node node : nodes) {
            if (node instanceof Transition) {
                sb.append(node.toString()).append(" ");
            }
        }
        sb.append("\n").append("[Graph]");
        for (Map.Entry<Node, List<Node>> entry : graph.entrySet()) {
            sb.append("\n");
            sb.append(entry.getKey().toString()).append(" => ");
            sb.append(entry.getValue().stream().map(Object::toString).collect(Collectors.joining(" ")));
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Collects and prints the logs of graph.
     */
    public void getLogOfGraph(String logFile) {
        List<List<Node>> logs = new ArrayList<>();
        List<Node> curNodes = new ArrayList<>();
        curNodes.add(this.in);
        getLogOfGraph(this.graph, this.out, curNodes, new HashSet<>(), new ArrayList<>(), logs);
        logs = distinct(logs);
        if (logFile == null) {
            printLogs(logs);
        } else {
            try {
                printLogsToFile(logs, logFile);
            } catch (IOException e) {
                System.err.println("logFile is invalid");
            }
        }
    }

    /**
     * Collects the execution (raw) logs of graph recursively.
     */
    private static void getLogOfGraph(Map<Node, List<Node>> graph, Node exit, List<Node> curNodes, Set<Node> cycleEntries, List<Node> path, List<List<Node>> res) {
        curNodes = removeAccessibleNodes(graph, curNodes);
        for (int i = 0; i < curNodes.size(); i++) {
            Node curNode = curNodes.get(i);
            if (curNode.equals(exit)) {
                res.add(new ArrayList<>(path));
                continue;
            }
            if (!graph.containsKey(curNode)) {
                continue;
            }
            if (curNode instanceof Transition) {
                path.add(curNode);
            }
            int curNodeIndex = curNodes.indexOf(curNode);
            curNodes.remove(curNodeIndex);
            Map<Node, List<Node>> tmpGraph = copyOfGraph(graph);
            if (curNode instanceof Place) {
                for (Node v : graph.get(curNode)) {
                    if (outDegreeOf(tmpGraph, v) > 0 || v.equals(exit)) {
                        curNodes.add(v);
                        getLogOfGraph(tmpGraph, exit, curNodes, cycleEntries, path, res);
                        curNodes.remove(v);
                    }
                }
            } else {
                List<Node> neighbors = graph.get(curNode);
                Set<Node> tmpEntries = new HashSet<>(cycleEntries);
                for (Node v : neighbors) {
                    if (inDegreeOf(graph, v) > 1 && v instanceof Place) {
                        if (cycleEntries.contains(v)) {
                            removeEdge(tmpGraph, curNode, v);
                        } else {
                            tmpEntries.add(v);
                        }
                    }
                }
                curNodes.addAll(neighbors);
                getLogOfGraph(tmpGraph, exit, curNodes, tmpEntries, path, res);
                curNodes.removeAll(neighbors);
            }
            if (curNode instanceof Transition) {
                path.remove(path.size() - 1);
            }
            curNodes.add(curNodeIndex, curNode);
        }
    }

    /**
     * Remove duplicated logs.
     */
    private static List<List<Node>> distinct(List<List<Node>> logs) {
        return logs.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private static void printLogs(List<List<Node>> logs) {
        System.out.println(getLogsText(logs));
    }

    private static void printLogsToFile(List<List<Node>> logs, String logFile) throws IOException {
        FileWriter fw = new FileWriter(new File(logFile));
        fw.append(getLogsText(logs));
        fw.close();
    }

    private static String getLogsText(List<List<Node>> logs) {
        StringBuilder sb = new StringBuilder();
        for (List<Node> log : logs) {
            sb.append(log.stream().map(Node::toString).collect(Collectors.joining(" -> "))).append("\n");
        }
        sb.append(String.format("=== All logs fetched: %d ===", logs.size())).append("\n");
        return sb.toString();
    }

    /**
     * Returns in-degree of the given node.
     */
    private static int inDegreeOf(Map<Node, List<Node>> graph, Node node) {
        int res = 0;
        for (Map.Entry<Node, List<Node>> entry : graph.entrySet()) {
            if (entry.getValue().contains(node)) {
                res++;
            }
        }
        return res;
    }

    /**
     * Returns out-degree of the given node.
     */
    private static int outDegreeOf(Map<Node, List<Node>> graph, Node node) {
        if (graph.containsKey(node)) {
            return graph.get(node).size();
        } else {
            return 0;
        }
    }

    /**
     * Removes nodes that can be assessed by others and return.
     */
    private static List<Node> removeAccessibleNodes(Map<Node, List<Node>> graph, List<Node> nodes) {
        List<Node> res = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) instanceof Place) {
                res.add(nodes.get(i));
                continue;
            }
            boolean Accessible = false;
            for (int j = 0; j < nodes.size(); j++) {
                if (i != j && isAccessible(graph, nodes.get(j), nodes.get(i))) {
                    Accessible = true;
                    break;
                }
            }
            if (!Accessible) {
                res.add(nodes.get(i));
            }
        }
        return res;
    }

    /**
     * Checks whether {target} can be reached by {source} in the given graph.
     */
    private static boolean isAccessible(Map<Node, List<Node>> graph, Node source, Node target) {
        return isAccessible(graph, source, target, new HashSet<>());
    }

    /**
     * Performs DFS to check accessibility in the given graph.
     */
    private static boolean isAccessible(Map<Node, List<Node>> graph, Node cur, Node target, Set<Node> visited) {
        if (cur.equals(target)) {
            return true;
        }
        if (visited.contains(cur) || !graph.containsKey(cur)) {
            return false;
        }
        visited.add(cur);
        boolean res = false;
        for (Node v : graph.get(cur)) {
            res = res || isAccessible(graph, v, target, visited);
        }
        return res;
    }

    /**
     * Returns deep copy of the given graph.
     * Nodes are not recreated.
     */
    private static Map<Node, List<Node>> copyOfGraph(Map<Node, List<Node>> graph) {
        Map<Node, List<Node>> copied = new HashMap<>();
        for (Map.Entry<Node, List<Node>> entry : graph.entrySet()) {
            Node k = entry.getKey();
            List<Node> v = new ArrayList<>(entry.getValue());
            copied.put(k, v);
        }
        return copied;
    }

    /**
     * Returns node by nodeId.
     */
    private Node findNode(String nodeId) {
        for (Node node : nodes) {
            if (node.id.equals(nodeId)) {
                return node;
            }
        }
        return null;
    }
}
