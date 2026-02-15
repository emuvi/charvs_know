package br.com.pointel.charvs_know;

import java.io.File;
import java.nio.file.Files;

public enum Steps { 

    Upload(null, new ActUpload()), 
    Identify("S01 - Identify.txt", null), 
    Classify("S02 - Classify.txt", null), 
    Organize("S03 - Organize.txt", null), 
    Atomize("S04 - Atomize.txt", null), 
    Questify("S05 - Questify.txt", null), 
    Explaine("S06 - Explaine.txt", null);

    private final String commandName;
    private final Act stepAct;

    private Steps(String commandName, Act stepAct) {
        this.commandName = commandName;
        this.stepAct = stepAct;
    }

    public String getCommandName() {
        return this.commandName;
    }

    public Act getAct() {
        return this.stepAct;
    }
    
    public File getCommandFile() {
        return new File(STEPFS_FOLDER, this.commandName);
    }

    public String getCommand() throws Exception {
        return Files.readString(getCommandFile().toPath());
    }

    public String getCommand(String withInsertion) throws Exception {
        return Files.readString(getCommandFile().toPath()).replace("< INSERT >", withInsertion);
    }

    public static final File STEPFS_FOLDER = new File("steps");

}
