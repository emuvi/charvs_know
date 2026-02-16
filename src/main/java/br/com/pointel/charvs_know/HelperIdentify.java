package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;

import javax.swing.SwingUtilities;

import br.com.pointel.jarch.desk.DBordPane;
import br.com.pointel.jarch.desk.DButton;
import br.com.pointel.jarch.desk.DComboEdit;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DSplitter;
import br.com.pointel.jarch.mage.WizGUI;

public class HelperIdentify extends DFrame {

    private final DButton buttonAsk = new DButton("Ask")
            .onClick(this::buttonAskActionPerformed);
    private final DButton buttonClear = new DButton("Clear")
            .onClick(this::buttonClearActionPerformed);
    private final DPane paneAskActs = new DBordPane()
            .putCenter(buttonAsk)
            .putEast(buttonClear);

    private final TextEditor textAsk = new TextEditor();
     
    private final DBordPane paneAsk = new DBordPane()
            .putNorth(paneAskActs)
            .putCenter(textAsk);

    private final DButton buttonSet = new DButton("Set")
            .onClick(this::buttonSetActionPerformed);
    private final DComboEdit<String> comboGroup = new DComboEdit<String>()
            .add("Group 01", "Group 02", "Group 03", "Group 04")
            .add("Group 05", "Group 06", "Group 07", "Group 08")
            .add("Group 09", "Group 10", "Group 11", "Group 12")
            .onClick(this::comboGroupActionPerformed);
    private final DButton buttonSave = new DButton("Save")
            .onClick(this::buttonSaveActionPerformed);
    private final DPane paneGroupActs = new DBordPane()
            .putWest(buttonSet)
            .putCenter(comboGroup)
            .putEast(buttonSave);

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
        selectedRef.ref.group01.clear();
        selectedRef.ref.group02.clear();
        selectedRef.ref.group03.clear();
        selectedRef.ref.group04.clear();
        selectedRef.ref.group05.clear();
        selectedRef.ref.group06.clear();
        selectedRef.ref.group07.clear();
        selectedRef.ref.group08.clear();
        selectedRef.ref.group09.clear();
        selectedRef.ref.group10.clear();
        selectedRef.ref.group11.clear();
        selectedRef.ref.group12.clear();
        comboGroupActionPerformed(e);
    }

    private void buttonSetActionPerformed(ActionEvent e) {
        textGroup.setText(textAsk.edit().selectedText().trim());
        buttonSaveActionPerformed(e);
    }

    private void comboGroupActionPerformed(ActionEvent e) {
        if (comboGroup.getValue().equals("Group 01")) {
            textGroup.setText(selectedRef.ref.group01.topics);
        } else if (comboGroup.getValue().equals("Group 02")) {
            textGroup.setText(selectedRef.ref.group02.topics);
        } else if (comboGroup.getValue().equals("Group 03")) {
            textGroup.setText(selectedRef.ref.group03.topics);
        } else if (comboGroup.getValue().equals("Group 04")) {
            textGroup.setText(selectedRef.ref.group04.topics);
        } else if (comboGroup.getValue().equals("Group 05")) {
            textGroup.setText(selectedRef.ref.group05.topics);
        } else if (comboGroup.getValue().equals("Group 06")) {
            textGroup.setText(selectedRef.ref.group06.topics);
        } else if (comboGroup.getValue().equals("Group 07")) {
            textGroup.setText(selectedRef.ref.group07.topics);
        } else if (comboGroup.getValue().equals("Group 08")) {
            textGroup.setText(selectedRef.ref.group08.topics);
        } else if (comboGroup.getValue().equals("Group 09")) {
            textGroup.setText(selectedRef.ref.group09.topics);
        } else if (comboGroup.getValue().equals("Group 10")) {
            textGroup.setText(selectedRef.ref.group10.topics);
        } else if (comboGroup.getValue().equals("Group 11")) {
            textGroup.setText(selectedRef.ref.group11.topics);
        } else if (comboGroup.getValue().equals("Group 12")) {
            textGroup.setText(selectedRef.ref.group12.topics);
        }
    }

    private void buttonSaveActionPerformed(ActionEvent e) {
        try {
            if (comboGroup.getValue().equals("Group 01")) {
                selectedRef.ref.group01.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 02")) {
                selectedRef.ref.group02.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 03")) {
                selectedRef.ref.group03.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 04")) {
                selectedRef.ref.group04.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 05")) {
                selectedRef.ref.group05.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 06")) {
                selectedRef.ref.group06.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 07")) {
                selectedRef.ref.group07.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 08")) {
                selectedRef.ref.group08.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 09")) {
                selectedRef.ref.group09.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 10")) {
                selectedRef.ref.group10.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 11")) {
                selectedRef.ref.group11.topics = textGroup.getText();
            } else if (comboGroup.getValue().equals("Group 12")) {
                selectedRef.ref.group12.topics = textGroup.getText();
            }
            selectedRef.write();
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

}
