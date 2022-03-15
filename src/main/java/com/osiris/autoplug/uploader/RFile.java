/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

import java.io.File;

/**
 * Details about a file in a github repo that needs to be replaced.
 */
public class RFile {
    /**
     * The replacement file. <br>
     * Example: ./my-file.txt
     */
    public String source;
    /**
     * The replacement file. <br>
     * Example: C:/User/Uploader/my-file.txt
     */
    public File fileSource;
    /**
     * The file to replace. <br>
     * Example: /path/in/github/repo/my-file.txt
     */
    public String target;
    /**
     * The file to replace. <br>
     * Example: C:/Users/path/in/github/repo/my-file.txt <br>
     * Note that this gets set if the repo was cloned and is available locally.
     */
    public File fileTarget;
}
