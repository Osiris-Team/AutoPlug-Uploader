/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class UpdateJson {

    /**
     * Creates a update.json file inside the provided directory. <br>
     *
     * @param repoDetail     contains details about the GitHub repository.
     * @param propertiesFile The full file name of the .properties file, located inside {@code jar}.
     *                       Example: {@code "update.properties"}.
     * @param jar            path to the jar, which contains the properties file.
     * @param dir            path to the directory in which the update.json file should be created.
     * @return the generated update.json as {@link JsonObject}.
     */
    public JsonArray create(RepoDetail repoDetail, String propertiesFile, RFile jar, File dir) throws Exception {
        Properties p = getProperties(propertiesFile, jar.fileTarget);
        String id = p.getProperty("id");
        String installationPath = p.getProperty("installation-path");
        String version = p.getProperty("version");

        // Get the download url
        final String downloadUrl;
        if (jar.target.startsWith("/"))
            downloadUrl = "https://github.com/" + repoDetail.name + "/raw/" + repoDetail.branch + jar.target;
        else
            downloadUrl = "https://github.com/" + repoDetail.name + "/raw/" + repoDetail.branch + "/" + jar.target;

        // Create this files checksum
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(FileUtils.readFileToByteArray(jar.fileTarget));
        final String sha256 = bytesToHex(encodedHash); // hash result

        long fileSize = jar.fileTarget.length(); // in bytes
        String mainClass = null;
        try {
            mainClass = p.getProperty("main-class");
        } catch (Exception e) {
            System.err.println("Failed to retrieve main-class information (" + e.getMessage() + "). Setting null value.");
        }

        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);
        obj.addProperty("installation-path", installationPath);
        obj.addProperty("version", version);
        obj.addProperty("download-url", downloadUrl);
        obj.addProperty("sha-256", sha256);
        obj.addProperty("file-size", fileSize);
        obj.addProperty("main-class", mainClass);

        File file = new File(dir + "/update.json");
        file.createNewFile();
        JsonObject existingUpdateJson = null;
        JsonArray jsonElements = null;
        try{jsonElements = new Gson().fromJson(new FileReader(file), JsonArray.class);} catch (Exception e) {}
        if(jsonElements != null)
        for (JsonElement el : jsonElements){
                if(el.getAsJsonObject().get("id").getAsInt() == 0)
                    existingUpdateJson = el.getAsJsonObject();
            }
        if(existingUpdateJson == null) throw new Exception("Failed to find update json with id==0!");
        if (existingUpdateJson.get("id").getAsString().equals(id) &&
                existingUpdateJson.get("installation-path").getAsString().equals(installationPath) &&
                existingUpdateJson.get("version").getAsString().equals(version) &&
                existingUpdateJson.get("download-url").getAsString().equals(downloadUrl) &&
                existingUpdateJson.get("sha-256").getAsString().equals(sha256) &&
                existingUpdateJson.get("file-size").getAsLong() == fileSize &&
                existingUpdateJson.get("main-class").getAsString().equals(mainClass)) {
            System.err.println("No update.json generated since it already exists with the same content. "+file);
        } else
            Files.write(file.toPath(), new GsonBuilder().setPrettyPrinting().create().toJson(obj).getBytes(StandardCharsets.UTF_8));

        JsonArray arr = new JsonArray();
        arr.add(obj);
        return arr;
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * This creates an URLClassLoader so we can access the autoplug.properties file inside the jar and then returns the properties file.
     *
     * @param file The jars to get the properties from.
     * @return autoplug.properties
     * @throws Exception
     */
    private Properties getProperties(String propertiesName, File file) throws Exception {
        if (file.exists()) {
            Collection<URL> urls = new ArrayList<>();
            urls.add(file.toURI().toURL());
            URLClassLoader fileClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]));

            java.io.InputStream is = fileClassLoader.getResourceAsStream(propertiesName);
            java.util.Properties p = new java.util.Properties();
            p.load(is);
            return p;
        } else {
            throw new Exception("Couldn't find the file at: " + file.getAbsolutePath());
        }
    }
}
