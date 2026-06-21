package pe.nuevasonrisa.util;

public final class FeatureFlags {

    private FeatureFlags() {
    }

    public static boolean emailFeaturesEnabled() {
        String configuracion = System.getenv("FEATURE_EMAILS");
        if (configuracion != null && !configuracion.isBlank()) {
            return Boolean.parseBoolean(configuracion);
        }
        String resendApiKey = System.getenv("RESEND_API_KEY");
        return resendApiKey != null && !resendApiKey.isBlank();
    }
}
