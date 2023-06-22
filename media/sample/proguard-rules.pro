# https://issuetracker.google.com/issues/144631039
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { <fields>; }

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
