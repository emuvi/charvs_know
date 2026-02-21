package br.com.pointel.charvs_know;

public interface Talker {

    public static Talker get() {
        return  Setup.getTalkerKind() == SetupTalkerKind.Genai ? new TalkerGenai() : new TalkerClipboard();
    }

    public String talk(String command);

    public String talk(String command, UriMime... attachs);

}
