package com.drofff.palindrome.utils;

import com.drofff.palindrome.exception.PalindromeException;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.drofff.palindrome.utils.StreamUtils.streamOfEnumeration;
import static com.drofff.palindrome.utils.StringUtils.asHexStr;
import static java.util.stream.Collectors.joining;

public class NetUtils {

    private static final String WLAN_0 = "wlan0";

    private static final String MAC_ADDRESS_DELIMITER = ":";

    private NetUtils() {}

    public static String getMacAddress() {
        try {
            return getWifiMacAddress();
        } catch(SocketException e) {
            throw new PalindromeException(e.getMessage());
        }
    }

    private static String getWifiMacAddress() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        NetworkInterface wifiInterface = getWifiInterface(networkInterfaces);
        byte[] macAddress = wifiInterface.getHardwareAddress();
        return asMacAddressStr(macAddress);
    }

    private static NetworkInterface getWifiInterface(Enumeration<NetworkInterface> networkInterfaces) {
        return streamOfEnumeration(networkInterfaces)
                .filter(NetUtils::isWifiInterface)
                .findFirst()
                .orElseThrow(() -> new PalindromeException("Wifi interface is not available"));
    }

    private static boolean isWifiInterface(NetworkInterface networkInterface) {
        return networkInterface.getName().equals(WLAN_0);
    }

    private static String asMacAddressStr(byte[] macAddress) {
        List<String> octets = new ArrayList<>();
        for(byte octet : macAddress) {
            String octetStr = asHexStr(octet);
            octets.add(octetStr);
        }
        return octets.stream()
                .collect(joining(MAC_ADDRESS_DELIMITER));
    }

}
