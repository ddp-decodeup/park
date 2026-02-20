# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\asheq\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Retrofit
-keep class com.google.gson.** { *; }
-keep public class com.google.gson.** {public private protected *;}
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.xml.stream.** { *; }
-keep class retrofit.** { *; }
-keep class com.google.appengine.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.squareup.okhttp.*
-dontwarn rx.**
-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**



-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.

# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions



# Hide warnings about references to newer platforms in the library
-dontwarn android.support.v7.**
# don't process support library
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
# To support Enum type of class members
-keepclassmembers enum * { *; }

-keep class com.parkloyalty.lpr.scan.** { *; }
-keep class com.parkloyalty.lpr.scan.**.** { *; }
-keep class * extends com.parkloyalty.lpr.scan.ui.*
-keep class * extends com.parkloyalty.lpr.scan.vehiclestickerscan.*

#########################################
-keep class com.google.android.material.** { *; }

-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }


-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.* { *; }

-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-dontwarn android.net.SSLCertificateSocketFactory

-keepattributes *Annotation*
-keepattributes Signature
-dontwarn android.net.SSLCertificateSocketFactory
-dontwarn android.app.Notification
-dontwarn com.squareup.**
-dontwarn okio.**

-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.**

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

 # Keep generic signature of RxJava3 (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking class io.reactivex.Observable.**
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking class arrow.retrofit.**
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Flowable.*
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Maybe.*
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Observable.*
-keep,allowobfuscation,allowshrinking class io.reactivex.rxjava3.core.Single.*
-keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.NetworkDispatcher {
    void processRequest();
}
-keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.CacheDispatcher {
    void processRequest();
}



# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>
#
#

## Start of --- XF Printer ---
-keep class twotechnologies.com.n5library.** { *; }
-dontwarn twotechnologies.com.n5library.**

# --- KEEP all classes in the n5library ---
-keep class com.twotechnologies.n5library.** { *; }
-dontwarn com.twotechnologies.n5library.**

# --- PRESERVE Gson model classes (fields and constructors) ---
-keep class com.twotechnologies.n5library.** { *; }

# Keep class members used by Gson
-keepclassmembers class com.twotechnologies.n5library.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# If there are custom Gson serializers/deserializers in the library
-keep class * implements com.google.gson.JsonDeserializer
-keep class * implements com.google.gson.JsonSerializer

# Keep all no-arg constructors (required for reflection-based instantiation)
-keepclassmembers class * {
    public <init>();
}

# Keep any TypeAdapterFactories if used
-keep class * implements com.google.gson.TypeAdapterFactory

# --- KEEP BroadcastReceiver (in case the library uses it) ---
-keep public class * extends android.content.BroadcastReceiver {
    public <init>();
    public void onReceive(android.content.Context, android.content.Intent);
}

# --- KEEP Service (if used by the library) ---
-keep public class * extends android.app.Service

# Preserve annotations and generics used by Gson or Retrofit
-keepattributes Signature
-keepattributes *Annotation*

# Optional: if the library uses Retrofit (many SDKs do)
#-keep class retrofit2.** { *; }
#-dontwarn retrofit2.**
#-keepclasseswithmembers class * {
#    @retrofit2.http.* <methods>;
#}


# --- KEEP EVERYTHING IN xflibrary ---
-keep class com.xf.** { *; }
-dontwarn com.xf.**
## End of --- XF Printer ---


-keep class com.twotechnologies.n5library.** { *; }
-dontwarn com.twotechnologies.n5library.**

 ## Vinod code changes for XF
-keepclassmembers class com.xf.** {
    *;
}
-keepclassmembers class com.twotechnologies.n5library.** {
    *;
}
# Keep the broadcast receiver that maps to b.a$a (obfuscated as k.a)

-keep class b.b extends android.content.BroadcastReceiver {
    public <init>();
    public void onReceive(android.content.Context, android.content.Intent);
}

# Keep XF core classes
-keep class com.twotechnologies.** { *; }
-keepclassmembers class com.twotechnologies.** { *; }

# Keep all BroadcastReceiver implementations
-keep class * extends android.content.BroadcastReceiver {
    public <init>();
    public void onReceive(android.content.Context, android.content.Intent);
}

 -keep class b.b extends android.content.BroadcastReceiver {
     public <init>();
     public void onReceive(android.content.Context, android.content.Intent);
 }
 -keep class **.xflibrary.** { *; }


#  Keep all XF services (if any exist)
-keep class * extends android.app.Service {
    public <init>();
}
-keepclassmembers class * {
    @com.twotechnologies.n5library.annotation.Keep *;
}

# Keep XF-related classes used in manifest (declared via XML)
-keepnames class com.twotechnologies.**
-dontwarn com.twotechnologies.**

##---------------------
# Prevent removal of java.awt.Color if used reflectively
-keep class java.awt.Color { *; }
# Keep Apache POI classes that might reference java.awt.Color
-keep class org.apache.poi.** { *; }
# Optional: keep all utility classes related to HSSF color
-keep class org.apache.poi.hssf.util.HSSFColor$** { *; }
##---------------------Doubango---------
-keep class com.parkloyalty.lpr.scan.common.model.** { *; }

#Used to keep doubango related classes for release build
-keep class com.parkloyalty.lpr.scan.doubangoultimatelpr.** { *;}
-keep class org.doubango.ultimateAlpr.Sdk.** { *;}

# ML Kit Barcode Scanning
-keep class com.google.mlkit.** { *; }
-keep interface com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.google.android.gms.internal.mlkit_** { *; }

# Keep protobuf classes used by ML Kit internally
-keep class com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**

# Play Services / Vision dependencies (if any)
-keep class com.google.android.gms.vision.** { *; }
-dontwarn com.google.android.gms.vision.**

# Sometimes MLKit references java.nio packages in a way that confuses shrinker
-dontwarn java.nio.file.*

#Start of Hilt Rules
# Hilt ProGuard Rules
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations

# Keep Hilt generated code
-keep class androidx.hilt.** { *; }
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# Keep @Inject constructors
-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
}

# Keep Hilt-generated ComponentManager, ComponentImpl, module factory methods
-keep class * extends dagger.hilt.internal.GeneratedComponentManager
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep,allowobfuscation @dagger.hilt.EntryPoint class *
-keep,allowobfuscation @dagger.hilt.EntryPoints class *

# Keep field names of generated Hilt classes
-keepclasseswithmembers class * {
    @dagger.hilt.* <fields>;
}
-keepclasseswithmembers class * {
    @javax.inject.* <fields>;
}

# Keep lifecycle viewModels injection
-keepnames class * extends androidx.lifecycle.ViewModel
-keepnames class * extends dagger.hilt.android.lifecycle.HiltViewModel
#End of Hilt Rules




###############################################################################
# Keep Jackson library classes (safe and simple). If you want stricter rules
# you can narrow these, but keeping the library is the simplest reliable option.
###############################################################################
-keep class com.fasterxml.jackson.** { *; }
-keep class org.codehaus.** { *; }
###############################################################################
# Keep annotations & relevant attributes required by Jackson's reflective logic
###############################################################################
# Preserve annotation information used at runtime
-keepattributes *Annotation*
# Keep signatures needed for generic types
-keepattributes Signature
# Keep runtime-visible parameter annotations (for @JsonCreator constructors)
-keepattributes RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations
###############################################################################
# Keep model members annotated for JSON (fields, methods, constructors)
# This keeps fields/methods/ctors that are explicitly annotated with Jackson
###############################################################################
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.JsonProperty *;
}
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.JsonCreator <init>(...);
    @com.fasterxml.jackson.annotation.JsonCreator public *;
}
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.JsonSetter *;
    @com.fasterxml.jackson.annotation.JsonGetter *;
    @com.fasterxml.jackson.annotation.JsonAnySetter *;
    @com.fasterxml.jackson.annotation.JsonAnyGetter *;
}
###############################################################################
# Keep classes annotated with Jackson type info / mixins so polymorphism works
###############################################################################
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.JsonTypeInfo *;
    @com.fasterxml.jackson.annotation.JsonSubTypes *;
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties *;
    @com.fasterxml.jackson.annotation.JsonDeserialize *;
    @com.fasterxml.jackson.annotation.JsonSerialize *;
}
###############################################################################
# If you use jackson-module-kotlin: keep Kotlin metadata and kotlin-reflection
###############################################################################
-keep class kotlin.Metadata { *; }
# Prevent obfuscation of kotlin reflective helpers used by the module
-keep class com.fasterxml.jackson.module.kotlin.** { *; }
###############################################################################
# Keep Java 8 / JSR-310 module classes if you use them (date/time)
###############################################################################
-keep class com.fasterxml.jackson.datatype.jsr310.** { *; }
###############################################################################
# If you use afterburner, Joda, or other Jackson modules add them explicitly:
###############################################################################
-keep class com.fasterxml.jackson.module.afterburner.** { *; }
-keep class com.fasterxml.jackson.datatype.joda.** { *; }
###############################################################################
# Keep common databind entrypoints (ObjectMapper methods used via reflection)
###############################################################################
-keepclassmembers class com.fasterxml.jackson.databind.ObjectMapper {
    public *;
}
-keepclassmembers class com.fasterxml.jackson.databind.type.TypeFactory { *; }
###############################################################################
# Optional: If you prefer stricter rules (avoid keeping whole library),
# uncomment the following and remove the top-level -keep for com.fasterxml.jackson.**
###############################################################################
# -keep class com.fasterxml.jackson.annotation.** { *; }
# -keep class com.fasterxml.jackson.databind.** { *; }
# -keepclassmembers class * {
#     @com.fasterxml.jackson.annotation.JsonProperty *;
#     @com.fasterxml.jackson.annotation.JsonCreator <init>(...);
# }
###############################################################################
# Helpful: Keep enums' names (Jackson commonly uses enum names)
###############################################################################
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-dontwarn java.beans.ConstructorProperties
-dontwarn java.beans.Transient


