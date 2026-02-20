package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;

import org.apache.commons.io.FilenameUtils;

import br.com.pointel.jarch.desk.DButton;
import br.com.pointel.jarch.desk.DListDesk;
import br.com.pointel.jarch.desk.DTextDesk;
import br.com.pointel.jarch.mage.WizGUI;

public class LastSelectedDesk extends DListDesk<String> {

    private final CharvsKnowDesk parent;

    private final DButton buttonMemoa = new DButton("Memoa")
            .onClick(this::buttonMemoaActionPerformed);
    

    public LastSelectedDesk(CharvsKnowDesk parent) {
        super("Last Selected");
        this.parent = parent;
        initComponents();
    }
    
    private void initComponents() {
        var lastSelected = Setup.getLastSelectedRefs();
        for (var ref : lastSelected) {
            if (ref != null && !ref.isBlank()) {
                addOption(ref);
            }
        }
        onSelect(this::onSelect);
        putButton(buttonMemoa);
    }

    private void onSelect(String selected) {
        try {
            if (selected == null || selected.isBlank()) {
                return;
            }
            parent.selectRef(selected);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonMemoaActionPerformed(ActionEvent evt) {
        try {
            var refWithExtension = selected();
            if (refWithExtension == null || refWithExtension.isBlank()) {
                return;
            }
            var refFile = parent.getBaseRefFile(FilenameUtils.getBaseName(refWithExtension) + ".md");
            if (!refFile.exists()) {
                throw new Exception("Selected reference not found in the base.");
            }
            var ref = RefDatex.read(refFile);
            new DTextDesk("Memoa", ref.memoa.text)
                    .delButtons().editable(false).view();
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

}
