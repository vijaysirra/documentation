package com.appranix.platform.model;

import java.util.List;
import java.util.Map;

/**
 *
 * Variable declared for components.
 *
 */
public class Components {

    private String componentName;
    private String required;
    private String limit;
    private String monitor;
    private String flex;
    private Map<String, String> dependentComponent;
    private Map<String, Map<String, String>> attributes;
    private List<String> monitoringComponent;

    /**
     *
     * @return list of monitoring component.
     */
    public List<String> getMonitoringComponent() {
        return monitoringComponent;
    }

    /**
     *
     * @param monitoringComponent list of monitoring component.
     */
    public void setMonitoringComponent(List<String> monitoringComponent) {
        this.monitoringComponent = monitoringComponent;
    }

    /**
     *
     * @return list of attributes for component.
     */
    public Map<String, Map<String, String>> getAttributes() {
        return attributes;
    }

    /**
     *
     * @param attributes list of attributes for component.
     */
    public void setAttributes(Map<String, Map<String, String>> attributes) {
        this.attributes = attributes;
    }

    /**
     *
     * @return list of dependentComponent for component.
     */
    public Map<String, String> getDependentComponent() {
        return dependentComponent;
    }

    /**
     *
     * @param dependentComponent list of dependentComponent for component.
     */
    public void setDependentComponent(Map<String, String> dependentComponent) {
        this.dependentComponent = dependentComponent;
    }

    /**
     *
     * @return component name for a platform.
     */
    public String getComponentName() {
        return componentName;
    }

    /**
     *
     * @param componentName set component name for a platform.
     */
    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    /**
     *
     * @return component is required or not.
     */
    public String getRequired() {
        return required;
    }

    /**
     *
     * @param required set the component is required or not.
     */
    public void setRequired(String required) {
        this.required = required;
    }

    /**
     *
     * @return limit for a component.
     */
    public String getLimit() {
        return limit;
    }

    /**
     *
     * @param limit set for a component.
     */
    public void setLimit(String limit) {
        this.limit = limit;
    }

    /**
     *
     * @return component is monitoring or not.
     */
    public String getMonitor() {
        return monitor;
    }

    /**
     *
     * @param monitor set component is monitoring or not.
     */
    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }

    /**
     *
     * @return flexible or not.
     */
    public String getFlex() {
        return flex;
    }

    /**
     *
     * @param flex set flexible or not.
     */
    public void setFlex(String flex) {
        this.flex = flex;
    }
}