package br.com.pointel.charvs_know;

import java.io.Serializable;

public class Replace implements Serializable {

    public String name;
    public Boolean regex;
    public String of;
    public String to;

    public Replace() {
        this.name = "";
        this.regex = false;
        this.of = "";
        this.to = "";
    }

    public Replace(String name, Boolean regex, String of, String to) {
        this.name = name;
        this.regex = regex;
        this.of = of;
        this.to = to;
    }

    public String apply(String text) {
        if (Boolean.TRUE.equals(regex)) {
            return text.replaceAll(of, to);
        } else {
            return text.replace(of, to);
        }
    }

    @Override
    public String toString() {
        return "( " + name + " - " + (Boolean.TRUE.equals(regex) ? "Regex )" : "Literal )") + " | " + of + " -> " + to;
    }

}
