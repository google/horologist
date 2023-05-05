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

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation