package com.kgh.korquiz.utils;

import lombok.experimental.UtilityClass;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@UtilityClass
public class NodeListConverter {
    public static Element[] toElementArray(NodeList nodeList) {
        if (nodeList == null || nodeList.getLength() == 0) {
            return new Element[0];
        }

        Element[] array = new Element[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                array[i] = (Element) node;
            } else {
                array[i] = null;
            }
        }
        return array;
    }
}
