package br.com.pointel.charvs_know;

import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import br.com.pointel.jarch.flow.MimeType;

public class Talker implements AutoCloseable {

    private final Client client = new Client();
    private final Chat chat = client.chats.create("gemini-3-pro-preview");

    public String talk(String command) {
        var response = chat.sendMessage(command);
        return response.text();
    }

    public String talk(String command, String uriFile, MimeType mimeType) {
        Content content = Content.fromParts(
                Part.fromText(command),
                Part.fromUri(uriFile, mimeType.asString()));
        var response = chat.sendMessage(content);
        return response.text();
    }

    @Override
    public void close() {
        client.close();
    }

}
