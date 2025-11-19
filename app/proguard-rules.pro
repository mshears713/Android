# Proguard rules for Frontier Command Center
# Keep serialization classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all model classes for serialization
-keep,includedescriptorclasses class com.frontiercommand.model.**$$serializer { *; }
-keepclassmembers class com.frontiercommand.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.frontiercommand.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}
