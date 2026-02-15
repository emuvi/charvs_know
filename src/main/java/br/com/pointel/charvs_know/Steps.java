package br.com.pointel.charvs_know;

import java.io.File;
import java.nio.file.Files;

public enum Steps { 

    Upload(null), 
    Identify("S01 - Identify.txt"), 
    Classify("S02 - Classify.txt"), 
    Organize("S03 - Organize.txt"), 
    Atomize("S04 - Atomize.txt"), 
    Questify("S05 - Questify.txt"), 
    Explaine("S06 - Explaine.txt");

    private final String commandName;

    private Steps(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return this.commandName;
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
