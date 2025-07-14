package com.aixo.userservice.config;

public final class AppConfig {

    private AppConfig() {}

    public static final class Jwt {
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String OAUTH_ID = "oAuthId";
        public static final String ISSUED_AT = "issuedAt";
        public static final String EXPIRATION = "expiration";

        private Jwt() {}
    }

    // ========================
    // HTTP Headers
    // ========================
    public static final class Http {
        public static final String AUTHORIZATION = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";
        public static final String CONTENT_TYPE_JSON = "application/json";

        private Http() {}
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
}
