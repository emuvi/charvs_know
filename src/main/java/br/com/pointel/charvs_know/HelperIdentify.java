package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;

import br.com.pointel.jarch.desk.DBordPane;
import br.com.pointel.jarch.desk.DButton;
import br.com.pointel.jarch.desk.DComboEdit;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DScroll;
import br.com.pointel.jarch.desk.DSplitter;
import br.com.pointel.jarch.desk.DText;

public class HelperIdentify extends DFrame {

    private final DButton buttonAsk = new DButton("Ask")
            .onClick(this::buttonAskActionPerformed);
    private final DPane paneAskActs = new DBordPane()
            .putCenter(buttonAsk);

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
        
    }

    private void buttonSetActionPerformed(ActionEvent e) {
        
    }

    private void comboGroupActionPerformed(ActionEvent e) {
        
    }

    private void buttonSaveActionPerformed(ActionEvent e) {
        
    }

}
