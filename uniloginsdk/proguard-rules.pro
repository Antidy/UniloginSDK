#移动sdk
-keep class com.cmic.sso.sdk.**{*;}
#联通sdk
-dontwarn com.sdk.**
-keep class com.sdk.** { *;}
#电信sdk
-keep class cn.com.chinatelecom.account.**{*;}
#emay
-keep class cn.emay.ql.UniSDK{*;}
-keep class cn.emay.ql.net.**{*;}
-keep class cn.emay.ql.utils.**{*;}
#V6新增
-keep class cn.emay.ql.listeners.**{*;}