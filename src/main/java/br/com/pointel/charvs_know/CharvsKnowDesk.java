package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.pointel.jarch.desk.DColPane;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.mage.WizGUI;
import br.com.pointel.jarch.mage.WizObject;
import br.com.pointel.jarch.mage.WizFile;
import br.com.pointel.jarch.mage.WizString;
import br.com.pointel.jarch.mage.WizText;
import br.com.pointel.jarch.mage.WizThread;
import br.com.pointel.jarch.mage.WizUtilDate;

public class CharvsKnowDesk extends DFrame {

    private static final Logger LOG = LoggerFactory.getLogger(CharvsKnowDesk.class);

    private final JButton buttonSetup = new JButton("#");
    private final JButton buttonReplaces = new JButton("$");
    private final JButton buttonBufferAppend = new JButton("Append");
    private final JButton buttonBufferClean = new JButton("C");
    private final JButton buttonInsert = new JButton("Insert");
    private final JTextField fieldInsertTitle = new JTextField();
    private final DRowPane rowMain = new DRowPane().insets(2)
            .growNone().insetsRight(0).put(buttonSetup)
            .growNone().insetsRight(7).put(buttonReplaces)
            .growNone().insetsRight(0).put(buttonBufferAppend)
            .growNone().insetsRight(7).put(buttonBufferClean)
            .growNone().insetsRight(0).put(buttonInsert)
            .growHorizontal().insetsRight(0).put(fieldInsertTitle);

    private final JButton buttonInputSelect = new JButton("Input");
    private final JButton buttonInputOpen = new JButton("*");
    private final JTextField fieldInput = new JTextField();
    private final JButton buttonLoad = new JButton("Load");
    private final DRowPane rowInput = new DRowPane().insets(2)
            .growNone().put(buttonInputSelect)
            .growNone().put(buttonInputOpen)
            .growHorizontal().put(fieldInput)
            .growNone().put(buttonLoad);

    private final JButton buttonInputUpdate = new JButton("~");
    private final JButton buttonInputFileOpen = new JButton("*");
    private final DefaultComboBoxModel<String> modelInput = new DefaultComboBoxModel<>();
    private final JComboBox<String> comboInput = new JComboBox<>(modelInput);
    private final JButton buttonInputFirst = new JButton("^");
    private final JButton buttonInputPrior = new JButton("<");
    private final JButton buttonInputNext = new JButton(">");
    private final JButton buttonInputSwitch = new JButton("%");
    private final DRowPane rowInputFile = new DRowPane().insets(2)
            .growNone().put(buttonInputUpdate)
            .growNone().put(buttonInputFileOpen)
            .growHorizontal().put(comboInput)
            .growNone().put(buttonInputFirst)
            .growNone().put(buttonInputPrior)
            .growNone().put(buttonInputNext)
            .growNone().put(buttonInputSwitch);

    private final JButton buttonOutputSelect = new JButton("Output");
    private final JButton buttonOutputOpen = new JButton("*");
    private final JTextField fieldOutput = new JTextField();
    private final JButton buttonSave = new JButton("Save");
    private final JButton buttonSaveMultiple = new JButton("+");
    private final JButton buttonSaveOpen = new JButton("*");
    private final DRowPane rowOutput = new DRowPane().insets(2)
            .growNone().put(buttonOutputSelect)
            .growNone().put(buttonOutputOpen)
            .growHorizontal().put(fieldOutput)
            .growNone().put(buttonSave)
            .growNone().put(buttonSaveMultiple)
            .growNone().put(buttonSaveOpen);

    private final JLabel labelOutputName = new JLabel("Name:");
    private final JTextField fieldOutputName = new JTextField();
    private final DRowPane rowOutputName = new DRowPane().insets(2)
            .anchorCenter().growNone().put(labelOutputName)
            .growHorizontal().put(fieldOutputName);

    private final JButton buttonRecordSelect = new JButton("Record");
    private final JButton buttonRecordOpen = new JButton("*");
    private final JTextField fieldRecord = new JTextField();
    private final JCheckBox checkRecordMake = new JCheckBox("Make");
    private final DRowPane rowRecord = new DRowPane().insets(2)
            .growNone().put(buttonRecordSelect)
            .growNone().put(buttonRecordOpen)
            .growHorizontal().put(fieldRecord)
            .growNone().put(checkRecordMake);

    private final JButton buttonArchiveSelect = new JButton("Archive");
    private final JButton buttonArchiveOpen = new JButton("*");
    private final JTextField fieldArchive = new JTextField();
    private final JCheckBox checkArchiveMake = new JCheckBox("Make");
    private final DRowPane rowArchive = new DRowPane().insets(2)
            .growNone().put(buttonArchiveSelect)
            .growNone().put(buttonArchiveOpen)
            .growHorizontal().put(fieldArchive)
            .growNone().put(checkArchiveMake);

    private final JTextField fieldStatus = new JTextField();
    private final JComboBox<String> comboPerfil = new JComboBox<>();
    private final JButton buttonPerfilAdd = new JButton("+");
    private final JButton buttonPerfilDel = new JButton("-");
    private final DRowPane rowStatus = new DRowPane().insets(2)
            .growBoth().put(fieldStatus)
            .growVertical().put(comboPerfil)
            .growVertical().put(buttonPerfilAdd)
            .growVertical().put(buttonPerfilDel);

    private final DPane paneBody = new DColPane()
            .growHorizontal().put(rowMain)
            .growHorizontal().put(rowInput)
            .growHorizontal().put(rowInputFile)
            .growHorizontal().put(rowOutput)
            .growHorizontal().put(rowOutputName)
            .growHorizontal().put(rowRecord)
            .growHorizontal().put(rowArchive)
            .growBoth().put(rowStatus)
            .borderEmpty(7);

    private String bufferBody = "";
    private Integer bufferSize = 0;
    private String originLast = "";
    private String originActual = "";
    private File savedLast = null;

    private volatile String clipboardText = null;
    private String partialInsert = null;
    private File partialFile = null;

    public CharvsKnowDesk() {
        super("Charvs");
        initComponents();
        initShortcuts();
        initWatcher();
    }
    
    private void initComponents() {
        exitOnClose();
        body(paneBody);

        buttonSetup.setToolTipText("Setup");
        buttonSetup.setName("");
        buttonSetup.addActionListener(this::buttonSetupActionPerformed);

        buttonReplaces.setToolTipText("Replaces");
        buttonReplaces.setName("");
        buttonReplaces.addActionListener(this::buttonReplacesActionPerformed);

        buttonBufferAppend.setMnemonic('A');
        buttonBufferAppend.setToolTipText("Append Buffer (ctrl+A)");
        buttonBufferAppend.addActionListener(this::buttonBufferAppendActionPerformed);
        buttonBufferClean.setMnemonic('C');
        buttonBufferClean.setToolTipText("Clear Buffer");
        buttonBufferClean.addActionListener(this::buttonBufferCleanActionPerformed);

        buttonInsert.setMnemonic('I');
        buttonInsert.setToolTipText("Insert on < INSERT > tag in Input chain and puts on clipboard (ctrl+E)");
        buttonInsert.addActionListener(this::buttonInsertActionPerformed);
        fieldInsertTitle.setEditable(false);

        buttonInputSelect.setMnemonic('n');
        buttonInputSelect.setText("Input");
        buttonInputSelect.setToolTipText("Select Input Folder");
        buttonInputSelect.addActionListener(this::buttonInputSelectActionPerformed);
        buttonInputOpen.setToolTipText("Open Origin Folder");
        buttonInputOpen.addActionListener(this::buttonInputOpenActionPerformed);
        fieldInput.setName("InputFolder");
        fieldInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Setup.setInputFolder(fieldInput.getText());
            }
        });
        buttonLoad.setMnemonic('L');
        buttonLoad.setToolTipText("Loads selected Input on clipboard (ctrl+C)");
        buttonLoad.addActionListener(this::buttonLoadActionPerformed);
        
        buttonInputUpdate.setToolTipText("Update Input Files");
        buttonInputUpdate.addActionListener(this::buttonInputUpdateActionPerformed);
        buttonInputFileOpen.setToolTipText("Open Input File");
        buttonInputFileOpen.addActionListener(this::buttonInputFileOpenActionPerformed);
        comboInput.addActionListener(this::comboInputActionPerformed);
        buttonInputFirst.setMnemonic('^');
        buttonInputFirst.setToolTipText("Select First Input File");
        buttonInputFirst.addActionListener(this::buttonInputFirstActionPerformed);
        buttonInputPrior.setMnemonic('<');
        buttonInputPrior.setToolTipText("Select Prior Input File");
        buttonInputPrior.addActionListener(this::buttonInputPriorActionPerformed);
        buttonInputNext.setMnemonic('>');
        buttonInputNext.setToolTipText("Select Next Input File");
        buttonInputNext.addActionListener(this::buttonInputNextActionPerformed);
        buttonInputSwitch.setMnemonic('%');
        buttonInputSwitch.setToolTipText("Switch between Input files (ctrl+S)");
        buttonInputSwitch.addActionListener(this::buttonInputSwitchActionPerformed);

        buttonOutputSelect.setMnemonic('O');
        buttonOutputSelect.setToolTipText("Select Output Folder");
        buttonOutputSelect.addActionListener(this::buttonOutputSelectActionPerformed);
        buttonOutputOpen.setToolTipText("Open Output Folder");
        buttonOutputOpen.addActionListener(this::buttonOutputOpenActionPerformed);
        fieldOutput.setName("OutputFolder");
        fieldOutput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Setup.setOutputFolder(fieldOutput.getText());
            }
        });
        buttonSave.setMnemonic('S');
        buttonSave.setToolTipText("Saves clipboard on Output folder (ctrl+V)");
        buttonSave.addActionListener(this::buttonSaveActionPerformed);
        buttonSaveMultiple.setMnemonic('+');
        buttonSaveMultiple.setToolTipText("Saves clipboard on Multiple files (ctrl+M)");
        buttonSaveMultiple.addActionListener(this::buttonSaveMultipleActionPerformed);
        buttonSaveOpen.setToolTipText("Open Last Saved File");
        buttonSaveOpen.addActionListener(this::buttonSaveOpenActionPerformed);
        
        fieldOutputName.setName("OutputName");
        fieldOutputName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Setup.setOutputName(fieldOutputName.getText());
            }
        });

        buttonRecordSelect.setMnemonic('r');
        buttonRecordSelect.setToolTipText("Select Record File");
        buttonRecordSelect.addActionListener(this::buttonRecordSelectActionPerformed);
        buttonRecordOpen.setToolTipText("Open Record File");
        buttonRecordOpen.addActionListener(this::buttonRecordOpenActionPerformed);
        fieldRecord.setName("RecordFile");
        fieldRecord.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Setup.setRecordFile(fieldRecord.getText());
            }
        });
        checkRecordMake.setName("RecordMake");
        checkRecordMake.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Setup.setRecordMake(checkRecordMake.isSelected());
            }
        });

        buttonArchiveSelect.setMnemonic('h');
        buttonArchiveSelect.setToolTipText("Select Archive Folder");
        buttonArchiveSelect.addActionListener(this::buttonArchiveSelectActionPerformed);
        buttonArchiveOpen.setToolTipText("Open Archive Folder");
        buttonArchiveOpen.addActionListener(this::buttonArchiveOpenActionPerformed);
        fieldArchive.setName("ArchiveFolder");
        fieldArchive.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Setup.setArchiveFolder(fieldArchive.getText());
            }
        });
        checkArchiveMake.setName("ArchiveMake");
        checkArchiveMake.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                Setup.setArchiveMake(checkArchiveMake.isSelected());
            }
        });

        fieldStatus.setEditable(false);
        comboPerfil.setEditable(true);
        comboPerfil.addActionListener(this::comboPerfilActionPerformed);
        comboPerfil.setName("Perfil");
        buttonPerfilAdd.setToolTipText("Add Perfil");
        buttonPerfilAdd.addActionListener(this::comboPerfilAddActionPerformed);
        buttonPerfilDel.setToolTipText("Del Perfil");
        buttonPerfilDel.addActionListener(this::comboPerfilDelActionPerformed);
    }
    
    private void initShortcuts() {
        var loadActionKey = "LoadActionKey";
        var keyCtrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK);
        var loadAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonLoadActionPerformed(e);
            }
        };
        var keyCtrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK);
        var saveActionKey = "SaveActionKey";
        var saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonSaveActionPerformed(e);
            }
        };
        var keyCtrlM = KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK);
        var saveMultipleActionKey = "SaveMultipleActionKey";
        var saveMultipleAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonSaveMultipleActionPerformed(e);
            }
        };
        var keyCtrlA = KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK);
        var appendActionKey = "AppendActionKey";
        var appendAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonBufferAppendActionPerformed(e);
            }
        };
        var keyCtrlE = KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK);
        var insertActionKey = "InsertActionKey";
        var insertAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonInsertActionPerformed(e);
            }
        };
        var keyCtrlW = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        var switchActionKey = "SwitchActionKey";
        var switchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonInputSwitchActionPerformed(e);
            }
        };
        var inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = getRootPane().getActionMap();
        inputMap.put(keyCtrlC, loadActionKey);
        actionMap.put(loadActionKey, loadAction);
        inputMap.put(keyCtrlV, saveActionKey);
        actionMap.put(saveActionKey, saveAction);
        inputMap.put(keyCtrlM, saveMultipleActionKey);
        actionMap.put(saveMultipleActionKey, saveMultipleAction);
        inputMap.put(keyCtrlA, appendActionKey);
        actionMap.put(appendActionKey, appendAction);
        inputMap.put(keyCtrlE, insertActionKey);
        actionMap.put(insertActionKey, insertAction);
        inputMap.put(keyCtrlW, switchActionKey);
        actionMap.put(switchActionKey, switchAction);
    }
    
    private void initWatcher() {
        new Thread("Watcher") {
            @Override
            public void run() {
                WizThread.sleep(1000);
                SwingUtilities.invokeLater(() -> {
                    buttonInputUpdateActionPerformed(null);
                });
                while (isDisplayable()) {
                    WizThread.sleep(1000);
                    try {
                        watch();
                    } catch (Exception e) {
                        LOG.error("Error on watcher.", e);
                    }
                }
            }
        }.start();
    }

    private void watch() throws Exception {
        try {
            watchClipboardText();
        } catch (Exception e) {
            LOG.error("Error on watcher clipboard text.", e);
        }
    }
    
    private void watchClipboardText() throws Exception {
        if (checkClipboardNewText()) {
            if (Setup.getOnNewClipboardText() == OnNewClipboardText.ShowDesk) {
                showDesk();
            } else if (Setup.getOnNewClipboardText() == OnNewClipboardText.AppendOnBuffer) {
                buttonBufferAppendActionPerformed(null);
            } else if (Setup.getOnNewClipboardText() == OnNewClipboardText.InsertOnInput) {
                buttonInsertActionPerformed(null);
            }
        }
    }

    private boolean checkClipboardNewText() throws Exception {
        var actualClipboard = WizGUI.getStringFromClipboard();
        if (!Objects.equals(actualClipboard, clipboardText)) {
            clipboardText = actualClipboard;
            return true;
        }
        return false;
    }
    
    private void showDesk() {
        requestFocus();
        requestFocusInWindow();
        toFront();
        setAlwaysOnTop(true);
        setAlwaysOnTop(false);
    }

    private String cleanFileName(String title) {
        title = title.trim();
        if (title.equals(title.toUpperCase())) {
            title = WizString.capitalizeWords(title.toLowerCase());
        }
        title = title
                .replace("\"", "”")
                .replace("'", "”")
                .replace("/", "-")
                .replace("|", "-")
                .replace("\\", "-")
                .replace("?", "")
                .replace("!", "")
                .replace("<", "")
                .replace(">", "")
                .replace("*", "")
                .replace("#", "")
                .replace(": ", " - ")
                .replace(":", ",")
                .replace(";", ",")
                .trim();
        return title.replaceAll("\\s+", " ");
    }

    private void putStatus(String status, String archive) throws Exception {
        fieldStatus.setText(status);
        fieldStatus.setSelectionStart(0);
        fieldStatus.setSelectionEnd(0);
        if (checkArchiveMake.isSelected()) {
            var now = WizUtilDate.formatTimestampFile(new Date());
            var builder = new StringBuilder();
            builder.append("Time: ");
            builder.append(now);
            builder.append("\nStatus: ");
            builder.append(status);
            builder.append("\nArchive:\n\n");
            builder.append(archive);
            var folder = new File(fieldArchive.getText());
            var file = new File(folder, now + ".txt");
            Files.writeString(file.toPath(), builder.toString(), StandardCharsets.UTF_8);
        }
    }

    private void buttonSetupActionPerformed(ActionEvent evt) {
        new SetupDesk().setVisible(true);
    }

    private void buttonReplacesActionPerformed(ActionEvent evt) {
        new ReplacesDesk().setVisible(true);
    }

    private void buttonBufferAppendActionPerformed(ActionEvent evt) {
        try {
            var body = WizGUI.getStringFromClipboard();
            bufferBody = (bufferBody.trim() + "\n\n" + body.trim()).trim();
            bufferSize++;
            WizGUI.putStringOnClipboard(bufferBody);
            putStatus("Appended " + bufferSize + " on Buffer", bufferBody);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonBufferCleanActionPerformed(ActionEvent evt) {
        try {
            bufferBody = "";
            bufferSize = 0;
            putStatus("Cleaned Buffer", "");
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonInsertActionPerformed(ActionEvent evt) {
        try {
            var body = WizGUI.getStringFromClipboard();
            var folder = new File(fieldInput.getText());
            var file = new File(folder, comboInput.getSelectedItem().toString());
            var input = partialInsert;
            if (input == null) {    
                input = Files.readString(file.toPath());
            }
            int insertPos = input.indexOf("< INSERT");
            if (insertPos > -1) {
                int insertEnd = input.indexOf(">", insertPos);
                if (insertEnd > -1) {
                    input = input.substring(0, insertPos) + body + input.substring(insertEnd + 1);
                } else {
                    throw new Exception("Malformed < INSERT > tag.");
                }
            } else {
                throw new Exception("Not found < INSERT > tag.");
            }
            var remains = WizString.count(input, "< INSERT");
            WizGUI.putStringOnClipboard(input);
            putStatus("Inserted " + (remains == 0 ? "Done" : "Left " + remains) + " on " + (partialInsert != null ?  "Partial of " + partialFile.getName() : file.getName()), input);
            if (remains == 0) {
                partialInsert = null;
                partialFile = null;
                int warnPos = input.indexOf("< WARN");
                if (warnPos > -1) {
                    int warnEnd = input.indexOf(">", warnPos);
                    if (warnEnd > -1) {
                         var warnText = input.substring(warnPos + 6, warnEnd).trim();
                        if (warnText.startsWith(":")) {
                            warnText = warnText.substring(1).trim();
                        }
                        WizGUI.showInfo(warnText);
                    } else {
                        throw new Exception("Malformed < WARN > tag.");
                    }
                }
            } else {
                partialInsert = input;
                partialFile = file;
            }
            bufferBody = "";
            bufferSize = 0;
            putInsertTitle();
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void putInsertTitle() {
        try {
            var folder = new File(fieldInput.getText());
            var file = new File(folder, comboInput.getSelectedItem().toString());
            var input = partialInsert;
            if (input == null) {    
                input = Files.readString(file.toPath());
            }
            int insertPos = input.indexOf("< INSERT");
            if (insertPos > -1) {
                int insertEnd = input.indexOf(">", insertPos);
                if (insertEnd > -1) {
                    var insertTitle = input.substring(insertPos + 8, insertEnd).trim();
                    if (insertTitle.startsWith(":")) {
                        insertTitle = insertTitle.substring(1).trim();
                    }
                    fieldInsertTitle.setText(insertTitle.isEmpty() ? "Found <INSERT> tag." : insertTitle);
                    fieldInsertTitle.setSelectionStart(0);
                    fieldInsertTitle.setSelectionEnd(0);
                } else {
                    throw new Exception("Malformed < INSERT > tag.");
                }
            } else {
                throw new Exception("Not found < INSERT > tag.");
            }
        } catch (Exception e) {
            fieldInsertTitle.setText(e.getMessage());
            fieldInsertTitle.setSelectionStart(0);
            fieldInsertTitle.setSelectionEnd(0);
        }
    }

    private void buttonInputSelectActionPerformed(ActionEvent evt) {
        var selected = new File(fieldInput.getText());
        selected = WizGUI.selectFolder(selected);
        if (selected != null) {
            fieldInput.setText(selected.getAbsolutePath());
        }
    }

    private void buttonInputOpenActionPerformed(ActionEvent evt) {
        try {
            var selected = new File(fieldInput.getText());
            WizGUI.open(selected);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonLoadActionPerformed(ActionEvent evt) {
        try {
            var folder = new File(fieldInput.getText());
            var file = new File(folder, comboInput.getSelectedItem().toString());
            var input = Files.readString(file.toPath());
            WizGUI.putStringOnClipboard(input);
            putStatus("Loaded from " + file.getName(), input);
            bufferBody = "";
            bufferSize = 0;
            int warnPos = input.indexOf("< WARN");
            if (warnPos > -1) {
                int warnEnd = input.indexOf(">", warnPos);
                if (warnEnd > -1) {
                        var warnText = input.substring(warnPos + 6, warnEnd).trim();
                    if (warnText.startsWith(":")) {
                        warnText = warnText.substring(1).trim();
                    }
                    WizGUI.showInfo(warnText);
                } else {
                    throw new Exception("Malformed < WARN > tag.");
                }
            }
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonInputUpdateActionPerformed(ActionEvent evt) {
        var selected = WizObject.getFirstNonNull(comboInput.getSelectedItem(), "").toString();
        modelInput.removeAllElements();
        var folder = new File(fieldInput.getText());
        if (!folder.exists()) {
            return;
        }
        var found = false;
        for (var inside : folder.listFiles()) {
            if (inside.getName().toLowerCase().endsWith(".txt")) {
                modelInput.addElement(inside.getName());
                if (selected.equals(inside.getName())) {
                    found = true;
                }
            }
        }
        if (found) {
            comboInput.setSelectedItem(selected);
        } else {
            comboInput.setSelectedIndex(-1);
        }
    }

    private void buttonInputFileOpenActionPerformed(ActionEvent evt) {
        try {
            var folder = new File(fieldInput.getText());
            var file = new File(folder, comboInput.getSelectedItem().toString());
            WizGUI.open(file);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void comboInputActionPerformed(ActionEvent evt) {
        if (comboInput.getSelectedItem() != null) {
            originLast = originActual;
            originActual = comboInput.getSelectedItem().toString();
            partialInsert = null;
            partialFile = null;
            putInsertTitle();
        }
    }

    private void buttonInputFirstActionPerformed(ActionEvent evt) {
        try {
            comboInput.setSelectedIndex(0);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonInputPriorActionPerformed(ActionEvent evt) {
        try {
            var toSelect = comboInput.getSelectedIndex() - 1;
            if (toSelect < 0) {
                toSelect = modelInput.getSize() - 1;
            }
            comboInput.setSelectedIndex(toSelect);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonInputNextActionPerformed(ActionEvent evt) {
        try {
            var toSelect = comboInput.getSelectedIndex() + 1;
            if (toSelect > modelInput.getSize() -1) {
                toSelect =  0;
            }
            comboInput.setSelectedIndex(toSelect);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonInputSwitchActionPerformed(ActionEvent evt) {
        try {
            comboInput.setSelectedItem(originLast);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonOutputSelectActionPerformed(ActionEvent evt) {
        var selected = new File(fieldOutput.getText());
        selected = WizGUI.selectFolder(selected);
        if (selected != null) {
            fieldOutput.setText(selected.getAbsolutePath());
        }
    }

    private void buttonOutputOpenActionPerformed(ActionEvent evt) {
        try {
            var selected = new File(fieldOutput.getText());
            WizGUI.open(selected);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonSaveActionPerformed(ActionEvent evt) {
        try {
            saveClipboardOnOutput();
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void saveClipboardOnOutput() throws Exception {
        var text = WizGUI.getStringFromClipboard();
        var folder = new File(fieldOutput.getText());
        var fileName = fieldOutputName.getText();
        var fileExtension = Setup.getNameExtension();
        var setupNaming = Setup.getOnNaming();
        if (setupNaming == OnNaming.FirstLine) {
            fileName = cleanFileName(WizString.getFirstLine(text));
        } else if (setupNaming == OnNaming.Timestamp) {
            fileName = WizUtilDate.formatTimestampFile(new Date());
        } else if (setupNaming == OnNaming.Numbered) {
            var prefix = Setup.getNameNumberedPrefix();
            var index = 1;
            var size = Setup.getNameNumberedSize();
            var suffix = Setup.getNameNumberedSuffix();
            fileName = prefix + WizString.fillAtStart(index + "", '0', size) + suffix;
            var file = new File(folder, fileName + fileExtension);
            while (file.exists()) {
                index++;
                fileName = prefix + WizString.fillAtStart(index + "", '0', size) + suffix;
                file = new File(folder, fileName + fileExtension);
            }
        }
        if (Setup.getStripFirstLines() > 0) {
            text = WizString.stripFirstLines(text, Setup.getStripFirstLines());
        }
        text = Setup.getInsertAtBegin() + text + Setup.getInsertAtEnd();
        if (Boolean.TRUE.equals(Setup.getApplyReplacesList())) {
            text = applyReplacesList(text);
        }
        if (Boolean.TRUE.equals(Setup.getReplaceVarsHolders())) {
            text = WizString.replaceVarsHolders(text);
        }
        if (Boolean.TRUE.equals(Setup.getTrimFinalText())) {
            text = text.trim();
        }
        var file = new File(folder, fileName + fileExtension);
        if (Setup.getOnSaveExists() == OnSaveExists.KeepAll) {
            file = WizFile.notOverride(file);
        }
        var override = file.exists();
        Files.writeString(file.toPath(), text);
        putStatus((override ? "Override on " : "Saved on ") + file.getName(), text);
        bufferBody = "";
        bufferSize = 0;
        savedLast = file;
        if (checkRecordMake.isSelected()) {
            makeRecord();
        }
    }

    private String applyReplacesList(String text) throws Exception {
        var replaces = Setup.readReplacesList();
        if (replaces == null || replaces.isEmpty()) {
            return text;
        }
        for (var replace : replaces) {
            text = replace.apply(text);
        }
        return text;
    }

    private void buttonSaveMultipleActionPerformed(ActionEvent evt) {
        try {
            var text = WizGUI.getStringFromClipboard();
            var divider = Setup.getSaveMultipleDivider();
            var minimumSize = Setup.getMultipleMinimumSize();
            var parts = text.split(divider);
            for (var part : parts) {
                part = part.trim();
                if (part.isEmpty() || part.length() < minimumSize) {
                    putStatus("Passed to saved part of the text.", part);
                    continue;
                }
                WizGUI.putStringOnClipboard(part);
                saveClipboardOnOutput();
            }
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonSaveOpenActionPerformed(ActionEvent evt) {
        try {
            WizGUI.open(savedLast);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonRecordSelectActionPerformed(ActionEvent evt) {
        var selected = new File(fieldRecord.getText());
        selected = WizGUI.selectFile(selected);
        if (selected != null) {
            fieldRecord.setText(selected.getAbsolutePath());
        }
    }

    private void makeRecord() throws Exception {
        var recordText = WizString.replaceVarsHolders(Setup.getRecordPrefix());
        switch (Setup.getOnRecord()) {
            case FileBase:
                recordText += FilenameUtils.getBaseName(savedLast.getName());
                break;
            case FileName:
                recordText += savedLast.getName();
                break;
            case FilePath:
                recordText += savedLast.getAbsolutePath();
                break;
        }
        recordText += WizString.replaceVarsHolders(Setup.getRecordSuffix());
        var file = new File(fieldRecord.getText());
        var text = WizText.read(file);
        var exists = text.contains(recordText);
        if (exists && Setup.getOnRecordExists() == OnRecordExists.Nothing) {
            return;
        } else if (exists && Setup.getOnRecordExists() == OnRecordExists.Replace) {
            text = text.replace(recordText, "");
        }
        while (text.contains("\n\n\n")) {
            text = text.replace("\n\n\n", "\n\n");
        }
        text = text.trim();
        if (!text.isEmpty()) {
            text += "\n\n";
        }
        text += recordText;
        WizText.write(file, text);
    }

    private void buttonRecordOpenActionPerformed(ActionEvent evt) {
        try {
            var selected = new File(fieldRecord.getText());
            WizGUI.open(selected);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void buttonArchiveSelectActionPerformed(ActionEvent evt) {
        var selected = new File(fieldArchive.getText());
        selected = WizGUI.selectFolder(selected);
        if (selected != null) {
            fieldArchive.setText(selected.getAbsolutePath());
        }
    }

    private void buttonArchiveOpenActionPerformed(ActionEvent evt) {
        try {
            var selected = new File(fieldArchive.getText());
            WizGUI.open(selected);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void comboPerfilActionPerformed(ActionEvent evt) {
        try {
            var perfil = WizObject.getFirstNonNull(comboPerfil.getSelectedItem(), "").toString();
            Setup.setPerfil(perfil);
            fieldInput.setText(Setup.getInputFolder());
            fieldInput.setName("Input " + perfil);
            buttonInputUpdateActionPerformed(evt);
            fieldOutput.setText(Setup.getOutputFolder());
            fieldOutput.setName("Output " + perfil);
            fieldOutputName.setText(Setup.getOutputName());
            fieldOutputName.setName("OutputName " + perfil);
            fieldRecord.setText(Setup.getRecordFile());
            fieldRecord.setName("Record " + perfil);
            checkRecordMake.setSelected(Setup.getRecordMake());
            checkRecordMake.setName("RecordMake " + perfil);
            fieldArchive.setText(Setup.getArchiveFolder());
            fieldArchive.setName("Archive " + perfil);
            checkArchiveMake.setSelected(Setup.getArchiveMake());
            checkArchiveMake.setName("ArchiveMake " + perfil);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

    private void comboPerfilAddActionPerformed(ActionEvent evt) {
        comboPerfil.addItem(comboPerfil.getSelectedItem().toString());
    }
    
    private void comboPerfilDelActionPerformed(ActionEvent evt) {
        comboPerfil.removeItem(comboPerfil.getSelectedItem());
    }

}
