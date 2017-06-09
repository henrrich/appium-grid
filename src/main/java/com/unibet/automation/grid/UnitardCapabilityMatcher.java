package com.unibet.automation.grid;

import org.openqa.grid.internal.utils.DefaultCapabilityMatcher;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by viodia on 26/01/2016.
 */
public class UnitardCapabilityMatcher extends DefaultCapabilityMatcher {

    private static final Logger logger = Logger.getLogger(UnitardCapabilityMatcher.class.getName());

    private final static String CAP_PLATFORM_VERSION = "platformVersion";
    private final static String CAP_PLATFORM_NAME = "platformName";
    private final static String CAP_DEVICE_NAME = "deviceName";
    private final static String CAP_BROWSER_NAME = "browserName";

    @Override
    public boolean matches(Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
        boolean basicChecks = super.matches(nodeCapability, requestedCapability);

        logger.info("basic check result: " + basicChecks);

        // deviceName, platformName and platformVersion are required for mobile web app and native app testing
        // if any of them is null, handle the request as desktop test session
        if (!requestedCapability.containsKey(CAP_DEVICE_NAME) || !requestedCapability.containsKey(CAP_PLATFORM_NAME) || !requestedCapability.containsKey(CAP_PLATFORM_VERSION)) {
            logger.info("DeviceName, platformName or platformVersion are missing from request!");
            return basicChecks;
        }

        return matchMobileTestRequest(nodeCapability, requestedCapability);
    }

    private boolean matchMobileTestRequest(Map<String, Object> nodeCapability, Map<String, Object> requestedCapability) {
        boolean isMatched = true;

        final String[] mobileCaps = {CAP_DEVICE_NAME, CAP_PLATFORM_NAME, CAP_PLATFORM_VERSION};
        for (String cap : mobileCaps) {

            logger.info("Checking capability " + cap);

            if (!nodeCapability.containsKey(cap)) {
                logger.info("Capability " + cap + " is missing from node config!");
                isMatched = false;
                break;
            }
            if (!nodeCapability.get(cap).toString().equalsIgnoreCase(requestedCapability.get(cap).toString())) {
                logger.info("Value of capability " + cap + " is not matched!");
                isMatched = false;
                break;
            }
        }

        // if any of deviceName, platformName and platformVersion is not matched, return false
        if (!isMatched) {
            logger.info("DeviceName, platformName or platformVersion are not matched!");
            return false;
        }

        if (requestedCapability.containsKey(CAP_BROWSER_NAME)) {
            // if browserName is specified in the request, go for mobile web app test session
            logger.info("Checking browserName, it is a web app test session!");
            if (!nodeCapability.get(CAP_BROWSER_NAME).toString().equalsIgnoreCase(requestedCapability.get(CAP_BROWSER_NAME).toString())) {
                logger.info("Value of browserName is not matched!");
                isMatched = false;
            }
        }

        logger.info("Capability matching result: " + isMatched);
        return isMatched;
    }


}

