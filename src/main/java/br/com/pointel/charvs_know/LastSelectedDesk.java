package br.com.pointel.charvs_know;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import br.com.pointel.jarch.desk.DColPane;
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.mage.WizGUI;

public class LastSelectedDesk extends DFrame {

    private final DefaultListModel<String> modelLastSelected = new DefaultListModel<>();
    private final JList<String> listLastSelected = new JList<>(modelLastSelected);
    private final JScrollPane scrollLastSelected = new JScrollPane(listLastSelected);

    private final DPane paneBody = new DColPane()
            .growBoth().put(scrollLastSelected)
            .borderEmpty(7);

    private final CharvsKnowDesk parent;

    public LastSelectedDesk(CharvsKnowDesk parent) {
        super("Last Selected");
        this.parent = parent;
        initComponents();
    }
    
    private void initComponents() {
        body(paneBody);
        var lastSelected = Setup.getLastSelectedRefs();
        for (var ref : lastSelected) {
            if (ref != null && !ref.isBlank()) {
                modelLastSelected.addElement(ref);
            }
        }
        listLastSelected.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    select();
                }
            }
        });
        listLastSelected.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    select();
                }
            }
        });
    }

    public void select() {
        try {
            var selected = listLastSelected.getSelectedValue();
            if (selected == null || selected.isBlank()) {
                return;
            }
            parent.selectRef(selected);
            WizGUI.close(this);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }


}
