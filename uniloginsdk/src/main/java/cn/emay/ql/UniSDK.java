package cn.emay.ql;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;

import com.cmic.sso.sdk.AuthThemeConfig;
import com.cmic.sso.sdk.auth.AuthnHelper;
import com.cmic.sso.sdk.auth.TokenListener;
import com.sdk.base.api.CallBack;
import com.sdk.base.api.OnCustomViewListener;
import com.sdk.base.api.UiOauthListener;
import com.sdk.base.framework.bean.OauthResultMode;
import com.sdk.base.module.manager.SDKManager;
import com.sdk.mobile.handler.UiHandler;
import com.sdk.mobile.manager.login.cucc.UiOauthManager;
import com.sdk.mobile.manager.login.manager.RegisterManager;
import com.sdk.mobile.manager.login.manager.UiConfig;
import com.sdk.mobile.manager.login.views.Brand;
import com.sdk.mobile.manager.login.views.LoginButton;
import com.sdk.mobile.manager.login.views.Protocol;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.com.chinatelecom.account.api.CtSetting;
import cn.com.chinatelecom.account.sdk.AuthPageConfig;
import cn.com.chinatelecom.account.sdk.AuthViewConfig;
import cn.com.chinatelecom.account.sdk.CtAuth;
import cn.com.chinatelecom.account.sdk.ResultListener;
import cn.emay.ql.listeners.InitCallback;
import cn.emay.ql.listeners.LoginCallback;
import cn.emay.ql.listeners.OnCustomListener;
import cn.emay.ql.net.HttpCallback;
import cn.emay.ql.net.HttpUtils;
import cn.emay.ql.utils.CtClickableSpan;
import cn.emay.ql.utils.DeviceUtil;
import cn.emay.ql.utils.LoginUiConfig;

public class UniSDK {
    private Context mContext;
    private String mAppId;
    private String mAesKey = "";
    protected String mSdkSign;
    private String mSdkId;
    private String mSdkSecretKey;
    private boolean isDialogStyle;
    protected String mToken;
    protected String mAuthCode;
    protected String mBusinessId;
    private boolean initSucc;
    private static UniSDK instance;
    protected InitCallback mInitCallBack;
    protected LoginCallback mLoginCallback;

    private LoginUiConfig mLoginUiConfig;

    private UniSDK() {
    }

    public void initAuth(Context context, String appId, String appSecret,InitCallback initCallback) {
        mContext = context;
        this.mAppId = appId;
        this.mAesKey = appSecret;
        mInitCallBack = initCallback;
        //首先访问亿美接口获取key等信息
        getAppConfig();
    }

    public static synchronized UniSDK getInstance() {
        if (instance == null) {
            instance = new UniSDK();
        }
        return instance;
    }

    public void login(Context context, LoginCallback callback, LoginUiConfig loginUiConfig) {
        mContext = context;
        mLoginCallback = callback;
        this.mLoginUiConfig = loginUiConfig;
        loginAuth();
    }

    public void login(Context context, LoginCallback callback, LoginUiConfig loginUiConfig, boolean isDialogStyle) {
        mContext = context;
        mLoginCallback = callback;
        this.mLoginUiConfig = loginUiConfig;
        this.isDialogStyle = isDialogStyle;
        loginAuth();
    }

    public void closeAuthActivity() {
        if (Constans.SDK_SIGN_YIDONG.equals(mSdkSign)) {
            AuthnHelper authnHelper = AuthnHelper.getInstance(mContext);
            authnHelper.quitAuthActivity();
        } else if (Constans.SDK_SIGN_LIANTONG.equals(mSdkSign)) {
            if (mUiHandler != null) {
                mUiHandler.finish();
            }
        } else if (Constans.SDK_SIGN_DIANXIN.equals(mSdkSign)) {
            if (isDialogStyle) {
                CtAuth.getInstance().finishMiniAuthActivity();
            } else {
                CtAuth.getInstance().finishAuthActivity();
            }
        }
    }

    private static Handler myHandler = new Handler(Looper.getMainLooper());

    private void getAppConfig() {
        //通过code去取access_token
        byte[] commonData = DataUtil.getConfigJsonData(mContext, mAesKey);
        HttpUtils.sendRequest(Constans.URL_SDK_CONFIG, mAppId, mAesKey, commonData, new HttpCallback() {
            @Override
            public void onSuccess(String msg) {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    mSdkSign = jsonObject.getString("sdkSign");
                    mSdkId = jsonObject.getString("appId");
                    mSdkSecretKey = jsonObject.getString("secretKey");
                    mBusinessId = jsonObject.getString("businessId");
                    if (mInitCallBack != null) {
                        mInitCallBack.onSuccess("初始化成功");
                    }
                    initSucc = true;
                    initSDKLogin();
                } catch (Exception e) {
                    initSucc = false;
                    e.printStackTrace();
                    if (mInitCallBack != null) {
                        myHandler.removeCallbacksAndMessages(null);
                        mInitCallBack.onFailed("取配置json解析失败" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailed(String msg) {
                initSucc = false;
                if (mInitCallBack != null) {
                    mInitCallBack.onFailed(msg);
                }
                myHandler.removeCallbacksAndMessages(null);
            }
        });
    }

    private void initSDKLogin() {
        if (Constans.SDK_SIGN_LIANTONG.equals(mSdkSign)) {
            SDKManager.init(mContext, mSdkId, mSdkSecretKey);
        } else if (Constans.SDK_SIGN_DIANXIN.equals(mSdkSign)) {
            CtAuth.getInstance().init(mContext, mSdkId, mSdkSecretKey, false);
        }
    }

    boolean hadReturned = false;
    private UiHandler mUiHandler;

    protected void loginAuth() {
        if (Constans.SDK_SIGN_YIDONG.equals(mSdkSign)) {
            hadReturned = false;
            final AuthnHelper authnHelper = AuthnHelper.getInstance(mContext);
            if (mLoginUiConfig != null && mLoginUiConfig.getYiDongLoginConfig() != null) {
                LoginUiConfig.YiDongLoginConfig yiDongLoginConfig = mLoginUiConfig.getYiDongLoginConfig();
                AuthThemeConfig.Builder config = new AuthThemeConfig.Builder();
                if (yiDongLoginConfig.getAuthView() != null) {
                    config.setAuthContentView(yiDongLoginConfig.getAuthView());
                } else {
                    config.setAuthLayoutResID(yiDongLoginConfig.getAuthResId());
                }
                int margin = (DeviceUtil.getScreenWidth(mContext) - dip2px(mContext,yiDongLoginConfig.getLogBtnWidth())) /2;
                margin = DeviceUtil.pxTodip(mContext,margin);
                config.setStatusBar(yiDongLoginConfig.getStatusBarColor(), yiDongLoginConfig.isStatusBarLightColor())
                        .setNavTextColor(yiDongLoginConfig.getNavTextColor())//隐私页导航栏字体颜色
                        .setNavTextSize(yiDongLoginConfig.getNavTextSize())
                        .setNumberColor(yiDongLoginConfig.getNumberColor())//手机号码字体颜色
                        .setNumberSize(yiDongLoginConfig.getNumberSize(),false)////手机号码字体大小
                        .setNumFieldOffsetY(yiDongLoginConfig.getNumFieldOffsetY())//号码栏Y偏移量
                        .setLogBtnText(yiDongLoginConfig.getLogBtnText())//登录按钮文本
                        .setLogBtnTextColor(yiDongLoginConfig.getLogBtnTextColor())//登录按钮文本颜色
                        .setLogBtnImgPath(yiDongLoginConfig.getLogBtnImgPath())//登录按钮背景
                        .setLogBtnText(yiDongLoginConfig.getLogBtnText(), yiDongLoginConfig.getLogBtnTextColor(), yiDongLoginConfig.getLogBtnSize(),false)
                        .setLogBtnOffsetY(yiDongLoginConfig.getLogBtnOffsetY())//登录按钮Y偏移量
                        .setLogBtn(yiDongLoginConfig.getLogBtnWidth(),yiDongLoginConfig.getLogBtnHeight())
                        .setLogBtnMargin(margin,margin)
                        .setUncheckedImgPath(yiDongLoginConfig.getUncheckedImgPath())//chebox未被勾选图片
                        .setCheckedImgPath(yiDongLoginConfig.getCheckedImgPath())//chebox被勾选图片
                        .setCheckBoxImgPath(yiDongLoginConfig.getCheckedImgPath(), yiDongLoginConfig.getUncheckedImgPath(), yiDongLoginConfig.getCheckBoxImgPathSize(), yiDongLoginConfig.getCheckBoxImgPathSize())
                        .setPrivacyState(yiDongLoginConfig.isPrivacyState());//授权页check
                String protocolName1 =  mLoginUiConfig.getProtocolName1().replaceAll("《","").replaceAll("》","");
                String protocolName2 =  mLoginUiConfig.getProtocolName2().replaceAll("《","").replaceAll("》","");
                config.setPrivacyAlignment("登录即同意" + AuthThemeConfig.PLACEHOLDER +  protocolName1 + protocolName2+ "并使用本机号码登录", protocolName1, mLoginUiConfig.getProtocolUrl1(), protocolName2, mLoginUiConfig.getProtocolUrl2(),"","","","");
                config.setPrivacyText(yiDongLoginConfig.getPrivacyTextSize(), yiDongLoginConfig.getPrivacyTextColor1(), yiDongLoginConfig.getPrivacyTextColor2(), true,false)
                        .setPrivacyOffsetY_B(yiDongLoginConfig.getPrivacyOffsetY_B())//隐私条款Y偏移量
                        .setPrivacyMargin(yiDongLoginConfig.getPrivacyMargin(), yiDongLoginConfig.getPrivacyMargin());
                authnHelper.setAuthThemeConfig(config.build());
            }
            View authView = authnHelper.getAuthThemeConfig().getContentView();
            authView.findViewById(R.id.protocol).setVisibility(View.INVISIBLE);
            authView.findViewById(R.id.brand).setVisibility(View.INVISIBLE);
            authView.findViewById(R.id.oauth_login).setVisibility(View.INVISIBLE);
            if (customClickMap != null) {
                for (final View key : customClickMap.keySet()) {
                    String custom_id = key.getResources().getResourceEntryName(key.getId());
                    key.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(key).onClick(v);
                        }
                    });
                }
            }
            authnHelper.loginAuth(mSdkId, mSdkSecretKey, new TokenListener() {
                @Override
                public void onGetTokenComplete(int i, JSONObject jsonObject) {
                    try {
                        int code = jsonObject.getInt("resultCode");
                        if (hadReturned) {
                            return;
                        }
                        if (code == 103000) {
                            hadReturned = true;
                            mToken = jsonObject.getString("token");
                            accessEmayLogin();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    authnHelper.quitAuthActivity();
                                }
                            });
                        } else {
                            myHandler.removeCallbacksAndMessages(null);
                            if (mLoginCallback != null) {
                                mLoginCallback.onFailed(jsonObject.getString("resultDesc"));
                            }
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    authnHelper.quitAuthActivity();
                                }
                            });
                        }
                    } catch (Exception e) {
                        myHandler.removeCallbacksAndMessages(null);
                        if (mLoginCallback != null) {
                            mLoginCallback.onFailed("移动登录结果解析失败" + e.getMessage());
                        }
                    }
                }
            });
        } else if (Constans.SDK_SIGN_LIANTONG.equals(mSdkSign)) {
            UiOauthManager.getInstance(mContext).login(5000, new CallBack() {
                @Override
                public void onSuccess(int code, String s, int i1, Object o, String s1) {
                    if (code == 0) {
                        UiConfig config = new UiConfig();
                        if (mLoginUiConfig != null && mLoginUiConfig.getLianTongLoginConfig() != null) {
                            LoginUiConfig.LianTongLoginConfig lianTongLoginConfig = mLoginUiConfig.getLianTongLoginConfig();
                            config.setShowProtocolBox(lianTongLoginConfig.isShowProtocolBox());
                            config.setLoginButton(new LoginButton(lianTongLoginConfig.getLoginButtonWidth(), lianTongLoginConfig.getLoginButtonHeight(), lianTongLoginConfig.getOffsetY(), lianTongLoginConfig.getLoginButtonText(), lianTongLoginConfig.getProtocolCheckRes(), lianTongLoginConfig.getProtocolUnCheckRes()));
                            Protocol protocol = new Protocol();
                            protocol.setCustomProtocol1_id(mLoginUiConfig.getProtocolID1());
                            protocol.setCustomProtocol1_text(mLoginUiConfig.getProtocolName1());
                            protocol.setCustomProtocol1_Link(mLoginUiConfig.getProtocolUrl1());
                            protocol.setCustomProtocol2_id(mLoginUiConfig.getProtocolID2());
                            protocol.setCustomProtocol2_text(mLoginUiConfig.getProtocolName2());
                            protocol.setCustomProtocol2_Link(mLoginUiConfig.getProtocolUrl2());
                            protocol.setTextColor(mLoginUiConfig.getProtocolTextColor2());
                            config.setProtocol(protocol);
                            config.setBrand(new Brand(0,0,false));

                        }
                        if (customClickMap != null) {
                            for (final View key : customClickMap.keySet()) {
                                String custom_id = key.getResources().getResourceEntryName(key.getId());
                                RegisterManager.getInstance().setCustomViewListener(custom_id, new
                                        OnCustomViewListener() {
                                            @Override
                                            public void onClick(View view, UiHandler uiHandler) {
                                                mUiHandler = uiHandler;
                                                customClickMap.get(key).onClick(view);
                                            }
                                        });
                            }
                        }
                        UiOauthManager.getInstance(mContext).setOtherLoginListener(new OnCustomViewListener() {
                            @Override
                            public void onClick(View view, UiHandler uiHandler) {
                                myHandler.removeCallbacksAndMessages(null);
                                if (mLoginCallback != null) {
                                    mLoginCallback.onFailed("其他方式登录");
                                }
                                if (uiHandler != null) {
                                    uiHandler.finish();
                                }
                                mUiHandler = uiHandler;
                            }
                        });
                        UiOauthManager.getInstance(mContext).openActivity(config, 5000, new UiOauthListener() {
                            @Override
                            public void onSuccess(OauthResultMode oauthResultMode, UiHandler uiHandler) {
                                try {
                                    if (oauthResultMode.getCode() != 0) {
                                        myHandler.removeCallbacksAndMessages(null);
                                        if (mLoginCallback != null) {
                                            mLoginCallback.onFailed(oauthResultMode.getMsg());
                                        }
                                        return;
                                    }
                                    Object res = oauthResultMode.getObject();
                                    JSONObject jsonObject = new JSONObject(res.toString());
                                    mToken = jsonObject.getString("accessCode");
                                    accessEmayLogin();
                                    uiHandler.finish();
                                } catch (Exception e) {
                                    myHandler.removeCallbacksAndMessages(null);
                                    if (mLoginCallback != null) {
                                        mLoginCallback.onFailed("联通登录解析失败" + e.getMessage());
                                    }
                                    uiHandler.finish();
                                }
                                mUiHandler = uiHandler;
                            }

                            @Override
                            public void onFailed(OauthResultMode oauthResultMode, UiHandler uiHandler) {
                                myHandler.removeCallbacksAndMessages(null);
                                if (mLoginCallback != null) {
                                    mLoginCallback.onFailed("登录失败 ： " + oauthResultMode.getMsg());
                                }
                                mUiHandler = uiHandler;
                            }
                        });
                    } else {
                        myHandler.removeCallbacksAndMessages(null);
                        if (mLoginCallback != null) {
                            mLoginCallback.onFailed("登录失败 ： " + s);
                        }
                    }
                }

                @Override
                public void onFailed(int i, int i1, String s, String s1) {
                    myHandler.removeCallbacksAndMessages(null);
                    if (mLoginCallback != null) {
                        mLoginCallback.onFailed("登录失败 ： " + s);
                    }
                }
            });
        } else if (Constans.SDK_SIGN_DIANXIN.equals(mSdkSign)) {
            CtSetting ctSetting = new CtSetting(3000, 3000, 3000);
            CtAuth.getInstance().requestPreLogin(ctSetting, new ResultListener() {
                @Override
                public void onResult(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int code = jsonObject.getInt("result");
                        String msg = jsonObject.getString("msg");
                        if (code == 0) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    openAuthActivity();
                                }
                            });
                        } else {
                            if (mLoginCallback != null) {
                                mLoginCallback.onFailed(msg);
                            }
                        }
                    } catch (Exception e) {
                        myHandler.removeCallbacksAndMessages(null);
                        if (mLoginCallback != null) {
                            mLoginCallback.onFailed("电信预登录解析失败" + e.getMessage());
                        }
                    }
                }
            });
        } else {
            if(initSucc){
                myHandler.removeCallbacksAndMessages(null);
                if (mLoginCallback != null) {
                    mLoginCallback.onFailed("配置了不支持的SDK");
                }
            }else{
                if (mLoginCallback != null) {
                    mLoginCallback.onFailed("初始化未成功，请先初始化");
                }
            }
        }
    }

    private void openAuthActivity() {
        if (isDialogStyle) {
            AuthPageConfig miniAuthPageConfig = getMiniAuthPageConfig(mLoginUiConfig);
            AuthViewConfig authViewConfig = getMiniAuthViewDynamicConfig(mContext);
            CtAuth.getInstance().openMiniAuthActivity(mContext, miniAuthPageConfig, authViewConfig, new
                    ResultListener() {
                        @Override
                        public void onResult(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("result");
                                String msg = jsonObject.getString("msg");
                                if (code == 0) {
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    mAuthCode = data.getString("authCode");
                                    mToken = data.getString("accessCode");
                                    accessEmayLogin();
                                    CtAuth.getInstance().finishMiniAuthActivity();
                                } else {
                                    myHandler.removeCallbacksAndMessages(null);
                                    if (mLoginCallback != null) {
                                        mLoginCallback.onFailed(jsonObject.toString());
                                    }
                                    CtAuth.getInstance().finishMiniAuthActivity();
                                }
                            } catch (Exception e) {
                                myHandler.removeCallbacksAndMessages(null);
                                if (mLoginCallback != null) {
                                    mLoginCallback.onFailed("电信授权页结果解析失败" + e.getMessage());
                                }
                            }
                        }
                    });
        } else {
            AuthPageConfig authPageConfig = getAuthPageConfig();
            AuthViewConfig authViewConfig = getAuthViewDynamicConfig(mContext);
            CtAuth.getInstance().openAuthActivity(mContext, authPageConfig, authViewConfig, new
                    ResultListener() {
                        @Override
                        public void onResult(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                int code = jsonObject.getInt("result");
                                String msg = jsonObject.getString("msg");
                                if (code == 0) {
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    mAuthCode = data.getString("authCode");
                                    mToken = data.getString("accessCode");
                                    accessEmayLogin();
                                    CtAuth.getInstance().finishAuthActivity();
                                } else {
                                    myHandler.removeCallbacksAndMessages(null);
                                    if (mLoginCallback != null) {
                                        mLoginCallback.onFailed(jsonObject.toString());
                                    }
                                    CtAuth.getInstance().finishAuthActivity();
                                }
                            } catch (Exception e) {
                                myHandler.removeCallbacksAndMessages(null);
                                if (mLoginCallback != null) {
                                    mLoginCallback.onFailed("电信授权页结果解析失败" + e.getMessage());
                                }
                            }
                        }
                    });
        }
    }


    protected void accessEmayLogin() {
        //访问亿美login接口
        byte[] commonData = DataUtil.getLoginJsonData(mContext, mAesKey);
        HttpUtils.sendRequest(Constans.URL_SDK_LOGIN, mAppId, mAesKey, commonData, new HttpCallback() {
            @Override
            public void onSuccess(String msg) {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    String mobile = jsonObject.getString("mobile");
                    myHandler.removeCallbacksAndMessages(null);
                    if (mLoginCallback != null) {
                        mLoginCallback.onSuccess(mobile);
                    }
                } catch (Exception e) {
                    myHandler.removeCallbacksAndMessages(null);
                    if (mLoginCallback != null) {
                        mLoginCallback.onFailed("解析最终登录结果失败" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailed(String msg) {
                myHandler.removeCallbacksAndMessages(null);
                if (mLoginCallback != null) {
                    mLoginCallback.onFailed(msg);
                }
            }
        });

    }

    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    private AuthPageConfig getAuthPageConfig() {

        AuthPageConfig.Builder configBuilder = new AuthPageConfig.Builder()
                //设置“登录界面”的布局文件ID
                .setAuthActivityLayoutId(R.layout.activity_oauth)
                //设置“登录界面”的控件ID
                .setAuthActivityViewIds(R.id.oauth_back, //导航栏返回按钮ID
                        R.id.oauth_mobile_et, //脱敏号码文本控件ID
                        R.id.brand, //品牌标识文本控件ID
                        R.id.oauth_login, //登录按钮控件ID
                        0, //登录加载中控件ID（必须为ImageView控件）
                        R.id.oauth_login, //登录按钮文本控件ID
                        R.id.other_login, //其他登录方式控件ID
                        R.id.is_agree, //隐私协议勾选框控件ID
                        R.id.service_and_privacy   //“服务与隐私协议”文本控件ID
                )

                //设置隐私协议对话框的布局文件ID
                .setPrivacyDialogLayoutId(R.layout.ct_account_privacy_dialog)
                //设置隐私协议对话框的控件ID
                .setPrivacyDialogViewIds(R.id.ct_account_dialog_privacy, //“服务与隐私协议”文本控件ID
                        R.id.ct_account_dialog_cancel, // 返回按钮控件ID
                        R.id.ct_account_dialog_confirm) //确认按钮控件ID
                //设置隐私协议WebviewActivity的布局文件ID
                .setWebviewActivityLayoutId(R.layout.ct_account_privacy_webview_activity)
                //设置隐私协议界面的控件ID
                .setWebviewActivityViewIds(R.id.ct_account_webview_goback, //导航栏返回按钮ID
                        R.id.ct_account_progressbar_gradient, //进度条控件ID（ProgressBar控件）
                        R.id.ct_account_webview); //协议内容WebView控件ID
        //扩展一：添加View及点击事件 （可选）
//        .setExtendView1(view1 ,onClickListener1);
        //.setExtendView1(view2 ,onClickListener2)
        //.setExtendView1(view2 ,onClickListener2)
        //扩展二：配置登录Activity进入动画和退出动画 （可选）
        //.setStartActivityTransition(enterAnim ,exitAnim)
        //.setFinishActivityTransition(enterAnim ,exitAnim)
        if(customClickMap != null){
            final  List<View> indexes = new ArrayList<View>(customClickMap.keySet());
            for (int i=0;i<indexes.size();i++) {
                if(i == 0){
                    configBuilder.setExtendView1(indexes.get(0).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(0)).onClick(v);
                        }
                    });
                }else if(i == 1){
                    configBuilder.setExtendView2(indexes.get(1).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(1)).onClick(v);
                        }
                    });
                }else if(i == 2){
                    configBuilder.setExtendView3(indexes.get(2).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(2)).onClick(v);
                        }
                    });
                }else if(i == 3){
                    configBuilder.setExtendView4(indexes.get(3).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(3)).onClick(v);
                        }
                    });
                }else if(i == 4){
                    configBuilder.setExtendView5(indexes.get(4).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(4)).onClick(v);
                        }
                    });
                }
            }
        }
        AuthPageConfig authPageConfig = configBuilder.build();
        return authPageConfig;
    }

    private AuthViewConfig getMiniAuthViewDynamicConfig(Context context) {

        AuthViewConfig.Builder miniConfigBuilder = new AuthViewConfig.Builder()
                .setPrivacyTextView(R.id.ct_auth_privacy_text_dynamic, getPrivacyAgreementConfig(context))
                //设置隐私协议WebViewActivity的导航栏，包括导航栏ViewId、标题控件id，设置后可保持与登录界面的导航栏样式一致；参数说明(int navParentViewId, int navTitleViewId)
                .setPrivacyWebviewActivity(R.id.ct_account_webview_nav_layout, R.id.ct_account_webview_nav_title)
                // 设置隐私协议WebViewActivity的导航栏返回按钮
                .setPrivacyGoBackResId(R.id.ct_account_webview_goback, R.drawable.ct_account_auth_goback_selector);
        return miniConfigBuilder.build();
    }

    private AuthViewConfig getAuthViewDynamicConfig(Context context) {
        AuthViewConfig.Builder configBuilder = new AuthViewConfig.Builder()
                // 设置底部隐私协议的文本内容
                .setPrivacyTextView(R.id.service_and_privacy, getPrivacyAgreementConfig(context))
                /** 隐私协议WebViewActivity */
                //设置隐私协议WebViewActivity的导航栏，包括导航栏ViewId、标题控件id，设置后可保持与登录界面的导航栏样式一致；参数说明(int navParentViewId, int navTitleViewId)
                .setPrivacyWebviewActivity(R.id.ct_account_webview_nav_layout, R.id.ct_account_webview_nav_title);
        return configBuilder.build();
    }

    private SpannableStringBuilder getPrivacyAgreementConfig(Context context) {
        if (mLoginUiConfig != null && mLoginUiConfig.getProtocolName1() != null) {
            String text = "《天翼账号服务与隐私协议》与" + mLoginUiConfig.getProtocolName1()  + mLoginUiConfig.getProtocolName2() + "并使用本机号码登录";
            SpannableStringBuilder privacySpannableBuilder = new SpannableStringBuilder(text);
            CtClickableSpan clickableSpan1 = new CtClickableSpan(context, CtAuth.getCtPrivacyUrl(), CtAuth.CT_PRIVACY_TITLE, mLoginUiConfig.getProtocolTextColor2());
            privacySpannableBuilder.setSpan(clickableSpan1, 0, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            CtClickableSpan clickableSpan2 = new CtClickableSpan(context, mLoginUiConfig.getProtocolUrl1(), mLoginUiConfig.getProtocolName1(), mLoginUiConfig.getProtocolTextColor2());
            privacySpannableBuilder.setSpan(clickableSpan2, 14, 14 + mLoginUiConfig.getProtocolName1().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            CtClickableSpan clickableSpan3 = new CtClickableSpan(context, mLoginUiConfig.getProtocolUrl2(), mLoginUiConfig.getProtocolName2(), mLoginUiConfig.getProtocolTextColor2());
            privacySpannableBuilder.setSpan(clickableSpan3, 14 + mLoginUiConfig.getProtocolName1().length(), 14 + mLoginUiConfig.getProtocolName1().length() + mLoginUiConfig.getProtocolName2().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return privacySpannableBuilder;
        }
        SpannableStringBuilder privacySpannableBuilder = new SpannableStringBuilder("登录即同意《天翼账号服务与隐私协议》并授权本应用获取本机号码");
        CtClickableSpan clickableSpan1 = new CtClickableSpan(context, CtAuth.getCtPrivacyUrl(), CtAuth.CT_PRIVACY_TITLE, mLoginUiConfig.getProtocolTextColor2());
        privacySpannableBuilder.setSpan(clickableSpan1, 5, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return privacySpannableBuilder;

    }

    /**
     * 步骤6.打开Mini登录界面之信息配置
     * 以下提供登录界面配置信息的示例，若无界面样式自定义需求，只需修改App Logo，即可复制使用
     * （注意：隐私协议文本链接的点击事件由SDK处理，颜色通过android:tag标签传入，并且“登录即同意《天翼账号服务与隐私协议》”文字不可修改，
     * v3.7.2 新增设置Mini登录界面的宽度、高度及位置
     */
    private  AuthPageConfig getMiniAuthPageConfig(LoginUiConfig loginUiConfig) {
        AuthPageConfig.Builder miniConfigBuilder = new AuthPageConfig.Builder()
                //设置“登录界面”的布局文件ID
                .setAuthActivityLayoutId(R.layout.ct_account_mini_auth_activity_dynamic)
                //设置“登录界面”的控件ID
                .setAuthActivityViewIds(R.id.ct_account_nav_goback, //导航栏返回按钮ID
                        R.id.ct_account_desensphone, //脱敏号码文本控件ID
                        0, //品牌标识文本控件ID , 小窗传入0
                        R.id.ct_account_login_btn, //登录按钮控件ID
                        R.id.ct_account_login_loading, //登录加载中控件ID（必须为ImageView控件）
                        R.id.ct_account_login_text, //登录按钮文本控件ID
                        R.id.ct_account_other_login_way, //其他登录方式控件ID
                        0, //隐私协议勾选框控件ID, 小窗传入0
                        R.id.ct_auth_privacy_text_dynamic   //“服务与隐私协议”文本控件ID
                );
        //设置Mini小窗的宽度、高度及位置(v3.7.2新增，宽高单位为px，位置分为居中(AuthPageConfig.CENTER)，底部(AuthPageConfig.BOTTOM))
        if (loginUiConfig != null && loginUiConfig.getDianXinLoginConfig() != null) {
            miniConfigBuilder.setMiniAuthActivityStyle(loginUiConfig.getDianXinLoginConfig().getDialogWidth(), loginUiConfig.getDianXinLoginConfig().getDialogHeight(), loginUiConfig.getDianXinLoginConfig().getLocation());
        } else {
            miniConfigBuilder.setMiniAuthActivityStyle(0, 0, AuthPageConfig.CENTER);
        }
        //设置隐私协议界面的布局文件ID
        miniConfigBuilder.setWebviewActivityLayoutId(R.layout.ct_account_privacy_webview_activity)
                //设置隐私协议界面的控件ID
                .setWebviewActivityViewIds(R.id.ct_account_webview_goback, //导航栏返回按钮ID
                        R.id.ct_account_progressbar_gradient, //进度条控件ID（ProgressBar控件）
                        R.id.ct_account_webview) //协议内容WebView控件ID
                //扩展一：添加View及点击事件 （可选）
                //.setExtendView1(viewId1 ,onClickListener1)
                //.setExtendView1(viewId2 ,onClickListener2)
                //.setExtendView1(viewId3 ,onClickListener3)
                //.setExtendView4(viewId4 ,onClickListener4)
                //.setExtendView5(viewId5 ,onClickListener5)
                //扩展二：配置登录Activity进入动画和退出动画 （可选）
                .setStartActivityTransition(R.anim.push_bottom_in, 0)
                .setFinishActivityTransition(0, R.anim.push_bottom_out);
        if(customClickMap != null){
            final  List<View> indexes = new ArrayList<View>(customClickMap.keySet());
            for (int i=0;i<indexes.size();i++) {
                if(i == 0){
                    miniConfigBuilder.setExtendView1(indexes.get(0).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(0)).onClick(v);
                        }
                    });
                }else if(i == 1){
                    miniConfigBuilder.setExtendView2(indexes.get(1).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(1)).onClick(v);
                        }
                    });
                }else if(i == 2){
                    miniConfigBuilder.setExtendView3(indexes.get(2).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(2)).onClick(v);
                        }
                    });
                }else if(i == 3){
                    miniConfigBuilder.setExtendView4(indexes.get(3).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(3)).onClick(v);
                        }
                    });
                }else if(i == 4){
                    miniConfigBuilder.setExtendView5(indexes.get(4).getId(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customClickMap.get(indexes.get(4)).onClick(v);
                        }
                    });
                }
            }
        }
        AuthPageConfig miniAuthPageConfig = miniConfigBuilder.build();
        return miniAuthPageConfig;
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private LinkedHashMap<View, OnCustomListener> customClickMap = new LinkedHashMap<>();

    public void addOnCustomClickListener(View customView, OnCustomListener customClickListener) {
        customClickMap.put(customView, customClickListener);
    }

}
