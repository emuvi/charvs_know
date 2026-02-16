package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import br.com.pointel.jarch.desk.DBordPane;
import br.com.pointel.jarch.desk.DButton;
import br.com.pointel.jarch.desk.DComboEdit;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.desk.DSplitter;
import br.com.pointel.jarch.mage.WizGUI;

public class HelperIdentify extends DFrame {

    private final DButton buttonAsk = new DButton("Ask")
            .onClick(this::buttonAskActionPerformed);
    private final DButton buttonClear = new DButton("Clear")
            .onClick(this::buttonClearActionPerformed);
    private final DPane paneAskActs = new DRowPane().insets(2)
            .growHorizontal().put(buttonAsk)
            .growNone().put(buttonClear);

    private final TextEditor textAsk = new TextEditor();
     
    private final DBordPane paneAsk = new DBordPane()
            .putNorth(paneAskActs)
            .putCenter(textAsk);

    private final DButton buttonSet = new DButton("Set")
            .onClick(this::buttonSetActionPerformed);
    private final DComboEdit<String> comboGroup = new DComboEdit<String>()
            .onClick(this::comboGroupActionPerformed);
    private final DButton buttonSave = new DButton("Save")
            .onClick(this::buttonSaveActionPerformed);
    private final DPane paneGroupActs = new DRowPane().insets(2)
            .growNone().put(buttonSet)
            .growHorizontal().put(comboGroup)
            .growNone().put(buttonSave);

    private final TextEditor textGroup = new TextEditor();

    private final DBordPane paneGroup = new DBordPane()
            .putNorth(paneGroupActs)
            .putCenter(textGroup);

    private final DSplitter splitterBody = new DSplitter(paneAsk, paneGroup)
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
        onFirstOpened(e -> buttonAskActionPerformed(null));
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

    private void buttonClearActionPerformed(ActionEvent e) {
        for (var group : selectedRef.ref.groups) {
            group.clear();
        }
        comboGroupActionPerformed(e);
    }

    private void buttonSetActionPerformed(ActionEvent e) {
        textGroup.setText(textAsk.edit().selectedText().trim());
        buttonSaveActionPerformed(e);
    }

    private void comboGroupActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        var start = textGroup.edit().selectionStart();
        var end = textGroup.edit().selectionEnd();
        if (index == -1 || index >= selectedRef.ref.groups.size()) {
            textGroup.setText("");
        } else {
            textGroup.setText(selectedRef.ref.groups.get(index).topics);
        }
        textGroup.edit().selectionStart(start);
        textGroup.edit().selectionEnd(end);
    }

    private void buttonSaveActionPerformed(ActionEvent e) {
        try {
            var index = comboGroup.selectedIndex();
            if (index == -1) {
                return;
            }
            selectedRef.ref.groups.get(index).topics = textGroup.getText().trim();
            selectedRef.write();
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

}
