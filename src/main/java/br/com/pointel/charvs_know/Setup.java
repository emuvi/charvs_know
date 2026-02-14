package br.com.pointel.charvs_know;

import java.io.File;
import java.util.ArrayList;

import br.com.pointel.jarch.mage.WizObject;
import br.com.pointel.jarch.mage.WizProps;

public class Setup {

    public static final String KEY_BASE = "FRAME_CHARVS_COMP_BASE";
    
    public static String getBase() {
        return WizProps.get(KEY_BASE, "");
    }

    public static void setBase(String base) {
        WizProps.set(KEY_BASE, base);
    }

    public static final String KEY_WORKING = "FRAME_CHARVS_COMP_ACT_REFS";
    
    public static String getActRefs() {
        return WizProps.get(KEY_WORKING, "");
    }

    public static void setActRefs(String refs) {
        WizProps.set(KEY_WORKING, refs);
    }

    public static File getReplacesListFile() {
        return new File("replaces.ser");
    }

    public static ArrayList<Replace> readReplacesList() throws Exception {
        var file = getReplacesListFile();
        if (!file.exists()) {
            return new ArrayList<>();
        }
        return (ArrayList<Replace>) WizObject.read(file);
    }

    public static void writeReplacesList(ArrayList<Replace> replaces) throws Exception {
        WizObject.write(getReplacesListFile(), replaces);
    }
    
}
