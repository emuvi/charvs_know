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
import javax.swing.SwingUtilities;

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
    
    private final JButton buttonSelectRef = new JButton("&");
    private final JButton buttonLastSelected = new JButton("%");
    private final JButton buttonSelectedOpen = new JButton("*");
    private final JTextField fieldSelectedRefWithExtension = new JTextField();
    private final DefaultComboBoxModel<String> modelActChoose = new DefaultComboBoxModel<>();
    private final JComboBox<String> comboActChoose = new JComboBox<>(modelActChoose);
    private final JButton buttonActExecute = new JButton(">");
    private final JButton buttonStepOpen = new JButton("*");
    private final DRowPane rowActs = new DRowPane().insets(2)
            .growNone().put(buttonSelectRef)
            .growNone().put(buttonLastSelected)
            .growNone().put(buttonSelectedOpen)
            .growHorizontal().put(fieldSelectedRefWithExtension)
            .growHorizontal().put(comboActChoose)
            .growNone().put(buttonActExecute)
            .growNone().put(buttonStepOpen);

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

        buttonSelectRef.setToolTipText("Select Reference to Act");
        buttonSelectRef.addActionListener(this::buttonSelectRefActionPerformed);
        buttonLastSelected.setToolTipText("Select Reference from Last Selected");
        buttonLastSelected.addActionListener(this::buttonLastSelectedActionPerformed);
        buttonSelectedOpen.setToolTipText("Open Selected Reference");
        buttonSelectedOpen.addActionListener(this::buttonSelectedOpenActionPerformed);
        fieldSelectedRefWithExtension.setEditable(false);
        comboActChoose.setEditable(false);
        for (var step : Steps.values()) {
            modelActChoose.addElement(step.name());
        }
        buttonActExecute.setToolTipText("Execute Act on Reference");
        buttonActExecute.addActionListener(this::buttonActExecuteActionPerformed);
        buttonStepOpen.setToolTipText("Open the Step Command");
        buttonStepOpen.addActionListener(this::buttonStepOpenActionPerformed);
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

    private void buttonSelectRefActionPerformed(ActionEvent evt) {
        try {
            if (lastSelectedFile == null) {
                lastSelectedFile = getBaseFolder();
            }
            var selectedFile = WizGUI.selectFile(lastSelectedFile);
            if (selectedFile != null) {
                selectRef(selectedFile);
            }
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void buttonLastSelectedActionPerformed(ActionEvent evt) {
        new LastSelectedDesk(this).setVisible(true);
    }

    private void buttonSelectedOpenActionPerformed(ActionEvent evt) {
        try {
            if (selectedRef == null) {
                return;
            }
            WizGUI.open(selectedRef.sourceFile);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonActExecuteActionPerformed(ActionEvent evt) {
        try {
            if (selectedRef == null) {
                throw new Exception("Reference not selected.");
            }
            var step = getSelectedStep();
            if (step == null) {
                throw new Exception("Step not selected.");
            }
            step.getAct().execute(selectedRef);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonStepOpenActionPerformed(ActionEvent evt) {
        try {
            var selectedStep = getSelectedStep();
            if (selectedStep == null) {
                return;
            }
            if (selectedStep.getCommandName() == null) {
                return;
            }
            WizGUI.open(selectedStep.getCommandFile());
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }


    private transient File lastSelectedFile = null;
    private transient SelectedRef selectedRef = null;

    public void selectRef(File selectFile) throws Exception {
        var hashMD5 = WizBytes.getMD5(selectFile);
        var refFile = getBaseRefFile(hashMD5 + ".md");
        var refWithExtension = hashMD5 + "." + FilenameUtils.getExtension(selectFile.getName());
        var sourceFile = getBaseRefFile(refWithExtension);
        if (!sourceFile.exists()) {
            if (WizGUI.showConfirm("Selected reference not found in the base. Do you wanna to move it inside?")) {
                Files.move(selectFile.toPath(), sourceFile.toPath());
                WizGUI.showInfo("Selected reference moved to the base.");
            } else if (WizGUI.showConfirm("Do you wanna to copy it to the base?")) {
                Files.copy(selectFile.toPath(), sourceFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
                WizGUI.showInfo("Selected reference copied to the base.");
            } else {
                throw new Exception("Selected reference not found in the base.");
            }
        }
        Ref ref;
        if (!refFile.exists()) {
            ref = new Ref();
            ref.props.hashMD5 = hashMD5;
            ref.props.createdAt = WizUtilDate.formatDateMach(new Date());
            ref.props.revisedOn = ref.props.createdAt;
            ref.props.revisedCount = "1";
            RefDatex.write(ref, refFile);
        } else {
            ref = RefDatex.read(refFile);
        }
        fieldSelectedRefWithExtension.setText(refWithExtension);
        SwingUtilities.updateComponentTreeUI(this);
        Setup.putSelectedRef(refWithExtension);
        lastSelectedFile = selectFile;
        selectedRef = new SelectedRef(ref, refFile, sourceFile, refWithExtension, this::updateStatus);
        updateStatus();
    }

    public void selectRef(String refWithExtension) throws Exception {
        var refFile = getBaseRefFile(FilenameUtils.getBaseName(refWithExtension) + ".md");
        var sourceFile = getBaseRefFile(refWithExtension);
        if (!sourceFile.exists()) {
            throw new Exception("Selected reference not found in the base.");
        }
        Ref ref;
        if (!refFile.exists()) {
            var hashMD5 = WizBytes.getMD5(sourceFile);
            ref = new Ref();
            ref.props.hashMD5 = hashMD5;
            ref.props.createdAt = WizUtilDate.formatDateMach(new Date());
            ref.props.revisedOn = ref.props.createdAt;
            ref.props.revisedCount = "1";
            RefDatex.write(ref, refFile);
        } else {
            ref = RefDatex.read(refFile);
        }
        fieldSelectedRefWithExtension.setText(refWithExtension);
        SwingUtilities.updateComponentTreeUI(this);
        Setup.putSelectedRef(refWithExtension);
        lastSelectedFile = sourceFile;
        selectedRef = new SelectedRef(ref, refFile, sourceFile, refWithExtension, this::updateStatus);
        updateStatus();
    }

    public void updateStatus() {
        var start = textStatus.getSelectionStart();
        var end = textStatus.getSelectionEnd();
        textStatus.setText(RefDatex.getRefSource(selectedRef.ref));
        textStatus.setSelectionStart(start);
        textStatus.setSelectionEnd(end);
    }

    public File getBaseRefFile(String refWithExtension) {
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

    public File getBaseFolder() {
        return new File(getSelectedBase());
    }

    public File getBaseRefsFolder() {
        return new File(getBaseFolder(), "+ Refs");
    }

    public String getSelectedBase() {
        return comboBase.getSelectedItem().toString();
    }

    public void setSelectedBase(String base) {
        comboBase.setSelectedItem(base);
    }

    public Steps getSelectedStep() {
        if (comboActChoose.getSelectedItem() == null) {
            return null;
        }
        return Steps.valueOf(comboActChoose.getSelectedItem().toString());
    }

}
