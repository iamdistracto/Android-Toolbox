# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified in
# the Android SDK tools/proguard/proguard-android-optimize.txt.

# Keep Room entities
-keep class com.toolbox.data.history.** { *; }

# Keep FFmpegKit
-keep class com.arthenica.ffmpegkit.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Gson models
-keep class com.toolbox.domain.model.** { *; }

# General
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.instrument.UnmodifiableClassException
-dontwarn java.lang.instrument.ClassDefinition
-dontwarn java.lang.instrument.Instrumentation
