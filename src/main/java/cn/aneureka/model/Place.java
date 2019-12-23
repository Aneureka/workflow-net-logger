package cn.aneureka.model;

/**
 * @author Aneureka
 * @createdAt 2019-12-19 20:53
 * @description
 **/
public class Place extends Node {

    public Place(String id) {
        super(id);
    }

    public Place(String id, String name) {
        super(id, name);
    }

    @Override
    public Node copy() {
        return new Place(this.id, this.name);
    }

    @Override
    public String toString() {
        return String.format("(%s)", super.toString());
    }
}
