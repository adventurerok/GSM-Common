package com.ithinkrok.msm.common.permission;

import com.ithinkrok.msm.common.permission.PermissionContext;

public class ContextedPermission {

    private final String permission;
    private final boolean value;
    private final PermissionContext context;


    public ContextedPermission(String permission, boolean value,
                               PermissionContext context) {
        this.permission = permission;
        this.value = value;
        this.context = context;
    }


    public String getPermission() {
        return permission;
    }


    public boolean getValue() {
        return value;
    }


    public PermissionContext getContext() {
        return context;
    }
}
