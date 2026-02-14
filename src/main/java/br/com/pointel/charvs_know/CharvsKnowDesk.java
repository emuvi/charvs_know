package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.io.FilenameUtils;

import br.com.pointel.jarch.desk.DColPane;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.mage.WizBytes;
import br.com.pointel.jarch.mage.WizGUI;
import br.com.pointel.jarch.mage.WizUtilDate;

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
        buttonActRef.addActionListener(this::buttonActRefActionPerformed);
        fieldActRef.setEditable(false);
        comboActChoose.setEditable(false);
        buttonActGo.setToolTipText("Execute Act on Reference");
        buttonActGo.addActionListener(this::buttonActGoActionPerformed);
        textStatus.setEditable(false);
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
        comboBase.addItem(getSelectedBase());
    }
    
    private void comboBaseDelActionPerformed(ActionEvent evt) {
        comboBase.removeItem(getSelectedBase());
    }


    private File lastSelectedFile = null;
    private File selectedRefFile = null;
    private File selectedSourceFile = null;
    private Ref selectedRef = null;

    private void buttonActRefActionPerformed(ActionEvent evt) {
        try {
            if (lastSelectedFile == null) {
                lastSelectedFile = getBaseFolder();
            }
            var selectedFile = WizGUI.selectFile(lastSelectedFile);
            if (selectedFile != null) {
                lastSelectedFile = selectedFile;
                var hashMD5 = WizBytes.getMD5(selectedFile);
                var refFile = getBaseRefFile(hashMD5 + ".md");
                var sourceFile = getBaseRefFile(hashMD5 + "." + FilenameUtils.getExtension(selectedFile.getName()));
                if (!sourceFile.exists()) {
                    Files.move(selectedFile.toPath(), sourceFile.toPath());
                    WizGUI.showInfo("Selected reference moved to the base.");
                } else {
                    WizGUI.showInfo("Selected reference already in the base.");
                }
                if (!refFile.exists()) {
                    selectedRef = new Ref();
                    selectedRef.props.hashMD5 = hashMD5;
                    selectedRef.props.createdAt = WizUtilDate.formatDateMach(new Date());
                    selectedRef.props.revisedOn = WizUtilDate.formatDateMach(new Date());
                    selectedRef.props.revisedCount = "1";
                    RefDatex.write(selectedRef, refFile);
                } else {
                    selectedRef = RefDatex.read(refFile);
                }
                fieldActRef.setText(selectedRef.props.hashMD5);
                selectedRefFile = refFile;
                selectedSourceFile = sourceFile;
                updateStatus();
            }
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void buttonActGoActionPerformed(ActionEvent evt) {

    }

    public void updateStatus() {
        var start = textStatus.getSelectionStart();
        var end = textStatus.getSelectionEnd();
        textStatus.setText(RefDatex.getRefSource(selectedRef));
        textStatus.setSelectionStart(start);
        textStatus.setSelectionEnd(end);
    }

    private String getSelectedBase() {
        return comboBase.getSelectedItem().toString();
    }

    private void setSelectedBase(String base) {
        comboBase.setSelectedItem(base);
    }

    private File getBaseFolder() {
        return new File(getSelectedBase());
    }

    private File getBaseRefsFolder() {
        return new File(getBaseFolder(), "+ Refs");
    }

    private File getBaseRefFile(String refWithExtension) {
        var baseRefsFolder = getBaseRefsFolder();
        if (!baseRefsFolder.exists()) {
            baseRefsFolder.mkdirs();
        }
        var baseRefFolder = new File(baseRefsFolder, refWithExtension.substring(0, 3));
        if (!baseRefFolder.exists()) {
            baseRefFolder.mkdirs();
        }
        return new File(baseRefFolder, refWithExtension);
    }

}
