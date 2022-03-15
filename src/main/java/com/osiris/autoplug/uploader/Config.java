/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import com.osiris.dyml.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config extends Yaml {
    public YamlSection accessToken;
    public YamlSection repos;

    public Config() throws NotLoadedException, IllegalKeyException, YamlReaderException, IOException, DuplicateKeyException, IllegalListException {
        super("config.yml");
        load();
        accessToken = put("access-token");
        repos = put("repos");
    }

    public List<RepoDetail> getRepos() throws Exception {
        List<RepoDetail> list = new ArrayList<>();
        for (YamlSection child :
                repos.getChildModules()) {
            RepoDetail repoDetail = new RepoDetail();
            list.add(repoDetail);
            repoDetail.name = child.getLastKey();
            repoDetail.branch = get("repos", repoDetail.name, "branch").asString();
            List<String> replacementList = get("repos", repoDetail.name, "replacement").asStringList();
            List<String> toReplaceList = get("repos", repoDetail.name, "to-replace").asStringList();
            repoDetail.localPath = new File(System.getProperty("user.dir") + "/" + repoDetail.name);
            if (toReplaceList.size() != replacementList.size())
                throw new Exception("replacement and to-replace lists must have the same length!");
            List<RFile> rFiles = new ArrayList<>();
            repoDetail.filesToReplace = rFiles;
            for (int i = 0; i < toReplaceList.size(); i++) {
                RFile rFile = new RFile();
                rFiles.add(rFile);

                rFile.source = replacementList.get(i);
                if (rFile.source.startsWith("./"))
                    rFile.fileSource = new File(rFile.source.replace("./", System.getProperty("user.dir") + "/"));
                else
                    rFile.fileSource = new File(rFile.source);

                rFile.target = toReplaceList.get(i);
                if (rFile.target.contains("/"))
                    rFile.fileTarget = new File(repoDetail.localPath + rFile.target);
                else
                    rFile.fileTarget = new File(repoDetail.localPath + "/" + rFile.target);
            }
        }
        return list;
    }

    public void setRepos(List<RepoDetail> repos) throws YamlReaderException, YamlWriterException, IOException, DuplicateKeyException, IllegalListException, NotLoadedException, IllegalKeyException {
        for (RepoDetail repoDetail :
                repos) {
            put("repos", repoDetail.name);
            put("repos", repoDetail.name, "branch").setValues(repoDetail.branch);
            for (RFile rFile :
                    repoDetail.filesToReplace) {
                put("repos", repoDetail.name, "replacement").setValues(rFile.source);
                put("repos", repoDetail.name, "to-replace").setValues(rFile.target);
            }
        }
        save();
    }

}
