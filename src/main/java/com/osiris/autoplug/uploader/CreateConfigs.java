/*
 * Copyright (c) 2020 [Osiris Team](https://github.com/Osiris-Team)
 *  All rights reserved.
 *
 *  This software is copyrighted work licensed under the terms of the
 *  AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

import org.update4j.Configuration;
import org.update4j.FileMetadata;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CreateConfigs {

    public List<Configuration> createAllLocalUpdateConfigs(){
        try {
            List<Configuration> allConfigs = new ArrayList<>();

            for (int i = 0; i < YmlConfig.github_upload_files_names.size(); i++) {

                String file_name = YmlConfig.github_upload_files_names.get(i);
                String launcher = YmlConfig.github_upload_files_launchers.get(i);
                allConfigs.add(
                        createLocalUpdatesConfig(
                        YmlConfig.github_releases_repo, YmlConfig.github_branch, YmlConfig.github_upload_dir,
                        new File(FileManager.upload+"/"+file_name),
                                file_name, YmlConfig.user_downloads_dir, launcher));
            }

            return  allConfigs;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //This creates a local updates.xml file first and after it fetches for the online xml and returns it
    private Configuration createLocalUpdatesConfig(String releases_repo, String branch, String upload_dir, File file_path,
                                                   String file_name, String user_downloads_dir, String launcher) throws Exception {

        //Without file extensions
        String[] clean_name = file_path.getName().split("\\.");

        final String UPLOAD_BASE = "https://github.com/"+releases_repo+"/raw/"+branch+"/"+upload_dir;
        final String DOWNLOAD_URL = UPLOAD_BASE+"/"+file_name; //Full file name AutoPlug.jar needed here
        final String CONFIG_FILENAME = "update-"+clean_name[0]+".xml"; //Gets AutoPlug.jar -> AutoPlug
        final String CONFIG_URL = UPLOAD_BASE+"/"+CONFIG_FILENAME;
        File configFile = new File(FileManager.upload + "/"+CONFIG_FILENAME);

        //Add this config file to the list
        FileManager.xml_files.add(configFile);


        System.out.println(" ");
        System.out.println("Creating "+ CONFIG_FILENAME + " for " + file_name);

        if (!configFile.exists()){
            configFile.createNewFile();
        }
        else{
            configFile.delete();
            configFile.createNewFile();
        }

        //Builds local update xml config
        Configuration config;
        Configuration.Builder builder = Configuration.builder();
        builder.baseUri(UPLOAD_BASE);
        builder.basePath(System.getProperty("user.dir")+"/"+user_downloads_dir);
        builder.file(FileMetadata.readFrom(file_path.toPath())
                .ignoreBootConflict(true)
                .classpath()
                .uri(DOWNLOAD_URL));

        if (!launcher.equals("none")){
            builder.launcher(launcher);
        }

        config = builder.build();

        //Writes this updates.xml config to local file
        try (Writer out = Files.newBufferedWriter(configFile.toPath())) {
            config.write(out);
            System.out.println("Created xml at: "+ configFile.toPath());
        }

        //Reads the newly created config and returns it
        try (Reader in = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            config = Configuration.read(in);
        } catch (IOException e) {
            System.err.println("ERROR READING XML!");
        }

        return config;

    }

}
