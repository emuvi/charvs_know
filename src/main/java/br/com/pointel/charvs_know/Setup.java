package br.com.pointel.charvs_know;

import java.io.File;
import java.util.ArrayList;

import br.com.pointel.jarch.mage.WizObject;
import br.com.pointel.jarch.mage.WizProps;

public class Setup {

    private static final String KEY_BASE = "FRAME_CHARVS_KNOW_COMP_BASE";
    
    public static String getBase() {
        return WizProps.get(KEY_BASE, "");
    }

    public static void setBase(String base) {
        WizProps.set(KEY_BASE, base);
    }

    private static final String KEY_LAST_SELECTED_REF01 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF01";
    private static final String KEY_LAST_SELECTED_REF02 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF02";
    private static final String KEY_LAST_SELECTED_REF03 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF03";
    private static final String KEY_LAST_SELECTED_REF04 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF04";
    private static final String KEY_LAST_SELECTED_REF05 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF05";
    private static final String KEY_LAST_SELECTED_REF06 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF06";
    private static final String KEY_LAST_SELECTED_REF07 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF07";
    private static final String KEY_LAST_SELECTED_REF08 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF08";
    private static final String KEY_LAST_SELECTED_REF09 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF09";
    private static final String KEY_LAST_SELECTED_REF10 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF10";
    private static final String KEY_LAST_SELECTED_REF11 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF11";
    private static final String KEY_LAST_SELECTED_REF12 = "FRAME_CHARVS_KNOW_LAST_SELECTED_REF12";
    private static final String[] KEYS_LAST_SELECTED_REFS = new String[] {
            KEY_LAST_SELECTED_REF01,
            KEY_LAST_SELECTED_REF02,
            KEY_LAST_SELECTED_REF03,
            KEY_LAST_SELECTED_REF04,
            KEY_LAST_SELECTED_REF05,
            KEY_LAST_SELECTED_REF06,
            KEY_LAST_SELECTED_REF07,
            KEY_LAST_SELECTED_REF08,
            KEY_LAST_SELECTED_REF09,
            KEY_LAST_SELECTED_REF10,
            KEY_LAST_SELECTED_REF11,
            KEY_LAST_SELECTED_REF12
    };

    public static String[] getLastSelectedRefs() {
        var result = new String[KEYS_LAST_SELECTED_REFS.length];
        for (int i = 0; i < KEYS_LAST_SELECTED_REFS.length; i++) {
            result[i] = WizProps.get(KEYS_LAST_SELECTED_REFS[i], "");
        }
        return result;
    }

    public static void putSelectedRef(String refWithExtension) {
        int foundIndex = -1;
        for (int i = 0; i < KEYS_LAST_SELECTED_REFS.length; i++) {
            if (WizProps.get(KEYS_LAST_SELECTED_REFS[i], "").equals(refWithExtension)) {
                foundIndex = i;
                break;
            }
        }
        if (foundIndex == 0) {
            return;
        }
        int limit = (foundIndex == -1) ? KEYS_LAST_SELECTED_REFS.length - 1 : foundIndex;
        for (int i = limit; i > 0; i--) {
            var previous = WizProps.get(KEYS_LAST_SELECTED_REFS[i - 1], "");
            WizProps.set(KEYS_LAST_SELECTED_REFS[i], previous);
        }
        WizProps.set(KEYS_LAST_SELECTED_REFS[0], refWithExtension);
    }

    public static final String KEY_GENAI_MODEL = "FRAME_SETUP_COMP_GENAI_MODEL";
    
    public static GenaiModel getGenaiModel() {
        return GenaiModel.values()[WizProps.get(KEY_GENAI_MODEL, 0)];
    }

    public static void setGenaiModel(GenaiModel genaiModel) {
        WizProps.set(KEY_GENAI_MODEL, genaiModel.ordinal());
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
