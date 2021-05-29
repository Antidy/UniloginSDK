
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#移动sdk
-dontwarn com.cmic.sso.sdk.**
-keep class com.cmic.sso.sdk.**{*;}
#联通sdk
-dontwarn com.sdk.**
-keep class com.sdk.** { *;}
#电信sdk
-dontwarn cn.com.chinatelecom.**
-keep class cn.com.chinatelecom.account.**{*;}
#emay
-keep class cn.emay.ql.UniSDK{*;}
-keep class cn.emay.ql.net.**{*;}
-keep class cn.emay.ql.utils.**{*;}
#V6新增
-keep class cn.emay.ql.listeners.**{*;}