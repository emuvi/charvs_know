package br.com.pointel.charvs_know;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.pointel.jarch.mage.WizString;
import br.com.pointel.jarch.mage.WizText;
import br.com.pointel.jarch.mage.WizUtilDate;

public class CKUtils {

    public static void createClassFile(File classFile) throws Exception  {
        var classProps = "---\n";
        classProps = classProps + "nature: class\n";
        classProps = classProps + "created-at: " + WizUtilDate.formatDateMach(new Date()) + "\n";
        classProps = classProps + "---\n";
        WizText.write(classFile, classProps);
    }

    public static void putMarkDownLink(File classFile, String link) throws Exception {
        if (link == null || link.isBlank()) {
            return;
        }
        if (!classFile.exists()) {
            var folder = classFile.getParentFile();
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new Exception("Failed to create class folder: " + folder.getAbsolutePath());
                }
            }
            createClassFile(classFile);
        }
        var source = WizText.read(classFile);
        link = putBrackets(link);
        if (source.contains(link)) {
            return;
        }
        source = source.trim();
        if (!source.isEmpty()) {
            source = source + "\n";
        }
        if (source.contains("[[")) {
            source = source + "\n";
        }
        source = source + link;
        WizText.write(classFile, source);
    }

    public static void delMarkDownLink(File classFile, String link) throws Exception {
        if (link == null || link.isBlank()) {
            return;
        }
        if (!classFile.exists()) {
            return;
        }
        var source = WizText.read(classFile);
        link = putBrackets(link);
        if (!source.contains(link)) {
            return;
        }
        source = source.replace(link, "");
        source = source.trim();
        while (source.contains("\n\n\n")) {
            source = source.replace("\n\n\n", "\n\n");
        }
        WizText.write(classFile, source);
    }

    public static List<String> getMarkDownLinks(File classFile) throws Exception {
        var result = new ArrayList<String>();
        if (!classFile.exists()) {
            return result;
        }
        var source = WizText.read(classFile);
        var start = source.indexOf("[[");
        while (start > -1) {
            var end = source.indexOf("]]", start);
            if (end > -1) {
                result.add(source.substring(start + 2, end));
                start = source.indexOf("[[", end);
            } else {
                break;
            }
        }
        return result;
    }

    public static String cleanFileName(String title) {
        title = title.trim();
        if (title.equals(title.toUpperCase())) {
            title = WizString.capitalizeWords(title.toLowerCase());
        }
        title = title
                .replace("\"", "”")
                .replace("'", "”")
                .replace("/", "-")
                .replace("|", "-")
                .replace("\\", "-")
                .replace("?", "")
                .replace("!", "")
                .replace("<", "")
                .replace(">", "")
                .replace("*", "")
                .replace("#", "")
                .replace(": ", " - ")
                .replace(":", ",")
                .replace(";", ",");
        return title.replaceAll("\\s+", " ").trim();
    }

    public static String putBrackets(String link) {
        if (link == null || link.isBlank()) {
            return "";
        }
        if (!link.startsWith("[[")) {
            link = "[[" + link;
        }
        if (!link.endsWith("]]")) {
            link = link + "]]";
        }
        return link;
    }

    public static String delBrackets(String link) {
        if (link == null || link.isBlank()) {
            return "";
        }
        if (link.startsWith("[[")) {
            link = link.substring(2);
        }
        if (link.endsWith("]]")) {
            link = link.substring(0, link.length() - 2);
        }
        return link;
    }

}
