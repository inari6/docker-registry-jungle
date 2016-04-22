package com.salesforceiq.jungle.model;

/**
 * Created by smulakala on 4/21/16.
 */
public class TreeVertex {
    private final String name;
    private final String sha256;

    public TreeVertex(String name, String sha256) {
        this.name = name;
        this.sha256 = sha256.substring(0,18);
    }

    public String getName() {
        return name;
    }

    public String getSha256() {
        return sha256;
    }

    @Override
    public String toString() {
        if(name != null && !name.isEmpty()) return this.name + ":" + this.sha256;
        return this.sha256;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) { return true; } if (obj == null || obj.getClass() != this.getClass()) { return false; }

        TreeVertex guest = (TreeVertex) obj;
        return (guest.getSha256() != null && guest.getSha256().equals(this.getSha256()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sha256 == null) ? 0 : sha256.hashCode());
        return result;
    }
}
