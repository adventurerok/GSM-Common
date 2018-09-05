package com.ithinkrok.msm.common.permission;

import com.ithinkrok.msm.common.permission.PermissionInfo;

public interface PermissionLookup {

    PermissionInfo getPermission(String name);

}
