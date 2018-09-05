package com.ithinkrok.msm.common.permission;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PermissionsCache {


    private final PermissionLookup permissionLookup;
    private final GroupLookup groupLookup;


    public PermissionsCache(PermissionLookup permissionLookup, GroupLookup groupLookup) {
        this.permissionLookup = permissionLookup;
        this.groupLookup = groupLookup;
    }

    private final Map<Set<String>, PermissionClump> cache = new ConcurrentHashMap<>();

    public PermissionClump getPermissions(Set<String> groups) {
        return cache.computeIfAbsent(includeParents(groups), this::computeClump);
    }


    private Set<String> includeParents(Set<String> groups) {
        Set<String> result = new HashSet<>();

        for (String group : groups) {
            includeParents(group, result);
        }


        return result;
    }

    private void includeParents(String group, Set<String> result) {
        //prevent an infinite loop of parents
        if(!result.add(group)) return;

        Group groupDetails = groupLookup.getGroup(group);
        if(groupDetails != null) {
            for (String parent : groupDetails.getParents()) {
                includeParents(parent, result);
            }

        }
    }


    public void reloadPermissions() {
        cache.forEach((groups, permissionClump) -> {
            permissionClump.setBasePermissions(computePermissions(groups));
        });
    }

    private PermissionClump computeClump(Set<String> groups) {
        List<ContextedPermission> basePermissions = computePermissions(groups);

        return new PermissionClump(basePermissions, permissionLookup);
    }


    private List<ContextedPermission> computePermissions(Set<String> groups) {
        return groups.stream()
                .map(groupLookup::getGroup)
                .filter(Objects::nonNull)
                .sorted()
                .map(Group::getPermissions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
