package pe.nuevasonrisa.util;

public final class FeatureFlags {

    private FeatureFlags() {
    }

    public static boolean emailFeaturesEnabled() {
        return Boolean.parseBoolean(System.getenv().getOrDefault("FEATURE_EMAILS", "false"));
    }
}
