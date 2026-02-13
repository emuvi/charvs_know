package br.com.pointel.charvs_know;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public enum Steps { 
    
    Identificar, Classificar, Organizar, Atomizar, Questionar, Explicar;

    public String getCommand() throws Exception {
        return Files.readString(list.get(this.ordinal()).toPath());
    }

    public String getCommand(String withInsertion) throws Exception {
        return Files.readString(list.get(this.ordinal()).toPath()).replace("< INSERT >", withInsertion);
    }

    private static final List<File> list = new ArrayList<>();

    static {
        var folder = new File("steps");
        if (folder.exists()) {
            var files = folder.listFiles();
            if (files != null) {
                for (var file : files) {
                    if (file.isFile()) {
                        list.add(file);
                    }
                }
                list.sort((a, b) -> a.getName().compareTo(b.getName()));
            }
        }
    }
}
