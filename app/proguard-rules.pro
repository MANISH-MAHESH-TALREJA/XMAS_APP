
-dontwarn com.squareup.okhttp.**
-dontwarn org.apache.http.**
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**
-keepattributes *Annotation,Signature
-keepattributes *Annotation*
-dontwarn okhttp3.internal.platform.*
-dontwarn org.conscrypt.*

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes Annotation

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.* { ; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------

-keep class com.startapp.** {
      *;
}
-keep class com.truenet.** {
      *;
}
-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile,
LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**
-dontwarn org.jetbrains.annotations.**