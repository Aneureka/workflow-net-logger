package cn.aneureka.model;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:53
 * @description
 **/
public class Transition extends Node {

    public Transition(String id) {
        super(id);
    }

    public Transition(String id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return String.format("[%s]", super.toString());
    }
}
