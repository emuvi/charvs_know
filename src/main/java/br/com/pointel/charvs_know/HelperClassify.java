package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import br.com.pointel.jarch.desk.DButton;
import br.com.pointel.jarch.desk.DCheckEdit;
import br.com.pointel.jarch.desk.DColPane;
import br.com.pointel.jarch.desk.DComboEdit;
import br.com.pointel.jarch.desk.DEdit;
import br.com.pointel.jarch.desk.DFieldEdit;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DIntegerField;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.desk.DScroll;
import br.com.pointel.jarch.desk.DSplitter;
import br.com.pointel.jarch.desk.DText;
import br.com.pointel.jarch.mage.WizGUI;
import br.com.pointel.jarch.mage.WizUtilDate;

public class HelperClassify extends DFrame {

    private final DButton buttonClear = new DButton("Clear")
            .onAction(this::buttonClearActionPerformed);
    private final DButton buttonAsk = new DButton("Ask")
            .onAction(this::buttonAskActionPerformed);
    private final DButton buttonPaste = new DButton("Ʇ")
            .onAction(this::buttonPasteActionPerformed);
    private final DButton buttonParse = new DButton("Parse")
            .onAction(this::buttonParseActionPerformed);
    private final DButton buttonBring = new DButton("Bring")
            .onAction(this::buttonBringActionPerformed);
    
    private final DPane paneAskActs = new DRowPane().insets(2)
        .growNone().put(buttonClear)
        .growHorizontal().put(buttonAsk)
        .growNone().put(buttonPaste)
        .growNone().put(buttonParse)
        .growNone().put(buttonBring);

    private final TextEditor textAsk = new TextEditor();
     
    private final DPane paneAsk = new DColPane().insets(2)
            .growHorizontal().put(paneAskActs)
            .growBoth().put(textAsk);

    private final DButton buttonSame = new DButton("Same")
            .onAction(this::buttonSameActionPerformed);
    private final DButton buttonAuto = new DButton("Auto")
            .onAction(this::buttonAutoActionPerformed);
    private final DButton buttonSet = new DButton("Set")
            .onAction(this::buttonSetActionPerformed);
    private final DComboEdit<String> comboGroup = new DComboEdit<String>()
            .onAction(this::comboGroupActionPerformed);
    private final DEdit<Boolean> checkAutoSave = new DCheckEdit()
            .name("AutoSave");
    private final DButton buttonSave = new DButton("Save")
            .onAction(this::buttonSaveActionPerformed);
    private final DButton buttonWrite = new DButton("Write")
            .onAction(this::buttonWriteActionPerformed);
    private final DPane paneGroupActs = new DRowPane().insets(2)
            .growNone().put(buttonSame)
            .growNone().put(buttonAuto)
            .growNone().put(buttonSet)
            .growHorizontal().put(comboGroup)
            .growNone().put(checkAutoSave)
            .growNone().put(buttonSave)
            .growNone().put(buttonWrite);

    private final DEdit<Integer> fieldClassOrder = new DIntegerField()
            .cols(4).horizontalAlignmentCenter()
            .onFocusLost(this::callSaveOnFocusLost);
    private final TextEditor fieldClassTitle = new TextEditor()
            .onFocusLost(this::callSaveOnFocusLost);
    private final DSplitter splitterClass = new DSplitter()
            .horizontal().left(fieldClassOrder).right(fieldClassTitle)
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

    private final DSplitter splitterBody = new DSplitter()
            .horizontal().left(paneAsk).right(paneGroup)
            .divider(0.5f)
            .name("splitterBody")
            .borderEmpty(7);


    private final WorkRef workRef;

    
    public HelperClassify(WorkRef workRef) {
        super("Helper Classify");
        this.workRef = workRef;
        body(splitterBody);
        comboGroup.clear();
        for (int i = 0; i < workRef.ref.groups.size(); i++) {
            comboGroup.add("Group " + String.format("%02d", i + 1));
        }
        onFirstActivated(e -> buttonBringActionPerformed(null));
    }

    private void buttonClearActionPerformed(ActionEvent e) {
        for (var group : workRef.ref.groups) {
            group.clearClassified();
        }
        comboGroupActionPerformed(e);
    }

    private volatile AskThread askThread = null;

    private void buttonAskActionPerformed(ActionEvent e) {
        if (askThread != null) {
            askThread.stop = true;
            askThread = null;
            buttonAsk.setText("Ask");
        } else {
            askThread = new AskThread();
            askThread.start();
            buttonAsk.setText("Asking...");
        }
    }

    private void buttonPasteActionPerformed(ActionEvent e) {
        textAsk.edit().clear();
        textAsk.edit().paste();
    }

    private void buttonParseActionPerformed(ActionEvent e) {
        try {
            var source = textAsk.edit().getValue().trim();
            if (source.isBlank()) {
                return;
            }
            for (var replace : Setup.getReplacesList(ReplaceAutoOn.OnClassify)) {
                source = replace.apply(source);
            }
            textAsk.edit().setValue(source);
            var index = 0;
            var orders = List.copyOf(getOrders());
            var start = source.indexOf("[[");
            while (start > -1) {
                if (index >= orders.size()) {
                    break;
                }
                var end = source.indexOf("]]", start);
                if (end > -1) {
                    var classification = source.substring(start + 2, end).trim();
                    if (!classification.startsWith("-")) {
                        classification = "-" + classification;
                    }
                    classification = CKUtils.cleanFileName(classification);
                    var order = orders.get(index).toString();
                    for (var group : workRef.ref.groups) {
                        if (order.equals(group.order)) {
                            group.classification = classification;
                        }
                    }
                    start = source.indexOf("[[", end);
                    index++;
                } else {
                    break;
                }
            }
            comboGroupActionPerformed(e);
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void buttonBringActionPerformed(ActionEvent e) {
        var map = new LinkedHashMap<Integer, String>();
        for (var group : workRef.ref.groups) {
            if (group.order == null || group.order.isBlank()) {
                continue;
            }
            if (group.classification == null || group.classification.isBlank()) {
                continue;
            }
            try {
                var order = Integer.parseInt(group.order);
                map.put(order, group.classification);
            } catch (Exception ex) {}
        }
        var builder = new StringBuilder();
        for (var order : map.entrySet()) {
            builder.append("[[");
            builder.append(order.getValue());
            builder.append("]]\n\n");
        }
        textAsk.setValue(builder.toString());
    }

    private void buttonSameActionPerformed(ActionEvent e) {
        for (var group : workRef.ref.groups) {
            group.order = 1 + "";
        }
        comboGroupActionPerformed(e);
    }

    private void buttonAutoActionPerformed(ActionEvent e) {
        createOrders();
        comboGroupActionPerformed(e);
    }

    private void buttonSetActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index > -1) {
            fieldClassTitle.setValue(textAsk.edit().selectedText().trim());
            saveIfAutoSave(e != null ? e.getSource() : null);
        }
    }

    private void comboGroupActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index == -1 || index >= workRef.ref.groups.size()) {
            fieldClassTitle.setValue("");
            textTitration.setValue("");
            textTopics.setValue("");
            return;
        }
        var group = workRef.ref.groups.get(index);
        Integer orderInt = null;
        try {
            orderInt = Integer.parseInt(group.order.trim());
        } catch (Exception ex) {}
        fieldClassOrder.setValue(orderInt);
        fieldClassTitle.setValue(group.classification);
        textTitration.setValue(group.titration);
        textTopics.setValue(group.topics);
    }

    private void buttonSaveActionPerformed(ActionEvent e) {
        var index = comboGroup.selectedIndex();
        if (index == -1) {
            return;
        }
        var group = workRef.ref.groups.get(index);
        var orderStr = "";
        try {
            orderStr = fieldClassOrder.getValue().toString();
        } catch (Exception ex) {}
        group.order = orderStr;
        group.classification = fieldClassTitle.getValue().trim();
    }

    private void buttonWriteActionPerformed(ActionEvent e) {
        try {
            workRef.ref.props.classifiedAt = WizUtilDate.formatDateMach(new Date());
            for (var group : workRef.ref.groups) {
                group.hierarchy = getHierarchy(group.classification);
                group.writeClassification(workRef.baseFolder);
            }
            workRef.write();
            WizGUI.close(this);
        } catch (Exception ex) {
            WizGUI.showError(ex);
        }
    }

    private void callSaveOnFocusLost(FocusEvent e) {
        saveIfAutoSave(e != null ? e.getSource() : null);
    }

    private void saveIfAutoSave(Object source) {
        if (Boolean.TRUE.equals(checkAutoSave.value())) {
            buttonSaveActionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, "AutoSave"));
        }
    }

    private String getHierarchy(String ofClassification) {
        var builder = new StringBuilder("[[index]]");
        if (ofClassification == null || ofClassification.isBlank()) {
            return builder.toString();
        }
        var parts = ofClassification.split("\\-");
        var currentPath = new StringBuilder();
        for (var p : parts) {
            var part = p.trim();
            if (part.isBlank()) {
                continue;
            }
            var formattedPart = "- " + part;
            if (!currentPath.isEmpty()) {
                currentPath.append("/");
            }
            currentPath.append(formattedPart);
            builder.append(" [[").append(currentPath)
                   .append("/").append(formattedPart)
                   .append("]]");
        }
        return builder.toString();
    }

    private String getInsertion() {
        var result = new StringBuilder();
        var first = true;
        var orders = getOrders();
        for (var order : orders) {
            if (first) {
                first = false;
            } else {
                result.append("\n\n---\n\n");
            }
            result.append(getInsertion(order));
        }
        return result.toString();
    }

    private String getInsertion(Integer of) {
        var result = new StringBuilder();
        for (var group : workRef.ref.groups) {
            if (group.order == null || group.order.isBlank()) {
                continue;
            }
            if (!group.order.equals(of.toString())) {
                continue;
            }
            result.append(group.topics.trim());
            result.append("\n");
        }
        return result.toString();
    }

    private Set<Integer> getOrders() {
        var result = new LinkedHashSet<Integer>();
        for (var group : workRef.ref.groups) {
            if (group.order == null || group.order.isBlank()) {
                continue;
            }
            try {
                group.order = group.order.trim();
                var order = Integer.parseInt(group.order);
                result.add(order);
            } catch (Exception ex) {}
        }
        if (!result.isEmpty()) {
            return result;
        } else {
            return createOrders();
        }
    }

    private Set<Integer> createOrders() {
        var result = new LinkedHashSet<Integer>();
        var size = (int) Math.ceil(workRef.ref.groups.size() / 3.0);
        if (workRef.ref.groups.size() % 3 == 1) {
            size--;
        }
        Integer order = 1;
        int count = 0;
        for (var group : workRef.ref.groups) {
            group.order = order.toString();
            if (!result.contains(order)) {
                result.add(order);
            }
            count++;
            if (count % 3 == 0 && order < size) {
                order++;
            }
        }
        return result;
    }

    private class AskThread extends Thread {

        public volatile boolean stop = false;

        public AskThread() {
            super("Asking Identify");
        }

        @Override
        public void run() {
            try {
                var result = workRef.talkWithBase(Steps.Classify.getCommand(getInsertion()));
                if (stop) {
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    textAsk.setValue(result);
                    textAsk.edit().selectionStart(0);
                    textAsk.edit().selectionEnd(0);
                });
            } catch (Exception ex) {
                WizGUI.showError(ex);
            } finally {
                if (askThread == this) {
                    askThread = null;
                    SwingUtilities.invokeLater(() -> buttonAsk.setText("Ask"));
                }
            }
        }
    }

}
