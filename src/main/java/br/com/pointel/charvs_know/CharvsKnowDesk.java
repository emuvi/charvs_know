package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import br.com.pointel.jarch.desk.DColPane;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.mage.WizGUI;

public class CharvsKnowDesk extends DFrame {

    private final JButton buttonBaseSelect = new JButton("Base");
    private final JButton buttonBaseOpen = new JButton("*");
    private final JComboBox<String> comboBase = new JComboBox<>();
    private final JButton buttonBaseAdd = new JButton("+");
    private final JButton buttonBaseDel = new JButton("-");
    private final DRowPane rowBase = new DRowPane().insets(2)
            .growNone().put(buttonBaseSelect)
            .growNone().put(buttonBaseOpen)
            .growHorizontal().put(comboBase)
            .growNone().put(buttonBaseAdd)
            .growNone().put(buttonBaseDel);
    
    private final JButton buttonActSelect = new JButton("Select");
    private final JTextField fieldActWorking = new JTextField();
    private final DRowPane rowActs = new DRowPane().insets(2)
            .growNone().put(buttonActSelect)
            .growHorizontal().put(fieldActWorking);

    private final DPane paneBody = new DColPane()
            .growHorizontal().put(rowBase)
            .growHorizontal().put(rowActs)
            .borderEmpty(7);

    public CharvsKnowDesk() {
        super();
        initComponents();
    }
    

    private void initComponents() {
        exitOnClose();
        body(paneBody);

        buttonBaseSelect.setMnemonic('b');
        buttonBaseSelect.setToolTipText("Select Base Folder");
        buttonBaseSelect.addActionListener(this::buttonBaseSelectActionPerformed);
        buttonBaseOpen.setToolTipText("Open Base Folder");
        buttonBaseOpen.addActionListener(this::buttonBaseOpenActionPerformed);
        comboBase.setEditable(true);
        comboBase.setName("Base");
        comboBase.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Setup.setBase(getSelectedBase());
            }
        });
        buttonBaseAdd.setToolTipText("Add Base");
        buttonBaseAdd.addActionListener(this::comboBaseAddActionPerformed);
        buttonBaseDel.setToolTipText("Del Base");
        buttonBaseDel.addActionListener(this::comboBaseDelActionPerformed);

        fieldActWorking.setName("ActWorking");
        comboBase.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Setup.setActWorking(fieldActWorking.getText());
            }
        });
    }

    private String getSelectedBase() {
        return comboBase.getSelectedItem().toString();
    }

    private void setSelectedBase(String base) {
        comboBase.setSelectedItem(base);
    }

    private void buttonBaseSelectActionPerformed(ActionEvent evt) {
        var selected = new File(getSelectedBase());
        selected = WizGUI.selectFolder(selected);
        if (selected != null) {
            setSelectedBase(selected.getAbsolutePath());
        }
    }

    private void buttonBaseOpenActionPerformed(ActionEvent evt) {
        try {
            var selected = new File(getSelectedBase());
            WizGUI.open(selected);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void comboBaseAddActionPerformed(ActionEvent evt) {
        comboBase.addItem(comboBase.getSelectedItem().toString());
    }
    
    private void comboBaseDelActionPerformed(ActionEvent evt) {
        comboBase.removeItem(comboBase.getSelectedItem());
    }

}
