package br.com.pointel.charvs_know;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import br.com.pointel.jarch.desk.DBordPane;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DListEditor;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.mage.WizGUI;

public class ReplacesDesk extends DFrame {

    private final DListEditor<Replace> listEditor = new DListEditor<>(ReplaceEditFrame.class);
    private final DPane paneBody = new DBordPane().putCenter(listEditor).borderEmpty(7);

    public ReplacesDesk() {
        super("Replaces");
        body(paneBody);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                read();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                write();
            }
        });
    }

    private void read() {
        try {
            listEditor.setValue(Setup.readReplacesList());
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void write() {
        try {
            Setup.writeReplacesList(listEditor.getValue());
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

}
