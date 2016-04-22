package com.salesforceiq.jungle.util;

import com.salesforceiq.jungle.model.TreeNode;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.IOException;
import java.io.StringWriter;

/**
 *
 * Created by smulakala on 4/21/16.
 */
public class GraphUtil {

    private final SingleGraph graph;

    public GraphUtil() {
        graph = new SingleGraph("DockerRegistry");
        graph.setStrict(false);
        graph.setAutoCreate(false);
        graph.setNullAttributesAreErrors(false);
    }

    public void addNode(TreeNode node) {
        Node n = graph.addNode(node.getSha256());
        if(node.getName() != null) {
            n.setAttribute("label", node.getName());
            n.setAttribute("color", "Green");
            n.setAttribute("style", "filled");
        }
    }

    public void addEdge(TreeNode node1, TreeNode node2) {
        String id = String.format("%s-%s", node1.getSha256(), node2.getSha256());
        graph.addEdge(id, node2.getSha256(), node1.getSha256(), true);
    }

    public void display() {
        graph.display();
    }

    public String generateDotFile() throws IOException {
        FileSinkDOT fs = new FileSinkDOT(true);
        String dotFileContent;
        try(StringWriter writer = new StringWriter()) {
            fs.writeAll(graph, writer);
            dotFileContent =  writer.toString();
        }
        return dotFileContent;
    }
}
