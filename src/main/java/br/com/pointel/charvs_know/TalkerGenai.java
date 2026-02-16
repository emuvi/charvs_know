package br.com.pointel.charvs_know;

import java.util.ArrayList;

import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import br.com.pointel.jarch.mage.WizEnv;

public class TalkerGenai implements Talker, AutoCloseable {

    private final Client client = Client.builder().apiKey(WizEnv.get("CHARVS_KNOW_GENAI_API_KEY", "")).build();
    private final Chat chat = client.chats.create(Setup.getGenaiModel().code());

    @Override
    public String talk(String command) {
        var response = chat.sendMessage(command);
        return response.text();
    }

    @Override
    public String talk(String command, UriMime... attachs) {
        var parts = new ArrayList<Part>();
        parts.add(Part.fromText(command));
        for (var attach : attachs) {
            parts.add(Part.fromUri(attach.fileUri, attach.mimeType.code()));
        }
        var content = Content.builder().role("user").parts(parts).build();
        var response = chat.sendMessage(content);
        return response.text();
    }

    @Override
    public void close() {
        client.close();
    }

}
