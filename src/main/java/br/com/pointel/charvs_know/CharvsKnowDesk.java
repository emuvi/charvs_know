package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.com.pointel.jarch.desk.DColPane;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.desk.DScroll;
import br.com.pointel.jarch.desk.DText;
import br.com.pointel.jarch.mage.WizGUI;

public class CharvsKnowDesk extends DFrame {

    private final JButton buttonBaseSelect = new JButton("^");
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
    
    private final JButton buttonActRef = new JButton("&");
    private final JTextField fieldActRef = new JTextField();
    private final DefaultComboBoxModel<String> modelActChoose = new DefaultComboBoxModel<>(new String[] { "Upload", "Identify", "Classify", "Organize", "Atomize", "Questify", "Explaine" });
    private final JComboBox<String> comboActChoose = new JComboBox<>(modelActChoose);
    private final JButton buttonActGo = new JButton(">");
    private final DRowPane rowActs = new DRowPane().insets(2)
            .growNone().put(buttonActRef)
            .growHorizontal().put(fieldActRef)
            .growHorizontal().put(comboActChoose)
            .growNone().put(buttonActGo);

    private final JTextArea textStatus = new JTextArea();
    private final JScrollPane scrollStatus = new JScrollPane(textStatus);
    private final DRowPane rowStatus = new DRowPane().insets(2)
            .growBoth().put(scrollStatus);

    private final DPane paneBody = new DColPane()
            .growHorizontal().put(rowBase)
            .growHorizontal().put(rowActs)
            .growBoth().put(rowStatus)
            .borderEmpty(7);

    public CharvsKnowDesk() {
        super();
        initComponents();
    }
    

    private void initComponents() {
        exitOnClose();
        body(paneBody);

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

        buttonActRef.setToolTipText("Select Reference to Act");
        fieldActRef.setEditable(false);
        comboActChoose.setEditable(false);
        buttonActGo.setToolTipText("Execute Act on Reference");
        textStatus.setEditable(false);
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
