# Retrofit / Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class org.truthwatchindia.app.data.model.** { *; }
-keepclassmembers class org.truthwatchindia.app.data.model.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Coil
-keep class coil.** { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**
