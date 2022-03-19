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


# 避免混淆泛型
-keepattributes Signature
-keepattributes Exceptions,InnerClasses
 #包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#不优化输入的类文件
-dontoptimize
#保护注解
-keepattributes *Annotation*
#忽略警告
-ignorewarning
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 保持四大组件等不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
# 保留R下面的资源
-keep class **.R$* {*;}
# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保留在Activity中的方法参数是view的方法，
# 这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-keep class com.google.** { *; }

-keep class com.linxcool.sdkface.feature.YmnPluginWrapper { *; }
-keep class com.linxcool.sdkface.feature.plugin.YmnChannelInterface { *;}
-keep class com.linxcool.sdkface.feature.YmnDataBuilder { *; }
-keep class com.linxcool.sdkface.feature.YmnDataBuilder$* { *; }
-keep class com.linxcool.sdkface.feature.YmnCallbackInterceptor { *; }

-keep class com.linxcool.sdkface.* { *; }
-keep class com.linxcool.sdkface.entity.* { *; }
-keep class com.linxcool.sdkface.util.* { *; }

-keep class com.linxcool.sdkface.action.HttpHelper { *; }
-keep class com.linxcool.sdkface.action.HttpListener { *; }

-keep class com.linxcool.sdkface.feature.protocol.* { *; }
-keep class com.linxcool.sdkface.feature.YmnProperties { *; }
-keep public class * extends com.bianfeng.ymnsdk.feature.protocol.*

-keep class * extends com.linxcool.sdkface.feature.YmnPluginWrapper{ *; }
-keep class * extends com.linxcool.sdkface.feature.plugin.YmnChannelInterfac{ *; }
-keep class * extends com.linxcool.sdkface.feature.plugin.YmnUserInterfac{ *; }
-keep class * extends com.linxcool.sdkface.feature.plugin.YmnPaymentInterfac{ *; }
