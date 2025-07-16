package com.aixo.apigateway.config;

public final class AppConfig {

    private AppConfig() {
    }

    // ========================
    // JWT Claims
    // ========================
    public static final class Jwt {
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String OAUTH_ID = "oAuthId";

        private Jwt() {
        }
    }

    // ========================
    // HTTP Headers
    // ========================
    public static final class Http {
        public static final String AUTHORIZATION = "Authorization";
        public static final String BEARER_PREFIX = "Bearer ";

        private Http() {
        }
    }

    // ========================
    // Redis Keys
    // ========================
    public static final class Redis {
        public static final String DENYLIST_PREFIX = "jti:denylist:";

        private Redis() {
        }
    }
}
