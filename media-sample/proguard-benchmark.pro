# Preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Uncomment this to hide the original source file name.
-renamesourcefileattribute SourceFile

# Repackage classes into the top-level.
-repackageclasses

# When generating the baseline profile we want the proper names of
# the methods and classes
-dontobfuscate

# https://issuetracker.google.com/issues/144631039
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { <fields>; }

# https://github.com/square/okhttp/blob/master/okhttp/src/jvmMain/resources/META-INF/proguard/okhttp3.pro

# OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**