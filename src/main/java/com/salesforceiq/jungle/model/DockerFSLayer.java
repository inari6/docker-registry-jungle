package com.salesforceiq.jungle.model;

/**
 * Created by smulakala on 4/21/16.
 */
public class DockerFSLayer {

    private String blobSum;

    public DockerFSLayer() {

    }

    public DockerFSLayer(String blobSum) {
        this.blobSum = blobSum;
    }

    public String getBlobSum() {
        return blobSum;
    }

    public void setBlobSum(String blobSum) {
        this.blobSum = blobSum;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) { return true; } if (obj == null || obj.getClass() != this.getClass()) { return false; }

        DockerFSLayer guest = (DockerFSLayer) obj;
        return (guest.getBlobSum() != null && guest.getBlobSum().equals(this.getBlobSum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((blobSum == null) ? 0 : blobSum.hashCode());
        return result;
    }
}
