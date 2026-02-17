package br.com.pointel.charvs_know;

public class RefGroup {

    public String classification = "";
    public String titration = "";
    public String topics = "";
    public String cardsAt = "";
    public String questsAt = "";
    public String textsAt = "";

    public RefGroup() {}

    public boolean isPresent() {
        return topics != null && !topics.isBlank();
    }

    public void clearIdentified() {
        classification = "";
        titration = "";
        topics = "";
    }

    public void clearOrganized() {
        classification = "";
        titration = "";
    }

    public void clearClassified() {
        classification = "";
    }

}
