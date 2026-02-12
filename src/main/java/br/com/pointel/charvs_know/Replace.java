package br.com.pointel.charvs_know;

import java.io.Serializable;

public class Replace implements Serializable {

    public Boolean active;
    public Boolean regex;
    public String of;
    public String to;

    public Replace() {
        this.active = false;
        this.regex = false;
        this.of = "";
        this.to = "";
    }

    public Replace(Boolean active, Boolean regex, String of, String to) {
        this.active = active;
        this.regex = regex;
        this.of = of;
        this.to = to;
    }

    public String apply(String text) {
        if (!Boolean.TRUE.equals(active)) {
            return text;
        }
        if (Boolean.TRUE.equals(regex)) {
            return text.replaceAll(of, to);
        } else {
            return text.replace(of, to);
        }
    }

    @Override
    public String toString() {
        return (Boolean.TRUE.equals(active) ? "(X)" : "( )") + (Boolean.TRUE.equals(regex) ? "(R)" : "( )") + " | " + of + " -> " + to;
    }

}
