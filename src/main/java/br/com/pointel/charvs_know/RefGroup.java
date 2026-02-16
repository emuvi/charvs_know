package br.com.pointel.charvs_know;

public class RefGroup {

    public String classification = "";
    public String titration = "";
    public String topics = "";
    public String statusNotes = "";
    public String statusQuests = "";
    public String statusTexts = "";

    public RefGroup() {}

    public boolean isPresent() {
        return topics != null && !topics.isBlank();
    }

    public void clear() {
        classification = "";
        titration = "";
        topics = "";
    }

}
