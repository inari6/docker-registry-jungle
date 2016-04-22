package com.salesforceiq.jungle;

import com.salesforceiq.jungle.model.TreeVertex;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by smulakala on 4/21/16.
 */
public class JungUtil {

    DirectedSparseGraph graph;

    public JungUtil() {

        graph = new DirectedSparseGraph();
    }

    public void addVertex(TreeVertex vertex) {
        graph.addVertex(vertex);
    }

    public void addEdge(TreeVertex v1, TreeVertex v2) {
        String id= String.format("%s-%s", v1.getSha256(), v2.getSha256());
        graph.addEdge(id, v1, v2);
    }

    @SuppressWarnings("unchecked")
    public void display() {

        /*VisualizationImageServer vs =
                new VisualizationImageServer(
                        new CircleLayout(graph), new Dimension(350, 350));

        vs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());

        JFrame frame = new JFrame();
        frame.getContentPane().add(vs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);*/

        BasicVisualizationServer<Integer,String> vv =
                new BasicVisualizationServer<>(new CircleLayout(graph), new Dimension(350, 350));
        vv.setPreferredSize(new Dimension(350,350));

        // Set up a new stroke Transformer for the edges
        float dash[] = {10.0f};
        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
        Transformer<String, Stroke> edgeStrokeTransformer =
                new Transformer<String, Stroke>() {
                    public Stroke transform(String s) {
                        return edgeStroke;
                    }
                };
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());

        JFrame frame = new JFrame("Simple Graph View 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv);
        frame.pack();
        frame.setVisible(true);
    }
}
