package br.com.pointel.charvs_know;

import java.io.File;
import java.util.ArrayList;

import br.com.pointel.jarch.mage.WizObject;
import br.com.pointel.jarch.mage.WizProps;
import br.com.pointel.jarch.mage.WizString;

public class Setup {

    public static final String KEY_BASE = "FRAME_CHARVS_COMP_BASE";
    
    public static String getBase() {
        return WizProps.get(KEY_BASE, "");
    }

    public static void setBase(String perfil) {
        WizProps.set(KEY_BASE, perfil);
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
