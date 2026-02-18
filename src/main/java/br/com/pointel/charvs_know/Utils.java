package br.com.pointel.charvs_know;

import java.io.File;

import br.com.pointel.jarch.mage.WizString;
import br.com.pointel.jarch.mage.WizText;

public class Utils {

    public static void putMarkDownLink(File markDown, String link) throws Exception {
        if (link == null || link.isBlank()) {
            return;
        }
        if (!markDown.exists()) {
            var folder = markDown.getParentFile();
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new Exception("Failed to create folder: " + folder.getAbsolutePath());
                }
            }
            if (!markDown.createNewFile()) {
                throw new Exception("Failed to create file: " + markDown.getAbsolutePath());
            }
        }
        var source = WizText.read(markDown);
        link = putBrackets(link);
        if (source.contains(link)) {
            return;
        }
        source = source.trim();
        if (!source.isEmpty()) {
            source = source + "\n\n";
        }
        source = source + link;
        WizText.write(markDown, source);
    }

    public static void delMarkDownLink(File markDown, String link) throws Exception {
        if (link == null || link.isBlank()) {
            return;
        }
        if (!markDown.exists()) {
            return;
        }
        var source = WizText.read(markDown);
        link = putBrackets(link);
        if (!source.contains(link)) {
            return;
        }
        source = source.replace(link, "");
        source = source.trim();
        while (source.contains("\n\n\n")) {
            source = source.replace("\n\n\n", "\n\n");
        }
        WizText.write(markDown, source);
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
