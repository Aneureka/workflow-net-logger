package cn.aneureka.model;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:53
 * @description
 **/
public class Transition extends Node {

    public Transition() {
    }

    public Transition(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return String.format("[%s]", id);
    }
}
