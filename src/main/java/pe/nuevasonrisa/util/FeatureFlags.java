package pe.nuevasonrisa.util;

import pe.nuevasonrisa.config.CorreoConfig;

public final class FeatureFlags {

    private FeatureFlags() {
    }

    public static boolean emailFeaturesEnabled() {
        String configuracion = System.getenv("FEATURE_EMAILS");
        if (configuracion != null && !configuracion.isBlank()) {
            return Boolean.parseBoolean(configuracion);
        }
        String resendApiKey = CorreoConfig.resendApiKey();
        return resendApiKey != null && !resendApiKey.isBlank();
    }
}
