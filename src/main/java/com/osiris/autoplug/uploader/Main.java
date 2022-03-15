/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

import com.google.gson.JsonObject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        Config config = new Config();
        if (config.accessToken.asString() == null) {
            System.err.println("No access token provided in config.yml. It's recommended to use the config preset from the example.");
            System.exit(-1);
        }
        if (config.getRepos().isEmpty()) {
            System.err.println("Repos list in config.yml is empty. It's recommended to use the config preset from the example.");
            System.exit(-1);
        }

        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(config.accessToken.asString(), "");
        for (RepoDetail repoDetail :
                config.getRepos()) {
            Git git = null;
            if (!repoDetail.localPath.exists()){
                System.out.println("Cloning repo "+repoDetail.name);
                git = Git.cloneRepository()
                        .setURI("https://github.com/" + repoDetail.name + ".git")
                        .setDirectory(repoDetail.localPath)
                        .setBranch("refs/heads/" + repoDetail.branch)
                        .setProgressMonitor(new SideBandProgressMonitor(System.out))
                        .call();
            }
            else {
                System.out.println("Pulling repo "+repoDetail.name);
                git = Git.open(repoDetail.localPath);
                git.pull()
                        .setProgressMonitor(new SideBandProgressMonitor(System.out))
                        .call();
            }
            System.out.println("Success!");

            for (RFile rFile : repoDetail.filesToReplace) {
                System.out.println("Replacing: "+rFile.fileTarget);
                System.out.println("with: "+rFile.fileSource);
                Files.copy(rFile.fileSource.toPath(), rFile.fileTarget.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JsonObject updateJson = new UpdateJson().create(repoDetail, "autoplug.properties", rFile, rFile.fileTarget.getParentFile());
                String msg = "Updated " + rFile.fileTarget.getName() + " to " + updateJson.get("version").getAsString() + ".";
                List<DiffEntry> diffEntries = git.diff().call();
                if(diffEntries == null || diffEntries.isEmpty())
                    System.err.println("No commit since unchanged file: "+rFile.fileTarget);
                else{
                    git.commit()
                            .setAll(true)
                            .setAuthor(new PersonIdent("AutoPlug-Uploader", "bot@github.com"))
                            .setMessage(msg)
                            .call();
                    System.out.println(msg);
                }
            }

            git.push()
                    .setProgressMonitor(new SideBandProgressMonitor(System.out))
                    .setCredentialsProvider(credentialsProvider)
                    .call();
        }
        System.out.println("Finished!");
    }

}
