package br.com.pointel.charvs_know;

import java.io.File;

public class RefGroup {

    public String order = "";
    public String classification = "";
    public String titration = "";
    public String topics = "";
    public String cardsAt = "";
    public String questsAt = "";
    public String textsAt = "";

    public RefGroup() {}

    public boolean isPresent() {
        return topics != null && !topics.isBlank();
    }

    public void clearIdentified() {
        order = "";
        classification = "";
        titration = "";
        topics = "";
    }

    public void clearOrganized() {
        order = "";
        classification = "";
        titration = "";
    }

    public void clearClassified() {
        order = "";
        classification = "";
    }

    public void writeClassification(File onBaseFolder) throws Exception {
        if (classification == null || classification.isBlank()) {
            return;
        }
        File actualFolder = null;
        var hierarchy = classification.split("\\-");
        for (int i = 0; i < hierarchy.length; i++) {
            var level = hierarchy[i].trim();
            if (level.isBlank()) {
                continue;
            }
            level = "- " + level;
            File markDownFile;
            if (actualFolder == null) {
                actualFolder = onBaseFolder;
                markDownFile = new File(actualFolder, "index.md");
            } else {
                markDownFile = new File(actualFolder, actualFolder.getName() + ".md");
            }
            Utils.putMarkDownLink(markDownFile, level);
            actualFolder = new File(actualFolder, level);
        }
        if (actualFolder != null) {
            if (!actualFolder.exists()) {
                actualFolder.mkdirs();
            }
            var classificationFile = new File(actualFolder, actualFolder.getName() + ".md");
            if (!classificationFile.exists()) {
                classificationFile.createNewFile();
            }
        }
    }

    public File getClassificationFolder(File onBaseFolder) throws Exception {
        if (classification == null || classification.isBlank()) {
            return null;
        }
        File actualFolder = onBaseFolder;
        var hierarchy = classification.split("\\-");
        for (int i = 0; i < hierarchy.length; i++) {
            var level = hierarchy[i].trim();
            if (level.isBlank()) {
                continue;
            }
            level = "- " + level;
            actualFolder = new File(actualFolder, level);
        }
        if (!actualFolder.exists()) {
            actualFolder.mkdirs();
        }
        return actualFolder;
    }

    public File getClassificationFile(File onBaseFolder) throws Exception {
        if (classification == null || classification.isBlank()) {
            return null;
        }
        File actualFolder = onBaseFolder;
        var hierarchy = classification.split("\\-");
        for (int i = 0; i < hierarchy.length; i++) {
            var level = hierarchy[i].trim();
            if (level.isBlank()) {
                continue;
            }
            level = "- " + level;
            actualFolder = new File(actualFolder, level);
        }
        if (!actualFolder.exists()) {
            actualFolder.mkdirs();
        }
        var file = new File(actualFolder, actualFolder.getName() + ".md");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

}
