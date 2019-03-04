package com.geekbrains.geekmarketwinter.config.support;

import java.util.Locale;

public class Constants {
    //    API Main

    public static final String PATH_SEPARATOR = "/";

    /**
     * Application pages constants
     */
    public static final String LOGIN_PAGE = "login";
    public static final String LOGOUT_PAGE = "logout";
    public static final String SHOP_PAGE = "shop";
    public static final String CART_PAGE = "cart";
    public static final String CONFIRM_ORDER_PAGE = "confirm-order";
    public static final String PROFILE_PAGE = "profile";

    /* ADMINS PAGES */
    public static final String ADMIN_PAGE = "admin";
    public static final String CATEGORIES_PAGE = "categories";
    public static final String ORDER_STATUSES_PAGE = "order-statuses";
    public static final String PRODUCTS_PAGE = "products";
    public static final String ADMIN_PRODUCTS_PAGE = ADMIN_PAGE + PATH_SEPARATOR + PRODUCTS_PAGE;
    public static final String ADMIN_CATEGORIES_PAGE = ADMIN_PAGE + PATH_SEPARATOR + CATEGORIES_PAGE;
    public static final String ADMIN_ORDER_STATUSES_PAGE = ADMIN_PAGE + PATH_SEPARATOR + ORDER_STATUSES_PAGE;
    public static final String USERS_PAGE = "users";
    public static final String ADMIN_USERS_PAGE = ADMIN_PAGE + PATH_SEPARATOR + USERS_PAGE;
    public static final String ROLES_PAGE = "roles";
    public static final String ADMIN_ROLES_PAGE = ADMIN_PAGE + PATH_SEPARATOR + ROLES_PAGE;


    /* MANAGER PAGES */
    public static final String MANAGER_PAGE = "manager";
    public static final String ORDERS_PAGE = "orders";
    public static final String MANAGER_ORDERS_PAGE = MANAGER_PAGE + PATH_SEPARATOR + ORDERS_PAGE;

    /**
     * Application roles constants
     */
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ADMIN = "ADMIN";
    public static final String EMPLOYEE = "EMPLOYEE";
    public static final String MANAGER = "MANAGER";
    public static final String ROLE_ADMIN = ROLE_PREFIX + ADMIN;
    public static final String ROLE_EMPLOYEE = ROLE_PREFIX + EMPLOYEE;
    public static final String ROLE_MANAGER = ROLE_PREFIX + MANAGER;

    public static final Locale LOCALE_RU = new Locale("ru", "RU");

    public static final String LOGOUT_URL = PATH_SEPARATOR + LOGOUT_PAGE;
    public static final String LOGIN_URL = PATH_SEPARATOR + LOGIN_PAGE;

    public static final String DEFAULT_SRC = "images/users-png.png";

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
}
