package inc.wastedmynd.keylesstorage.ui.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * <div>
 * <p>
 * created on 29 Apr 2015 at 7:23:13 PM
 * </p>
 * </div>
 * Copyright � 2015 wastedMind Media Projects.
 * <p>
 * Copyright . All Rights Reserved.
 * </p>
 *
 * <div>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * </div>
 *
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 *
 * <div>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </div>
 *
 * <div>
 * <p>
 * email at
 * </p>
 * <p>
 * kin.afro@gmail.com
 * </p>
 * </div>
 *
 * @author sizwe
 */

/**
 * <div>
 * <a field="Copyrights"></a> <h3>Copyright</h3>
 * <p>
 * created on 29 Apr 2015 at 7:23:13 PM
 * </p>
 * </div>
 * <p/>
 * <p>
 * Copyright � 2015 wastedMind Media Projects. All Rights Reserved.
 * </p>
 * <p/>
 * <div>
 * <a field="ContactInfo"></a> <h3>Contact Information</h3>
 * <p/>
 * <p>
 * Email : kin.afro@gmail.com
 * </p>
 * </div>
 * <p/>
 * <div>
 * <a field="ImportantNote"></a> <h3>Important Note</h3>
 * <p>
 * <!--Note-->
 * </p>
 * </div>
 *
 * @author sizwe
 */
public class Network {
    private static final String LOG = "Network";

    private NetworkInterface netInterface = null;
    private String iPv4StartingIP;
    private String iPv4BroadCastIP;
    private String iPv4Mask;

    /**
     * @return the netInterface
     * @throws SocketException
     */
    public NetworkInterface getNetInterface() throws SocketException {
        if (netInterface == null) netInterface = NetworkInterface.getByName("wlan0");

        return netInterface;
    }

    public static String getIPv4Address() {
        try {

            for (Enumeration<NetworkInterface> networkInterfaceEnum = NetworkInterface.getNetworkInterfaces(); networkInterfaceEnum
                    .hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaceEnum.nextElement();

                for (Enumeration<InetAddress> ipAddressEnum = networkInterface.getInetAddresses(); ipAddressEnum
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddressEnum.nextElement();
                    // ---check that it is not a loopback address and
                    // it is IPv4---
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress
                                .getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("getLocalIpv4Address", ex.toString());
        } catch (IOException e) {
            Log.e("getLocalIpv4Address", e.toString());
        } catch (Exception f) {
            Log.e("getLocalIpv4Address", f.toString());
        }
        return "127.0.0.1";
    }

    public Network() {
        try {
            processNetInfo();
        } catch (Exception e) {
            Log.e(LOG + " Constuctor", e.toString());
        }
    }


    public static boolean connected(Context context)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean connectViaWifi;
        connectViaWifi = pref.getBoolean(PreferenceHelper.
                GENERAL_PREF_KEY_NETWORK_CONNECTION, true);

        return ((connectViaWifi && Network.isWifiConnected(context))||Network.isMobileConnected(context));
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public boolean isNetworkUp() throws SocketException {
        return getNetInterface().isUp();
    }

    /**
     * @param iPv4StartingIP the iPv4StartingIP to set
     */
    private void setiPv4StartingIP(String iPv4StartingIP) {
        this.iPv4StartingIP = iPv4StartingIP;
    }

    /**
     * @param iPv4BroadCastIP the iPv4BroadCastIP to set
     */
    private void setiPv4BroadCastIP(String iPv4BroadCastIP) {
        this.iPv4BroadCastIP = iPv4BroadCastIP;
    }

    /**
     * @param iPv4Mask the iPv4Mask to set
     */
    private void setiPv4Mask(String iPv4Mask) {
        this.iPv4Mask = iPv4Mask;
    }

    /**
     * @return the iPv4BroadCastIP
     */
    public String getiPv4BroadCastIP() {
        return iPv4BroadCastIP;
    }

    /**
     * @return the iPv4Mask
     */
    public String getiPv4Mask() {
        return iPv4Mask;
    }

    /**
     * @return the iPv4StartingIP
     */
    public String getiPv4StartingIP() {
        return iPv4StartingIP;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void processNetInfo() throws SocketException {
        if (!isNetworkUp()) return;

        String mRange = getNetInterface().getInterfaceAddresses().toString().replace("[", "");
        String[] range = mRange.replace("]", "").split(" ");

        setiPv4StartingIP(range[0].split("/")[1]);
        setiPv4BroadCastIP(range[1]);
        setiPv4Mask(range[0].split("/")[2]);

    }

    public static final int SEARCH_PORT = 4000;

    private static Context context;
    private static ConnectivityManager connectivityMan;
    private static NetworkInfo wifiNetInfo, mobileNetInfo;
    private static WifiManager wifiManager;

    public Network(Context context) {
        Network.context = context;
        connectivityMan = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);

    }


    public static String getLocalIpv4Address() {
        try {

            for (Enumeration<NetworkInterface> networkInterfaceEnum = NetworkInterface.getNetworkInterfaces(); networkInterfaceEnum
                    .hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaceEnum.nextElement();

                for (Enumeration<InetAddress> ipAddressEnum = networkInterface.getInetAddresses(); ipAddressEnum
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddressEnum.nextElement();
                    // ---check that it is not a loopback address and
                    // it is IPv4---
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress
                                .getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("getLocalIpv4Address", ex.toString());
        } catch (IOException e) {
            Log.e("getLocalIpv4Address", e.toString());
        } catch (Exception f) {
            Log.e("getLocalIpv4Address", f.toString());
        }
        return "127.0.0.1";
    }

    public String getLocalIpv6Address() {
        try {
            for (Enumeration<NetworkInterface> networkInterfaceEnum = NetworkInterface.getNetworkInterfaces(); networkInterfaceEnum
                    .hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaceEnum.nextElement();
                for (Enumeration<InetAddress> ipAddressEnum = networkInterface.getInetAddresses(); ipAddressEnum
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddressEnum.nextElement();
                    // ---check that it is not a loopback address and
                    // it is not IPv4---

                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress
                                .getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("getLocalIpv6Address", ex.toString());
        }
        return null;

    }

    public static boolean checkIPValid(String YourIPAddress, TextView messageText) {
        boolean result = false;
        if (YourIPAddress.length() == 0) {
            if (messageText != null) messageText
                    .setText("Warning ! IP Address , is not of the correct length. \neg.it should have 4 octates or sub divisions");

            return result;
        }
        if (!YourIPAddress.contains(".")) {
            if (messageText != null) messageText
                    .setText("Warning ! IP Address ,has no sub divisions. \neg.it should have 4 octates or sub divisions");

            return result;
        }
        if (!(YourIPAddress.length() > 0) && !(YourIPAddress.length() < 16)) {
            if (messageText != null) messageText
                    .setText("Warning ! IP Address ,length is short or too big.the correct length,is any thing between 7-16 chars. \neg.it should have 4 octates or sub divisions");

            return result;
        }
        String invalidChars = "abcdefghijklmnopqrstuvwxyz~!#$%^&*()_+:\"{}|[]\\<,>?/`-=";
        // checking for invalid char
        for (int x = 0; x < invalidChars.length(); x++) {
            String invalid = String.valueOf(invalidChars.charAt(x));
            if (YourIPAddress.contains(invalid)) {
                if (messageText != null)
                    messageText.setText("Warning ! IP Address , Contains Invailid chars like "
                            + invalidChars);
                return result;
            }
        }

        // checking subnets
        for (int x = 0, dot = 0; x < YourIPAddress.length(); x++) {
            char chr = YourIPAddress.charAt(x);
            if (chr == '.') {
                dot += 1;
            }

            if (x >= YourIPAddress.length() - 1) {
                if (dot < 3) {
                    if (messageText != null) messageText
                            .setText("Warning ! IP Address , is not of the correct Subnet length. \neg. it should be 10.10.10.1 having 4 octates or sub divisions");
                    return result;
                }
            }
        }

        String[] subnets = YourIPAddress.split("\\.");
        for (int x = 0; x < subnets.length; x++) {
            int sub = Integer.parseInt(subnets[x]);

            if (sub < 0) {
                if (messageText != null) messageText.setText(String.format(
                        "Warning ! IP Address , Subnet %s .is either below or above the subnet limit", 4 - x));
                return result;
            }

            if (sub > 254) {
                if (messageText != null) messageText.setText(String.format(
                        "Warning ! IP Address , Subnet %s .is either below or above the subnet limit", 4 - x));
                return result;
            }

        }

        result = true;
        return result;
    }

    // wifi
    public boolean isWifiConnected() {
        boolean result = false;
        wifiNetInfo = connectivityMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetInfo.isConnectedOrConnecting()) result = true;
        return result;
    }

    public static boolean isWifiConnected(Context context) {
        boolean result = false;

        ConnectivityManager connectivityMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetInfo =null ;

        try
        {
            wifiNetInfo = connectivityMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
        catch (Exception e)
        {

        }
        if (wifiNetInfo==null)return  false;

        if(wifiNetInfo.isConnectedOrConnecting()) result = true;

        return result;
    }

    // mobile
    public boolean isMobileConnected() {
        boolean result = false;
        mobileNetInfo = connectivityMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetInfo.isConnectedOrConnecting()) result = true;
        return result;
    }

    public static boolean isMobileConnected(Context context) {
        boolean result = false;
        ConnectivityManager connectivityMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetInfo =null;

        try
        {
            mobileNetInfo = connectivityMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        }
        catch (Exception e)
        {

        }

        if(mobileNetInfo==null)return false;

        if (mobileNetInfo.isConnectedOrConnecting()) result = true;

        return result;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // wifi Status

    public boolean setWifiState(boolean state) {

        wifiManager.setWifiEnabled(state);
        return wifiManager.isWifiEnabled();
        // wifiManager.getConnectionInfo().
    }

    public String getWifiBSSID() {
        return wifiManager.getConnectionInfo().getBSSID();
    }

    public String getWifiMacAddress() {
        return wifiManager.getConnectionInfo().getMacAddress();
    }

    @SuppressLint("MissingPermission")
    public String getWifiSSID() {
        return wifiManager.getConnectionInfo().getSSID();
    }

    public int getWifiIpAddress() {
        return wifiManager.getConnectionInfo().getIpAddress();
    }

    public int getWifiNetId() {
        return wifiManager.getConnectionInfo().getNetworkId();
    }

    public int getWifiLinkSpeed() {
        return wifiManager.getConnectionInfo().getLinkSpeed();
    }

    public String getWifiLinkSpeedtoString() {
        return String.valueOf(wifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getLinkSpeed(), 3)) + " : "
                + "3";
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getIMEI(Context context) {
        if (context.equals(null)) return "";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        String timeStamp = dateFormat.format(new Date());

        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (intf != null) {
                    String name = intf.getName();
                    if (!intf.getName().equalsIgnoreCase("wlan0")) continue;
                }

                byte[] mac = intf.getHardwareAddress();

                if (mac == null) return "";

                StringBuilder buf = new StringBuilder();

                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));

                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);

                return buf.toString();

            }
        } catch (Exception e) {

            return timeStamp;
        }

        return timeStamp;
    }

}
