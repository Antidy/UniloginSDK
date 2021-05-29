package cn.emay.ql;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.net.URLEncoder;

import cn.emay.ql.utils.DeviceUtil;
import cn.emay.ql.utils.GzipUtils;
import cn.emay.ql.net.AES;

/**
 * Created by lee on 2018/2/2.
 */

public class DataUtil {

    public static byte[] getConfigJsonData(Context context, String aesKey) {
        JSONObject object = new JSONObject();
        byte[] result = null;
        try {
            object.put("version", "v7");
            object.put("operatorCode", DeviceUtil.getSimOperator(context));
            JSONObject softwareInfo = new JSONObject();
            softwareInfo.put("deviceLanguage", DeviceUtil.getSystemLanguage());
            softwareInfo.put("osVersion", DeviceUtil.getOSSdkVersion());
            softwareInfo.put("appName", DeviceUtil.getApplicationName(context));
            softwareInfo.put("appPackageName", context.getPackageName());
            object.put("softwareInfo", softwareInfo);

            JSONObject hardwareInfo = new JSONObject();
            hardwareInfo.put("screenWidth", DeviceUtil.getScreenWidth(context));
            hardwareInfo.put("screenHeight", DeviceUtil.getScreenHeight(context));
            hardwareInfo.put("screenDensity", DeviceUtil.getDensity(context));
            hardwareInfo.put("imei", DeviceUtil.getImei(context));
            hardwareInfo.put("imsi", DeviceUtil.getIMSI(context));
            hardwareInfo.put("deviceName", DeviceUtil.getDeviceBrand() + DeviceUtil.getDeviceName());
            object.put("hardwareInfo", hardwareInfo);
            object.put("platform", "android");
            object.put("sdkSign", UniSDK.getInstance().mSdkSign);
            object.put("token", UniSDK.getInstance().mToken);
            object.put("mobile", DeviceUtil.getPhoneNum(context));
            object.put("address", "string");
            result = GzipUtils.compress(object.toString());
            result = AES.encrypt(result, aesKey.getBytes("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] getLoginJsonData(Context context, String aesKey) {
        JSONObject object = new JSONObject();
        byte[] result = null;
        try {
            object.put("version", "v7");
            object.put("operatorCode", DeviceUtil.getSimOperator(context));
            object.put("mobile", DeviceUtil.getPhoneNum(context));
            object.put("sdkSign", UniSDK.getInstance().mSdkSign);
            object.put("platform", "android");
            object.put("token", UniSDK.getInstance().mToken);
            object.put("authCode",UniSDK.getInstance().mAuthCode);
            object.put("businessId", UniSDK.getInstance().mBusinessId);
            result = GzipUtils.compress(object.toString());
            result = AES.encrypt(result, aesKey.getBytes("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }
        try {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        } catch (Exception localException) {
        }

        return "";
    }

    public static String getMeTaString(Context c, String name) {
        ApplicationInfo info = null;
        String value = "";
        try {
            info = c.getPackageManager().getApplicationInfo(c.getPackageName(), PackageManager.GET_META_DATA);
            if (info.metaData.containsKey(name)) {
                try {
                    value = info.metaData.getString(name);
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
