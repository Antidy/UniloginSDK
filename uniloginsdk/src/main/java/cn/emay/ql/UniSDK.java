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
        //??????????????????????????????key?????????
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
        //??????code??????access_token
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
                    initSDKLogin();
                    if (mInitCallBack != null) {
                        mInitCallBack.onSuccess("???????????????");
                    }
                    initSucc = true;
                } catch (Exception e) {
                    initSucc = false;
                    e.printStackTrace();
                    if (mInitCallBack != null) {
                        myHandler.removeCallbacksAndMessages(null);
                        mInitCallBack.onFailed("?????????json????????????" + e.getMessage());
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
                        .setNavTextColor(yiDongLoginConfig.getNavTextColor())//??????????????????????????????
                        .setNavTextSize(yiDongLoginConfig.getNavTextSize())
                        .setNumberColor(yiDongLoginConfig.getNumberColor())//????????????????????????
                        .setNumberSize(yiDongLoginConfig.getNumberSize(),false)////????????????????????????
                        .setNumFieldOffsetY(yiDongLoginConfig.getNumFieldOffsetY())//?????????Y?????????
                        .setLogBtnText(yiDongLoginConfig.getLogBtnText())//??????????????????
                        .setLogBtnTextColor(yiDongLoginConfig.getLogBtnTextColor())//????????????????????????
                        .setLogBtnImgPath(yiDongLoginConfig.getLogBtnImgPath())//??????????????????
                        .setLogBtnText(yiDongLoginConfig.getLogBtnText(), yiDongLoginConfig.getLogBtnTextColor(), yiDongLoginConfig.getLogBtnSize(),false)
                        .setLogBtnOffsetY(yiDongLoginConfig.getLogBtnOffsetY())//????????????Y?????????
                        .setLogBtn(yiDongLoginConfig.getLogBtnWidth(),yiDongLoginConfig.getLogBtnHeight())
                        .setLogBtnMargin(margin,margin)
                        .setUncheckedImgPath(yiDongLoginConfig.getUncheckedImgPath())//chebox??????????????????
                        .setCheckedImgPath(yiDongLoginConfig.getCheckedImgPath())//chebox???????????????
                        .setCheckBoxImgPath(yiDongLoginConfig.getCheckedImgPath(), yiDongLoginConfig.getUncheckedImgPath(), yiDongLoginConfig.getCheckBoxImgPathSize(), yiDongLoginConfig.getCheckBoxImgPathSize())
                        .setPrivacyState(yiDongLoginConfig.isPrivacyState());//?????????check
                String protocolName1 =  mLoginUiConfig.getProtocolName1().replaceAll("???","").replaceAll("???","");
                String protocolName2 =  mLoginUiConfig.getProtocolName2().replaceAll("???","").replaceAll("???","");
                config.setPrivacyAlignment("???????????????" + AuthThemeConfig.PLACEHOLDER +  protocolName1 + protocolName2+ "???????????????????????????", protocolName1, mLoginUiConfig.getProtocolUrl1(), protocolName2, mLoginUiConfig.getProtocolUrl2(),"","","","");
                config.setPrivacyText(yiDongLoginConfig.getPrivacyTextSize(), yiDongLoginConfig.getPrivacyTextColor1(), yiDongLoginConfig.getPrivacyTextColor2(), true,false)
                        .setPrivacyOffsetY_B(yiDongLoginConfig.getPrivacyOffsetY_B())//????????????Y?????????
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
                            mLoginCallback.onFailed("??????????????????????????????" + e.getMessage());
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
                                    mLoginCallback.onFailed("??????????????????");
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
                                        mLoginCallback.onFailed("????????????????????????" + e.getMessage());
                                    }
                                    uiHandler.finish();
                                }
                                mUiHandler = uiHandler;
                            }

                            @Override
                            public void onFailed(OauthResultMode oauthResultMode, UiHandler uiHandler) {
                                myHandler.removeCallbacksAndMessages(null);
                                if (mLoginCallback != null) {
                                    mLoginCallback.onFailed("???????????? ??? " + oauthResultMode.getMsg());
                                }
                                mUiHandler = uiHandler;
                            }
                        });
                    } else {
                        myHandler.removeCallbacksAndMessages(null);
                        if (mLoginCallback != null) {
                            mLoginCallback.onFailed("???????????? ??? " + s);
                        }
                    }
                }

                @Override
                public void onFailed(int i, int i1, String s, String s1) {
                    myHandler.removeCallbacksAndMessages(null);
                    if (mLoginCallback != null) {
                        mLoginCallback.onFailed("???????????? ??? " + s);
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
                            mLoginCallback.onFailed("???????????????????????????" + e.getMessage());
                        }
                    }
                }
            });
        } else {
            if(initSucc){
                myHandler.removeCallbacksAndMessages(null);
                if (mLoginCallback != null) {
                    mLoginCallback.onFailed("?????????????????????SDK");
                }
            }else{
                if (mLoginCallback != null) {
                    mLoginCallback.onFailed("????????????????????????????????????");
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
                                    mLoginCallback.onFailed("?????????????????????????????????" + e.getMessage());
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
                                    mLoginCallback.onFailed("?????????????????????????????????" + e.getMessage());
                                }
                            }
                        }
                    });
        }
    }


    protected void accessEmayLogin() {
        //????????????login??????
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
                        mLoginCallback.onFailed("??????????????????????????????" + e.getMessage());
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
                //???????????????????????????????????????ID
                .setAuthActivityLayoutId(R.layout.activity_oauth)
                //?????????????????????????????????ID
                .setAuthActivityViewIds(R.id.oauth_back, //?????????????????????ID
                        R.id.oauth_mobile_et, //????????????????????????ID
                        R.id.brand, //????????????????????????ID
                        R.id.oauth_login, //??????????????????ID
                        0, //?????????????????????ID????????????ImageView?????????
                        R.id.oauth_login, //????????????????????????ID
                        R.id.other_login, //????????????????????????ID
                        R.id.is_agree, //???????????????????????????ID
                        R.id.service_and_privacy   //???????????????????????????????????????ID
                )

                //??????????????????????????????????????????ID
                .setPrivacyDialogLayoutId(R.layout.ct_account_privacy_dialog)
                //????????????????????????????????????ID
                .setPrivacyDialogViewIds(R.id.ct_account_dialog_privacy, //???????????????????????????????????????ID
                        R.id.ct_account_dialog_cancel, // ??????????????????ID
                        R.id.ct_account_dialog_confirm) //??????????????????ID
                //??????????????????WebviewActivity???????????????ID
                .setWebviewActivityLayoutId(R.layout.ct_account_privacy_webview_activity)
                //?????????????????????????????????ID
                .setWebviewActivityViewIds(R.id.ct_account_webview_goback, //?????????????????????ID
                        R.id.ct_account_progressbar_gradient, //???????????????ID???ProgressBar?????????
                        R.id.ct_account_webview); //????????????WebView??????ID
        //??????????????????View??????????????? ????????????
//        .setExtendView1(view1 ,onClickListener1);
        //.setExtendView1(view2 ,onClickListener2)
        //.setExtendView1(view2 ,onClickListener2)
        //????????????????????????Activity??????????????????????????? ????????????
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
                //??????????????????WebViewActivity??????????????????????????????ViewId???????????????id???????????????????????????????????????????????????????????????????????????(int navParentViewId, int navTitleViewId)
                .setPrivacyWebviewActivity(R.id.ct_account_webview_nav_layout, R.id.ct_account_webview_nav_title)
                // ??????????????????WebViewActivity????????????????????????
                .setPrivacyGoBackResId(R.id.ct_account_webview_goback, R.drawable.ct_account_auth_goback_selector);
        return miniConfigBuilder.build();
    }

    private AuthViewConfig getAuthViewDynamicConfig(Context context) {
        AuthViewConfig.Builder configBuilder = new AuthViewConfig.Builder()
                // ???????????????????????????????????????
                .setPrivacyTextView(R.id.service_and_privacy, getPrivacyAgreementConfig(context))
                /** ????????????WebViewActivity */
                //??????????????????WebViewActivity??????????????????????????????ViewId???????????????id???????????????????????????????????????????????????????????????????????????(int navParentViewId, int navTitleViewId)
                .setPrivacyWebviewActivity(R.id.ct_account_webview_nav_layout, R.id.ct_account_webview_nav_title);
        return configBuilder.build();
    }

    private SpannableStringBuilder getPrivacyAgreementConfig(Context context) {
        if (mLoginUiConfig != null && mLoginUiConfig.getProtocolName1() != null) {
            String text = "??????????????????????????????????????????" + mLoginUiConfig.getProtocolName1()  + mLoginUiConfig.getProtocolName2() + "???????????????????????????";
            SpannableStringBuilder privacySpannableBuilder = new SpannableStringBuilder(text);
            CtClickableSpan clickableSpan1 = new CtClickableSpan(context, CtAuth.getCtPrivacyUrl(), CtAuth.CT_PRIVACY_TITLE, mLoginUiConfig.getProtocolTextColor2());
            privacySpannableBuilder.setSpan(clickableSpan1, 0, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            CtClickableSpan clickableSpan2 = new CtClickableSpan(context, mLoginUiConfig.getProtocolUrl1(), mLoginUiConfig.getProtocolName1(), mLoginUiConfig.getProtocolTextColor2());
            privacySpannableBuilder.setSpan(clickableSpan2, 14, 14 + mLoginUiConfig.getProtocolName1().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            CtClickableSpan clickableSpan3 = new CtClickableSpan(context, mLoginUiConfig.getProtocolUrl2(), mLoginUiConfig.getProtocolName2(), mLoginUiConfig.getProtocolTextColor2());
            privacySpannableBuilder.setSpan(clickableSpan3, 14 + mLoginUiConfig.getProtocolName1().length(), 14 + mLoginUiConfig.getProtocolName1().length() + mLoginUiConfig.getProtocolName2().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return privacySpannableBuilder;
        }
        SpannableStringBuilder privacySpannableBuilder = new SpannableStringBuilder("??????????????????????????????????????????????????????????????????????????????????????????");
        CtClickableSpan clickableSpan1 = new CtClickableSpan(context, CtAuth.getCtPrivacyUrl(), CtAuth.CT_PRIVACY_TITLE, mLoginUiConfig.getProtocolTextColor2());
        privacySpannableBuilder.setSpan(clickableSpan1, 5, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return privacySpannableBuilder;

    }

    /**
     * ??????6.??????Mini???????????????????????????
     * ????????????????????????????????????????????????????????????????????????????????????????????????App Logo?????????????????????
     * ??????????????????????????????????????????????????????SDK?????????????????????android:tag??????????????????????????????????????????????????????????????????????????????????????????????????????
     * v3.7.2 ????????????Mini???????????????????????????????????????
     */
    private  AuthPageConfig getMiniAuthPageConfig(LoginUiConfig loginUiConfig) {
        AuthPageConfig.Builder miniConfigBuilder = new AuthPageConfig.Builder()
                //???????????????????????????????????????ID
                .setAuthActivityLayoutId(R.layout.ct_account_mini_auth_activity_dynamic)
                //?????????????????????????????????ID
                .setAuthActivityViewIds(R.id.ct_account_nav_goback, //?????????????????????ID
                        R.id.ct_account_desensphone, //????????????????????????ID
                        0, //????????????????????????ID , ????????????0
                        R.id.ct_account_login_btn, //??????????????????ID
                        R.id.ct_account_login_loading, //?????????????????????ID????????????ImageView?????????
                        R.id.ct_account_login_text, //????????????????????????ID
                        R.id.ct_account_other_login_way, //????????????????????????ID
                        0, //???????????????????????????ID, ????????????0
                        R.id.ct_auth_privacy_text_dynamic   //???????????????????????????????????????ID
                );
        //??????Mini?????????????????????????????????(v3.7.2????????????????????????px?????????????????????(AuthPageConfig.CENTER)?????????(AuthPageConfig.BOTTOM))
        if (loginUiConfig != null && loginUiConfig.getDianXinLoginConfig() != null) {
            miniConfigBuilder.setMiniAuthActivityStyle(loginUiConfig.getDianXinLoginConfig().getDialogWidth(), loginUiConfig.getDianXinLoginConfig().getDialogHeight(), loginUiConfig.getDianXinLoginConfig().getLocation());
        } else {
            miniConfigBuilder.setMiniAuthActivityStyle(0, 0, AuthPageConfig.CENTER);
        }
        //???????????????????????????????????????ID
        miniConfigBuilder.setWebviewActivityLayoutId(R.layout.ct_account_privacy_webview_activity)
                //?????????????????????????????????ID
                .setWebviewActivityViewIds(R.id.ct_account_webview_goback, //?????????????????????ID
                        R.id.ct_account_progressbar_gradient, //???????????????ID???ProgressBar?????????
                        R.id.ct_account_webview) //????????????WebView??????ID
                //??????????????????View??????????????? ????????????
                //.setExtendView1(viewId1 ,onClickListener1)
                //.setExtendView1(viewId2 ,onClickListener2)
                //.setExtendView1(viewId3 ,onClickListener3)
                //.setExtendView4(viewId4 ,onClickListener4)
                //.setExtendView5(viewId5 ,onClickListener5)
                //????????????????????????Activity??????????????????????????? ????????????
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
