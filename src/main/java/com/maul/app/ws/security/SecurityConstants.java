package com.maul.app.ws.security;

import com.maul.app.ws.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 3600000; // 864000000 10 days
    public static final long PASSWORD_RESET_EXPIRATION_TIME = 3600000; // 1 hour
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users/signUp";
    public static final String VERIFICATION_EMAIL_URL = "/users/emailVerification";
    public static final String REVERIFY = "/users/reVerify";
    public static final String PASSWORD_RESET_REQUEST_URL = "/users/passwordResetRequest";
    public static final String PASSWORD_RESET_URL = "/users/passwordReset";
    public static final String H2_CONSOLE = "/h2-console/**";

    public static String getTokenSecret() {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getTokenSecret();
    }
}
