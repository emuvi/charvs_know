package br.com.pointel.charvs_know;

import java.io.File;

import br.com.pointel.jarch.mage.WizString;
import br.com.pointel.jarch.mage.WizText;

public class Utils {

    public static void putMarkDownLink(File markDown, String link) throws Exception {
        if (link == null || link.isBlank()) {
            return;
        }
        if (!link.startsWith("[[")) {
            link = "[[" + link;
        }
        if (!link.endsWith("]]")) {
            link = link + "]]";
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

}
