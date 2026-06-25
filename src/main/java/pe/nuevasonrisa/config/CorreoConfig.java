package pe.nuevasonrisa.config;

public final class CorreoConfig {

    private static final String RESEND_API_KEY_INTERNA = "";
    private static final String RESEND_FROM_INTERNO = "Nueva Sonrisa <onboarding@resend.dev>";

    private CorreoConfig() {
    }

    public static String resendApiKey() {
        String variableEntorno = System.getenv("re_ZFwKwQ9t_PCba6AyeRJuytarNqkgme5Y1");
        return valorConfigurado(variableEntorno, RESEND_API_KEY_INTERNA);
    }

    public static String resendFrom() {
        String variableEntorno = System.getenv("RESEND_FROM");
        return valorConfigurado(variableEntorno, RESEND_FROM_INTERNO);
    }

    private static String valorConfigurado(String principal, String respaldo) {
        if (principal != null && !principal.isBlank()) {
            return principal.trim();
        }
        return respaldo == null ? "" : respaldo.trim();
    }
}
