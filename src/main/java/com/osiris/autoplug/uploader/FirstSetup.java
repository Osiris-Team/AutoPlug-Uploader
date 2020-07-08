/*
 * Copyright (c) 2020 [Osiris Team](https://github.com/Osiris-Team)
 *  All rights reserved.
 *
 *  This software is copyrighted work licensed under the terms of the
 *  AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.uploader;

import java.util.Scanner;

public class FirstSetup {


    public FirstSetup(){

        Scanner scanner = new Scanner(System.in);

        boolean setup = true;
        while (setup){
            System.out.println(" ");
            System.out.println("Do you want to go through the first run setup?");
            System.out.println("Otherwise the config.yml will be created with placeholder data and");
            System.out.println("you will have to fill it out and restart AutoPlug-Uploader.");
            System.out.println("Awnser with y/n:");
            String response = scanner.nextLine();

            if (response.equals("y")){

                System.out.println(" ");
                System.out.println("Enter your GitHub-OAuth key or personal access token:");
                YmlConfig.setOAuthKey(scanner.nextLine());

                System.out.println(" ");
                System.out.println("Enter your releases repository (example: Author/Repository):");
                YmlConfig.setReleasesRepo(scanner.nextLine());

                System.out.println(" ");
                System.out.println("Enter your branch (example: master):");
                YmlConfig.setBranch(scanner.nextLine());

                System.out.println(" ");
                System.out.println("Enter your uploads directory name (example: beta-builds):");
                YmlConfig.setUploadDir(scanner.nextLine());

                boolean done = false;
                while (!done){
                    System.out.println(" ");
                    System.out.println("Add a file to upload, by entering its full name(example: FileToUpload.jar ; You can add multiple files. Enter 'done' when you are done):");

                    String line = scanner.nextLine();
                    if (line.equals("done")){
                        done=true;
                    } else{
                        YmlConfig.addRelease(line);
                    }
                }//End of 2nd while loop

                System.out.println(" ");
                System.out.println("Enter the users download directory name:");
                YmlConfig.setUserDownloadsDir(scanner.nextLine());
                setup=false;
            }
            else if (response.equals("n")){
                System.out.println("Skipping setup...");
                setup=false;
            }
            else{
                System.out.println("Not a valid input! Valid inputs are: 'y'(yes) and 'n'(no)");
            }//End of if
        }//End of 1st while loop
    }


}
