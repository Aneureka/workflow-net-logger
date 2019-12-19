package cn.aneureka.model;

import java.util.*;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:40
 * @description
 **/
public class Net {

    private Set<Node> nodes;

    private Map<Node, List<Node>> graph;

    private Place in;

    private Place out;

    public Net() {
        nodes = new HashSet<>();
        graph = new HashMap<>();
        in = null;
        out = null;
    }

}
