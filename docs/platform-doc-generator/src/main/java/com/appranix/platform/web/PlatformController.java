package com.appranix.platform.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.appranix.platform.service.PackService;

/**
 *
 * Controller to get platform details.
 *
 */
@Controller
public class PlatformController {

    @Autowired
    PackService packService;

    @Value("${ref.doc.url}")
    private String basePath;

    @Value("${asset.url}")
    private String assetUrl;

    /**
     * To get pack details.
     *
     * @param model
     *            to set attributes.
     * @return index page.
     */
    @RequestMapping(value = {"/", "/packs"}, method = RequestMethod.GET)
    protected String getPacks(Model model) {

        model.addAttribute("basePath", basePath);
        model.addAttribute("pack", packService.getPacks());
        model.addAttribute("platform", packService.getPlatforms("core"));
        model.addAttribute("activePack", "core");
        model.addAttribute("assetUrl", assetUrl);
        return "index";

    }

    /**
     * To get platform details for a particular packs.
     *
     * @param model
     *            to set attributes.
     * @param packName
     *            package name.
     * @return index page.
     */
    @RequestMapping(value = "/packs/{packName}", method = RequestMethod.GET)
    protected String getPlatformsByPackName(Model model, @PathVariable String packName) {

        model.addAttribute("basePath", basePath);
        model.addAttribute("pack", packService.getPacks());
        model.addAttribute("platform", packService.getPlatforms(packName));
        model.addAttribute("activePack", packName);
        model.addAttribute("assetUrl", assetUrl);
        return "index";

    }

    /**
     * To get platforms for all packs.
     *
     * @param model
     *            to set attributes.
     * @return platform page.
     */
    @RequestMapping(value = "/platforms", method = RequestMethod.GET)
    protected String getPlatforms(Model model) {

        model.addAttribute("basePath", basePath);
        model.addAttribute("pack", packService.getPacks());
        model.addAttribute("activePack", "core");
        model.addAttribute("assetUrl", assetUrl);
        model.addAttribute("platform", packService.getPlatformsForAllPacks());
        return "platform";

    }

    /**
     * To get platform details for a particular platforms.
     *
     * @param packName
     *            package name.
     * @param platformName
     *            platform name.
     * @param version
     *            version name.
     * @param type
     *            mode of platform.
     * @param model
     *            to set attributes.
     * @return platform details page.
     */
    @RequestMapping(value = "/packs/{packName}/{platformName}/{version}/{type}", method = RequestMethod.GET)
    protected String getPlatformDetailsByVersion(@PathVariable String packName, @PathVariable String platformName,
            @PathVariable String version, @PathVariable String type, Model model) {

        model.addAttribute("basePath", basePath);
        model.addAttribute("assetUrl", assetUrl);
        if (version.equals("latest")) {
            model.addAttribute("platformAttribute",
                    packService.getComponentDetailsForPlatform(packName, platformName, type, null));
        } else {
            model.addAttribute("platformAttribute",
                    packService.getComponentDetailsForPlatform(packName, platformName, type, version));
        }
        return "platformdetails";

    }

    /**
     * To get component details for a particular component.
     *
     * @param packName
     *            pack name.
     * @param platformName
     *            platform name of the pack.
     * @param type
     *            mode of the platform.
     * @param version
     *            of platform.
     * @param component
     *            of platform.
     * @param model
     *            to set attributes.
     * @return component details page.
     */
    @RequestMapping(value = "/packs/{packName}/{platformName}/{version}/{type}/{component}", method = RequestMethod.GET)
    protected String getComponentDetails(@PathVariable String packName, @PathVariable String platformName,
            @PathVariable String type, @PathVariable String version, @PathVariable String component, Model model) {

        model.addAttribute("basePath", basePath);
        model.addAttribute("assetUrl", assetUrl);
        model.addAttribute("platformAttribute",
                packService.getComponentDetail(packName, platformName, type, version, component));
        return "componentdetails";

    }

}
