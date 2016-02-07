package com.ithinkrok.util.command.event;

import com.ithinkrok.util.command.CustomCommand;
import com.ithinkrok.util.command.CustomCommandSender;
import com.ithinkrok.util.event.CustomEvent;

/**
 * Created by paul on 07/02/16.
 */
public interface CustomCommandEvent extends CustomEvent {

    CustomCommand getCommand();
    CustomCommandSender getCommandSender();

    boolean isHandled();
    void setHandled(boolean handled);

    boolean isValidCommand();
    void setValidCommand(boolean validCommand);
}
