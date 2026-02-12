package br.com.pointel.charvs_know;

import java.io.File;
import java.util.ArrayList;

import br.com.pointel.jarch.mage.WizObject;
import br.com.pointel.jarch.mage.WizProps;
import br.com.pointel.jarch.mage.WizString;

public class Setup {

    public static final String KEY_PERFIL = "FRAME_CHARVS_COMP_PERFIL";
    
    public static String getPerfil() {
        return WizProps.get(KEY_PERFIL, "");
    }

    public static void setPerfil(String perfil) {
        WizProps.set(KEY_PERFIL, perfil);
    }

    public static String getPerfilParameter() {
        var perfil = getPerfil();
        if (perfil.isEmpty()) {
            return "";
        }
        return "_" + WizString.getParameterName(getPerfil());
    }

    public static final String KEY_INPUT_FOLDER = "FRAME_CHARVS_COMP_INPUT_FOLDER";

    public static String getInputFolder() {
        return WizProps.get(KEY_INPUT_FOLDER + getPerfilParameter(), "");
    }

    public static void setInputFolder(String inputFolder) {
        WizProps.set(KEY_INPUT_FOLDER + getPerfilParameter(), inputFolder);
    }
    
    public static final String KEY_OUTPUT_FOLDER = "FRAME_CHARVS_COMP_OUTPUT_FOLDER";

    public static String getOutputFolder() {
        return WizProps.get(KEY_OUTPUT_FOLDER + getPerfilParameter(), "");
    }

    public static void setOutputFolder(String outputFolder) {
        WizProps.set(KEY_OUTPUT_FOLDER + getPerfilParameter(), outputFolder);
    }

    public static final String KEY_OUTPUT_NAME = "FRAME_CHARVS_COMP_OUTPUT_NAME";

    public static String getOutputName() {
        return WizProps.get(KEY_OUTPUT_NAME + getPerfilParameter(), "");
    }

    public static void setOutputName(String outputName) {
        WizProps.set(KEY_OUTPUT_NAME + getPerfilParameter(), outputName);
    }

    public static final String KEY_RECORD_FILE = "FRAME_CHARVS_COMP_RECORD_FILE";

    public static String getRecordFile() {
        return WizProps.get(KEY_RECORD_FILE + getPerfilParameter(), "");
    }

    public static void setRecordFile(String recordFile) {
        WizProps.set(KEY_RECORD_FILE + getPerfilParameter(), recordFile);
    }
    
    public static final String KEY_RECORD_MAKE = "FRAME_CHARVS_COMP_RECORD_MAKE";

    public static Boolean getRecordMake() {
        return WizProps.get(KEY_RECORD_MAKE + getPerfilParameter(), false);
    }

    public static void setRecordMake(Boolean recordMake) {
        WizProps.set(KEY_RECORD_MAKE + getPerfilParameter(), recordMake);
    }
    
    public static final String KEY_ARCHIVE_FOLDER = "FRAME_CHARVS_COMP_ARCHIVE_FOLDER";

    public static String getArchiveFolder() {
        return WizProps.get(KEY_ARCHIVE_FOLDER + getPerfilParameter(), "");
    }

    public static void setArchiveFolder(String archiveFolder) {
        WizProps.set(KEY_ARCHIVE_FOLDER + getPerfilParameter(), archiveFolder);
    }

    public static final String KEY_ARCHIVE_MAKE = "FRAME_CHARVS_COMP_ARCHIVE_MAKE";

    public static Boolean getArchiveMake() {
        return WizProps.get(KEY_ARCHIVE_MAKE + getPerfilParameter(), false);
    }

    public static void setArchiveMake(Boolean archiveMake) {
        WizProps.set(KEY_ARCHIVE_MAKE + getPerfilParameter(), archiveMake);
    }

    public static final String KEY_ON_NEW_CLIPBOARD_TEXT = "FRAME_SETUP_COMP_ON_CLIPBOARD_NEW_TEXT";
    
    public static OnNewClipboardText getOnNewClipboardText() {
        return OnNewClipboardText.values()[WizProps.get(KEY_ON_NEW_CLIPBOARD_TEXT + getPerfilParameter(), 0)];
    }

    public static void setOnNewClipboardText(OnNewClipboardText onNewClipboardText) {
        WizProps.set(KEY_ON_NEW_CLIPBOARD_TEXT + getPerfilParameter(), onNewClipboardText.ordinal());
    }

    public static final String KEY_ON_NAMING = "FRAME_SETUP_COMP_ON_NAMING";
    
    public static OnNaming getOnNaming() {
        return OnNaming.values()[WizProps.get(KEY_ON_NAMING + getPerfilParameter(), 0)];
    }

    public static void setOnNaming(OnNaming onNaming) {
        WizProps.set(KEY_ON_NAMING + getPerfilParameter(), onNaming.ordinal());
    }

    public static final String KEY_NAME_NUMBERED_PREFIX = "FRAME_SETUP_COMP_NAME_NUMBERED_PREFIX";
    
    public static String getNameNumberedPrefix() {
        return WizProps.get(KEY_NAME_NUMBERED_PREFIX + getPerfilParameter(), "");
    }

    public static void setNameNumberedPrefix(String nameNumberedPrefix) {
        WizProps.set(KEY_NAME_NUMBERED_PREFIX + getPerfilParameter(), nameNumberedPrefix);
    }

    public static final String KEY_NAME_NUMBERED_SIZE = "FRAME_SETUP_COMP_NAME_NUMBERED_SIZE";
    
    public static Integer getNameNumberedSize() {
        return WizProps.get(KEY_NAME_NUMBERED_SIZE + getPerfilParameter(), 0);
    }

    public static void setNameNumberedSize(Integer nameNumberedSize) {
        WizProps.set(KEY_NAME_NUMBERED_SIZE + getPerfilParameter(), nameNumberedSize);
    }

    public static final String KEY_NAME_NUMBERED_SUFFIX = "FRAME_SETUP_COMP_NAME_NUMBERED_SUFFIX";
    
    public static String getNameNumberedSuffix() {
        return WizProps.get(KEY_NAME_NUMBERED_SUFFIX + getPerfilParameter(), "");
    }

    public static void setNameNumberedSuffix(String nameNumberedSuffix) {
        WizProps.set(KEY_NAME_NUMBERED_SUFFIX + getPerfilParameter(), nameNumberedSuffix);
    }

    public static final String KEY_NAME_EXTENSION = "FRAME_SETUP_COMP_NAME_EXTENSION";
    
    public static String getNameExtension() {
        return WizString.getFirstNonEmpty(WizProps.get(KEY_NAME_EXTENSION + getPerfilParameter(), ""), ".txt");
    }

    public static void setNameExtension(String nameExtension) {
        WizProps.set(KEY_NAME_EXTENSION + getPerfilParameter(), nameExtension);
    }

    public static final String KEY_STRIP_FIRST_LINES = "FRAME_SETUP_COMP_STRIP_FIRST_LINES";
    
    public static Integer getStripFirstLines() {
        return WizProps.get(KEY_STRIP_FIRST_LINES + getPerfilParameter(), 0);
    }

    public static void setStripFirstLines(Integer stripFirstLines) {
        WizProps.set(KEY_STRIP_FIRST_LINES + getPerfilParameter(), stripFirstLines);
    }

    public static final String KEY_INSERT_AT_BEGIN = "FRAME_SETUP_COMP_INSERT_AT_BEGIN";
    
    public static String getInsertAtBegin() {
        return WizProps.get(KEY_INSERT_AT_BEGIN + getPerfilParameter(), "");
    }

    public static void setInsertAtBegin(String insertAtBegin) {
        WizProps.set(KEY_INSERT_AT_BEGIN + getPerfilParameter(), insertAtBegin);
    }

    public static final String KEY_INSERT_AT_END = "FRAME_SETUP_COMP_INSERT_AT_END";
    
    public static String getInsertAtEnd() {
        return WizProps.get(KEY_INSERT_AT_END + getPerfilParameter(), "");
    }

    public static void setInsertAtEnd(String insertAtEnd) {
        WizProps.set(KEY_INSERT_AT_END + getPerfilParameter(), insertAtEnd);
    }

    public static final String KEY_APPLY_REPLACES_LIST = "FRAME_SETUP_COMP_APPLY_REPLACES_LIST";
    
    public static Boolean getApplyReplacesList() {
        return WizProps.get(KEY_APPLY_REPLACES_LIST + getPerfilParameter(), false);
    }

    public static void setApplyReplacesList(Boolean applyReplacesList) {
        WizProps.set(KEY_APPLY_REPLACES_LIST + getPerfilParameter(), applyReplacesList);
    }

    public static final String KEY_REPLACE_VARS_HOLDERS = "FRAME_SETUP_COMP_REPLACE_VARS_HOLDERS";
    
    public static Boolean getReplaceVarsHolders() {
        return WizProps.get(KEY_REPLACE_VARS_HOLDERS + getPerfilParameter(), false);
    }

    public static void setReplaceVarsHolders(Boolean replaceVarsHolders) {
        WizProps.set(KEY_REPLACE_VARS_HOLDERS + getPerfilParameter(), replaceVarsHolders);
    }

    public static final String KEY_TRIM_FINAL_TEXT = "FRAME_SETUP_COMP_TRIM_FINAL_TEXT";
    
    public static Boolean getTrimFinalText() {
        return WizProps.get(KEY_TRIM_FINAL_TEXT + getPerfilParameter(), false);
    }

    public static void setTrimFinalText(Boolean trimFinalText) {
        WizProps.set(KEY_TRIM_FINAL_TEXT + getPerfilParameter(), trimFinalText);
    }

    public static final String KEY_SAVE_MULTIPLE_DIVIDER = "FRAME_SETUP_COMP_SAVE_MULTIPLE_DIVIDER";
    
    public static String getSaveMultipleDivider() {
        return WizProps.get(KEY_SAVE_MULTIPLE_DIVIDER + getPerfilParameter(), "");
    }

    public static void setSaveMultipleDivider(String saveMultipleDivider) {
        WizProps.set(KEY_SAVE_MULTIPLE_DIVIDER + getPerfilParameter(), saveMultipleDivider);
    }

    public static final String KEY_MULTIPLE_MINIMUM_SIZE = "FRAME_SETUP_COMP_MULTIPLE_MINIMUM_SIZE";
    
    public static Integer getMultipleMinimumSize() {
        return WizProps.get(KEY_MULTIPLE_MINIMUM_SIZE + getPerfilParameter(), 0);
    }

    public static void setMultipleMinimumSize(Integer multipleMinimumSize) {
        WizProps.set(KEY_MULTIPLE_MINIMUM_SIZE + getPerfilParameter(), multipleMinimumSize);
    }
    
    public static final String KEY_ON_SAVE_EXISTS = "FRAME_SETUP_COMP_ON_SAVE_EXISTS";
    
    public static OnSaveExists getOnSaveExists() {
        return OnSaveExists.values()[WizProps.get(KEY_ON_SAVE_EXISTS + getPerfilParameter(), 0)];
    }

    public static void setOnSaveExists(OnSaveExists onSaveExists) {
        WizProps.set(KEY_ON_SAVE_EXISTS + getPerfilParameter(), onSaveExists.ordinal());
    }
    
    public static final String KEY_ON_RECORD = "FRAME_SETUP_COMP_ON_RECORD";
    
    public static OnRecord getOnRecord() {
        return OnRecord.values()[WizProps.get(KEY_ON_RECORD + getPerfilParameter(), 0)];
    }

    public static void setOnRecord(OnRecord onRecord) {
        WizProps.set(KEY_ON_RECORD + getPerfilParameter(), onRecord.ordinal());
    }

    public static final String KEY_RECORD_PREFIX = "FRAME_SETUP_COMP_RECORD_PREFIX";
    
    public static String getRecordPrefix() {
        return WizProps.get(KEY_RECORD_PREFIX + getPerfilParameter(), "");
    }

    public static void setRecordPrefix(String recordPrefix) {
        WizProps.set(KEY_RECORD_PREFIX + getPerfilParameter(), recordPrefix);
    }

    public static final String KEY_RECORD_SUFFIX = "FRAME_SETUP_COMP_RECORD_SUFFIX";
    
    public static String getRecordSuffix() {
        return WizProps.get(KEY_RECORD_SUFFIX + getPerfilParameter(), "");
    }

    public static void setRecordSuffix(String recordSuffix) {
        WizProps.set(KEY_RECORD_SUFFIX + getPerfilParameter(), recordSuffix);
    }

    public static final String KEY_ON_RECORD_EXISTS = "FRAME_SETUP_COMP_ON_RECORD_EXISTS";
    
    public static OnRecordExists getOnRecordExists() {
        return OnRecordExists.values()[WizProps.get(KEY_ON_RECORD_EXISTS + getPerfilParameter(), 0)];
    }

    public static void setOnRecordExists(OnRecordExists onRecordExists) {
        WizProps.set(KEY_ON_RECORD_EXISTS + getPerfilParameter(), onRecordExists.ordinal());
    }

    public static File getReplacesListFile() {
        return new File("replaces" + getPerfilParameter().toLowerCase() + ".ser");
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
