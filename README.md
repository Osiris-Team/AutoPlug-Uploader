# AutoPlug-Uploader
A tool to simplify the updating with update4j

Installation:
1. Download the [AutoPlugUploader.jar](https://link) and place it into a new empty directory
2. Create a startup script that runs the program (java -jar AutoPlugUploader.jar)
3. Go through the setup and enter the needed details (Config explained here)
4. Done!

Workflow:
1. Starts up and checks if /upload dir exists (If not you will be prompted to go through the setup)
2. Reads the configuration yml file and extracts its values
3. Checks if all the files you want to upload (listed in the config) can be found at /upload
4. Creates the update configuration xml files (in /upload)
5. Uploads all the files located in /upload to your choosen GitHub repository
