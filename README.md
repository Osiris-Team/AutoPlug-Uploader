# AutoPlug-Uploader
Upload updates to [AutoPlug-Releases](https://github.com/Osiris-Team/AutoPlug-Client) 
with an auto-generated update.json file easily.

### Why?
The update.json file contains extra necessary information like the installation path of
the jar or the main classes' path.

# Installation
- Download the [AutoPlug-Uploader.jar](AutoPlug-Uploader.jar) and put it into a empty directory.
- Create a config.yml in that directory and use the preset below.

## Example Configuration
```yml
# Insert your O_AUTH access token here.
# Token needs to have permissions to push/commit on the desired repos.
access-token: INSERT_HERE
repos:
  # Repository name: <author>/<repo>
  Osiris-Team/AutoPlug-Releases:
    branch: master
    replacement:
      - ./stable/AutoPlug-Client.jar # ./ is the current directory
      - ./beta/AutoPlug-Client.jar 
    to-replace:
      - /stable-builds/AutoPlug-Client.jar
      - /beta-builds/AutoPlug-Client.jar
```
