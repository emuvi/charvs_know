package br.com.pointel.charvs_know;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import br.com.pointel.jarch.desk.DFrame;

public class SetupDesk extends DFrame {

    private JPanel panelBody = new JPanel();
    private JScrollPane scrollBody = new JScrollPane(panelBody);

    private JLabel labelGenaiModel = new JLabel("Genai Model:");
    private DefaultComboBoxModel<String> modelGenaiModel = new DefaultComboBoxModel<>(new String[] { "Gemini3Flash", "Gemini3Pro", "Gemini2Flash", "Gemini2Pro" });
    private JComboBox<String> comboGenaiModel = new JComboBox<>(modelGenaiModel);

    private JLabel labelTalkerKind = new JLabel("Talker Kind:");
    private DefaultComboBoxModel<String> modelTalkerKind = new DefaultComboBoxModel<>(new String[] { "Genai", "Clipboard" });
    private JComboBox<String> comboTalkerKind = new JComboBox<>(modelTalkerKind);
    
    public SetupDesk() {
        super("Setup");
        initComponents();
    }

    private void initComponents() {
        comboGenaiModel.setName("GenaiModel");
        comboTalkerKind.setName("TalkerKind");

        panelBody.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

        panelBody.setLayout(new GridLayout(2, 2, 2, 2));
        panelBody.add(labelGenaiModel);
        panelBody.add(comboGenaiModel);
        panelBody.add(labelTalkerKind);
        panelBody.add(comboTalkerKind);

        setContentPane(scrollBody);
        setLocationRelativeTo(null);
        pack();
    }

}
