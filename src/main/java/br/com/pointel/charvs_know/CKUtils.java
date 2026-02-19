package br.com.pointel.charvs_know;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.pointel.jarch.mage.WizString;
import br.com.pointel.jarch.mage.WizText;
import br.com.pointel.jarch.mage.WizUtilDate;

public class CKUtils {

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
        }
        var classData = ClassDatex.read(classFile);
        link = delBrackets(link);
        if (link.startsWith("^")) {
            if (!classData.textsLinks.contains(link)) {
                classData.textsLinks.add(link);
            }
        } else {
            if (!classData.cardsLinks.contains(link)) {
                classData.cardsLinks.add(link);
            }
        }
        ClassDatex.write(classData, classFile);
    }

    public static void delMarkDownLink(File classFile, String link) throws Exception {
        if (link == null || link.isBlank()) {
            return;
        }
        if (!classFile.exists()) {
            return;
        }
        var classData = ClassDatex.read(classFile);
        link = delBrackets(link);
        var updated = false;
        if (link.startsWith("^")) {
            updated = classData.textsLinks.remove(link);
        } else {
            updated = classData.cardsLinks.remove(link);
        }
        if (updated) {
            ClassDatex.write(classData, classFile);
        }
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
            return link;
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
