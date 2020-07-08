/*
 * Copyright (c) 2020 [Osiris Team](https://github.com/Osiris-Team)
 *  All rights reserved.
 *
 *  This software is copyrighted work licensed under the terms of the
 *  AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;


import org.kohsuke.github.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GithubUploader {

    /**
     * Uploads all files located in /upload to a specified location on GitHub
     */
    public void uploadAll() {

        GitHub user = getUser();
        GHRepository repo = getRepo(user);
        deleteOld(repo);

        try {

            for (String file :
                    YmlConfig.github_upload_files_names) {
                uploadFile(file, repo);
            }

            for (File file :
                    FileManager.xml_files) {
                uploadFile(file.getName(), repo);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to upload files!");
        }
    }

    /**
     * Uploads one file to the repository
     * @param file
     * @param repo
     * @throws IOException
     */
    private void uploadFile(String file, GHRepository repo) throws IOException {

        System.out.println(" ");
        System.out.println("#########################################");
        System.out.println(" - Upload path: " + YmlConfig.github_upload_dir+"/"+file);

        //Create content
        GHContentBuilder content = repo.createContent()
        .message("Uploaded latest build")
        .path(YmlConfig.github_upload_dir+"/"+file) //beta-builds or stable-builds + / + AutoPlug.jar or another
        .branch(YmlConfig.github_branch)
        .content(Files.readAllBytes(Paths.get(FileManager.upload.getPath() + "/"+file)));

        try {
            System.out.println(" - Uploading...");
            GHCommit commit = content.commit().getCommit();
            System.out.println(" - Upload successful at: " + commit.getCommitDate());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" - Upload failed!");
        }

    }

    /**
     * Connects to github via token given in the yml config.
     * @return GitHub user
     */
    private GitHub getUser(){

        GitHub user;
        try{
            System.out.println(" ");
            System.out.println("Logging in to github with token...");
            user = new GitHubBuilder()
                    .withOAuthToken(YmlConfig.github_oauth)
                    .build();
            System.out.println("Success!");
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            try{
                System.out.println("Failed to login! Please enter a valid token!");
                Thread.sleep(10000);
                System.exit(0);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            return null;
        }

    }

    /**
     * Connects to a users repository by the repository name in the yml config.
     * @param user
     * @return User repository
     */
    private GHRepository getRepo(GitHub user){

        GHRepository repo;
        try{
            System.out.println(" ");
            System.out.println("Fetching repository...");
            repo = user.getRepository(YmlConfig.github_releases_repo);
            System.out.println("Success!");

            System.out.println(" > REPO-INFO >");
            System.out.println(" > Name: " + repo.getFullName());
            System.out.println(" > Stars: " + repo.getStargazersCount());
            System.out.println(" > Subs: " + repo.getSubscribersCount());
            System.out.println(" > Views: " + repo.getViewTraffic().getViews().size());
            System.out.println(" > Size: " + repo.getSize()/1024+"mb");
            System.out.println(" > Last push: " + repo.getPushedAt());

            return repo;
        } catch (IOException e) {
            e.printStackTrace();
            try{
                System.out.println("Failed to get repository '"+YmlConfig.github_releases_repo+"'!");
                Thread.sleep(10000);
                System.exit(0);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            return null;
        }

    }

    /**
     * Deletes all files in the upload directory of this repository
     * @param repo GitHub repository
     */
    private void deleteOld(GHRepository repo){

        try {
            System.out.println(" ");
            System.out.println("Deleting old files in "+YmlConfig.github_upload_dir+"...");
            List<GHContent> dir_content = repo.getDirectoryContent("" + YmlConfig.github_upload_dir);
            for (GHContent single_content :
                    dir_content) {
                single_content.delete("Deleted file because of update!");
            }
            System.out.println("Success!");
        } catch (Exception e) {
            System.out.println("Failed to delete old files! No files found to delete!");
        }

    }



}
