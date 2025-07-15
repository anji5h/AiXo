package com.aixo.userservice.config;

public final class AppConfig {

    private AppConfig() {}

    // ========================
    // JWT Claims
    // ========================
    public static final class Jwt {
        public static final String ID = "jti";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String OAUTH_ID = "oAuthId";
        public static final String ISSUED_AT = "issuedAt";
        public static final String EXPIRATION = "expiration";

        private Jwt() {}
    }

    // ========================
    // OAuth2 Provider Info
    // ========================
    public static final class OAuth2 {
        public static final String ATTRIBUTE_SUB = "sub";
        public static final String ATTRIBUTE_EMAIL = "email";
        public static final String ATTRIBUTE_NAME = "name";
        public static final String ATTRIBUTE_PICTURE = "picture";

        private OAuth2() {}
    }

    // ========================
    // Redis Keys
    // ========================
    public static final class Redis {
        public static final String DENYLIST_PREFIX = "jti:denylist:";

        private Redis() {}
    }
}
