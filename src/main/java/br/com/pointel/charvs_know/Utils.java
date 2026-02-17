package br.com.pointel.charvs_know;

import br.com.pointel.jarch.mage.WizString;

public class Utils {

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
