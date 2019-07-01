package com.appranix.platform.service;

import java.util.List;
import java.util.Map;

import com.appranix.platform.model.Platform;

/**
 *
 * PackService is to get platform details.
 *
 */
public interface PackService {

    /**
     * To get list of packs and platform.
     *
     * @return list of packs.
     */
    List<String> getPacks();

    /**
     * Get list of platforms for a particular pack.
     *
     * @param packName
     *            pack name of platforms.
     * @return particular pack of platforms.
     */
    Map<String, Map<String, List<Platform>>> getPlatforms(String packName);

    /**
     * To get list of platforms for all packs.
     *
     * @return platforms of all packs.
     */
    Map<String, Map<String, List<Platform>>> getPlatformsForAllPacks();

    /**
     * Get list of platforms for all packs.
     *
     * @param pack
     *            name.
     * @param platform
     *            name.
     * @param type
     *            mode of platform.
     * @param version
     *            version of platform.
     * @return component list for a platform.
     */
    Platform getComponentDetailsForPlatform(String pack, String platform, String type, String version);

    /**
     * Get component details for a particular platforms.
     *
     * @param pack
     *            name.
     * @param platform
     *            name.
     * @param type
     *            mode of platform.
     * @param version
     *            of platform.
     * @param componentName
     *            of platform.
     * @return attributes and dependent component of a particulat component.
     */
    Platform getComponentDetail(String pack, String platform, String type, String version, String componentName);
}
