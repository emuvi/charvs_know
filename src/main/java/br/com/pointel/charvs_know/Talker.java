package br.com.pointel.charvs_know;

public interface Talker {

    public static Talker get() {
        return new TalkerGenai();
    }

    public String talk(String command);

    public String talk(String command, UriMime... attachs);

}
