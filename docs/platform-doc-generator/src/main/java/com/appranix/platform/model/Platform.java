package com.appranix.platform.model;

import java.util.List;
import java.util.Map;

/**
 *
 * Variable declared for platform.
 *
 */
public class Platform {

    private String platformName;
    private List<String> modes;
    private String pack;
    private String version;
    private String mode;
    private Map<String, String> versions;
    private String category;
    private List<Components> components;
    private Components component;
    private String description;
    private String platform;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return platform name.
     */
    public String getPlatform() {
        return platform;
    }

    /**
     *
     * @param platform name.
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     *
     * @return platform description.
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description set platform description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return component name.
     */
    public Components getComponent() {
        return component;
    }

    /**
     *
     * @param component set component name.
     */
    public void setComponent(Components component) {
        this.component = component;
    }

    /**
     *
     * @return list of components for a platform.
     */
    public List<Components> getComponents() {
        return components;
    }

    /**
     *
     * @param components set list of components for a platform.
     */
    public void setComponents(List<Components> components) {
        this.components = components;
    }

    /**
     *
     * @return category of platform.
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category set category of platform.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return list of version for a platform.
     */
    public Map<String, String> getVersions() {
        return versions;
    }

    /**
     *
     * @param versions set list of version for a platform.
     */
    public void setVersions(Map<String, String> versions) {
        this.versions = versions;
    }

       /**
        *
        * @return version for platform.
        */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @param version for a platform.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     *
     * @return platform name.
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     *
     * @param platformName platform name.
     */
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    /**
     *
     * @return list of modes of platform.
     */
    public List<String> getModes() {
        return modes;
    }

    /**
     *
     * @return mode of platform
     */
    public String getMode() {
        return mode;
    }

    /**
     *
     * @param mode set mode of platorm.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     *
     * @param modes set modes.
     */
    public void setModes(List<String> modes) {
        this.modes = modes;
    }

    /**
     *
     * @return pack name.
     */
    public String getPack() {
        return pack;
    }

    /**
     *
     * @param pack set pack name.
     */
    public void setPack(String pack) {
        this.pack = pack;
    }

}