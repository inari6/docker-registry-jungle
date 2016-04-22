package com.salesforceiq.jungle;


import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by smulakala on 4/21/16.
 */
public class TreeUtil extends JFrame{

    private JTree tree;
    private JLabel selectedLabel;

    private DefaultMutableTreeNode root;
    private Map<String, DefaultMutableTreeNode> nodes = new HashMap<>();

    public TreeUtil() {
        root = new DefaultMutableTreeNode("Root");
    }

    public void addToRoot(String node) {
        DefaultMutableTreeNode n = new DefaultMutableTreeNode(node);
        nodes.put(node, n);
        root.add(n);
    }

    public void addChild(String parent, String child) {
        DefaultMutableTreeNode parentNode;
        if(nodes.containsKey(parent)) {
             parentNode = nodes.get(parent);
        } else {
            parentNode = new DefaultMutableTreeNode(parent);
            nodes.put(parent, parentNode);
        }
       parentNode.add(new DefaultMutableTreeNode(child));
    }

    public void display() {

        tree = new JTree(root);

        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

        tree.setCellRenderer(renderer);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);
        add(new JScrollPane(tree));

        selectedLabel = new JLabel();
        add(selectedLabel, BorderLayout.SOUTH);

        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                selectedLabel.setText(selectedNode.getUserObject().toString());
            }
        });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("JTree Example");
        this.setSize(200, 200);
        this.setVisible(true);
    }
}
