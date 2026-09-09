# Retrofit / OkHttp keep generic signatures & annotations for reflection-based conversion.
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# kotlinx.serialization keeps its own consumer-rules.pro, but the generated
# serializers referenced only via reflection need an explicit keep.
-keepclassmembers class **$$serializer {
    *** INSTANCE;
    *** serializer(...);
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room entities/DTOs kept as data classes with no reflection needs beyond this.
-keep class com.elyndra.app.data.remote.dto.** { *; }
