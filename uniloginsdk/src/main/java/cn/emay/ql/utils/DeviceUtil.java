package cn.emay.ql.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.Locale;

/**
 * Created by lee on 2018/2/1.
 */

public class DeviceUtil {

    public static String getImei(Context context) {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception e) {

        }
        return imei;
    }

    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 得到MAC地址
     */
    public static String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 得到设备名称
     */
    public static String getDeviceName() {
        return android.os.Build.MODEL;
    }

    /**
     * 得到系统版本
     */
    public static String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static int getOSSdkVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取IMSI
     */
    public static String getIMSI(Context context) {
        try {
            TelephonyManager ts = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return ts.getSubscriberId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * make true current connect service is wifi
     *
     * @param context Application context
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI || activeNetInfo.getType() == ConnectivityManager.TYPE_BLUETOOTH);
    }


    /**
     * 获取density
     */
    public static float getDensity(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

    /**
     * 获取density
     */
    public static int getDPI(Context context) {
        final int scale = context.getResources().getDisplayMetrics().densityDpi;
        return scale;
    }

    /**
     * 得到屏幕宽度
     *
     * @return 宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) (context.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    /**
     * 得到屏幕高度
     *
     * @return 高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) (context.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    public static String getANDROID_ID(Context c) {
        return Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
    }

    public static String getOnlyPhoneId(Context c) {
        String id = getAndroidId(c) + getImei(c);
        return MD5Encoder.EncoderByMd5(id);
    }

    /**
     * 获取androidId
     */
    public static String getAndroidId(Context c) {
        String s = "";
        s = Secure.getString(c.getContentResolver(), "android_id");
        if (TextUtils.isEmpty(s)) {
            s = "";
        }
        return s;
    }

    public static String mDeviceId;

    public static String getDeviceID(Context context) {
        if (mDeviceId == null) {
            SharedPreferences sharedpreferences = context.getSharedPreferences("dkadids", 0);
            String s = sharedpreferences.getString("i", null);

            if (s == null) {
                s = getImei(context);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("i", s);
                editor.commit();
            }
            String s1 = sharedpreferences.getString("a", null);
            if (s1 == null) {
                s1 = getAndroidId(context);
                SharedPreferences.Editor editor1 = sharedpreferences.edit();
                editor1.putString("a", s1);
                editor1.commit();
            }
            // s1 = String.valueOf(System.currentTimeMillis());
            mDeviceId = Md5Tools.toMd5((new StringBuilder()).append("com.duoku.ssp").append(s).append(s1).toString().getBytes(), true);
        }

        return mDeviceId;
    }

    public static String getICCID(Context c) {
        try {
            TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSimSerialNumber(); // 取出MSISDN，很可能为空
        } catch (Exception e) {

        }
        return "";
    }

    // 获取手机运营商
    public static String getSimOperator(Context c) {
        TelephonyManager telManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telManager.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")|| operator.equals("46004")|| operator.equals("46008")) {
                // 中国移动
                return "CMCC";
            } else if (operator.equals("46001")|| operator.equals("46006")|| operator.equals("46009")) {
                // 中国联通
                return "CUCC";
            } else if (operator.equals("46003")|| operator.equals("46005")|| operator.equals("46011")) {
                // 中国电信
                return "CTCC";
            }

        }
        return "UNKONWN";
    }


    /**
     * 获取网络类型
     * 取值：0 －未知，1 －有线网，2 - WIFI，3 －未知的蜂窝网 络，4 － 2G，5  － 3G， 6 – 4G；32 位整数
     *
     * @return
     */
    // 适配低版本手机
    /** Network type is unknown */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    public static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    public static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    public static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B */
    public static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0 */
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A */
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT */
    public static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
    public static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
    public static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B */
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
    public static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
    public static final int NETWORK_TYPE_HSPAP = 15;

    private static final int NETWORK_TYPE_UNAVAILABLE = -1;
    // private static final int NETWORK_TYPE_MOBILE = -100;
    private static final int NETWORK_TYPE_WIFI = -101;

    private static final int NETWORK_CLASS_WIFI = -101;
    private static final int NETWORK_CLASS_UNAVAILABLE = -1;
    /** Unknown network class. */
    private static final int NETWORK_CLASS_UNKNOWN = 0;
    /** Class of broadly defined "2G" networks. */
    private static final int NETWORK_CLASS_2_G = 1;
    /** Class of broadly defined "3G" networks. */
    private static final int NETWORK_CLASS_3_G = 2;
    /** Class of broadly defined "4G" networks. */
    private static final int NETWORK_CLASS_4_G = 3;

    public static int getCurrentNetworkType(Context ctx) {
        int networkClass = getNetworkClass(ctx);
        // String type = "未知";
        int type = 0;
        switch (networkClass) {
            case NETWORK_CLASS_UNAVAILABLE:
                // type = "无";
                type = 0;
                break;
            case NETWORK_CLASS_WIFI:
                // type = "Wi-Fi";
                type = 2;
                break;
            case NETWORK_CLASS_2_G:
                // type = "2G";
                type = 4;
                break;
            case NETWORK_CLASS_3_G:
                // type = "3G";
                type = 5;
                break;
            case NETWORK_CLASS_4_G:
                // type = "4G";
                type = 6;
                break;
            case NETWORK_CLASS_UNKNOWN:
                // type = "未知";
                type = 0;
                break;
        }
        return type;
    }

    private static int getNetworkClass(Context ctx) {
        int networkType = NETWORK_TYPE_UNKNOWN;
        try {
            final NetworkInfo network = ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            if (network != null && network.isAvailable() && network.isConnected()) {
                int type = network.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    networkType = NETWORK_TYPE_WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(
                            Context.TELEPHONY_SERVICE);
                    networkType = telephonyManager.getNetworkType();
                }
            } else {
                networkType = NETWORK_TYPE_UNAVAILABLE;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getNetworkClassByType(networkType);
    }

    private static int getNetworkClassByType(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_UNAVAILABLE:
                return NETWORK_CLASS_UNAVAILABLE;
            case NETWORK_TYPE_WIFI:
                return NETWORK_CLASS_WIFI;
            case NETWORK_TYPE_GPRS:
            case NETWORK_TYPE_EDGE:
            case NETWORK_TYPE_CDMA:
            case NETWORK_TYPE_1xRTT:
            case NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case NETWORK_TYPE_UMTS:
            case NETWORK_TYPE_EVDO_0:
            case NETWORK_TYPE_EVDO_A:
            case NETWORK_TYPE_HSDPA:
            case NETWORK_TYPE_HSUPA:
            case NETWORK_TYPE_HSPA:
            case NETWORK_TYPE_EVDO_B:
            case NETWORK_TYPE_EHRPD:
            case NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    public static String getApplicationName(Context ctx) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        String applicationName = "";
        try {
            packageManager = ctx.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), 0);
            if (applicationInfo != null) {
                applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
            }
            return applicationName;
        } catch (PackageManager.NameNotFoundException e) {
            return applicationName;
        }
    }

    public static String getPhoneNum(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number().replace("+86", "");
        } catch (Exception e) {
            return "";
        }
    }


    public static int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int pxTodip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
