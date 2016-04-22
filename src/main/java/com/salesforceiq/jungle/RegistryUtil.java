package com.salesforceiq.jungle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesforceiq.jungle.model.DockerFSLayer;
import com.salesforceiq.jungle.model.DockerManifest;
import com.salesforceiq.jungle.model.TreeVertex;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Created by smulakala on 4/21/16.
 */
class RegistryUtil {

    private static final String DOCKER_REGISTRY = "http://192.168.99.100:5000/v2";
    private static final String DOCKER_REPO_MANIFEST = "%s/%s/manifests/%s";

    private static final String INDENT = "  ";
    private static final String ARROW = " -> ";
    private static final String LINE = " -- ";


    List<String> getAllRepositories() throws IOException {
        Map<String, List<String>> repositories = null;
        HttpGet getCatalog = new HttpGet(DOCKER_REGISTRY + "/_catalog");
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(getCatalog)) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("Unable to get signature from funnel");
                } else {
                    String data = EntityUtils.toString(entity);
                    repositories = new ObjectMapper().readValue(data,
                            new TypeReference<Map<String,List<String>>>() {});

                    System.out.println(repositories);
                }
            }
        }
        return repositories.get("repositories");

    }

    DockerManifest getManifest(String repoName) throws IOException {

        String manifestUrl = String.format(DOCKER_REPO_MANIFEST, DOCKER_REGISTRY, repoName, "latest");

        DockerManifest dockerManifest;
        HttpGet getManifest = new HttpGet(manifestUrl);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpclient.execute(getManifest)) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("Unable to get signature from funnel");
                } else {
                    String data = EntityUtils.toString(entity);
                    dockerManifest = new ObjectMapper().readValue(data, DockerManifest.class);

                    List<DockerFSLayer> fsLayers = dockerManifest.getFsLayers();
                    System.out.println("Name: " + dockerManifest.getName());
                    for(DockerFSLayer fsLayer : fsLayers) {
                        System.out.println(fsLayer.getBlobSum());
                    }
                }
            }
        }
        return dockerManifest;
    }

    public static void main(String[] args) throws Exception {

        RegistryUtil registryUtil = new RegistryUtil();
        GraphUtil graphUtil = new GraphUtil();
        JungUtil jungUtil = new JungUtil();
        TreeUtil treeUtil = new TreeUtil();

        List<String> repos = registryUtil.getAllRepositories();

        /*for(String repo: repos) {
            DockerManifest manifest = registryUtil.getManifest(repo);
            List<DockerFSLayer> fsLayers = manifest.getFsLayers();
            System.out.println();
            if(fsLayers.size() == 1) {
                graphUtil.addEdge(fsLayers.get(0).getBlobSum(), "ROOT");
            } else {
                for(int i=fsLayers.size()-1; i>0; i--) {
                    String nodeA = fsLayers.get(i).getBlobSum();
                    String nodeB = fsLayers.get(i-1).getBlobSum();
                    System.out.println(nodeA + " ----> " + nodeB);
                    graphUtil.addEdge(nodeA, nodeB);
                }
            }
        }

        graphUtil.display();*/

        /*for(String repo: repos) {
            DockerManifest manifest = registryUtil.getManifest(repo);
            List<DockerFSLayer> fsLayers = manifest.getFsLayers();
            System.out.println();
            if(fsLayers.size() == 1) {
                jungUtil.addVertex(fsLayers.get(0).getBlobSum());
            } else {
                for(int i=fsLayers.size()-1; i>0; i--) {
                    String nodeA = fsLayers.get(i).getBlobSum();
                    String nodeB = fsLayers.get(i-1).getBlobSum();
                    jungUtil.addVertex(nodeA);
                    jungUtil.addVertex(nodeB);
                    System.out.println(nodeA + " ----> " + nodeB);
                    jungUtil.addEdge(nodeB, nodeA);
                }
            }
        }

        jungUtil.display();*/

        /*for(String repo: repos) {
            DockerManifest manifest = registryUtil.getManifest(repo);
            List<DockerFSLayer> fsLayers = manifest.getFsLayers();
            System.out.println();
            if(fsLayers.size() == 1) {
                treeUtil.addToRoot(fsLayers.get(0).getBlobSum());
            } else {
                for(int i=fsLayers.size()-1; i>0; i--) {
                    String nodeA = fsLayers.get(i).getBlobSum();
                    String nodeB = fsLayers.get(i-1).getBlobSum();
                    System.out.println(nodeA + " ----> " + nodeB);
                    treeUtil.addChild(nodeA, nodeB);
                }
            }
        }

        treeUtil.display();*/

        StringBuilder builder = new StringBuilder();
        builder.append("digraph");
        builder.append(INDENT);
        builder.append("registry");
        builder.append(" {\n");
        builder.append(INDENT);
        builder.append("node [color=lightblue2, style=filled];\n");

        List<String> nodeNames = new ArrayList<>();
        List<String> edgeNames = new ArrayList<>();

        String nodeNameFormatter = "\"%s\" [label=\"%s\" color=\"Red\"]";

        for(String repo: repos) {
            DockerManifest manifest = registryUtil.getManifest(repo);
            List<DockerFSLayer> fsLayers = manifest.getFsLayers();
            System.out.println();
            if(fsLayers.size() == 1) {
                TreeVertex vertex = new TreeVertex(manifest.getName(), fsLayers.get(0).getBlobSum());
                jungUtil.addVertex(vertex);
                nodeNames.add(String.format(nodeNameFormatter, vertex.getSha256(), vertex.getName() == null ? vertex.getSha256() : vertex.getName()));
            } else {
                fsLayers = registryUtil.updateDuplicates(fsLayers);

                for(int i=fsLayers.size()-1; i>0; i--) {
                    TreeVertex nodeA = new TreeVertex(null, fsLayers.get(i).getBlobSum());
                    TreeVertex nodeB;
                    if(i == 1) {
                        nodeB = new TreeVertex(manifest.getName(), fsLayers.get(i-1).getBlobSum());
                        nodeNames.add(String.format(nodeNameFormatter, nodeB.getSha256(), nodeB.getName() == null ? nodeB.getSha256() : nodeB.getName()));
                    } else {
                        String blobSum = fsLayers.get(i-1).getBlobSum();
                        nodeB = new TreeVertex(null, blobSum);
                    }

                    jungUtil.addVertex(nodeA);
                    jungUtil.addVertex(nodeB);
                    System.out.println(nodeA.getSha256() + " ----> " + nodeB.getSha256());
                    jungUtil.addEdge(nodeB, nodeA);

                    edgeNames.add("\""+nodeA.getSha256() + "\"" + ARROW + "\"" + nodeB.getSha256() + "\"");
                }
            }
        }

        Collections.sort(nodeNames);
        List<String> uniqueEdgeNames = edgeNames.stream().distinct().collect(Collectors.toList());
        Collections.sort(uniqueEdgeNames);

        for (String nodeName : nodeNames) {
            builder.append(INDENT);
            builder.append(nodeName);
            builder.append(";\n");
        }

        for (String edgeName : uniqueEdgeNames) {
            builder.append(INDENT);
            builder.append(edgeName);
            builder.append(";\n");
        }

        builder.append("}\n");

        System.out.println(builder.toString());

        jungUtil.display();


    }


    public List<DockerFSLayer> updateDuplicates(List<DockerFSLayer> originalLayers) {

        Map<String, Integer> fsLayerMap = new HashMap<>();
        List<DockerFSLayer> updatedLayers = new LinkedList<>();

        for(DockerFSLayer layer: originalLayers) {
            String blobSum = layer.getBlobSum();
            if(fsLayerMap.containsKey(blobSum)) {
                fsLayerMap.put(blobSum, fsLayerMap.get(blobSum)+1);
            } else {
                fsLayerMap.put(blobSum, 1);
            }
        }

        for(DockerFSLayer layer: originalLayers) {
            if(fsLayerMap.get(layer.getBlobSum()) >= 2) {
                int count = fsLayerMap.get(layer.getBlobSum());
                updatedLayers.add(new DockerFSLayer(count+"-"+layer.getBlobSum()));
                fsLayerMap.put(layer.getBlobSum(), --count);
            } else {
                updatedLayers.add(layer);
            }
        }
        return updatedLayers;
    }
}
