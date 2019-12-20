package cn.aneureka;

import cn.aneureka.model.Node;
import cn.aneureka.model.Place;
import cn.aneureka.model.Transition;

/**
 * @author Aneureka
 * @createdAt 2019-12-20 15:42
 * @description
 **/
public class NodeFactory {

    private final static String PLACE_PREFIX = "place";

    private final static String TRANSITION_PREFIX = "trans";

    public Node createNode(String id) {
        if (id == null) {
            throw new NullPointerException("id is null");
        }
        if (id.startsWith(PLACE_PREFIX)) {
            return new Place(id);
        } else if (id.startsWith(TRANSITION_PREFIX)) {
            return new Transition(id);
        } else {
            throw new IllegalArgumentException("invalid id");
        }
    }
}
