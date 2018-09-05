package com.ithinkrok.msm.common.permission;

import com.ithinkrok.msm.common.permission.PermissionContext;
import com.ithinkrok.util.config.Config;

public interface PermissionHolder {


    /**
     * Checks if this PermissionHolder has this permission given no context.
     * If this is true, it usually means they always have this permission.
     *
     * @param permission The permission to check
     * @return If the holder has this permission, given no context
     */
    default boolean hasPermission(String permission){
        return hasPermission(permission, PermissionContext.NULL);
    }

    /**
     * Checks if this PermissionHolder has this permission in the specified context.
     *
     * @param permission The permission to check
     * @param context The context in which this permission is being checked.
     * @return
     */
    boolean hasPermission(String permission, PermissionContext context);


    /**
     * Gets the permitted config for this permission holder.
     * This effectively allows for ranks etc to specify permissions other than booleans.
     *
     * @return The permitted config.
     */
    Config getPermittedConfig();

}
