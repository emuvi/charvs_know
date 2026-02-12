package br.com.pointel.charvs_know;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import java.awt.GridLayout;

import br.com.pointel.jarch.mage.WizGUI;

public class SetupDesk extends JFrame {

    private JPanel panelBody = new JPanel();
    private JScrollPane scrollBody = new JScrollPane(panelBody);

    private JLabel labelOnClipboardNewText = new JLabel("On Clipboard New Text:");
    private DefaultComboBoxModel<String> modelOnClipboardNewText = new DefaultComboBoxModel<>(new String[] { "Nothing", "ShowDesk", "AppendOnBuffer", "InsertOnInput" });
    private JComboBox<String> comboOnClipboardNewText = new JComboBox<>(modelOnClipboardNewText);
    private JLabel labelOnNaming = new JLabel("On Naming:");
    private DefaultComboBoxModel<String> modelOnNaming = new DefaultComboBoxModel<>(new String[] { "OutputName", "FirstLine", "Timestamp", "Numbered" });
    private JComboBox<String> comboOnNaming = new JComboBox<>(modelOnNaming);
    private JLabel labelNumberedPrefix = new JLabel("  Numbered Prefix:");
    private JTextField fieldNumberedPrefix = new JTextField(20);
    private JLabel labelNumberedSize = new JLabel("  Numbered Size:");
    private JSpinner spinnerNumberedSize = new JSpinner();
    private JLabel labelNumberedSuffix = new JLabel("  Numbered Suffix:");
    private JTextField fieldNumberedSuffix = new JTextField(20);
    private JLabel labelNameExtension = new JLabel("Name Extension:");
    private JTextField fieldNameExtension = new JTextField(20);
    private JLabel labelStripFirstLines = new JLabel("Strip First Lines:");
    private JSpinner spinnerStripFirstLines = new JSpinner();
    private JLabel labelInsertAtBegin = new JLabel("Insert At Begin:");
    private JTextField fieldInsertAtBegin = new JTextField(20);
    private JLabel labelInsertAtEnd = new JLabel("Insert At End:");
    private JTextField fieldInsertAtEnd = new JTextField(20);
    private JLabel labelApplyReplacesList = new JLabel("Apply Replaces List:");
    private JCheckBox checkApplyReplacesList = new JCheckBox();
    private JLabel labelReplaceVarsHolders = new JLabel("Replace Vars Holders:");
    private JCheckBox checkReplaceVarsHolders = new JCheckBox();
    private JLabel labelTrimFinalText = new JLabel("Trim Final Text:");
    private JCheckBox checkTrimFinalText = new JCheckBox();
    private JLabel labelSaveMultipleDivider = new JLabel("Save Multiple Divider:");
    private JTextField fieldSaveMultipleDivider = new JTextField(20);
    private JLabel labelMultipleMinimumSize = new JLabel("Multiple Minimum Size:");
    private JSpinner spinnerMultipleMinimumSize = new JSpinner();
    private JLabel labelOnSaveExists = new JLabel("On Save Exists:");
    private DefaultComboBoxModel<String> modelOnSaveExists = new DefaultComboBoxModel<>(new String[] { "Override", "KeepAll" });
    private JComboBox<String> comboOnSaveExists = new JComboBox<>(modelOnSaveExists);
    private JLabel labelOnRecord = new JLabel("On Record:");
    private DefaultComboBoxModel<String> modelOnRecord = new DefaultComboBoxModel<>(new String[] { "Empty", "FileBase", "FileName", "FilePath" });
    private JComboBox<String> comboOnRecord = new JComboBox<>(modelOnRecord);
    private JLabel labelRecordPrefix = new JLabel("  Prefix:");
    private JTextField fieldRecordPrefix = new JTextField(20);
    private JLabel labelRecordSuffix = new JLabel("  Suffix:");
    private JTextField fieldRecordSuffix = new JTextField(20);
    private JLabel labelOnRecordExists = new JLabel("On Record Exists:");
    private DefaultComboBoxModel<String> modelOnRecordExists = new DefaultComboBoxModel<>(new String[] { "Nothing", "Replace", "Append" });
    private JComboBox<String> comboOnRecordExists = new JComboBox<>(modelOnRecordExists);
    
    public SetupDesk() {
        initDesk();
    }
    
    private void initDesk() {
        initComponents();
        setIconImage(WizGUI.getLogo());
        WizGUI.initFrame(this);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Setup");
        setName("Setup");
        
        comboOnClipboardNewText.setName("OnClipboardNewText " + Setup.getPerfil());
        comboOnNaming.setName("OnNaming " + Setup.getPerfil());
        fieldNumberedPrefix.setName("NameNumberedPrefix " + Setup.getPerfil());
        spinnerNumberedSize.setName("NameNumberedSize " + Setup.getPerfil());
        fieldNumberedSuffix.setName("NameNumberedSuffix " + Setup.getPerfil());
        fieldNameExtension.setName("NameExtension " + Setup.getPerfil());
        fieldInsertAtBegin.setName("InsertAtBegin " + Setup.getPerfil());
        fieldInsertAtEnd.setName("InsertAtEnd " + Setup.getPerfil());
        spinnerStripFirstLines.setName("StripFirstLines " + Setup.getPerfil());
        checkApplyReplacesList.setName("ApplyReplacesList " + Setup.getPerfil());
        checkReplaceVarsHolders.setName("ReplaceVarsHolders " + Setup.getPerfil());
        checkTrimFinalText.setName("TrimFinalText " + Setup.getPerfil());
        fieldSaveMultipleDivider.setName("SaveMultipleDivider " + Setup.getPerfil());
        spinnerMultipleMinimumSize.setName("MultipleMinimumSize " + Setup.getPerfil());
        comboOnSaveExists.setName("OnSaveExists " + Setup.getPerfil());
        comboOnRecord.setName("OnRecord " + Setup.getPerfil());
        fieldRecordPrefix.setName("RecordPrefix " + Setup.getPerfil());
        fieldRecordSuffix.setName("RecordSuffix " + Setup.getPerfil());
        comboOnRecordExists.setName("OnRecordExists " + Setup.getPerfil());

        panelBody.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));

        panelBody.setLayout(new GridLayout(19, 2, 2, 2));
        panelBody.add(labelOnClipboardNewText);
        panelBody.add(comboOnClipboardNewText);
        panelBody.add(labelOnNaming);
        panelBody.add(comboOnNaming);
        panelBody.add(labelNumberedPrefix);
        panelBody.add(fieldNumberedPrefix);
        panelBody.add(labelNumberedSize);
        panelBody.add(spinnerNumberedSize);
        panelBody.add(labelNumberedSuffix);
        panelBody.add(fieldNumberedSuffix);
        panelBody.add(labelNameExtension);
        panelBody.add(fieldNameExtension);
        panelBody.add(labelStripFirstLines);
        panelBody.add(spinnerStripFirstLines);
        panelBody.add(labelInsertAtBegin);
        panelBody.add(fieldInsertAtBegin);
        panelBody.add(labelInsertAtEnd);
        panelBody.add(fieldInsertAtEnd);
        panelBody.add(labelApplyReplacesList);
        panelBody.add(checkApplyReplacesList);
        panelBody.add(labelReplaceVarsHolders);
        panelBody.add(checkReplaceVarsHolders);
        panelBody.add(labelTrimFinalText);
        panelBody.add(checkTrimFinalText);
        panelBody.add(labelSaveMultipleDivider);
        panelBody.add(fieldSaveMultipleDivider);
        panelBody.add(labelMultipleMinimumSize);
        panelBody.add(spinnerMultipleMinimumSize);
        panelBody.add(labelOnSaveExists);
        panelBody.add(comboOnSaveExists);
        panelBody.add(labelOnRecord);
        panelBody.add(comboOnRecord);
        panelBody.add(labelRecordPrefix);
        panelBody.add(fieldRecordPrefix);
        panelBody.add(labelRecordSuffix);
        panelBody.add(fieldRecordSuffix);
        panelBody.add(labelOnRecordExists);
        panelBody.add(comboOnRecordExists);

        setContentPane(scrollBody);
        setLocationRelativeTo(null);
        pack();
    }

}
