package org.example.cy_shop.utils;

import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;

public class DeviceInfoUtil {

    private DeviceInfoUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getDeviceInfo(HttpServletRequest request) {
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String browser = userAgent.getBrowser().getName();
        String os = userAgent.getOperatingSystem().getName();
        String sessionId = request.getSession().getId();
        return browser + " on " + os + " with Session ID " + sessionId;
    }
}
