package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.SwingUtilities;

import org.apache.commons.io.FilenameUtils;

import br.com.pointel.jarch.desk.DButton;
import br.com.pointel.jarch.desk.DColPane;
import br.com.pointel.jarch.desk.DComboEdit;
import br.com.pointel.jarch.desk.DFieldEdit;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DIntegerField;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.desk.DScroll;
import br.com.pointel.jarch.desk.DSplitter;
import br.com.pointel.jarch.desk.DText;
import br.com.pointel.jarch.desk.DListDesk;
import br.com.pointel.jarch.mage.WizGUI;
import br.com.pointel.jarch.mage.WizString;
import br.com.pointel.jarch.mage.WizText;
import br.com.pointel.jarch.mage.WizUtilDate;

public class HelperExplains extends DFrame {

    private final DComboEdit<String> comboGroup = new DComboEdit<String>()
            .onClick(this::comboGroupActionPerformed);    
    private final DPane paneGroupActs = new DRowPane().insets(2)
            .growHorizontal().put(comboGroup);

    private final DFieldEdit<Integer> fieldClassOrder = new DIntegerField()
            .cols(4).editable(false).horizontalAlignmentCenter();
    private final DText fieldClassTitle = new DText().editable(false);
    private final DScroll scrollClassTitle = new DScroll(fieldClassTitle);
    private final DSplitter splitterClass = new DSplitter()
            .horizontal().left(fieldClassOrder).right(scrollClassTitle)
            .divider(0.2f)
            .name("splitterClass");

    private final DText textTitration = new DText().editable(false);
    private final DScroll scrollTitration = new DScroll(textTitration);
    private final DText textTopics = new DText().editable(false);
    private final DScroll scrollTopics = new DScroll(textTopics);
    private final DSplitter splitterGroup = new DSplitter()
            .vertical().top(scrollTitration).bottom(scrollTopics)
            .divider(0.5f)
            .name("splitterGroup");

    private final DSplitter splitterClassGroup = new DSplitter()
            .vertical().top(splitterClass).bottom(splitterGroup)
            .divider(0.3f)
            .name("splitterClassGroup");

    private final DPane paneGroup = new DColPane().insets(2)
            .growHorizontal().put(paneGroupActs)
            .growBoth().put(splitterClassGroup);


    private final DButton buttonClear = new DButton("Clear")
            .onClick(this::buttonClearActionPerformed);
    private final DButton buttonClearAll = new DButton("All")
            .onClick(this::buttonClearAllActionPerformed);
    private final DButton buttonAsk = new DButton("Ask")
            .onClick(this::buttonAskActionPerformed);
    private final DButton buttonBring = new DButton("<")
            .onClick(this::buttonBringActionPerformed);
    private final DButton buttonWrite = new DButton("Write")
            .onClick(this::buttonWriteActionPerformed);
    private final DButton buttonSound = new DButton(">")
            .onClick(this::buttonSoundActionPerformed);
    
    private final DPane paneAskActs = new DRowPane().insets(2)
        .growNone().put(buttonClear)
        .growNone().put(buttonClearAll)
        .growHorizontal().put(buttonAsk)
        .growNone().put(buttonBring)
        .growNone().put(buttonWrite)
        .growNone().put(buttonSound);

    private final TextEditor textAsk = new TextEditor();
     
    private final DPane paneAsk = new DColPane().insets(2)
            .growHorizontal().put(paneAskActs)
            .growBoth().put(textAsk);


    private final DSplitter splitterBody = new DSplitter()
            .horizontal().left(paneGroup).right(paneAsk)
            .divider(0.5f)
            .name("splitterBody")
            .borderEmpty(7);


    private final SelectedRef selectedRef;

    private File selectedFile = null;

    
    public HelperExplains(SelectedRef selectedRef) {
        super("Helper Explains");
        this.selectedRef = selectedRef;
        body(splitterBody);
        comboGroup.clear();
        for (int i = 0; i < selectedRef.ref.groups.size(); i++) {
            comboGroup.add("Group " + String.format("%02d", i + 1));
        }
    }

    private void comboGroupActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index == -1 || index >= selectedRef.ref.groups.size()) {
            fieldClassTitle.setValue("");
            textTitration.setValue("");
            textTopics.setValue("");
            textAsk.setValue("");
            return;
        }
        var group = selectedRef.ref.groups.get(index);
        Integer orderInt = null;
        try {
            orderInt = Integer.parseInt(group.order.trim());
        } catch (Exception ex) {}
        fieldClassOrder.setValue(orderInt);
        fieldClassTitle.setValue(group.classification);
        textTitration.setValue(group.titration);
        textTopics.setValue(group.topics);
        textAsk.setValue("");
        buttonBringActionPerformed(e);
    }

    private void buttonClearActionPerformed(ActionEvent e) {
        try {
            var index = comboGroup.selectedIndex();
            if (index == -1 || index >= selectedRef.ref.groups.size()) {
                throw new Exception("Select a group to clear.");
            }
            var group = selectedRef.ref.groups.get(index);
            clearGroup(group);
            buttonBringActionPerformed(e);
            WizGUI.showNotify("Cleared.");
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void buttonClearAllActionPerformed(ActionEvent e) {
        try {
            if (!WizGUI.showConfirm("Are you sure to clear all?")) {
                return;
            }
            for (var group : selectedRef.ref.groups) {
                clearGroup(group);
            }
            buttonBringActionPerformed(e);
            WizGUI.showNotify("Cleared All.");
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void clearGroup(RefGroup group) throws Exception {
        var titrationFile = group.getTitrationFile(selectedRef.baseFolder);
        if (!titrationFile.exists()) {
            return;
        }
        var titrationData = ClassDatex.read(titrationFile);
        var folder = group.getClassificationFolder(selectedRef.baseFolder);
        for (var link : titrationData.explainsLinks) {
            var explainsFile = new File(folder, link + ".md");
            if (explainsFile.exists()) {
                if (!explainsFile.delete()) {
                    throw new Exception("Failed to delete explains file: " + explainsFile.getAbsolutePath());
                }
            }
        }
    }

    private void buttonAskActionPerformed(ActionEvent e) {
        if (buttonAsk.getText().equals("Asking...")) {
            return;
        }
        buttonAsk.setText("Asking...");
        new Thread("Explains Asking") {
            @Override
            public void run() {
                try {
                    var result = selectedRef.talkWithAttach(Steps.Explains.getCommand(getInsertion()));
                    SwingUtilities.invokeLater(() -> {
                        textAsk.setValue(result);
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

    private void buttonWriteActionPerformed(ActionEvent e) {
        try {
            var index = comboGroup.selectedIndex();
            if (index == -1 || index >= selectedRef.ref.groups.size()) {
                throw new Exception("Select a group to write.");
            }
            var source = textAsk.edit().getValue().trim();
            if (source.isBlank()) {
                throw new Exception("Ask for a content to write.");
            }
            for (var replace : Setup.getReplacesList(ReplaceAutoOn.OnExplains)) {
                source = replace.apply(source);
            }
            textAsk.edit().setValue(source);
            var group = selectedRef.ref.groups.get(index);
            var title = CKUtils.cleanFileName(WizString.getFirstLine(source)).trim();
            if (title.isBlank()) {
                throw new Exception("The first line of the content must have a title.");
            }
            if (!title.startsWith("^")) {
                title = "^ " + title;
            }
            if (!source.contains("*Refs:*")) {
                source = source + "\n\n*Refs:* " + selectedRef.ref.props.hashMD5;
            }
            var folder = group.getClassificationFolder(selectedRef.baseFolder);
            var explainsFile = new File(folder, title + ".md");
            WizText.write(explainsFile, source);
            selectedFile = explainsFile;
            var titrationFile = group.getTitrationFile(selectedRef.baseFolder);
            CKUtils.putMarkDownLink(titrationFile, title);
            group.explainsAt = WizUtilDate.formatDateMach(new Date());
            selectedRef.write();
            WizGUI.showNotify("Explains written.", 1);
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void buttonBringActionPerformed(ActionEvent e) {
        try {
            var index = comboGroup.selectedIndex();
            if (index == -1 || index >= selectedRef.ref.groups.size()) {
                throw new Exception("Select a group to clear.");
            }
            var group = selectedRef.ref.groups.get(index);            
            var titrationFile = group.getTitrationFile(selectedRef.baseFolder);
            var titrationData = ClassDatex.read(titrationFile);
            if (titrationData.explainsLinks.isEmpty()) {
                return;
            }
            var folder = group.getClassificationFolder(selectedRef.baseFolder);
            if (titrationData.explainsLinks.size() > 1) {
                var selectLink = new DListDesk<String>("Select a text to bring");
                selectLink.options(titrationData.explainsLinks);
                selectLink.onSelect(selected -> {
                    try {
                        if (selected == null || selected.isBlank()) {
                            return;
                        }
                        bringExplains(folder, selected);
                    } catch (Exception ei) {
                        WizGUI.showError(ei);
                    }
                });
                selectLink.setVisible(true);
            } else {
                var link = titrationData.explainsLinks.get(0);
                bringExplains(folder, link);
            }
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void buttonSoundActionPerformed(ActionEvent e) {
        try {
            var index = comboGroup.selectedIndex();
            if (index == -1 || index >= selectedRef.ref.groups.size()) {
                throw new Exception("There are no group selected.");
            }
            var group = selectedRef.ref.groups.get(index);
            if (selectedFile == null) {
                throw new Exception("There are no explains selected.");
            }
            var poolFolder = new File(selectedRef.baseFolder, "+ Pool");
            if (!poolFolder.exists()) {
                poolFolder.mkdirs();
            }
            var mp3Files = new ArrayList<File>();
            for (var file : poolFolder.listFiles()) {
                if (file.getName().endsWith(".mp3")) {
                    mp3Files.add(file);
                }
            }
            var selectedName = FilenameUtils.getBaseName(selectedFile.getName());
            File mp3File = null;
            var minDiff = Integer.MAX_VALUE;
            for (var file : mp3Files) {
                var mp3Name = FilenameUtils.getBaseName(file.getName());
                var difference = WizString.getDistanceWords(selectedName, mp3Name);
                if (mp3Name.equals(selectedName)) {
                    mp3File = file;
                    break;
                } else if (difference < minDiff) {
                    mp3File = file;
                    minDiff = difference;
                }
            }
            if (mp3File == null) {
                throw new Exception("No mp3 file found in pool folder.");
            }
            var targetFile = new File(selectedFile.getParentFile(), selectedName + ".mp3");
            Files.move(mp3File.toPath(), targetFile.toPath());
            var titrationFile = group.getTitrationFile(selectedRef.baseFolder);
            CKUtils.putMarkDownLink(titrationFile, targetFile.getName());
            WizGUI.showNotify("Sound inserted.", 1);
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void bringExplains(File folder, String link) throws Exception {
        var explainsFile = new File(folder, link + ".md");
        if (!explainsFile.exists()) {
            return;
        }
        var source = WizText.read(explainsFile);
        textAsk.setValue(source);
        selectedFile = explainsFile;
    }

    private String getInsertion() {
        var index = comboGroup.selectedIndex();
        if (index == -1 || index >= selectedRef.ref.groups.size()) {
            return "";
        }
        var group = selectedRef.ref.groups.get(index);
        return group.topics.trim();
    }

}
