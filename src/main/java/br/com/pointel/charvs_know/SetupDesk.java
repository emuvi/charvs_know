package br.com.pointel.charvs_know;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.mage.WizString;

public class SetupDesk extends DFrame {

    private JPanel panelBody = new JPanel();
    private JScrollPane scrollBody = new JScrollPane(panelBody);

    private JLabel labelGenaiModel = new JLabel("Genai Model:");
    private DefaultComboBoxModel<String> modelGenaiModel = new DefaultComboBoxModel<>(WizString.getFromEnum(SetupGenaiModel.class));
    private JComboBox<String> comboGenaiModel = new JComboBox<>(modelGenaiModel);

    private JLabel labelTalkerKind = new JLabel("Talker Kind:");
    private DefaultComboBoxModel<String> modelTalkerKind = new DefaultComboBoxModel<>(WizString.getFromEnum(SetupTalkerKind.class));
    private JComboBox<String> comboTalkerKind = new JComboBox<>(modelTalkerKind);

    private JLabel labelSounderKind = new JLabel("Sounder Kind:");
    private DefaultComboBoxModel<String> modelSounderKind = new DefaultComboBoxModel<>(WizString.getFromEnum(SetupSounderKind.class));
    private JComboBox<String> comboSounderKind = new JComboBox<>(modelSounderKind);

    private JLabel labelBalconVoice = new JLabel("Balcon Voice:");
    private JTextField textBalconVoice = new JTextField();
    
    public SetupDesk() {
        super("Setup");
        initComponents();
    }

    private void initComponents() {
        comboGenaiModel.setName("GenaiModel");
        comboTalkerKind.setName("TalkerKind");
        comboSounderKind.setName("SounderKind");
        textBalconVoice.setName("BalconVoice");

        panelBody.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

        panelBody.setLayout(new GridLayout(4, 2, 2, 2));
        panelBody.add(labelGenaiModel);
        panelBody.add(comboGenaiModel);
        panelBody.add(labelTalkerKind);
        panelBody.add(comboTalkerKind);
        panelBody.add(labelSounderKind);
        panelBody.add(comboSounderKind);
        panelBody.add(labelBalconVoice);
        panelBody.add(textBalconVoice);

        setContentPane(scrollBody);
        setLocationRelativeTo(null);
        pack();
    }

}
