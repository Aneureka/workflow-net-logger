package cn.aneureka.model;

import com.sun.deploy.util.StringUtils;

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

    private Node findNode(String nodeId) {
        for (Node node: nodes) {
            if (node.id.equals(nodeId)) {
                return node;
            }
        }
        return null;
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
            System.out.println(String.format("remove: %s -> %s", source.toString(), target.toString()));
            graph.get(source).remove(target);
        }
    }

    private static Map<Node, List<Node>> copyOfGraph(Map<Node, List<Node>> graph) {
        Map<Node, List<Node>> copied = new HashMap<>();
        for (Map.Entry<Node, List<Node>> entry: graph.entrySet()) {
            Node k = entry.getKey();
            List<Node> v = new ArrayList<>(entry.getValue());
            copied.put(k, v);
        }
        return copied;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Node]").append("\n");
        for (Node node: nodes) {
            sb.append(node.toString()).append(" ");
        }
        sb.append("\n").append("[Graph]");
        for (Map.Entry<Node, List<Node>> entry: graph.entrySet()) {
            sb.append("\n");
            sb.append(entry.getKey().toString()).append(" => ");
            sb.append(StringUtils.join(entry.getValue().stream().map(Object::toString).collect(Collectors.toList()), " "));
        }
        return sb.toString();
    }

    public void init() {
        // find the in and out place
        for (Node node: nodes) {
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

    private static int inDegreeOf(Map<Node, List<Node>> graph, Node node) {
        int res = 0;
        for (Map.Entry<Node, List<Node>> entry: graph.entrySet()) {
            if (entry.getValue().contains(node)) {
                res++;
            }
        }
        return res;
    }

    private static int outDegreeOf(Map<Node, List<Node>> graph, Node node) {
        if (graph.containsKey(node)) {
            return graph.get(node).size();
        } else {
            return 0;
        }
    }

    private static List<Node> unreachableNodes(Map<Node, List<Node>> graph, List<Node> nodes) {
        List<Node> res = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) instanceof Place) {
                res.add(nodes.get(i));
                continue;
            }
            boolean reachable = false;
            for (int j = 0; j < nodes.size(); j++) {
                if (i != j && isReachable(graph, nodes.get(j), nodes.get(i))) {
                    reachable = true;
                    break;
                }
            }
            if (!reachable) {
                res.add(nodes.get(i));
            }
        }
        return res;
    }

    public boolean isReachable(Node source, Node target) {
        return isReachable(this.graph, source, target);
    }

    private static boolean isReachable(Map<Node, List<Node>> graph, Node source, Node target) {
        return isReachable(graph, source, target, new HashSet<>());
    }

    private static boolean isReachable(Map<Node, List<Node>> graph, Node cur, Node target, Set<Node> visited) {
        if (cur.equals(target)) {
            return true;
        }
        if (visited.contains(cur) || !graph.containsKey(cur)) {
            return false;
        }
        visited.add(cur);
        boolean res = false;
        for (Node v: graph.get(cur)) {
            res = res || isReachable(graph, v, target, visited);
        }
        return res;
    }

    public void getLogOfGraph() {
        List<List<Node>> res = new ArrayList<>();
        List<Node> curNodes = new ArrayList<>();
        curNodes.add(this.in);
        getLogOfGraph(this.graph, this.out, curNodes, new HashSet<>(), new ArrayList<>(), res);
        res = filterLog(res);
        printLog(res);
    }

    private static void getLogOfGraph(Map<Node, List<Node>> graph, Node exit, List<Node> curNodes, Set<Node> entries, List<Node> path, List<List<Node>> res) {
        curNodes = unreachableNodes(graph, curNodes);
//        System.out.println(curNodes);
        for (int i = 0; i < curNodes.size(); i++) {
            Node curNode = curNodes.get(i);
            if (curNode.equals(exit)) {
                path.add(curNode);
                res.add(new ArrayList<>(path));
                path.remove(curNode);
                continue;
            }
            if (!graph.containsKey(curNode)) {
                continue;
            }
            path.add(curNode);
            int curNodeIndex = curNodes.indexOf(curNode);
            curNodes.remove(curNodeIndex);
            Map<Node, List<Node>> tmpGraph = copyOfGraph(graph);
            if (curNode instanceof Place) {
                for (Node v: graph.get(curNode)) {
                    curNodes.add(v);
                    getLogOfGraph(tmpGraph, exit, curNodes, entries, path, res);
                    curNodes.remove(v);
                }
            } else {
                List<Node> neighbors = graph.get(curNode);
                Set<Node> tmpEntries = new HashSet<>(entries);
                for (Node v: neighbors) {
                    if (inDegreeOf(graph, v) > 1 && v instanceof Place) {
                        if (entries.contains(v)) {
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
            path.remove(path.size() - 1);
            curNodes.add(curNodeIndex, curNode);
        }
    }

    private List<List<Node>> filterLog(List<List<Node>> logs) {
        return logs.stream().map(log -> log.stream().filter(e -> e instanceof Transition).collect(Collectors.toList())).distinct().collect(Collectors.toList());
    }

    private void printLog(List<List<Node>> logs) {
        System.out.println("==== " + logs.size() + " ====");
        for (List<Node> log: logs) {
            System.out.println(log.stream().map(Node::toString).collect(Collectors.joining(" -> ")));
        }
    }

}
