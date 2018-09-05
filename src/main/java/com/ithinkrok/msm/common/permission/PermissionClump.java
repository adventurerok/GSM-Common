package com.ithinkrok.msm.common.permission;

import com.ithinkrok.util.config.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionClump implements PermissionHolder {


    private List<ContextedPermission> basePermissions;
    private final PermissionLookup lookup;


    public PermissionClump(List<ContextedPermission> basePermissions,
                           PermissionLookup lookup) {
        this.basePermissions = basePermissions;
        this.lookup = lookup;
    }


    public void setBasePermissions(List<ContextedPermission> basePermissions) {
        this.basePermissions = basePermissions;

        resolvedPermissions.clear();
    }


    private final Map<PermissionContext, Map<String, Boolean>> resolvedPermissions = new ConcurrentHashMap<>();


    private Map<String, Boolean> resolvePermissions(PermissionContext permissionContext) {
        Map<String, Boolean> map = new HashMap<>();

        for (ContextedPermission permission : basePermissions) {
            if(!permission.getContext().contains(permissionContext)) continue;

            resolvePermission(permission.getPermission(), permission.getValue(), map);
        }

        return map;
    }


    private void resolvePermission(String permName, boolean value, Map<String, Boolean> map) {
        map.put(permName, value);

        //don't enable child perms if we are set to false
        if(!value) return;

        PermissionInfo permission = lookup.getPermission(permName);
        if(permission == null) return;

        for (Map.Entry<String, Boolean> childPermission : permission.getChildren().entrySet()) {
            resolvePermission(childPermission.getKey(), childPermission.getValue(), map);
        }

    }


    @Override
    public boolean hasPermission(String permission, PermissionContext context) {
        Map<String, Boolean> contextPermissions = resolvedPermissions
                .computeIfAbsent(context, this::resolvePermissions);

        if(contextPermissions.get(permission)) return true;

        //allow wildcards for the last "directory" in the path, e.g. a.b.* counts for a.b.c, but not a.*
        int lastDotIndex = permission.lastIndexOf('.');
        if(lastDotIndex >= 0) {
            String wildcard = permission.substring(0, lastDotIndex) + ".*";
            return contextPermissions.get(wildcard);
        } else return false;
    }


    @Override
    public Config getPermittedConfig() {
        return null;
    }
}
