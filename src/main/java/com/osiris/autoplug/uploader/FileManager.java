/*
 * Copyright (c) 2020 [Osiris Team](https://github.com/Osiris-Team)
 *  All rights reserved.
 *
 *  This software is copyrighted work licensed under the terms of the
 *  AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    //Directories
    public static final File WORKING_DIR = new File(System.getProperty("user.dir"));
    public static final File upload = new File(WORKING_DIR+"/upload");
    public static List<File> xml_files = new ArrayList<>();

    private List<File> directories;

    private static int missing;
    private static int total;

    public void check(){

        missing=0;
        total=0;

        try {
            directories = Arrays.asList(
                    upload);
            checkFiles("dir", directories);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public boolean isFirstRun(){
        if (missing==total){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Checks if all files listed in the yml config are found in the /upload directory
     * @return
     */
    public boolean isFilesInDir(){
        for (String file :
                YmlConfig.github_upload_files_names) {

            if (!new File(upload+"/"+file).exists()){
                return false;
            }
        }
        return true;
    }

    /**
     * Checks the files and generates them if missing. Enter file_type dir to generate directories.
     * @param file_type
     * @param files
     * @throws IOException
     */
    private void checkFiles(String file_type, List<File> files) throws IOException {

        total++;

        //Iterate through all directories and create missing ones
        System.out.println("Checking "+file_type+"...");
        for (int i = 0; i < files.size(); i++) {

            if (!files.get(i).exists()) {
                System.out.println(" - Generating: " + files.get(i).getName());

                if (file_type.equals("dir")){
                    files.get(i).mkdirs();
                    missing++;
                } else{
                    files.get(i).createNewFile();
                    missing++;
                }

            }

        }
        System.out.println("All "+file_type+"s ok!");

    }

}
