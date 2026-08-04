package de.jplag.pdf.utils;

public enum JPlagLinkType {
    CLUSTER("cluster")
    ;

    private String prefix;

    JPlagLinkType(String prefix) {
        this.prefix = prefix;
    }

    public String resolve(Object identifier) {
        return prefix + "-" + String.valueOf(identifier);
    }
}
