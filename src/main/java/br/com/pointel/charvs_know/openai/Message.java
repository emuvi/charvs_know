package br.com.pointel.charvs_know.openai;

public class Message {

    public String role;
    public String content;

    public Message() {}

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }
    
}