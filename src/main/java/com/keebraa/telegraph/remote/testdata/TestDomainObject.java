package com.keebraa.telegraph.remote.testdata;

import java.util.Map;

/**
 * Just represents test return object that will be serialized, sent by socket,
 * and deserialized.
 * 
 * @author vvasianovych
 *
 */
public class TestDomainObject {

    private String id;

    private Map<String, String> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
