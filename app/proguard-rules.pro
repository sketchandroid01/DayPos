-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
-keepresourcexmlelements manifest/application/meta-data@value=GlideModule



-keep class com.epson.** { *; }
-dontwarn com.epson.**


-ignorewarnings
-keep class * {
    public private *;
}

-keepattributes *Annotation*
