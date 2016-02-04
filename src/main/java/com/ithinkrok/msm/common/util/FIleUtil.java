package com.ithinkrok.msm.common.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by paul on 04/02/16.
 */
public class FIleUtil {


    public static FileSystem createZipFileSystem(Path zipFile) throws IOException {
        //Absolute URI
        final URI uri = URI.create("jar:file:" + zipFile.toUri().getRawPath());

        return FileSystems.newFileSystem(uri, new HashMap<>());
    }
}
