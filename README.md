# AutoPlug-Uploader

### What it does?
It heavily simplifies using update4j to update your java program. The Uploader will create the needed configuration xml files and upload them together with your jar to github.

Installation:
 - (Optional) Create a new repository only for your release files
 - (Prerequisite) Create a personal access token with permissions to write/delete files

1. Download the [AutoPlugUploader.jar](https://link) and place it into a new empty directory
2. Create a startup script that runs the program (java -jar AutoPlugUploader.jar)
3. Go through the setup and enter the needed details (Config explained here)
4. Done!

App workflow:
1. Starts up and checks if /upload dir exists (If not you will be prompted to go through the setup)
2. Reads the configuration yml file and extracts its values
3. Checks if all the files you want to upload (listed in the config) can be found at /upload
4. Creates the update configuration xml files (in /upload)
5. Uploads all the files located in /upload to your choosen GitHub repository

Usage examples:
You have a new version of your java app ready? Copy the jar to /upload folder, run the Uploader and you are done! Just as simple as that. Now all your users will be able to update the new version.

## Example Configuration
```yml
autoplug-uploader-config:

  github:
    #Enter your personal access token or oauth token
    o-auth: enter_here
    
    #The repository where to upload the files
    releases-repo: Osiris-Team/AutoPlug-Releases
    
    #The branch
    branch: master
    
    #The directory where to upload your files.
    upload-dir: beta-builds
    
    #The files to upload. Full name with file-extension required.
    upload-files-names:
    - AutoPlug.jar
    - AutoPlugPlugin.jar
    - AutoPlugLauncher.jar
    
  user:
    #The download directory of your user. This is where the update gets downloaded to.
    downloads-dir: autoplug-system

```
