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

-ignorewarnings

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep public class com.android.vending.billing.**
-keep class android.support.v7.widget.**{*;}
-keep class com.bumptech.glide.**{*;}
-keep class org.json.** { *;}
-dontwarn okhttp3.**
-dontwarn okio.**

-libraryjars libs/pushservice-6.7.3.20.jar
-dontwarn com.baidu.**
-keep class com.baidu.**{*; }

# 변수명변경
-renamesourcefileattribute SourceFile

