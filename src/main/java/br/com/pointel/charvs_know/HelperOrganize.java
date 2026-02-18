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
import br.com.pointel.jarch.desk.DScroll;
import br.com.pointel.jarch.desk.DSplitter;
import br.com.pointel.jarch.desk.DText;
import br.com.pointel.jarch.mage.WizGUI;
import br.com.pointel.jarch.mage.WizUtilDate;

public class HelperOrganize extends DFrame {

    private final DButton buttonClear = new DButton("Clear")
            .onClick(this::buttonClearActionPerformed);
    private final DButton buttonAsk = new DButton("Ask")
            .onClick(this::buttonAskActionPerformed);
    private final DButton buttonParse = new DButton("Parse")
            .onClick(this::buttonParseActionPerformed);
    private final DButton buttonBring = new DButton("Bring")
            .onClick(this::buttonBringActionPerformed);
    
    private final DPane paneAskActs = new DRowPane().insets(2)
        .growNone().put(buttonClear)
        .growHorizontal().put(buttonAsk)
        .growNone().put(buttonParse)
        .growNone().put(buttonBring);

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

    private final TextEditor textTitration = new TextEditor();
    private final DText textTopics = new DText().editable(false);
    private final DScroll scrollTopics = new DScroll(textTopics);
    private final DSplitter splitterGroup = new DSplitter()
            .vertical().top(textTitration).bottom(scrollTopics)
            .divider(0.5f)
            .name("splitterGroup")
            .borderEmpty(7);

    private final DPane paneGroup = new DColPane().insets(2)
            .growHorizontal().put(paneGroupActs)
            .growBoth().put(splitterGroup);

    private final DSplitter splitterBody = new DSplitter()
            .horizontal().left(paneAsk).right(paneGroup)
            .divider(0.5f)
            .name("splitterBody")
            .borderEmpty(7);


    private final SelectedRef selectedRef;

    
    public HelperOrganize(SelectedRef selectedRef) {
        super("Helper Organize");
        this.selectedRef = selectedRef;
        body(splitterBody);
        comboGroup.clear();
        for (int i = 0; i < selectedRef.ref.groups.size(); i++) {
            comboGroup.add("Group " + String.format("%02d", i + 1));
        }
    }

    private void buttonClearActionPerformed(ActionEvent e) {
        for (var group : selectedRef.ref.groups) {
            group.clearOrganized();
        }
    }

    private void buttonAskActionPerformed(ActionEvent e) {
        if (buttonAsk.getText().equals("Asking...")) {
            return;
        }
        buttonAsk.setText("Asking...");
        new Thread("Asking Organize") {
            @Override
            public void run() {
                try {
                    var result = selectedRef.talkWithAttach(Steps.Organize.getCommand(getInsertion()));
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
            var text = textAsk.getText().trim();
            if (text.isBlank()) {
                return;
            }
            var index = 0;
            var start = text.indexOf("[[");
            while (start > -1) {
                if (index >= selectedRef.ref.groups.size()) {
                    break;
                }
                var end = text.indexOf("]]", start);
                if (end > -1) {
                    var titration = text.substring(start + 2, end).trim();
                    if (!titration.startsWith("+")) {
                        titration = "+ " + titration;
                    }
                    titration = Utils.cleanFileName(titration);
                    selectedRef.ref.groups.get(index).titration = "[[" + titration + "]]";
                    start = text.indexOf("[[", end);
                    index++;
                } else {
                    break;
                }
            }
            comboGroupActionPerformed(e);
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void buttonBringActionPerformed(ActionEvent e) {
        var builder = new StringBuilder();
        for (var group : selectedRef.ref.groups) {
            var titration = group.titration;
            if (titration == null || titration.isBlank()) {
                titration = "[[+ ]]";
            }
            titration = titration.trim();
            if (!titration.startsWith("[[")) {
                if (!titration.startsWith("+")) {
                    titration = "+ " + titration;
                }
                titration = "[[" + titration;
            }
            if (!titration.endsWith("]]")) {
                titration = titration + "]]";
            }
            builder.append(titration);
            builder.append("\n\n");
        }
        textAsk.setText(builder.toString());
    }

    private void buttonSetActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index > -1) {
            textTitration.setText(textAsk.edit().selectedText().trim());
            selectedRef.ref.groups.get(index).titration = textTitration.getText().trim();
        }
    }

    private void comboGroupActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index == -1 || index >= selectedRef.ref.groups.size()) {
            textTitration.setText("");
            textTopics.setValue("");
            return;
        }
        var group = selectedRef.ref.groups.get(index);
        var startTitration = textTitration.edit().selectionStart();
        var endTitration = textTitration.edit().selectionEnd();
        var startTopics = textTopics.selectionStart();
        var endTopics = textTopics.selectionEnd();
        textTitration.setText(group.titration);
        textTopics.setValue(group.topics);
        textTitration.edit().selectionStart(startTitration);
        textTitration.edit().selectionEnd(endTitration);
        textTopics.selectionStart(startTopics);
        textTopics.selectionEnd(endTopics);
    }

    private void buttonSaveActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index == -1) {
            return;
        }
        var group = selectedRef.ref.groups.get(index);
        group.titration = textTitration.getText().trim();
    }

    private void buttonWriteActionPerformed(ActionEvent e) {
        try {
            selectedRef.ref.props.organizedAt = WizUtilDate.formatDateMach(new Date());
            selectedRef.write();
            WizGUI.close(this);
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private String getInsertion() {
        var result = new StringBuilder();
        var first = true;
        for (var group : selectedRef.ref.groups) {
            if (first) {
                first = false;
            } else {
                result.append("\n\n---\n\n");
            }
            result.append(group.topics);
        }
        return result.toString();
    }

}
