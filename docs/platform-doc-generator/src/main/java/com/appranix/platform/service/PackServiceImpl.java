package com.appranix.platform.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.appranix.platform.model.Components;
import com.appranix.platform.model.Platform;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneops.cms.cm.domain.CmsCI;
import com.oneops.cms.cm.domain.CmsCIAttribute;
import com.oneops.cms.md.domain.CmsClazz;
import com.oneops.cms.md.domain.CmsClazzAttribute;
import com.oneops.cms.simple.domain.CmsCIRelationSimple;
import com.oneops.cms.simple.domain.CmsRfcRelationSimple;

/**
 *
 * The platform details for all packs were getting from this class.
 *
 */
@Component
public class PackServiceImpl implements PackService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${adapter}")
    private String adapter;

    @Override
    public List<String> getPacks() {

        logger.info("Get all the packs details.");
        int size = 0;
        List<String> packList = new LinkedList<String>();
        for (CmsCI pack : getCmsCIList("/public")) {
            for (CmsCIAttribute attribute : getCIAttributesById(pack.getCiId())) {
                if (attribute.getAttributeName().equals("visible") && attribute.getDfValue().equals("true")) {
                    if (pack.getCiName().equals("core")) {
                        packList.add(pack.getCiName());
                    }
                }
            }
            if(pack.getCiName().equals("core") && packList.size() != 0) {
                size = packList.size()-1;
            }
        }
        if (packList.contains("core") && size > 0) {
            Collections.swap(packList, 0, size);
        }
        return packList;
    }

    @Override
    public Map<String, Map<String, List<Platform>>> getPlatformsForAllPacks() {

        logger.info("Get the platforms details for all the packs");
        Map<String, Map<String, List<Platform>>> platformListByPackName = new LinkedHashMap<String, Map<String, List<Platform>>>();
        for (String packName : getPacks()) {
            platformListByPackName.put(packName, getPlatforms(packName).get(packName));
        }
        return platformListByPackName;

    }

    @Override
    public Map<String, Map<String, List<Platform>>> getPlatforms(String packName) {

        logger.info("Get the platform details for the pack : " + packName);
        if (!getPacks().contains(packName)) {
            return null;
        }
        Map<String, Map<String, List<Platform>>> platformListByPack = new LinkedHashMap<String, Map<String, List<Platform>>>();
        List<Platform> platformList = new LinkedList<Platform>();
        Set<String> category = new HashSet<String>();

        for (CmsCI pack : getCmsCIList("/public/" + packName + "/packs")) {
            Platform platform = new Platform();

            platform.setPack(packName);
            platform.setPlatformName(pack.getCiName());

            String version = getLatestVersion("/public/" + packName + "/packs/" + pack.getCiName());

            platform.setVersion(version);
            platform.setModes(getMode("/public/" + packName + "/packs/" + pack.getCiName() + "/" + version));

            for (CmsCIAttribute cmciAttribute : getCIAttributesById(pack.getCiId())) {
                if (cmciAttribute.getAttributeName().equals("category")) {
                    platform.setCategory(cmciAttribute.getDfValue());
                    category.add(platform.getCategory());
                }
                if (cmciAttribute.getAttributeName().equals("description")) {
                    platform.setPlatform(cmciAttribute.getDfValue());
                }
            }
            if (!version.equals("0")) {
                platformList.add(platform);
            }
        }

        platformListByPack.put(packName, platformByCategory(category, platformList));
        return platformListByPack;

    }

    /**
     * Get platforms for each category.
     *
     * @param categories
     *            list of categories.
     * @param platform
     *            list of platforms.
     * @return
     */
    public Map<String, List<Platform>> platformByCategory(Set<String> categories, List<Platform> platform) {

        logger.info("Order the platforms based on the category.");
        Map<String, List<Platform>> categoryList = new LinkedHashMap<String, List<Platform>>();
        for (String category : categories) {
            List<Platform> categoryPlatform = new LinkedList<Platform>();
            for (Platform platformlist : platform) {
                if (platformlist.getCategory().equals(category)) {
                    categoryPlatform.add(platformlist);
                }
            }
            categoryList.put(category, categoryPlatform);
        }
        return categoryList;

    }

    /**
     * Get platform details with their component.
     *
     * @param pack
     *            name.
     * @param platform
     *            platform name.
     * @param type
     *            of platform.
     * @param version
     *            of platform.
     * @return platform details.
     */
    public Platform getPlatformDetails(String pack, String platform, String type, String version) {

        logger.info("Collect the platform details for " + platform + " platform");
        Platform platforms = new Platform();
        platforms.setPack(pack);
        platforms.setPlatformName(platform);
        platforms.setVersions(getVersions("/public/" + pack + "/packs/" + platform));
        platforms.setVersion(version);
        platforms.setMode(type);
        Long platformId = (long) 0;
        for (CmsCI cms : getCmsCIList("/public/" + pack + "/packs")) {
            if (cms.getCiName().equals(platform)) {
                platformId = cms.getCiId();
            }
        }
        for (CmsCIAttribute cmciAttribute : getCIAttributesById(platformId)) {
            if (cmciAttribute.getAttributeName().equals("category")) {
                platforms.setCategory(cmciAttribute.getDfValue());
            }
            if (cmciAttribute.getAttributeName().equals("description")) {
                platforms.setPlatform(cmciAttribute.getDfValue());
            }
        }
        if (version == null) {
            version = getLatestVersion("/public/" + pack + "/packs/" + platform);
            platforms.setVersion(version);
        }
        return platforms;

    }

    @Override
    public Platform getComponentDetailsForPlatform(String pack, String platform, String type, String version) {

        logger.info("Get the component details for the " + platform + "platform");
        List<Components> componentList = new LinkedList<Components>();
        Platform platforms = new Platform();
        if (!getPacks().contains(pack)) {
            return platforms;
        }

        platforms = getPlatformDetails(pack, platform, type, version);
        String nsPath = "/public/" + pack + "/packs/" + platform + "/" + platforms.getVersion() + "/" + type;
        Set<String> check = new TreeSet<String>();
        for (CmsCIRelationSimple cmsCI : componentRequireStatus(nsPath, "Requires")) {
            Components component = new Components();
            component.setComponentName(cmsCI.getToCi().getCiName());
            if (getMonitor(cmsCI.getToCi().getCiId()).size() == 0) {
                component.setMonitor("No");
            } else {
                component.setMonitor("Yes");
            }

            String limit[] = cmsCI.getRelationAttributes().get("constraint").split("\\.");
            if (Integer.valueOf(limit[0]) > 0) {
                component.setRequired("Yes");
            } else {
                component.setRequired("Optional");
            }
            component.setLimit("Min : " + limit[0] + " / Max : " + limit[limit.length - 1]);
            String checkFlex = "false";
            for (CmsCIRelationSimple cmsCI1 : componentRequireStatus(nsPath, "DependsOn")) {
                if (cmsCI.getToCi().getCiName().equals(cmsCI1.getToCi().getCiName())) {
                    check.add(cmsCI.getToCi().getCiName());
                    if (cmsCI1.getRelationAttributes().get("flex").equals("true")) {
                        checkFlex = "true";
                    }
                }
            }
            if (!check.contains(cmsCI.getToCi().getCiName())) {
                component.setFlex("-");
            } else {
                component.setFlex(checkFlex);
            }

            componentList.add(component);
        }
        platforms.setComponents(componentList);
        return platforms;

    }

    @Override
    public Platform getComponentDetail(String pack, String platformName, String type, String version,
            String componentName) {

        logger.info("Get the component details for the " + componentName + "component");
        Platform platform = new Platform();
        Components component = new Components();

        if (!getPacks().contains(pack)) {
            component.setDependentComponent(new LinkedHashMap<String, String>());
            component.setMonitoringComponent(new LinkedList<String>());
            platform.setComponent(component);
            return platform;
        }

        platform = getPlatformDetails(pack, platformName, type, version);
        String nsPath = "/public/" + pack + "/packs/" + platformName + "/" + platform.getVersion() + "/" + type;
        List<CmsCI> components = getCmsCIList(nsPath);
        Map<String, String> depend = new LinkedHashMap<String, String>();

        for (CmsCI cms : components) {
            if (cms.getCiName().equals(componentName)) {

                CmsClazz cmsList = getAttributeList(cms.getCiClassName());
                component.setAttributes(getDescription(cmsList));
                component.setComponentName(componentName);
                for (String dependentcomp : dependentComponent(cms.getCiId())) {
                    for (CmsCIRelationSimple cmsCI : componentRequireStatus(nsPath, "Requires")) {
                        if (cmsCI.getToCi().getCiName().equals(dependentcomp)) {
                            String limit[] = cmsCI.getRelationAttributes().get("constraint").split("\\.");
                            depend.put(dependentcomp, "Min : " + limit[0] + " / Max : " + limit[limit.length - 1]);

                        }
                    }
                }
                for (CmsCIRelationSimple cmsCI : componentRequireStatus(nsPath, "Requires")) {
                    if (cmsCI.getToCi().getCiName().equals(componentName)) {
                        List<String> monitor = new LinkedList<String>();
                        for (CmsRfcRelationSimple monitoring : getMonitor(cmsCI.getToCi().getCiId())) {
                            monitor.add(monitoring.getToCi().getCiName());
                        }
                        component.setMonitoringComponent(monitor);
                    }
                }

            }
        }
        component.setDependentComponent(depend);
        platform.setComponent(component);
        return platform;

    }

    public Map<String, Map<String, String>> getDescription(CmsClazz cmsList) {

        Map<String, Map<String, String>> attribute = new LinkedHashMap<String, Map<String, String>>();
        JsonNode node = null;

        if (cmsList != null) {
            for (CmsClazzAttribute cmsAtr : cmsList.getMdAttributes()) {
                Map<String, String> value = new LinkedHashMap<String, String>();
                try {
                    node = new ObjectMapper().readValue(cmsAtr.getValueFormat(), JsonNode.class);
                    if (node.get("help") != null) {
                        value.put(cmsAtr.getDefaultValue(), node.get("help").asText());
                    } else {
                        value.put(cmsAtr.getDefaultValue(), "");
                    }
                } catch (JsonParseException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                attribute.put(cmsAtr.getDescription(), value);
            }
        }
        return attribute;

    }

    /**
     * To check whether the component is monitoring or not.
     *
     * @param id
     *            component id.
     * @return list of monitor.
     */
    public List<CmsRfcRelationSimple> getMonitor(long id) {

        RestTemplate rest = new RestTemplate();
        try {
            ResponseEntity<CmsRfcRelationSimple[]> adapterResult = rest.getForEntity(
                    adapter + "/dj/simple/relations?ciId=" + id
                            + "&direction=from&includeToCi=true&relationShortName=WatchedBy",
                    CmsRfcRelationSimple[].class);
            return Arrays.asList(adapterResult.getBody());
        } catch (Exception ex) {
            logger.debug("while fetching monitoring status of component " + id + "error occured");
            logger.error("Invalid url to fetch monitor");
        }
        return new LinkedList<CmsRfcRelationSimple>();

    }

    /**
     * List of version for a particular platform.
     *
     * @param nsPath
     *            nspath to get version list.
     * @return version list.
     */
    public Map<String, String> getVersions(String nsPath) {

        Map<String, String> versions = new LinkedHashMap<String, String>();
        for (CmsCI version : getCmsCIList(nsPath)) {
            for (CmsCIAttribute statusAttribute : getCIAttributesById(version.getCiId())) {
                if (statusAttribute.getAttributeName().equals("enabled")) {
                    versions.put(version.getCiName(), statusAttribute.getDfValue());
                }
            }
        }
        return versions;

    }

    /**
     * To get attributes for a platform or component.
     *
     * @param id
     *            platform or component id.
     * @return list of attributes.
     */
    public List<CmsCIAttribute> getCIAttributesById(long id) {

        RestTemplate rest = new RestTemplate();
        try {
            ResponseEntity<CmsCIAttribute[]> adapterResult = rest
                    .getForEntity(adapter + "/cm/simple/cis/source/attribute?ciId=" + id, CmsCIAttribute[].class);
            return Arrays.asList(adapterResult.getBody());
        } catch (Exception ex) {
            logger.debug("while fetching attributes of platform or component " + id + "error occured");
            logger.error("Invalid url to fetch packs");
        }
        return new LinkedList<CmsCIAttribute>();

    }

    /**
     * To get component required status for particular platform.
     *
     * @param nsPath
     *            of the packs or platform.
     * @param relationShortName.
     * @return list of platforms.
     */
    public List<CmsCIRelationSimple> componentRequireStatus(String nsPath, String relationShortName) {

        RestTemplate rest = new RestTemplate();
        try {
            ResponseEntity<CmsCIRelationSimple[]> adapterResult = rest
                    .getForEntity(adapter + "/cm/simple/relations?includeToCi=true&nsPath=" + nsPath
                            + "&relationShortName=" + relationShortName, CmsCIRelationSimple[].class);
            return Arrays.asList(adapterResult.getBody());
        } catch (Exception ex) {
            logger.debug("while fetching component required status of" + nsPath + "error occured");
            logger.error("Invalid url to fetch Platform");
        }
        return new LinkedList<CmsCIRelationSimple>();

    }

    /**
     * To get mode of the platform.
     *
     * @param modeApi
     *            to get mode of platforms.
     * @return mode of platorm.
     */
    public List<String> getMode(String modeApi) {

        RestTemplate rest = new RestTemplate();
        try {
            ResponseEntity<String[]> modeResult = rest.getForEntity(adapter + "/platform?nsPath=" + modeApi,
                    String[].class);
            List<String> modes = new LinkedList<String>();
            for (String mode : Arrays.asList(modeResult.getBody())) {
                modes.add(mode);
            }
            Collections.sort(modes, Collections.reverseOrder());
            return modes;
        } catch (Exception ex) {
            logger.debug("while fetching modes of platform " + modeApi + "error occured");
            logger.error("Invalid url to fetch mode");
        }
        return new LinkedList<String>();

    }

    /**
     * To get latest version of particular platform.
     *
     * @param nsPath
     *            of the packs or platform.
     * @return latest version.
     */
    public String getLatestVersion(String nsPath) {

        int platformVersion = 0;
        try {
            for (CmsCI version : getCmsCIList(nsPath)) {
                for (CmsCIAttribute statusAttribute : getCIAttributesById(version.getCiId())) {
                    if (statusAttribute.getAttributeName().equals("enabled")) {
                        if (statusAttribute.getDfValue().equals("true")) {
                            if (platformVersion < Integer.valueOf(version.getCiName())) {
                                platformVersion = Integer.valueOf(version.getCiName());
                            }
                        }
                    }
                }
            }
        } catch(NumberFormatException ex) {
            logger.warn("while fetching the version for the platform " + nsPath);
        }
        return String.valueOf(platformVersion);
    }

    /**
     * Get list of packs or platforms.
     *
     * @param nsPath
     *            of the packs or platform.
     * @return list of packs or platforms.
     */
    public List<CmsCI> getCmsCIList(String nsPath) {

        RestTemplate rest = new RestTemplate();
        try {
            ResponseEntity<CmsCI[]> adapterResult = rest
                    .getForEntity(adapter + "/cm/simple/cis/source?nsPath=" + nsPath, CmsCI[].class);
            return Arrays.asList(adapterResult.getBody());
        } catch (Exception ex) {
            logger.debug("while fetching packs or platform list " + nsPath + "error occured");
            logger.error("Invalid url to fetch packs");
        }
        return new ArrayList<CmsCI>();

    }

    /**
     * Get dependent component for a particular component.
     *
     * @param id
     *            ciId.
     * @return list of dependent component.
     */
    public List<String> dependentComponent(long id) {

        RestTemplate rest = new RestTemplate();
        try {
            ResponseEntity<CmsRfcRelationSimple[]> adapterResult = rest.getForEntity(adapter
                    + "/dj/simple/relations?attrProps=owner&ciId=" + id + "&direction=from&relationShortName=DependsOn",
                    CmsRfcRelationSimple[].class);
            List<String> dependentComponents = new LinkedList<String>();
            for (CmsRfcRelationSimple component : Arrays.asList(adapterResult.getBody())) {
                dependentComponents.add(component.getToCi().getCiName());
            }
            return dependentComponents;
        } catch (Exception ex) {
            logger.debug("while fetching dependent component for " + id + "error occured");
            logger.error("Invalid url to fetch dependentComponent");
        }
        return new LinkedList<String>();

    }

    /**
     * Get list of attributes for a particular component.
     *
     * @param packName
     *            pack name.
     * @param componentName
     *            component name.
     * @return list of attributes.
     */
    public CmsClazz getAttributeList(String componentName) {

        RestTemplate rest = new RestTemplate();
        try {
            ResponseEntity<CmsClazz> adapterResult = null;
            adapterResult = rest.getForEntity(adapter + "/md/classes/" + componentName + "?includeActions=true",
                    CmsClazz.class);
            return adapterResult.getBody();
        } catch (Exception ex) {
            logger.debug("while fetching Attribute list for component " + componentName + "error occured");
            logger.error("Invalid url to fetch AttributeList");
        }
        return new CmsClazz();

    }

}
