package com.salesforceiq.jungle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesforceiq.jungle.model.DockerFSLayer;
import com.salesforceiq.jungle.model.DockerManifest;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

/**
 * Registry util
 * Created by smulakala on 4/21/16.
 */
class RegistryUtil {

    private static final String DOCKER_REGISTRY = "http://192.168.99.100:5000/v2";
    private static final String DOCKER_REPO_MANIFEST = "%s/%s/manifests/%s";

    List<String> getAllRepositories() throws IOException {
        Map<String, List<String>> repositories;
        HttpGet getCatalog = new HttpGet(DOCKER_REGISTRY + "/_catalog");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(getCatalog)) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("Unable to get signature from funnel");
                } else {
                    String data = EntityUtils.toString(entity);
                    repositories = new ObjectMapper().readValue(data,
                            new TypeReference<Map<String, List<String>>>() {
                            });

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
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(getManifest)) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new RuntimeException("Unable to get signature from funnel");
                } else {
                    String data = EntityUtils.toString(entity);
                    dockerManifest = new ObjectMapper().readValue(data, DockerManifest.class);

                    List<DockerFSLayer> fsLayers = dockerManifest.getFsLayers();
                    System.out.println("Name: " + dockerManifest.getName());
                    for (DockerFSLayer fsLayer : fsLayers) {
                        System.out.println(fsLayer.getBlobSum());
                    }
                }
            }
        }
        return dockerManifest;
    }
}
