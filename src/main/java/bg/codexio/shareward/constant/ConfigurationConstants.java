package bg.codexio.shareward.constant;

public class ConfigurationConstants {

    public static final long EXPIRATION_TIME = 24 * 10 * 3600;
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static class User {
        public static final String ERROR_PASSWORD_VALIDATION = "ERROR_PASSWORD_VALIDATION";
        public static final String ERROR_EMAIL_VALIDATION = "ERROR_EMAIL_VALIDATION";
        public static final String ERROR_FULL_NAME_VALIDATION = "ERROR_FULL_NAME_VALIDATION";

        public static final double INITIAL_MONEY = 1000;
    }

    private ConfigurationConstants() {

    }

}
