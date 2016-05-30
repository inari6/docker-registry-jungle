package com.salesforceiq.jungle.controllers;

import com.salesforceiq.jungle.DockerRegistryGraphGenerator;
import com.salesforceiq.jungle.util.GraphUtil;
import com.salesforceiq.jungle.util.RegistryUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 *
 * Created by smulakala on 4/21/16.
 */

@RestController
public class GraphController {

    /*@RequestMapping("/")
    public String index() {
        return "Docker registry graph view ..";
    }*/

    @RequestMapping(value = "/graph", method = RequestMethod.GET)
    public String drawGraph() {

        DockerRegistryGraphGenerator drawer = new DockerRegistryGraphGenerator();
        RegistryUtil registryUtil = new RegistryUtil();
        GraphUtil graphUtil = new GraphUtil();
        try {
            List<String> repos = registryUtil.getAllRepositories();
            //List<String> repos = Files.readAllLines(new File("/tmp/reposlist").toPath());
            return drawer.drawGraph(graphUtil, registryUtil, repos);
        } catch (Exception e) {
            throw new RuntimeException("Not able to draw graph" + e.getMessage());
        }
    }
}
