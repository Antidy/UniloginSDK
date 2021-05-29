
package cn.emay.ql.utils;


import android.text.SpannableStringBuilder;
import android.view.View;

public class LoginUiConfig {
    private YiDongLoginConfig mYiDongLoginConfig;
    private LianTongLoginConfig mLianTongLoginConfig;
    private DianXinLoginConfig mDianXinLoginConfig;
    public class YiDongLoginConfig{
        private View authView;
        private int authResId;
        private int statusBarColor = 0xff0086d0;//状态栏颜色、字体颜色
        private boolean isStatusBarLightColor = false;
        private int navColor = 0xff0086d0;//导航栏颜色
        private String navText = "登录";//导航栏标题
        private int navTextColor = 0xffffffff;//导航栏字体颜色
        private int navTextSize = 17;//导航栏字体颜色

        private int numberColor = 0xff333333;//手机号码字体颜色
        private int numberSize = 18;////手机号码字体大小
        private int numFieldOffsetY = 170;//号码栏Y偏移量
        private String logBtnText = "本机号码一键登录";//登录按钮文本
        private int logBtnTextColor = 0xffffffff;//登录按钮文本颜色
        private String logBtnImgPath;//登录按钮背景
        private int logBtnSize = 15;//按钮文字大小
        private int logBtnWidth = 200;//按钮宽度
        private int logBtnHeight = 40;//按钮高度
        private int logBtnOffsetY = 254;//登录按钮Y偏移量
        private String uncheckedImgPath;//chebox未被勾选图片
        private String checkedImgPath;//chebox被勾选图片
        private int checkBoxImgPathSize = 9;//chebox被勾选图片大小
        private boolean privacyState = true;//授权页check

        private int privacyTextSize = 10;//10
        private int privacyTextColor1 = 0xff666666;//
        private int privacyTextColor2 = 0xff0085d0;//
        private int privacyOffsetY_B = 30;//隐私条款Y偏移量
        private int privacyMargin = 50;//

        public int getStatusBarColor() {
            return statusBarColor;
        }

        public void setStatusBarColor(int statusBarColor) {
            this.statusBarColor = statusBarColor;
        }

        public boolean isStatusBarLightColor() {
            return isStatusBarLightColor;
        }

        public void setStatusBarLightColor(boolean statusBarLightColor) {
            isStatusBarLightColor = statusBarLightColor;
        }

        public int getNavColor() {
            return navColor;
        }

        public void setNavColor(int navColor) {
            this.navColor = navColor;
        }


        public String getNavText() {
            return navText;
        }

        public void setNavText(String navText) {
            this.navText = navText;
        }

        public int getNavTextColor() {
            return navTextColor;
        }

        public void setNavTextColor(int navTextColor) {
            this.navTextColor = navTextColor;
        }


        public int getNumberColor() {
            return numberColor;
        }

        public void setNumberColor(int numberColor) {
            this.numberColor = numberColor;
        }

        public int getNumberSize() {
            return numberSize;
        }

        public void setNumberSize(int numberSize) {
            this.numberSize = numberSize;
        }

        public int getNumFieldOffsetY() {
            return numFieldOffsetY;
        }

        public void setNumFieldOffsetY(int numFieldOffsetY) {
            this.numFieldOffsetY = numFieldOffsetY;
        }

        public String getLogBtnText() {
            return logBtnText;
        }

        public void setLogBtnText(String logBtnText) {
            this.logBtnText = logBtnText;
        }

        public int getLogBtnTextColor() {
            return logBtnTextColor;
        }

        public void setLogBtnTextColor(int logBtnTextColor) {
            this.logBtnTextColor = logBtnTextColor;
        }

        public int getLogBtnWidth() {
            return logBtnWidth;
        }

        public void setLogBtnWidth(int logBtnWidth) {
            this.logBtnWidth = logBtnWidth;
        }

        public int getLogBtnHeight() {
            return logBtnHeight;
        }

        public void setLogBtnHeight(int logBtnHeight) {
            this.logBtnHeight = logBtnHeight;
        }

        public String getLogBtnImgPath() {
            return logBtnImgPath;
        }

        public void setLogBtnImgPath(String logBtnImgPath) {
            this.logBtnImgPath = logBtnImgPath;
        }

        public int getLogBtnSize() {
            return logBtnSize;
        }

        public void setLogBtnSize(int logBtnSize) {
            this.logBtnSize = logBtnSize;
        }

        public int getLogBtnOffsetY() {
            return logBtnOffsetY;
        }

        public void setLogBtnOffsetY(int logBtnOffsetY) {
            this.logBtnOffsetY = logBtnOffsetY;
        }

        public String getUncheckedImgPath() {
            return uncheckedImgPath;
        }

        public void setUncheckedImgPath(String uncheckedImgPath) {
            this.uncheckedImgPath = uncheckedImgPath;
        }

        public String getCheckedImgPath() {
            return checkedImgPath;
        }

        public void setCheckedImgPath(String checkedImgPath) {
            this.checkedImgPath = checkedImgPath;
        }

        public int getCheckBoxImgPathSize() {
            return checkBoxImgPathSize;
        }

        public void setCheckBoxImgPathSize(int checkBoxImgPathSize) {
            this.checkBoxImgPathSize = checkBoxImgPathSize;
        }

        public boolean isPrivacyState() {
            return privacyState;
        }

        public void setPrivacyState(boolean privacyState) {
            this.privacyState = privacyState;
        }

        public int getPrivacyTextSize() {
            return privacyTextSize;
        }

        public void setPrivacyTextSize(int privacyTextSize) {
            this.privacyTextSize = privacyTextSize;
        }

        public int getPrivacyTextColor1() {
            return privacyTextColor1;
        }

        public void setPrivacyTextColor1(int privacyTextColor1) {
            this.privacyTextColor1 = privacyTextColor1;
        }

        public int getPrivacyTextColor2() {
            return privacyTextColor2;
        }

        public void setPrivacyTextColor2(int privacyTextColor2) {
            this.privacyTextColor2 = privacyTextColor2;
        }

        public int getPrivacyOffsetY_B() {
            return privacyOffsetY_B;
        }

        public void setPrivacyOffsetY_B(int privacyOffsetY_B) {
            this.privacyOffsetY_B = privacyOffsetY_B;
        }

        public int getPrivacyMargin() {
            return privacyMargin;
        }

        public void setPrivacyMargin(int privacyMargin) {
            this.privacyMargin = privacyMargin;
        }

        public int getNavTextSize() {
            return navTextSize;
        }

        public void setNavTextSize(int navTextSize) {
            this.navTextSize = navTextSize;
        }

        public int getAuthResId() {
            return authResId;
        }

        public void setAuthResId(int authResId) {
            this.authResId = authResId;
        }

        public View getAuthView() {
            return authView;
        }

        public void setAuthView(View authView) {
            this.authView = authView;
        }
    }

    public class LianTongLoginConfig{
        private int loginLogo;
        private boolean showOtherLogin;
        private boolean showProtocolBox;
        private String loginButtonText;
        private int loginButtonWidth;
        private int loginButtonHeight;
        private int offsetY;
        private int protocolCheckRes;
        private int protocolUnCheckRes;

        public int getLoginLogo() {
            return loginLogo;
        }

        public void setLoginLogo(int loginLogo) {
            this.loginLogo = loginLogo;
        }


        public boolean isShowOtherLogin() {
            return showOtherLogin;
        }

        public void setShowOtherLogin(boolean showOtherLogin) {
            this.showOtherLogin = showOtherLogin;
        }


        public boolean isShowProtocolBox() {
            return showProtocolBox;
        }

        public void setShowProtocolBox(boolean showProtocolBox) {
            this.showProtocolBox = showProtocolBox;
        }

        public String getLoginButtonText() {
            return loginButtonText;
        }

        public void setLoginButtonText(String loginButtonText) {
            this.loginButtonText = loginButtonText;
        }

        public int getLoginButtonWidth() {
            return loginButtonWidth;
        }

        public void setLoginButtonWidth(int loginButtonWidth) {
            this.loginButtonWidth = loginButtonWidth;
        }

        public int getLoginButtonHeight() {
            return loginButtonHeight;
        }

        public void setLoginButtonHeight(int loginButtonHeight) {
            this.loginButtonHeight = loginButtonHeight;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
        }

        public int getProtocolCheckRes() {
            return protocolCheckRes;
        }

        public void setProtocolCheckRes(int protocolCheckRes) {
            this.protocolCheckRes = protocolCheckRes;
        }

        public int getProtocolUnCheckRes() {
            return protocolUnCheckRes;
        }

        public void setProtocolUnCheckRes(int protocolUnCheckRes) {
            this.protocolUnCheckRes = protocolUnCheckRes;
        }
    }

    public class DianXinLoginConfig{
        //隐私协议文本,其中配置说明如下
        // 1、$OAT 为运营商协议标题占位符，SDK程序默认替换为《天翼账号服务与隐私协议》，若有其它运营商协议配置需求，可添加配置；
        // 2、$CAT 为自定义协议标题占位符，SDK程序会替换为自定义标题字段的值；
        // 3、[应用名] ：修改为您应用的名称
        private int dialogWidth;//弹窗宽
        private int dialogHeight;//弹窗高
        private int location;//0:center,1:bottom

        private SpannableStringBuilder spannableStringBuilder;//自定义协议与隐私文本数据
        public int getDialogWidth() {
            return dialogWidth;
        }

        public void setDialogWidth(int dialogWidth) {
            this.dialogWidth = dialogWidth;
        }

        public int getDialogHeight() {
            return dialogHeight;
        }

        public void setDialogHeight(int dialogHeight) {
            this.dialogHeight = dialogHeight;
        }

        public int getLocation() {
            return location;
        }

        public void setLocation(int location) {
            this.location = location;
        }

    }

    public YiDongLoginConfig getYiDongLoginConfig() {
        return mYiDongLoginConfig;
    }

    public void setYiDongLoginConfig(YiDongLoginConfig yiDongLoginConfig) {
        mYiDongLoginConfig = yiDongLoginConfig;
    }

    public LianTongLoginConfig getLianTongLoginConfig() {
        return mLianTongLoginConfig;
    }

    public void setLianTongLoginConfig(LianTongLoginConfig lianTongLoginConfig) {
        mLianTongLoginConfig = lianTongLoginConfig;
    }

    public DianXinLoginConfig getDianXinLoginConfig() {
        return mDianXinLoginConfig;
    }

    public void setDianXinLoginConfig(DianXinLoginConfig dianXinLoginConfig) {
        mDianXinLoginConfig = dianXinLoginConfig;
    }


    private String protocolID1;
    private String protocolName1;
    private String protocolUrl1;
    private String protocolID2;
    private String protocolName2;
    private String protocolUrl2;
    private int protocolTextColor1;
    private int protocolTextColor2;

    public YiDongLoginConfig getmYiDongLoginConfig() {
        return mYiDongLoginConfig;
    }

    public void setmYiDongLoginConfig(YiDongLoginConfig mYiDongLoginConfig) {
        this.mYiDongLoginConfig = mYiDongLoginConfig;
    }

    public LianTongLoginConfig getmLianTongLoginConfig() {
        return mLianTongLoginConfig;
    }

    public void setmLianTongLoginConfig(LianTongLoginConfig mLianTongLoginConfig) {
        this.mLianTongLoginConfig = mLianTongLoginConfig;
    }

    public DianXinLoginConfig getmDianXinLoginConfig() {
        return mDianXinLoginConfig;
    }

    public void setmDianXinLoginConfig(DianXinLoginConfig mDianXinLoginConfig) {
        this.mDianXinLoginConfig = mDianXinLoginConfig;
    }

    public String getProtocolID1() {
        return protocolID1;
    }

    public void setProtocolID1(String protocolID1) {
        this.protocolID1 = protocolID1;
    }

    public String getProtocolName1() {
        return protocolName1;
    }

    public void setProtocolName1(String protocolName1) {
        this.protocolName1 = protocolName1;
    }

    public String getProtocolUrl1() {
        return protocolUrl1;
    }

    public void setProtocolUrl1(String protocolUrl1) {
        this.protocolUrl1 = protocolUrl1;
    }

    public String getProtocolID2() {
        return protocolID2;
    }

    public void setProtocolID2(String protocolID2) {
        this.protocolID2 = protocolID2;
    }

    public String getProtocolName2() {
        return protocolName2;
    }

    public void setProtocolName2(String protocolName2) {
        this.protocolName2 = protocolName2;
    }

    public String getProtocolUrl2() {
        return protocolUrl2;
    }

    public void setProtocolUrl2(String protocolUrl2) {
        this.protocolUrl2 = protocolUrl2;
    }

    public int getProtocolTextColor1() {
        return protocolTextColor1;
    }

    public void setProtocolTextColor1(int protocolTextColor1) {
        this.protocolTextColor1 = protocolTextColor1;
    }

    public int getProtocolTextColor2() {
        return protocolTextColor2;
    }

    public void setProtocolTextColor2(int protocolTextColor2) {
        this.protocolTextColor2 = protocolTextColor2;
    }
}
