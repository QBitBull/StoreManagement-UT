package com.sfinance.SFBackend.Constants;

public class Authority {
    public static final String[] USER_AUTHORITIES = {
            "user:read",
            "user:update", //temporary
            "user:create", //temporary
            "user:delete", //temporary
            "product:read",
            "product:update",
            "product:create",
            "product:delete",};
    public static final String[] ADMIN_AUTHORITIES = {
            "user:read",
            "user:update",
            "user:create",
            "user:delete",

            "product:read",
            "product:update",
            "product:create",
            "product:delete",

            "utility:read",
            "utility:update",
            "utility:create",
            "utility:delete",

            "finance:read",
            "finance:update",
            "finance:create",
            "finance:delete",

            "price:read",
            "price:update",
            "price:reset"};
}
