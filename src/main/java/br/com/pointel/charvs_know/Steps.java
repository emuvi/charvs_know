package br.com.pointel.charvs_know;

import java.io.File;
import java.nio.file.Files;

public enum Steps { 

    Upload(null, new ActUpload()), 
    Identify("S01 - Identify.txt", new ActIdentify()), 
    Organize("S02 - Organize.txt", new ActOrganize()), 
    Classify("S03 - Classify.txt", new ActClassify()),
    Atomize("S04 - Atomize.txt", new ActAtomize()), 
    Questify("S05 - Questify.txt", new ActQuestify()), 
    Explains("S06 - Explains.txt", new ActExplains()),
    Didactic("S07 - Didactic.txt", new ActDidactic()),
    DoneAtNow(null, new ActDoneAtNow()),
    RevisedAtNow(null, new ActRevisedAtNow());

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
