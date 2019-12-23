package cn.aneureka.model;

import java.util.Objects;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:52
 * @description
 **/
public abstract class Node {

    protected String id;

    protected String name;

    public Node() {
    }

    public Node(String id) {
        this.id = id;
    }

    public Node(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public abstract Node copy();

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Node node = (Node) object;
        return id.equals(node.id);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

