/*
 * Copyright (c) 2020 [Osiris Team](https://github.com/Osiris-Team)
 *  All rights reserved.
 *
 *  This software is copyrighted work licensed under the terms of the
 *  AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class YmlConfig {

    private static final YamlFile config = new YamlFile("autoplug-uploader-config.yml");

    private static final String path_github_oauth = "autoplug-uploader-config.github.o-auth";
    private static final String path_github_releases_repo = "autoplug-uploader-config.github.releases-repo";
    private static final String path_github_branch = "autoplug-uploader-config.github.branch";
    private static final String path_github_upload_dir = "autoplug-uploader-config.github.upload-dir";
    private static final String path_github_upload_files_names = "autoplug-uploader-config.github.upload-files-names";
    private static final String path_github_upload_files_launchers = "autoplug-uploader-config.github.launchers";

    private static final String path_user_downloads_dir = "autoplug-uploader-config.user.downloads-dir";

    public static void load(){

        // Load the YAML file if is already created or create new one otherwise
        try {
            if (!config.exists()) {
                System.out.println(" - autoplug-uploader-config.yml not found! Creating new one with defaults...");
                config.createNewFile(true);
            }
            else {
                System.out.println(" - Loading autoplug-uploader-config.yml...");
            }
            config.load(); // Loads the entire file
        } catch (Exception e) {
            System.out.println("Failed to load autoplug-uploader-config.yml...");
            e.printStackTrace();
        }

        // Insert defaults
        insertDefaults();

        // Makes settings globally accessible
        setDefaultToGlobal();

        // Validates options
        validateOptions();

        //Call save externally
        //save();

    }

    private static void insertDefaults(){

        config.addDefault(path_github_oauth, "insert_your_key_here");
        config.addDefault(path_github_releases_repo, "Author/Repository");
        config.addDefault(path_github_branch, "master");
        config.addDefault(path_github_upload_dir, "beta-builds");
        List<String> list = Arrays.asList("AutoPlug.jar AutoPlugPlugin.jar".split("[\\s]+"));
        config.addDefault(path_github_upload_files_names, list);
        List<String> list2 = Arrays.asList("none none".split("[\\s]+"));
        config.addDefault(path_github_upload_files_launchers, list2);
        config.addDefault(path_user_downloads_dir, "downloads");

    }

    //User configuration
    public static String github_oauth;
    public static String github_releases_repo;
    public static String github_branch;
    public static String github_upload_dir;
    public static List<String> github_upload_files_names;
    private static List<String> pre_github_upload_files_names = new ArrayList<>();
    public static List<String> github_upload_files_launchers;
    private static List<String> pre_github_upload_files_launchers = new ArrayList<>();
    public static String user_downloads_dir;


    private static void setDefaultToGlobal(){

        github_oauth = config.getString(path_github_oauth);
        github_releases_repo = config.getString(path_github_releases_repo);
        github_branch = config.getString(path_github_branch);
        github_upload_dir = config.getString(path_github_upload_dir);
        github_upload_files_names = config.getStringList(path_github_upload_files_names);
        github_upload_files_launchers = config.getStringList(path_github_upload_files_launchers);
        user_downloads_dir = config.getString(path_user_downloads_dir);

    }

    private static void validateOptions() {
    }

    public static void setOAuthKey(String key){
        config.set(path_github_oauth, key);
        github_oauth = key;
    }

    public static void setReleasesRepo(String repo){
        config.set(path_github_releases_repo, repo);
        github_releases_repo = repo;
    }

    public static void setBranch(String branch){
        config.set(path_github_branch, branch);
        github_branch = branch;
    }

    public static void setUploadDir(String dir){
        config.set(path_github_upload_dir, dir);
        github_upload_dir = dir;
    }

    public static void setUserDownloadsDir(String dir){
        config.set(path_user_downloads_dir, dir);
        user_downloads_dir = dir;
    }

    public static void addRelease(String release){
        pre_github_upload_files_names.add(release);
        pre_github_upload_files_launchers.add("none");
    }

    private static void setReleasesList(){
        config.set(path_github_upload_files_names, pre_github_upload_files_names);
        github_upload_files_names = pre_github_upload_files_names;

        config.set(path_github_upload_files_launchers, pre_github_upload_files_launchers);
        github_upload_files_launchers = pre_github_upload_files_launchers;
    }


    public static void save() {

        if (!pre_github_upload_files_names.isEmpty()){
            setReleasesList();
        }

        // Finally, save changes!
        try {
            config.saveWithComments();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Issues while saving config.yml");
        }

        System.out.println(" - Configuration file loaded!");

    }

}
