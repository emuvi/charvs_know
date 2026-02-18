package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.SwingUtilities;

import br.com.pointel.jarch.desk.DButton;
import br.com.pointel.jarch.desk.DColPane;
import br.com.pointel.jarch.desk.DComboEdit;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.desk.DSplitter;
import br.com.pointel.jarch.mage.WizGUI;
import br.com.pointel.jarch.mage.WizUtilDate;

public class HelperIdentify extends DFrame {

    private final DButton buttonClear = new DButton("Clear")
            .onClick(this::buttonClearActionPerformed);
    private final DButton buttonAsk = new DButton("Ask")
            .onClick(this::buttonAskActionPerformed);
    private final DButton buttonParse = new DButton("Parse")
            .onClick(this::buttonParseActionPerformed);
    private final DButton buttonBring = new DButton("Bring")
            .onClick(this::buttonBringActionPerformed);
    private final DButton buttonAdd = new DButton("Add")
            .onClick(this::buttonAddActionPerformed);
    
    private final DPane paneAskActs = new DRowPane().insets(2)
        .growNone().put(buttonClear)
        .growHorizontal().put(buttonAsk)
        .growNone().put(buttonParse)
        .growNone().put(buttonBring)
        .growNone().put(buttonAdd);

    private final TextEditor textAsk = new TextEditor();
     
    private final DPane paneAsk = new DColPane().insets(2)
            .growHorizontal().put(paneAskActs)
            .growBoth().put(textAsk);

    private final DButton buttonSet = new DButton("Set")
            .onClick(this::buttonSetActionPerformed);
    private final DComboEdit<String> comboGroup = new DComboEdit<String>()
            .onClick(this::comboGroupActionPerformed);
    private final DButton buttonSave = new DButton("Save")
            .onClick(this::buttonSaveActionPerformed);
    private final DButton buttonWrite = new DButton("Write")
            .onClick(this::buttonWriteActionPerformed);
    private final DPane paneGroupActs = new DRowPane().insets(2)
            .growNone().put(buttonSet)
            .growHorizontal().put(comboGroup)
            .growNone().put(buttonSave)
            .growNone().put(buttonWrite);

    private final TextEditor textTopics = new TextEditor();

    private final DPane paneGroup = new DColPane().insets(2)
            .growHorizontal().put(paneGroupActs)
            .growBoth().put(textTopics);

    private final DSplitter splitterBody = new DSplitter()
            .horizontal().left(paneAsk).right(paneGroup)
            .divider(0.5f)
            .name("splitterBody")
            .borderEmpty(7);


    private final SelectedRef selectedRef;

    
    public HelperIdentify(SelectedRef selectedRef) {
        super("Helper Identify");
        this.selectedRef = selectedRef;
        body(splitterBody);
        comboGroup.clear();
        for (int i = 0; i < selectedRef.ref.groups.size(); i++) {
            comboGroup.add("Group " + String.format("%02d", i + 1));
        }
    }

    private void buttonClearActionPerformed(ActionEvent e) {
        selectedRef.ref.groups.clear();
        comboGroup.clear();
    }

    private void buttonAskActionPerformed(ActionEvent e) {
        if (buttonAsk.getText().equals("Asking...")) {
            return;
        }
        buttonAsk.setText("Asking...");
        new Thread("Identify Asking") {
            @Override
            public void run() {
                try {
                    var result = selectedRef.talkWithAttach(Steps.Identify.getCommand());
                    SwingUtilities.invokeLater(() -> {
                        textAsk.setText(result);
                        textAsk.edit().selectionStart(0);
                        textAsk.edit().selectionEnd(0);
                    });
                } catch (Exception ex) {
                    WizGUI.showError(ex);
                } finally {
                    SwingUtilities.invokeLater(() -> buttonAsk.setText("Ask"));
                }
            }
        }.start();
    }

    private void buttonParseActionPerformed(ActionEvent e) {
        try {
            var parts = textAsk.edit().getValue().split("\\-\\-\\-");
            if (parts.length == 0) {
                return;
            }
            selectedRef.ref.groups.clear();
            comboGroup.clear();
            for (int i = 0; i < parts.length; i++) {
                var group = new RefGroup();
                group.topics = parts[i].trim();
                selectedRef.ref.groups.add(group);
                comboGroup.add("Group " + String.format("%02d", i + 1));
            }
            comboGroup.selectedIndex(0);
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void buttonBringActionPerformed(ActionEvent e) {
        var builder = new StringBuilder();
        var first = true;
        for (var group : selectedRef.ref.groups) {
            if (first) {
                first = false;
            } else {
                builder.append("\n\n---\n\n");
            }
            builder.append(group.topics);
        }
        textAsk.setText(builder.toString());
    }

    private void buttonAddActionPerformed(ActionEvent e) {
        var group = new RefGroup();
        group.topics = textAsk.edit().selectedText().trim();
        selectedRef.ref.groups.add(group);
        comboGroup.add("Group " + String.format("%02d", selectedRef.ref.groups.size()));
        comboGroup.selectedIndex(selectedRef.ref.groups.size() - 1);
    }

    private void buttonSetActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index > -1) {
            textTopics.setText(textAsk.edit().selectedText().trim());
            selectedRef.ref.groups.get(index).topics = textTopics.getText().trim();
        }
    }

    private void comboGroupActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index == -1 || index >= selectedRef.ref.groups.size()) {
            textTopics.setText("");
            return;
        }
        var group = selectedRef.ref.groups.get(index);
        var start = textTopics.edit().selectionStart();
        var end = textTopics.edit().selectionEnd();
        textTopics.setText(group.topics);
        textTopics.edit().selectionStart(start);
        textTopics.edit().selectionEnd(end);
    }

    private void buttonSaveActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index == -1) {
            return;
        }
        var group = selectedRef.ref.groups.get(index);
        group.topics = textTopics.getText().trim();
    }

    private void buttonWriteActionPerformed(ActionEvent e) {
        try {
            selectedRef.ref.props.identifiedAt = WizUtilDate.formatDateMach(new Date());
            selectedRef.write();
            WizGUI.close(this);
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

}
