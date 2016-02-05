package com.ithinkrok.msm.common.util.io;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Created by paul on 05/02/16.
 */
public interface DirectoryListener {

    void fileChanged(Path changed, WatchEvent.Kind<?> event);
}
