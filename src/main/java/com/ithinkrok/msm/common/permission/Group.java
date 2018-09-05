package com.ithinkrok.msm.common.permission;

import com.ithinkrok.util.config.Config;

import java.util.List;
import java.util.Set;

public interface Group extends Comparable<Group> {


    /**
     *
     * @return The name of the group
     */
    String getName();

    /**
     *
     * @return The priority of the group. Higher priority group's permissions resolve last.
     */
    int getPriority();

    /**
     *
     * @return The names of the groups that are parents of this group.
     */
    Set<String> getParents();

    /**
     *
     * @return The list of permissions specified for this group (and not for parent groups etc).
     */
    List<ContextedPermission> getPermissions();

    Config getPermittedConfig();

    @Override
    default int compareTo(Group o){
        if(o.getPriority() != this.getPriority()) {
            return Integer.compare(this.getPriority(), o.getPriority());
        } else return getName().compareTo(o.getName());
    }
}
