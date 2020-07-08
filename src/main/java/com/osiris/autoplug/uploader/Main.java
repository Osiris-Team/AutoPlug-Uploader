/*
 * Copyright (c) 2020 [Osiris Team](https://github.com/Osiris-Team)
 *  All rights reserved.
 *
 *  This software is copyrighted work licensed under the terms of the
 *  AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

public class Main {

    public static void main(String[] args) {

        YmlConfig.load();

        FileManager fm = new FileManager();
        fm.check();

        if (fm.isFirstRun()){
            System.out.println("Detected first run.");
            new FirstSetup();
        }

        YmlConfig.save();

        if (!fm.isFilesInDir()){
            System.out.println("Not all the files you want to upload exist at /upload !");
            System.out.println("Please make sure that all files you listed in the yml config are spelled correctly");
            System.out.println("and located at /upload directory!");
            System.exit(10000);
        }

        CreateConfigs configs = new CreateConfigs();
        configs.createAllLocalUpdateConfigs();

        GithubUploader git = new GithubUploader();
        git.uploadAll();

    }

}
