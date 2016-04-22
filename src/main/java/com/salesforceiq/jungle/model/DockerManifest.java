package com.salesforceiq.jungle.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

/**
 * Created by smulakala on 4/21/16.
 */

@JsonIgnoreProperties(ignoreUnknown =  true)
public class DockerManifest {

    private String name;
    private String tag;
    private String architecture;
    private List<DockerFSLayer> fsLayers;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public List<DockerFSLayer> getFsLayers() {
        if(fsLayers == null) return Collections.emptyList();
        return fsLayers;
    }

    public void setFsLayers(List<DockerFSLayer> fsLayers) {
        this.fsLayers = fsLayers;
    }
}
