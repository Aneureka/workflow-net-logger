package cn.aneureka.model;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:53
 * @description
 **/
public class Place extends Node {

    public Place() {
    }

    public Place(String id) {
        super(id);
    }

    @Override
    public String toString() {
        return String.format("(%s)", id);
    }
}
