package br.com.pointel.charvs_know;

import java.util.ArrayList;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.Part;

import br.com.pointel.jarch.mage.WizEnv;

public class TalkerGenai implements Talker, AutoCloseable {

    private final Client client = Client.builder().apiKey(WizEnv.get("CHARVS_KNOW_GENAI_API_KEY", "")).build();

    @Override
    public String talk(String command) {
        var content = Content.fromParts(Part.fromText(command));
        return sendMessage(content);
    }

    @Override
    public String talk(String command, UriMime... attachs) {
        var parts = new ArrayList<Part>();
        parts.add(Part.fromText(command));
        for (var attach : attachs) {
            parts.add(Part.fromUri(attach.fileUri, attach.mimeType.code()));
        }
        var content = Content.builder().role("user").parts(parts).build();
        return sendMessage(content);
    }

    private String sendMessage(Content content) {
        var response = client.models.generateContent(Setup.getGenaiModel().code(), content, null);
        return response.text();
    }

    @Override
    public void close() {
        client.close();
    }

}
