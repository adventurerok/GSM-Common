package com.ithinkrok.msm.common.permission;

import java.util.Objects;

public class PermissionContext {

    public static final PermissionContext NULL = new PermissionContext(null, null, null);


    /**
     * The game type the permission is being checked on. For discord users, this will be "discord"
     */
    private final String game;

    /**
     * The server type the permission is being checked on. For discord users, this will be "discord"
     */
    private final String server;

    /**
     * The sub type specified by the game server of the game.
     * For discord users, this will be the name of the channel the command is executed on, or "private" for private dms.
     */
    private final String subGame;


    public PermissionContext(String server, String game, String subGame) {
        this.server = server;
        this.game = game;
        this.subGame = subGame;
    }


    public String getServer() {
        return server;
    }


    public String getGame() {
        return game;
    }


    public String getSubGame() {
        return subGame;
    }


    /**
     * @return If this PermissionContext can contain the other permission context.
     */
    public boolean contains(PermissionContext other) {
        if(server != null && !Objects.equals(server, other.server)) return false;
        if(game != null && !Objects.equals(game, other.game)) return false;
        if(subGame != null && !Objects.equals(subGame, other.subGame)) return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionContext that = (PermissionContext) o;

        if (game != null ? !game.equals(that.game) : that.game != null) return false;
        if (server != null ? !server.equals(that.server) : that.server != null) return false;
        return subGame != null ? subGame.equals(that.subGame) : that.subGame == null;
    }


    @Override
    public int hashCode() {
        int result = game != null ? game.hashCode() : 0;
        result = 31 * result + (server != null ? server.hashCode() : 0);
        result = 31 * result + (subGame != null ? subGame.hashCode() : 0);
        return result;
    }
}
