package com.salesforceiq.jungle;

import com.salesforceiq.jungle.model.DockerFSLayer;
import com.salesforceiq.jungle.model.DockerManifest;
import com.salesforceiq.jungle.model.TreeNode;
import com.salesforceiq.jungle.util.GraphUtil;
import com.salesforceiq.jungle.util.RegistryUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by smulakala on 4/21/16.
 */
public class DockerRegistryGraphGenerator {

    public String drawGraph(GraphUtil graphUtil,
                          RegistryUtil registryUtil,
                          List<String> repos) throws Exception {

        for (String repo : repos) {
            DockerManifest manifest = registryUtil.getManifest(repo);
            List<DockerFSLayer> fsLayers = manifest.getFsLayers();
            System.out.println();
            if (fsLayers.size() == 1) {
                TreeNode vertex = new TreeNode(manifest.getName(), fsLayers.get(0).getBlobSum());
                graphUtil.addNode(vertex);
            } else {
                fsLayers = updateDuplicates(fsLayers);

                for (int i = fsLayers.size() - 1; i > 0; i--) {
                    TreeNode nodeA = new TreeNode(null, fsLayers.get(i).getBlobSum());
                    String imgName = null;
                    if (i == 1) {
                        imgName = manifest.getName();
                    }

                    TreeNode nodeB = new TreeNode(imgName, fsLayers.get(i - 1).getBlobSum());

                    graphUtil.addNode(nodeA);
                    graphUtil.addNode(nodeB);

                    System.out.println(nodeA.getSha256() + " ----> " + nodeB.getSha256());
                    graphUtil.addEdge(nodeB, nodeA);

                }
            }
        }


        //graphUtil.display();
        return graphUtil.generateDotFile();
    }

    public List<DockerFSLayer> updateDuplicates(List<DockerFSLayer> originalLayers) {

        Map<String, Integer> fsLayerMap = new HashMap<>();
        List<DockerFSLayer> updatedLayers = new LinkedList<>();

        for (DockerFSLayer layer : originalLayers) {
            String blobSum = layer.getBlobSum();
            if (fsLayerMap.containsKey(blobSum)) {
                fsLayerMap.put(blobSum, fsLayerMap.get(blobSum) + 1);
            } else {
                fsLayerMap.put(blobSum, 1);
            }
        }

        for (DockerFSLayer layer : originalLayers) {
            if (fsLayerMap.get(layer.getBlobSum()) >= 2) {
                int count = fsLayerMap.get(layer.getBlobSum());
                updatedLayers.add(new DockerFSLayer(count + "-" + layer.getBlobSum()));
                fsLayerMap.put(layer.getBlobSum(), --count);
            } else {
                updatedLayers.add(layer);
            }
        }
        return updatedLayers;
    }
}