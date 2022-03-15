/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

import java.io.File;
import java.util.List;

public class RepoDetail {
    public String name;
    public String branch;
    public File localPath;
    public List<RFile> filesToReplace;
}
