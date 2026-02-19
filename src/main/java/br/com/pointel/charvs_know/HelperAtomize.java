package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Date;

import javax.swing.SwingUtilities;

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
import br.com.pointel.jarch.mage.WizGUI;
import br.com.pointel.jarch.mage.WizString;
import br.com.pointel.jarch.mage.WizText;
import br.com.pointel.jarch.mage.WizUtilDate;

public class HelperAtomize extends DFrame {

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
    private final DButton buttonWrite = new DButton("Write")
            .onClick(this::buttonWriteActionPerformed);
    private final DButton buttonBring = new DButton("Bring")
            .onClick(this::buttonBringActionPerformed);
    
    private final DPane paneAskActs = new DRowPane().insets(2)
        .growNone().put(buttonClear)
        .growNone().put(buttonClearAll)
        .growHorizontal().put(buttonAsk)
        .growNone().put(buttonWrite)
        .growNone().put(buttonBring);

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

    
    public HelperAtomize(SelectedRef selectedRef) {
        super("Helper Atomize");
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
        var startClass = fieldClassTitle.selectionStart();
        var endClass = fieldClassTitle.selectionEnd();
        var startTitration = textTitration.selectionStart();
        var endTitration = textTitration.selectionEnd();
        var startTopics = textTopics.selectionStart();
        var endTopics = textTopics.selectionEnd();
        fieldClassTitle.setValue(group.classification);
        textTitration.setValue(group.titration);
        textTopics.setValue(group.topics);
        textAsk.setValue("");
        fieldClassTitle.selectionStart(startClass);
        fieldClassTitle.selectionEnd(endClass);
        textTitration.selectionStart(startTitration);
        textTitration.selectionEnd(endTitration);
        textTopics.selectionStart(startTopics);
        textTopics.selectionEnd(endTopics);
    }

    private void buttonClearActionPerformed(ActionEvent e) {
        try {
            var index = comboGroup.selectedIndex();
            if (index == -1 || index >= selectedRef.ref.groups.size()) {
                throw new Exception("Select a group to clear.");
            }
            var group = selectedRef.ref.groups.get(index);
            clearGroup(group);
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
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void clearGroup(RefGroup group) throws Exception {
        var folder = group.getClassificationFolder(selectedRef.baseFolder);
        var titrationFile = group.getTitrationFile(selectedRef.baseFolder);
        var titrationLinks = CKUtils.getMarkDownLinks(titrationFile);
        for (var link : titrationLinks) {
            var atomicFile = new File(folder, link + ".md");
            if (atomicFile.exists()) {
                atomicFile.delete();
            }
        }
        titrationFile.delete();
        var classificationFile = group.getClassificationFile(selectedRef.baseFolder);
        CKUtils.delMarkDownLink(classificationFile, group.titration);
    }

    private void buttonAskActionPerformed(ActionEvent e) {
        if (buttonAsk.getText().equals("Asking...")) {
            return;
        }
        buttonAsk.setText("Asking...");
        new Thread("Atomize Asking") {
            @Override
            public void run() {
                try {
                    var result = selectedRef.talkWithAttach(Steps.Atomize.getCommand(getInsertion()));
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
            var source = textAsk.getValue().trim();
            if (source.isBlank()) {
                throw new Exception("Ask for a content to write.");
            }
            var group = selectedRef.ref.groups.get(index);
            var classFolder = group.getClassificationFolder(selectedRef.baseFolder);
            var atomicsSources = source.split("\\-\\-\\-");
            for (var atomicSource : atomicsSources) {
                atomicSource = atomicSource.trim();
                if (atomicSource.isBlank()) {
                    continue;
                }
                var name = WizString.getFirstLine(atomicSource);
                atomicSource = WizString.stripFirstLines(atomicSource, 1).trim();
                if (atomicSource.isBlank()) {
                    continue;
                }
                name = CKUtils.cleanFileName(name);
                var atomicFile = new File(classFolder, name + ".md");
                AtomicNote atomicNote;
                if (atomicFile.exists()) {
                    atomicNote = AtomicNote.read(atomicFile);
                    atomicNote.props.put("updated-at", WizUtilDate.formatDateMach(new Date()));
                } else {
                    atomicNote = new AtomicNote();
                    atomicNote.props.put("nature", "atomic");
                    atomicNote.props.put("created-at", WizUtilDate.formatDateMach(new Date()));
                }
                atomicNote.note = AtomicNote.extractNote(atomicSource);
                atomicNote.tags = AtomicNote.extractTags(atomicSource);
                atomicNote.refs = selectedRef.ref.props.hashMD5;
                AtomicNote.write(atomicNote, atomicFile);
                var titrationFile = group.getTitrationFile(selectedRef.baseFolder);
                CKUtils.putMarkDownLink(titrationFile, name);
                var classificationFile = group.getClassificationFile(selectedRef.baseFolder);
                CKUtils.putMarkDownLink(classificationFile, group.titration);
            }
            group.cardsAt = WizUtilDate.formatDateMach(new Date());
            selectedRef.write();
            WizGUI.showInfo("Written.");
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
            var folder = group.getClassificationFolder(selectedRef.baseFolder);
            var titrationFile = group.getTitrationFile(selectedRef.baseFolder);
            var titrationLinks = CKUtils.getMarkDownLinks(titrationFile);
            var builder = new StringBuilder();
            builder.append("---\n\n");
            for (var link : titrationLinks) {
                builder.append(link);
                var atomicFile = new File(folder, link + ".md");
                if (atomicFile.exists()) {
                    builder.append("\n\n");
                    var atomicNote = AtomicNote.read(atomicFile);
                    builder.append(atomicNote.note.trim());
                    if (atomicNote.tags != null && !atomicNote.tags.isBlank()) {
                        builder.append("\n\n*Tags:* ").append(atomicNote.tags.trim());
                    }
                    builder.append("\n\n");
                }
                builder.append("---\n\n");
            }
            var start = textAsk.edit().selectionStart();
            var end = textAsk.edit().selectionEnd();
            textAsk.setValue(builder.toString());
            textAsk.edit().selectionStart(start);
            textAsk.edit().selectionEnd(end);
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
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
