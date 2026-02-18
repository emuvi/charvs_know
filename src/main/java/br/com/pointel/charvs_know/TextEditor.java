package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;

import br.com.pointel.jarch.desk.DBordPane;
import br.com.pointel.jarch.desk.DButton;
import br.com.pointel.jarch.desk.DLinePane;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DScroll;
import br.com.pointel.jarch.desk.DText;

public class TextEditor extends DBordPane {

    private final DText textEdit = new DText();
    private final DScroll scrollPane = new DScroll(textEdit);

    private final DButton buttonReplaces = new DButton("#")
            .onClick(this::buttonReplacesActionPerformed);
    private final DButton buttonGroovy = new DButton("$")
            .onClick(this::buttonGroovyActionPerformed);
    private final DPane paneActions = new DLinePane()
            .put(buttonReplaces)
            .put(buttonGroovy);

    public TextEditor() {
        putCenter(scrollPane);
        putSouth(paneActions);
    }

    public TextEditor addButton(DButton button) {
        paneActions.add(button);
        return this;
    }

    public DText edit() {
        return textEdit;
    }

    public String getValue() {
        return textEdit.value();
    }

    public void setValue(String text) {
        textEdit.value(text);
    }

    public void buttonReplacesActionPerformed(ActionEvent e) {
        new ReplacesDesk(this).setVisible(true);
    }

    public void buttonGroovyActionPerformed(ActionEvent e) {
        
    }

}
