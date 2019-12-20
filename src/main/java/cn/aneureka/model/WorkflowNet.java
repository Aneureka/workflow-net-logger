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

    private Place in;

    private Place out;

    public WorkflowNet() {
        nodes = new HashSet<>();
        graph = new HashMap<>();
        in = null;
        out = null;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addEdge(Node source, Node target) {
        List<Node> neighbors = graph.getOrDefault(source, new ArrayList<>());
        neighbors.add(target);
        graph.put(source, neighbors);
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
}
