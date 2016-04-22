package com.salesforceiq.jungle;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * Created by smulakala on 4/21/16.
 */
class GraphUtil {

    private final SingleGraph graph;

    public GraphUtil() {
        graph = new SingleGraph("DockerRegistry");
        graph.setStrict(false);
        graph.setAutoCreate(true);
    }

    public void addNode(String node) {
        graph.addNode(node);
    }

    public void addEdge(String a, String b) {

            String id= String.format("%s-%s", a, b);
            graph.addEdge(id, a, b);


    }

    public void display() {
        for(Node node: graph) {
            node.addAttribute("ui.label", node.getId());
        }

        graph.display();
    }

}
