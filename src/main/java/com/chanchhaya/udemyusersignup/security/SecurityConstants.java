package com.chanchhaya.udemyusersignup.security;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADERS_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/users";
    public static final String TOKEN_SECRET = "ggwpqwer";

}
