package com.geekbrains.geekmarketwinter.config.support;

public class Constants {
    //    API Main

    public static final String API = "/api";
    public static final String LOGOUT_URL = "/logout";
    public static final String LOGIN_URL = "/login";
    public static final String[] ALL_HTTP_MATCHERS = {
            "/VAADIN/**", "/HEARTBEAT/**", "/UIDL/**", "/resources/**",
            "/login", "/login**", "/login/**", "/manifest.json", "/icons/**", "/images/**",
            // (development mode) static resources
            "/frontend/**",
            // (development mode) webjars
            "/webjars/**",
            // (development mode) H2 debugging console
            "/h2-console/**",
            // (production mode) static resources
            "/frontend-es5/**", "/frontend-es6/**"
    };

    public static final String[] ALL_WEB_IGNORING_MATCHERS = {
            // Vaadin Flow static resources
            "/VAADIN/**",

            // the standard favicon URI
            "/favicon.ico",

            // web application manifest
            "/manifest.json",

            // icons and images
            "/icons/**",
            "/images/**",

            // (development mode) static resources
            "/frontend/**",

            // (development mode) webjars
            "/webjars/**",

            // (development mode) H2 debugging console
            "/h2-console/**",

            // (production mode) static resources
            "/frontend-es5/**", "/frontend-es6/**"
    };

    //    API для работы с User
    public static final String API_USERS = "/users";
    public static final String API_USER_ID = "userId";
    public static final String API_USERS_USER_ID = "/{userId}";
    public static final String API_AUTH_URL = API + API_USERS + "/auth";

    //    API для работы с Role
    public static final String API_ROLES = "/roles";
    public static final String API_ROLE_ID = "roleId";
    public static final String API_ROLES_ROLE_ID = "/{roleId}";

    //    Security Constants
    public static final String SIGN_UP_URL = "/users/sign-up";



}
