package com.zoothii.iwbtodojava.core.utulities.constants;

public class Messages {
    public static String successGetRoles = "Roles are successfully listed.";
    public static String successGetRoleByName = "Role returned by name.";

    public static String successRegister = "User registered successfully.";
    public static String successLogin = "User logged in successfully.";
    public static String successPassword = "Password is correct.";
    public static String errorPassword = "Password is not correct.";
    public static String successSetRequestedRolesStringToRole = "Roles successfully set.";
    public static String errorGetAuthenticatedUserDetails = "Please log in.";
    public static String successGetAuthenticatedUserDetails = "Owner.";

    public static String errorCreateDefaultRoleIfNotExists(String defaultRole) {
        return "Default role " + defaultRole + " already exists.";
    }

    public static String successCreateDefaultRoleIfNotExists(String defaultRole) {
        return "Default role " + defaultRole + " is successfully created.";
    }

    public static String successCheckIfRoleExists(String role) {
        return "Role " + role + " exists.";
    }

    public static String errorCheckIfRoleExists(String role) {
        return "Role " + role + " does not exist.";
    }
}
