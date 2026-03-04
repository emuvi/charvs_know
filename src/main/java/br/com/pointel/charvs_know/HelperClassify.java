package br.com.pointel.charvs_know;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
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
import br.com.pointel.jarch.desk.DFrame;
import br.com.pointel.jarch.desk.DIntegerField;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.desk.DScroll;
import br.com.pointel.jarch.desk.DSplitter;
import br.com.pointel.jarch.desk.DText;
import br.com.pointel.jarch.mage.WizGUI;
import br.com.pointel.jarch.mage.WizRand;
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

    private final DButton buttonOrdBy = new DButton("OrdBy")
            .onAction(this::buttonOrdByActionPerformed);
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
            .growNone().put(buttonOrdBy)
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

    private void buttonOrdByActionPerformed(ActionEvent e) {
        var ordBy = WizGUI.showInput("Orders Groups By:", "3");
        if (ordBy == null || ordBy.isBlank()) {
            return;
        }
        createOrders(Double.parseDouble(ordBy));
        comboGroupActionPerformed(e);
    }

    private void buttonAutoActionPerformed(ActionEvent e) {
        createOrders(3);
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

    private String[] getInsertions() throws Exception {
        return new String[] {
            getInsertionClasses(),
            getInsertionTopics(),
        };
    }

    private String getInsertionClasses() throws Exception {
        var classes = CKUtils.putBrackets(CKUtils.getAllClassifications(workRef.baseFolder));
        while (classes.size() < 64) {
            classes.add(classifyExamples[WizRand.getInt(classifyExamples.length)]);
        }
        return String.join("\n", classes);
    }

    private String getInsertionTopics() {
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
            result.append(group.titration.trim());
            result.append("\n\n");
            result.append(group.topics.trim());
            result.append("\n\n");
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
            return createOrders(3);
        }
    }

    private Set<Integer> createOrders(double ordBy) {
        var result = new LinkedHashSet<Integer>();
        var size = (int) Math.ceil(workRef.ref.groups.size() / ordBy);
        var iOrdBy = (int) ordBy;
        if (workRef.ref.groups.size() % iOrdBy == 1) {
            size--;
        }
        if (size < 1) {
            size = 1;
        }
        Integer order = 1;
        int count = 0;
        for (var group : workRef.ref.groups) {
            group.order = order.toString();
            if (!result.contains(order)) {
                result.add(order);
            }
            count++;
            if (count % iOrdBy == 0 && order < size) {
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
                var result = workRef.talkWithBase(Steps.Classify.getCommand(getInsertions()));
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

    private static final String[] classifyExamples = new String[] {
        // =====================================================================
        // 000 - OBRAS GERAIS, METODOLOGIA E PESQUISA ACADÊMICA
        // =====================================================================
        "[[- Elementar - Ciência da Informação - Metodologia Científica - Tipos de Pesquisa (Qualitativa e Quantitativa)]]",
        "[[- Elementar - Ciência da Informação - Metodologia Científica - Normas ABNT para Trabalhos Acadêmicos]]",
        "[[- Elementar - Ciência da Informação - Metodologia Científica - Estrutura do Artigo Científico]]",
        "[[- Elementar - Ciência da Informação - Epistemologia da Informação - Sociedade da Informação e do Conhecimento]]",
        "[[- Funcional - Ciência da Informação - Propriedade Intelectual - Patentes e Direitos Autorais]]",

        // =====================================================================
        // 004 / 005 - CIÊNCIA DA COMPUTAÇÃO: FUNDAMENTOS E TEORIA
        // =====================================================================
        "[[- Elementar - Ciência da Computação - Teoria da Computação - Máquinas de Turing e Autômatos]]",
        "[[- Elementar - Ciência da Computação - Lógica Computacional - Álgebra Booleana e Portas Lógicas]]",
        "[[- Elementar - Ciência da Computação - Matemática Discreta - Teoria dos Grafos]]",
        "[[- Elementar - Ciência da Computação - Paradigmas de Linguagens - Programação Funcional (Cálculo Lambda)]]",
        "[[- Elementar - Ciência da Computação - Compiladores - Análise Léxica, Sintática e Semântica]]",

        // =====================================================================
        // CIÊNCIA DA COMPUTAÇÃO: ALGORITMOS E ESTRUTURAS DE DADOS
        // =====================================================================
        "[[- Funcional - Tecnologia da Informação - Algoritmos e Estruturas de Dados - Notação Big-O (Complexidade de Tempo e Espaço)]]",
        "[[- Funcional - Tecnologia da Informação - Algoritmos e Estruturas de Dados - Estruturas Lineares (Listas, Pilhas e Filas)]]",
        "[[- Funcional - Tecnologia da Informação - Algoritmos e Estruturas de Dados - Árvores de Busca Binária (BST) e Árvores AVL]]",
        "[[- Funcional - Tecnologia da Informação - Algoritmos e Estruturas de Dados - Tabelas de Espalhamento (Hash Tables) e Tratamento de Colisões]]",
        "[[- Funcional - Tecnologia da Informação - Algoritmos e Estruturas de Dados - Algoritmos de Ordenação (Merge Sort, Quick Sort, Heap Sort)]]",
        "[[- Funcional - Tecnologia da Informação - Algoritmos e Estruturas de Dados - Algoritmos de Caminho Mínimo (Dijkstra, Bellman-Ford)]]",

        // =====================================================================
        // CIÊNCIA DA COMPUTAÇÃO: ARQUITETURA E SISTEMAS OPERACIONAIS
        // =====================================================================
        "[[- Funcional - Tecnologia da Informação - Arquitetura de Computadores - Arquitetura de Von Neumann e Harvard]]",
        "[[- Funcional - Tecnologia da Informação - Arquitetura de Computadores - Hierarquia de Memória (Registradores, Cache L1/L2/L3, RAM)]]",
        "[[- Funcional - Tecnologia da Informação - Arquitetura de Computadores - Pipeline de Instruções e Processamento Paralelo]]",
        "[[- Funcional - Tecnologia da Informação - Sistemas Operacionais - Escalonamento de Processos (FCFS, SJF, Round Robin)]]",
        "[[- Funcional - Tecnologia da Informação - Sistemas Operacionais - Gerenciamento de Memória e Paginação Virtual]]",
        "[[- Funcional - Tecnologia da Informação - Sistemas Operacionais - Concorrência, Semáforos e Prevenção de Deadlocks]]",
        "[[- Funcional - Tecnologia da Informação - Sistemas Operacionais - Sistemas de Arquivos (NTFS, ext4, FAT32)]]",

        // =====================================================================
        // CIÊNCIA DA COMPUTAÇÃO: REDES DE COMPUTADORES
        // =====================================================================
        "[[- Elementar - Tecnologia da Informação - Redes de Computadores - Modelo de Referência OSI (As 7 Camadas)]]",
        "[[- Funcional - Tecnologia da Informação - Redes de Computadores - Arquitetura TCP/IP e Encapsulamento de Dados]]",
        "[[- Funcional - Tecnologia da Informação - Redes de Computadores - Protocolos de Aplicação (HTTP/HTTPS, FTP, SMTP, DNS)]]",
        "[[- Funcional - Tecnologia da Informação - Redes de Computadores - Endereçamento IPv4 (Sub-redes e CIDR) e IPv6]]",
        "[[- Funcional - Tecnologia da Informação - Redes de Computadores - Protocolos de Transporte (Diferenças entre TCP e UDP)]]",
        "[[- Funcional - Tecnologia da Informação - Redes de Computadores - Roteamento Dinâmico (OSPF, BGP)]]",
        "[[- Funcional - Tecnologia da Informação - Redes de Computadores - Redes Sem Fio e Protocolos IEEE 802.11 (Wi-Fi)]]",

        // =====================================================================
        // CIÊNCIA DA COMPUTAÇÃO: ENGENHARIA DE SOFTWARE E DEVOPS
        // =====================================================================
        "[[- Funcional - Tecnologia da Informação - Ciclo de Vida do Software - Modelos em Cascata, Espiral e Iterativo]]",
        "[[- Funcional - Tecnologia da Informação - Metodologias Ágeis - Framework Scrum (Papéis, Artefatos e Cerimônias)]]",
        "[[- Funcional - Tecnologia da Informação - Metodologias Ágeis - Sistema Kanban e Limite de WIP]]",
        "[[- Funcional - Tecnologia da Informação - Engenharia de Requisitos - Elicitação e Requisitos Funcionais vs Não Funcionais]]",
        "[[- Funcional - Tecnologia da Informação - Modelagem de Sistemas - Diagramas UML (Casos de Uso, Classes, Sequência)]]",
        "[[- Funcional - Tecnologia da Informação - Padrões de Projeto (Design Patterns) - Criacionais (Singleton, Factory)]]",
        "[[- Funcional - Tecnologia da Informação - Padrões de Projeto (Design Patterns) - Estruturais e Comportamentais (Observer, Strategy)]]",
        "[[- Funcional - Tecnologia da Informação - Arquitetura de Software - Arquitetura de Microsserviços vs Monolítica]]",
        "[[- Funcional - Tecnologia da Informação - Arquitetura de Software - Padrão MVC (Model-View-Controller) e API RESTful]]",
        "[[- Funcional - Tecnologia da Informação - Testes de Software - Testes Unitários, de Integração e TDD (Test-Driven Development)]]",
        "[[- Funcional - Tecnologia da Informação - DevOps - Integração e Entrega Contínuas (CI/CD)]]",
        "[[- Funcional - Tecnologia da Informação - DevOps - Contêineres e Orquestração (Docker e Kubernetes)]]",

        // =====================================================================
        // CIÊNCIA DA COMPUTAÇÃO: BANCO DE DADOS E BIG DATA
        // =====================================================================
        "[[- Elementar - Tecnologia da Informação - Banco de Dados - Álgebra Relacional e Cálculo Relacional]]",
        "[[- Funcional - Tecnologia da Informação - Banco de Dados - Modelagem Entidade-Relacionamento (MER/DER)]]",
        "[[- Funcional - Tecnologia da Informação - Banco de Dados - Normalização de Dados (1FN a 5FN e BCNF)]]",
        "[[- Funcional - Tecnologia da Informação - Banco de Dados - Propriedades ACID em Transações]]",
        "[[- Funcional - Tecnologia da Informação - Banco de Dados - Comandos SQL: DDL, DML, DCL e DQL]]",
        "[[- Funcional - Tecnologia da Informação - Banco de Dados - Otimização de Consultas (Query Tuning) e Índices B-Tree]]",
        "[[- Funcional - Tecnologia da Informação - Banco de Dados - Bancos NoSQL (Chave-Valor, Grafos, Colunares e Documentos)]]",
        "[[- Funcional - Tecnologia da Informação - Ciência de Dados - Data Warehouse, Modelagem Multidimensional (Star Schema) e OLAP]]",
        "[[- Funcional - Tecnologia da Informação - Ciência de Dados - Processos ETL (Extract, Transform, Load) e Data Lakes]]",
        "[[- Funcional - Tecnologia da Informação - Ciência de Dados - Ecossistema Hadoop e Processamento Distribuído (Apache Spark)]]",

        // =====================================================================
        // CIÊNCIA DA COMPUTAÇÃO: INTELIGÊNCIA ARTIFICIAL E MACHINE LEARNING
        // =====================================================================
        "[[- Funcional - Tecnologia da Informação - Inteligência Artificial - Algoritmos de Busca (A*, Minimax)]]",
        "[[- Funcional - Tecnologia da Informação - Machine Learning - Aprendizado Supervisionado (Regressão Linear e Logística, Árvores de Decisão)]]",
        "[[- Funcional - Tecnologia da Informação - Machine Learning - Aprendizado Não Supervisionado (Clusterização K-Means, PCA)]]",
        "[[- Funcional - Tecnologia da Informação - Machine Learning - Aprendizado por Reforço (Q-Learning e Processos de Decisão de Markov)]]",
        "[[- Funcional - Tecnologia da Informação - Deep Learning - Redes Neurais Convolucionais (CNNs) para Visão Computacional]]",
        "[[- Funcional - Tecnologia da Informação - Deep Learning - Redes Neurais Recorrentes (RNNs) e Transformadores (LLMs, GPT)]]",
        "[[- Funcional - Tecnologia da Informação - Inteligência Artificial - Processamento de Linguagem Natural (PLN) e Análise de Sentimentos]]",

        // =====================================================================
        // CIÊNCIA DA COMPUTAÇÃO: SEGURANÇA DA INFORMAÇÃO E GOVERNANÇA DE TI
        // =====================================================================
        "[[- Funcional - Tecnologia da Informação - Segurança da Informação - Princípios Básicos (Confidencialidade, Integridade e Disponibilidade - CID)]]",
        "[[- Funcional - Tecnologia da Informação - Segurança da Informação - Criptografia Simétrica (AES) e Assimétrica (RSA)]]",
        "[[- Funcional - Tecnologia da Informação - Segurança da Informação - Assinaturas Digitais e Infraestrutura de Chaves Públicas (ICP/PKI)]]",
        "[[- Funcional - Tecnologia da Informação - Segurança da Informação - Malwares (Ransomware, Trojan, Rootkit, Worms)]]",
        "[[- Funcional - Tecnologia da Informação - Segurança da Informação - Ataques de Rede (DDoS, Man-in-the-Middle, Spoofing)]]",
        "[[- Funcional - Tecnologia da Informação - Segurança da Informação - Testes de Invasão (Penetration Testing) e Ethical Hacking]]",
        "[[- Funcional - Tecnologia da Informação - Segurança da Informação - Controle de Acesso (RBAC, DAC, MAC) e Autenticação Multifator (MFA)]]",
        "[[- Funcional - Tecnologia da Informação - Governança de TI - Framework ITIL v4 (Práticas de Gerenciamento de Serviços)]]",
        "[[- Funcional - Tecnologia da Informação - Governança de TI - Framework COBIT 2019 (Alinhamento de TI ao Negócio)]]",

        // =====================================================================
        // 020 - BIBLIOTECONOMIA E CIÊNCIA DA INFORMAÇÃO
        // =====================================================================
        "[[- Elementar - Biblioteconomia - Fundamentos - As Cinco Leis de Ranganathan]]",
        "[[- Funcional - Biblioteconomia - Representação Descritiva - Regras de Catalogação (AACR2 e RDA)]]",
        "[[- Funcional - Biblioteconomia - Representação Temática - Classificação Decimal de Dewey (CDD)]]",
        "[[- Funcional - Biblioteconomia - Representação Temática - Classificação Decimal Universal (CDU)]]",
        "[[- Funcional - Biblioteconomia - Indexação e Resumo - Linguagens Documentárias e Tesauros]]",
        "[[- Funcional - Biblioteconomia - Formatos de Intercâmbio - Formato MARC 21 (Campos e Subcampos)]]",
        "[[- Funcional - Biblioteconomia - Serviço de Referência - Disseminação Seletiva da Informação (DSI) e Comutação Bibliográfica]]",
        "[[- Funcional - Biblioteconomia - Gestão de Unidades de Informação - Desenvolvimento e Avaliação de Coleções]]",
        "[[- Funcional - Biblioteconomia - Bibliometria - Leis Bibliométricas (Lotka, Bradford, Zipf)]]",

        // =====================================================================
        // 060 - ARQUIVOLOGIA, MUSEOLOGIA E ORGANIZAÇÕES
        // =====================================================================
        "[[- Elementar - Arquivologia - Teoria Arquivística - Princípio da Proveniência e da Ordem Original]]",
        "[[- Funcional - Arquivologia - Ciclo Vital dos Documentos - Teoria das Três Idades (Corrente, Intermediário, Permanente)]]",
        "[[- Funcional - Arquivologia - Gestão de Documentos - Classificação e Tabelas de Temporalidade Documental (TTD)]]",
        "[[- Funcional - Arquivologia - Descrição Arquivística - Norma Brasileira de Descrição Arquivística (NOBRADE) e ISAD(G)]]",
        "[[- Funcional - Arquivologia - Preservação Documental - Conservação Preventiva e Restauração de Suportes Físicos]]",
        "[[- Funcional - Arquivologia - Arquivos Digitais - Gestão Eletrônica de Documentos (GED) e Modelo OAIS]]",
        "[[- Funcional - Museologia - Gestão Museológica - Plano Museológico e Documentação de Acervos]]",
        "[[- Funcional - Museologia - Expografia - Conservação Preventiva em Exposições e Iluminação Museal]]",

        // =====================================================================
        // 070 - COMUNICAÇÃO SOCIAL, JORNALISMO E EDITORAÇÃO
        // =====================================================================
        "[[- Elementar - Comunicação Social - Teorias da Comunicação - Teoria Hipodérmica e Agenda-Setting]]",
        "[[- Elementar - Jornalismo - Ética Jornalística - Código de Ética e Deontologia Profissional]]",
        "[[- Funcional - Jornalismo - Redação Jornalística - Estrutura do Lead e Pirâmide Invertida]]",
        "[[- Funcional - Jornalismo - Gêneros Jornalísticos - Reportagem, Entrevista e Opinião]]",
        "[[- Funcional - Jornalismo - Webjornalismo - Arquitetura da Informação e SEO para Notícias]]",
        "[[- Funcional - Jornalismo - Assessoria de Imprensa - Media Training e Produção de Press Releases]]",
        "[[- Funcional - Editoração - Produção Editorial - Projeto Gráfico, Diagramação e Revisão de Provas]]",

        // =====================================================================
        // 100 - FILOSOFIA: METAFÍSICA, ONTOLOGIA E EPISTEMOLOGIA (110 e 120)
        // =====================================================================
        "[[- Elementar - Filosofia - Ontologia - O Ser e o Nada (Teorias do Ser)]]",
        "[[- Elementar - Filosofia - Ontologia - Substância, Essência e Acidente]]",
        "[[- Elementar - Filosofia - Metafísica - Cosmologia Filosófica e a Origem do Universo]]",
        "[[- Elementar - Filosofia - Metafísica - O Problema do Livre-Arbítrio e Determinismo]]",
        "[[- Elementar - Filosofia - Epistemologia - Teoria do Conhecimento (Sujeito e Objeto)]]",
        "[[- Elementar - Filosofia - Epistemologia - Empirismo (A Experiência Sensível)]]",
        "[[- Elementar - Filosofia - Epistemologia - Racionalismo (A Razão como Fonte de Conhecimento)]]",
        "[[- Elementar - Filosofia - Epistemologia - Criticismo e a Síntese Kantiana]]",
        "[[- Elementar - Filosofia - Epistemologia - Construtivismo e Paradigmas Científicos (Thomas Kuhn)]]",

        // =====================================================================
        // 140/180/190 - FILOSOFIA: HISTÓRIA E ESCOLAS FILOSÓFICAS
        // =====================================================================
        "[[- Elementar - Filosofia - Filosofia Antiga - Pré-Socráticos e a Busca pela Arché]]",
        "[[- Elementar - Filosofia - Filosofia Antiga - Sócrates: Ironia e Maiêutica]]",
        "[[- Elementar - Filosofia - Filosofia Antiga - Platão: Teoria das Ideias e o Mito da Caverna]]",
        "[[- Elementar - Filosofia - Filosofia Antiga - Aristóteles: Hilemorfismo (Matéria e Forma)]]",
        "[[- Elementar - Filosofia - Filosofia Antiga - Helenismo: Estoicismo, Epicurismo e Ceticismo]]",
        "[[- Elementar - Filosofia - Filosofia Medieval - Patrística: Santo Agostinho e a Teoria da Iluminação]]",
        "[[- Elementar - Filosofia - Filosofia Medieval - Escolástica: São Tomás de Aquino e as Cinco Vias]]",
        "[[- Elementar - Filosofia - Filosofia Moderna - Descartes: Dúvida Metódica e o Cogito]]",
        "[[- Elementar - Filosofia - Filosofia Moderna - John Locke: A Mente como Tábula Rasa]]",
        "[[- Elementar - Filosofia - Filosofia Moderna - David Hume: O Problema da Causalidade]]",
        "[[- Elementar - Filosofia - Filosofia Moderna - Hegel: Dialética (Tese, Antítese, Síntese)]]",
        "[[- Elementar - Filosofia - Filosofia Contemporânea - Nietzsche: Niilismo, Super-homem e Vontade de Potência]]",
        "[[- Elementar - Filosofia - Filosofia Contemporânea - Fenomenologia: Edmund Husserl e a Intencionalidade da Consciência]]",
        "[[- Elementar - Filosofia - Filosofia Contemporânea - Existencialismo: Sartre, Heidegger e a Condição Humana]]",
        "[[- Elementar - Filosofia - Filosofia Contemporânea - Escola de Frankfurt: Teoria Crítica e Indústria Cultural (Adorno e Horkheimer)]]",
        "[[- Elementar - Filosofia - Filosofia Contemporânea - Pós-Estruturalismo: Foucault (Microfísica do Poder) e Deleuze]]",

        // =====================================================================
        // 160 - LÓGICA FILOSÓFICA E FORMAL
        // =====================================================================
        "[[- Elementar - Filosofia - Lógica - Lógica Aristotélica: O Silogismo Categórico]]",
        "[[- Elementar - Filosofia - Lógica - Princípios Lógicos: Identidade, Não Contradição e Terceiro Excluído]]",
        "[[- Elementar - Filosofia - Lógica - Lógica Proposicional: Conectivos e Tabelas Verdade]]",
        "[[- Elementar - Filosofia - Lógica - Lógica de Predicados: Quantificadores Universais e Existenciais]]",
        "[[- Elementar - Filosofia - Lógica - Argumentação: Indução, Dedução e Abdução]]",
        "[[- Elementar - Filosofia - Lógica - Falácias Não Formais: Ad Hominem, Espantalho e Apelo à Ignorância]]",
        "[[- Funcional - Filosofia - Lógica Aplicada - Pensamento Crítico na Resolução de Problemas Complexos]]",

        // =====================================================================
        // 170 - ÉTICA (MORAL)
        // =====================================================================
        "[[- Elementar - Filosofia - Ética Teórica - Ética das Virtudes (Aristóteles)]]",
        "[[- Elementar - Filosofia - Ética Teórica - Deontologia: O Imperativo Categórico (Kant)]]",
        "[[- Elementar - Filosofia - Ética Teórica - Utilitarismo: O Princípio da Maior Felicidade (Bentham e Stuart Mill)]]",
        "[[- Elementar - Filosofia - Ética Teórica - Relativismo Moral vs Absolutismo Moral]]",
        "[[- Funcional - Filosofia - Ética Aplicada - Bioética: Eutanásia, Aborto e Clonagem Genética]]",
        "[[- Funcional - Filosofia - Ética Aplicada - Ética Profissional e Deontologia Corporativa]]",
        "[[- Funcional - Filosofia - Ética Aplicada - Ética na Inteligência Artificial e Algoritmos]]",

        // =====================================================================
        // 150 - PSICOLOGIA: TEORIAS DE BASE E SISTEMAS
        // =====================================================================
        "[[- Elementar - Psicologia - Psicanálise - Tópicas de Freud: Consciente, Pré-Consciente e Inconsciente]]",
        "[[- Elementar - Psicologia - Psicanálise - Estrutura Dinâmica: Id, Ego e Superego]]",
        "[[- Elementar - Psicologia - Psicanálise - Mecanismos de Defesa (Recalque, Projeção, Sublimação)]]",
        "[[- Elementar - Psicologia - Psicologia Analítica (Jung) - Inconsciente Coletivo e Arquétipos]]",
        "[[- Elementar - Psicologia - Behaviorismo - Condicionamento Clássico (Pavlov e Watson)]]",
        "[[- Elementar - Psicologia - Behaviorismo - Condicionamento Operante e Reforço (Skinner)]]",
        "[[- Elementar - Psicologia - Psicologia Humanista - Pirâmide das Necessidades (Maslow)]]",
        "[[- Elementar - Psicologia - Psicologia Humanista - Abordagem Centrada na Pessoa (Carl Rogers)]]",
        "[[- Elementar - Psicologia - Psicologia da Gestalt - Leis da Percepção Visual e Fechamento]]",

        // =====================================================================
        // 150 - PSICOLOGIA: DESENVOLVIMENTO, COGNITIVA E SOCIAL
        // =====================================================================
        "[[- Elementar - Psicologia - Psicologia do Desenvolvimento - Epistemologia Genética e Fases (Piaget)]]",
        "[[- Elementar - Psicologia - Psicologia do Desenvolvimento - Teoria Histórico-Cultural e ZDP (Vygotsky)]]",
        "[[- Elementar - Psicologia - Psicologia do Desenvolvimento - Desenvolvimento Psicossocial ao Longo da Vida (Erikson)]]",
        "[[- Elementar - Psicologia - Psicologia do Desenvolvimento - Psicanálise Infantil e o Objeto Transicional (Winnicott)]]",
        "[[- Elementar - Psicologia - Psicologia Cognitiva - Processos Mnemônicos (Memória de Trabalho, Curto e Longo Prazo)]]",
        "[[- Elementar - Psicologia - Psicologia Cognitiva - Atenção Sustentada, Alternada e Seletiva]]",
        "[[- Elementar - Psicologia - Psicologia Cognitiva - Funções Executivas e Córtex Pré-Frontal]]",
        "[[- Elementar - Psicologia - Psicologia Social - Formação de Atitudes, Preconceito e Estereótipos]]",
        "[[- Elementar - Psicologia - Psicologia Social - Dinâmica de Grupo e Conformidade (Experimento de Asch)]]",

        // =====================================================================
        // 158/159 - PSICOLOGIA: APLICAÇÕES CLÍNICAS E MERCADOLÓGICAS (FUNCIONAL)
        // =====================================================================
        "[[- Funcional - Psicologia - Psicologia Clínica - Terapia Cognitivo-Comportamental (TCC): Crenças Centrais e Distorções Cognitivas]]",
        "[[- Funcional - Psicologia - Psicologia Clínica - Entrevista Psicológica e Anamnese]]",
        "[[- Funcional - Psicologia - Psicopatologia - Transtornos do Neurodesenvolvimento (TEA, TDAH)]]",
        "[[- Funcional - Psicologia - Psicopatologia - Transtornos de Humor (Depressão Maior e Transtorno Bipolar)]]",
        "[[- Funcional - Psicologia - Psicopatologia - Transtornos de Personalidade (Borderline, Narcisista, Antissocial)]]",
        "[[- Funcional - Psicologia - Psicometria - Testes Projetivos (Rorschach, TAT, HTP)]]",
        "[[- Funcional - Psicologia - Psicometria - Testes de Inteligência e Cognitivos (WISC, WAIS)]]",
        "[[- Funcional - Psicologia - Psicometria - Testes de Personalidade (Palográfico, Quati, Big Five)]]",
        "[[- Funcional - Psicologia - Psicologia Organizacional - Recrutamento, Seleção e Entrevistas por Competência]]",
        "[[- Funcional - Psicologia - Psicologia Organizacional - Avaliação de Desempenho e Clima Organizacional]]",
        "[[- Funcional - Psicologia - Psicologia Organizacional - Saúde do Trabalhador e Síndrome de Burnout]]",
        "[[- Funcional - Psicologia - Psicologia Jurídica - Perícia Psicológica e Avaliação de Imputabilidade Penal]]",
        "[[- Funcional - Psicologia - Psicologia do Esporte - Gestão de Ansiedade e Alta Performance em Atletas]]",
        "[[- Funcional - Psicologia - Psicologia Hospitalar - Cuidados Paliativos e Processos de Luto (Kübler-Ross)]]",

        // =====================================================================
        // 200 / 210 - FILOSOFIA DA RELIGIÃO E TEOLOGIA NATURAL
        // =====================================================================
        "[[- Elementar - Religião - Fenomenologia da Religião - O Sagrado e o Profano (Mircea Eliade)]]",
        "[[- Elementar - Religião - Sociologia da Religião - Secularização e Laicidade do Estado]]",
        "[[- Elementar - Religião - Filosofia da Religião - Provas da Existência de Deus (Argumentos Ontológico e Cosmológico)]]",
        "[[- Elementar - Religião - Teologia Natural - Teodiceia e o Problema do Mal]]",
        "[[- Elementar - Religião - Teologia Natural - Criacionismo, Design Inteligente e Evolução Teísta]]",

        // =====================================================================
        // 220 - BÍBLIA E ESTUDOS ESCRITURÍSTICOS
        // =====================================================================
        "[[- Elementar - Religião - Estudos Bíblicos - Exegese, Hermenêutica e Contexto Histórico-Cultural]]",
        "[[- Elementar - Religião - Antigo Testamento - Pentateuco (Gênesis, Êxodo, Levítico, Números, Deuteronômio)]]",
        "[[- Elementar - Religião - Antigo Testamento - Livros Históricos (Josué, Juízes, Reis, Crônicas)]]",
        "[[- Elementar - Religião - Antigo Testamento - Livros Proféticos (Profetas Maiores e Menores)]]",
        "[[- Elementar - Religião - Antigo Testamento - Livros Sapienciais e Poéticos (Salmos, Provérbios, Jó)]]",
        "[[- Elementar - Religião - Novo Testamento - Evangelhos Sinóticos (Mateus, Marcos e Lucas) e Problema Sinótico]]",
        "[[- Elementar - Religião - Novo Testamento - Evangelho de João e Teologia Joanina]]",
        "[[- Elementar - Religião - Novo Testamento - Epístolas Paulinas e Justificação pela Fé]]",
        "[[- Elementar - Religião - Novo Testamento - Epístolas Gerais e Apocalipse de João]]",
        "[[- Elementar - Religião - Escritos Apócrifos - Evangelhos Gnósticos e Manuscritos do Mar Morto (Qumran)]]",

        // =====================================================================
        // 230 / 240 - TEOLOGIA CRISTÃ (DOGMÁTICA E MORAL)
        // =====================================================================
        "[[- Elementar - Teologia - Teologia Sistemática - Doutrina da Trindade (Pai, Filho e Espírito Santo)]]",
        "[[- Elementar - Teologia - Cristologia - União Hipostática (Naturezas Divina e Humana de Cristo)]]",
        "[[- Elementar - Teologia - Soteriologia - Doutrinas da Salvação, Graça e Expiação]]",
        "[[- Elementar - Teologia - Pneumatologia - Pessoa e Obra do Espírito Santo (Dons Espirituais)]]",
        "[[- Elementar - Teologia - Eclesiologia - Marcas da Igreja e Modelos de Governo (Episcopal, Presbiteriano, Congregacional)]]",
        "[[- Elementar - Teologia - Escatologia - Visões do Milênio (Amilenismo, Pré-Milenismo, Pós-Milenismo) e Juízo Final]]",
        "[[- Elementar - Teologia - Mariologia - Dogmas Marianos (Imaculada Conceição, Assunção, Theotokos)]]",
        "[[- Elementar - Teologia - Teologia Moral - Pecado Original, Hamartiologia e Virtudes Cardeais]]",
        "[[- Elementar - Teologia - Espiritualidade Cristã - Misticismo, Ascetismo e Disciplinas Espirituais]]",

        // =====================================================================
        // 250 / 260 - TEOLOGIA PRÁTICA E IGREJA LOCAL (APLICAÇÃO FUNCIONAL)
        // =====================================================================
        "[[- Funcional - Teologia - Homilética - Oratória Sagrada e Estruturação de Sermões Expositivos]]",
        "[[- Funcional - Teologia - Aconselhamento Pastoral - Psicologia Pastoral e Cuidado de Almas]]",
        "[[- Funcional - Teologia - Capelania - Capelania Hospitalar, Prisional e Escolar]]",
        "[[- Funcional - Teologia - Gestão Eclesiástica - Administração Financeira e Jurídica de Igrejas e ONGs]]",
        "[[- Funcional - Teologia - Liturgia - Estruturação de Cultos, Cerimônias e Missas]]",
        "[[- Funcional - Teologia - Missiologia - Estratégias de Plantação de Igrejas e Antropologia Missionária]]",
        "[[- Funcional - Teologia - Pedagogia Cristã - Escola Dominical, Catequese e Discipulado]]",
        "[[- Elementar - Teologia - Sacramentos e Ordenanças - Batismo e Eucaristia/Ceia do Senhor]]",

        // =====================================================================
        // 270 / 280 - HISTÓRIA DA IGREJA E DENOMINAÇÕES CRISTÃS
        // =====================================================================
        "[[- Elementar - História - História da Igreja - Igreja Primitiva, Perseguição e Apologistas (Séculos I ao IV)]]",
        "[[- Elementar - História - História da Igreja - Concílios Ecumênicos da Antiguidade (Niceia, Éfeso, Calcedônia)]]",
        "[[- Elementar - História - História da Igreja - Cisma do Oriente (1054) e o Surgimento da Igreja Ortodoxa]]",
        "[[- Elementar - História - História da Igreja - Inquisição, Cruzadas e Papado na Idade Média]]",
        "[[- Elementar - História - História da Igreja - Reforma Protestante (Lutero, Calvino, Zwinglio, Knox)]]",
        "[[- Elementar - História - História da Igreja - Contrarreforma Católica, Companhia de Jesus e Concílio de Trento]]",
        "[[- Elementar - Teologia - Denominações Cristãs - Catolicismo Romano (Magistério e Tradição)]]",
        "[[- Elementar - Teologia - Denominações Cristãs - Anglicanismo e a Igreja da Inglaterra]]",
        "[[- Elementar - Teologia - Denominações Cristãs - Protestantismo Histórico (Luteranos, Presbiterianos, Metodistas, Batistas)]]",
        "[[- Elementar - Teologia - Denominações Cristãs - Pentecostalismo, Neopentecostalismo e Movimento Carismático]]",

        // =====================================================================
        // 290 - OUTRAS RELIGIÕES, MITOLOGIA E TRADIÇÕES
        // =====================================================================
        "[[- Elementar - Mitologia - Mitologia Greco-Romana - O Panteão Olímpico e os Mitos Heroicos]]",
        "[[- Elementar - Mitologia - Mitologia Nórdica - Deuses Aesir, Vanir e a Escatologia do Ragnarök]]",
        "[[- Elementar - Religião - Judaísmo - Textos Sagrados (Torá, Tanakh e Talmude)]]",
        "[[- Elementar - Religião - Judaísmo - Correntes Contemporâneas (Ortodoxo, Conservador e Reformista)]]",
        "[[- Elementar - Religião - Islamismo - Os Cinco Pilares do Islã e a Leitura do Alcorão]]",
        "[[- Elementar - Religião - Islamismo - Divisão Histórica e Teológica (Sunitas e Xiitas)]]",
        "[[- Elementar - Religião - Hinduísmo - Textos Védicos, Upanishads e o Bhagavad Gita]]",
        "[[- Elementar - Religião - Hinduísmo - Sistema de Castas, Karma e o Ciclo de Samsara]]",
        "[[- Elementar - Religião - Budismo - As Quatro Nobres Verdades e o Nobre Caminho Óctuplo]]",
        "[[- Elementar - Religião - Budismo - Vertentes Filosóficas (Theravada, Mahayana e Vajrayana/Tibetano)]]",
        "[[- Elementar - Religião - Religiões Orientais - Taoísmo, Yin-Yang e o Tao Te Ching]]",
        "[[- Elementar - Religião - Religiões Orientais - Confucionismo e Xintoísmo (Religião Tradicional Japonesa)]]",
        "[[- Elementar - Religião - Religiões Afro-Diaspóricas - Candomblé, Orixás e o Culto de Nação]]",
        "[[- Elementar - Religião - Religiões Afro-Diaspóricas - Umbanda (Surgimento, Entidades e Sincretismo)]]",
        "[[- Elementar - Religião - Religiões Indígenas e Tribais - Animismo, Xamanismo e Totemismo]]",
        "[[- Elementar - Religião - Novos Movimentos Religiosos - Espiritismo (Codificação de Allan Kardec)]]",
        "[[- Elementar - Religião - Novos Movimentos Religiosos - Fé Bahá'í, Cientologia e Movimentos de Nova Era]]",
        "[[- Elementar - Religião - Novos Movimentos Religiosos - Esoterismo, Ocultismo e Sociedades Secretas]]",

        // =====================================================================
        // 300 / 310 - SOCIOLOGIA, ANTROPOLOGIA E DEMOGRAFIA
        // =====================================================================
        "[[- Elementar - Sociologia - Teoria Clássica - Fatos Sociais e Suicídio (Émile Durkheim)]]",
        "[[- Elementar - Sociologia - Teoria Clássica - Ação Social e Burocracia (Max Weber)]]",
        "[[- Elementar - Sociologia - Teoria Clássica - Materialismo Histórico e Luta de Classes (Karl Marx)]]",
        "[[- Elementar - Sociologia - Sociologia Brasileira - Casa-Grande & Senzala (Gilberto Freyre)]]",
        "[[- Elementar - Sociologia - Sociologia Brasileira - O Povo Brasileiro e a Miscigenação (Darcy Ribeiro)]]",
        "[[- Elementar - Sociologia - Sociologia Urbana - Especulação Imobiliária, Favelização e Gentrificação]]",
        "[[- Elementar - Sociologia - Movimentos Sociais - Feminismo, Movimento Negro e Lutas Sindicais]]",
        "[[- Elementar - Antropologia - Antropologia Cultural - Relativismo Cultural e Etnocentrismo]]",
        "[[- Elementar - Antropologia - Antropologia Estrutural - Parentesco e Tabu do Incesto (Lévi-Strauss)]]",
        "[[- Funcional - Demografia - Estatística Social - Pirâmides Etárias, Taxa de Fecundidade e Mortalidade]]",
        "[[- Funcional - Demografia - Censos e Pesquisas - Metodologia do IBGE e PNAD Contínua]]",

        // =====================================================================
        // 320 - CIÊNCIA POLÍTICA E RELAÇÕES INTERNACIONAIS
        // =====================================================================
        "[[- Elementar - Ciência Política - Teoria do Estado - Formas de Governo (Monarquia, República)]]",
        "[[- Elementar - Ciência Política - Teoria do Estado - Sistemas de Governo (Presidencialismo, Parlamentarismo)]]",
        "[[- Elementar - Ciência Política - Teoria Política Moderna - O Príncipe e a Realpolitik (Maquiavel)]]",
        "[[- Funcional - Ciência Política - Sistemas Eleitorais - Voto Majoritário, Proporcional e Quociente Eleitoral]]",
        "[[- Funcional - Ciência Política - Partidos Políticos - Pluripartidarismo, Fundo Partidário e Cláusula de Barreira]]",
        "[[- Funcional - Relações Internacionais - Geopolítica - Hegemonia Americana e Ascensão do BRICS]]",
        "[[- Funcional - Relações Internacionais - Organizações Internacionais - Conselho de Segurança da ONU e Poder de Veto]]",
        "[[- Funcional - Relações Internacionais - Comércio Exterior - Barreiras Tarifárias, OMC e Acordos Bilaterais]]",

        // =====================================================================
        // 330 - ECONOMIA (TEORIA E APLICAÇÃO)
        // =====================================================================
        "[[- Elementar - Economia - História do Pensamento Econômico - Fisiocracia e Mercantilismo]]",
        "[[- Elementar - Economia - História do Pensamento Econômico - Mão Invisível e Liberalismo Clássico (Adam Smith)]]",
        "[[- Elementar - Economia - História do Pensamento Econômico - Teoria Geral do Emprego e Estado de Bem-Estar (Keynes)]]",
        "[[- Funcional - Economia - Microeconomia - Lei da Oferta e da Demanda e Ponto de Equilíbrio]]",
        "[[- Funcional - Economia - Microeconomia - Elasticidade-Preço da Demanda e Bens Substitutos/Complementares]]",
        "[[- Funcional - Economia - Microeconomia - Estruturas de Mercado (Monopólio, Oligopólio, Concorrência Perfeita)]]",
        "[[- Funcional - Economia - Macroeconomia - Contabilidade Social e Cálculo do PIB (Óticas da Produção, Renda e Despesa)]]",
        "[[- Funcional - Economia - Macroeconomia - Instrumentos de Política Monetária (Taxa Selic, Redesconto, Compulsório)]]",
        "[[- Funcional - Economia - Macroeconomia - Política Fiscal e Curva de Laffer (Carga Tributária vs Arrecadação)]]",
        "[[- Funcional - Economia - Economia do Trabalho - Taxa de Desemprego (Aberto e Oculto) e Curva de Phillips]]",
        "[[- Funcional - Economia - Sistema Financeiro Nacional - Papel do Banco Central (BACEN) e da CVM]]",

        // =====================================================================
        // 340 - DIREITO (CONSTITUCIONAL, ADMINISTRATIVO, CIVIL, PENAL, ETC.)
        // =====================================================================
        "[[- Elementar - Direito - Teoria Geral do Direito - Fontes do Direito (Lei, Jurisprudência, Costumes, Doutrina)]]",
        "[[- Elementar - Direito - Teoria Geral do Direito - Positivismo Jurídico (Hans Kelsen e a Norma Fundamental)]]",
        "[[- Funcional - Direito - Direito Constitucional - Direitos e Deveres Individuais e Coletivos (Art. 5º da CF)]]",
        "[[- Funcional - Direito - Direito Constitucional - Remédios Constitucionais (Habeas Corpus, Mandado de Segurança, Mandado de Injunção)]]",
        "[[- Funcional - Direito - Direito Constitucional - Controle de Constitucionalidade (ADI, ADC, ADPF e Súmula Vinculante)]]",
        "[[- Funcional - Direito - Direito Administrativo - Organização Administrativa (Administração Direta e Indireta)]]",
        "[[- Funcional - Direito - Direito Administrativo - Atos Administrativos (Requisitos: Competência, Finalidade, Forma, Motivo, Objeto)]]",
        "[[- Funcional - Direito - Direito Administrativo - Licitações e Contratos Administrativos (Nova Lei 14.133/2021)]]",
        "[[- Funcional - Direito - Direito Administrativo - Agentes Públicos e Regime Jurídico Único (Lei 8.112/90)]]",
        "[[- Funcional - Direito - Direito Administrativo - Improbidade Administrativa (Lei 8.429/92)]]",
        "[[- Funcional - Direito - Direito Civil - Lei de Introdução às Normas do Direito Brasileiro (LINDB)]]",
        "[[- Funcional - Direito - Direito Civil - Capacidade Civil, Personalidade e Emancipação]]",
        "[[- Funcional - Direito - Direito Civil - Negócio Jurídico (Defeitos: Erro, Dolo, Coação, Fraude contra Credores)]]",
        "[[- Funcional - Direito - Direito Civil - Direito das Obrigações (Dar, Fazer e Não Fazer)]]",
        "[[- Funcional - Direito - Direito Civil - Responsabilidade Civil (Dano Moral, Material e Nexo de Causalidade)]]",
        "[[- Funcional - Direito - Direito Civil - Direito de Família (Regimes de Bens e Pensão Alimentícia)]]",
        "[[- Funcional - Direito - Direito Penal - Teoria do Delito (Fato Típico, Ilicitude e Culpabilidade)]]",
        "[[- Funcional - Direito - Direito Penal - Crimes contra a Vida (Homicídio, Infanticídio, Aborto)]]",
        "[[- Funcional - Direito - Direito Penal - Crimes contra o Patrimônio (Furto, Roubo, Estelionato, Extorsão)]]",
        "[[- Funcional - Direito - Direito Penal - Crimes contra a Administração Pública (Corrupção Passiva, Peculato, Concussão)]]",
        "[[- Funcional - Direito - Direito Processual Penal - Inquérito Policial e Ação Penal (Pública e Privada)]]",
        "[[- Funcional - Direito - Direito Processual Penal - Prisões Cautelares (Flagrante, Preventiva e Temporária)]]",
        "[[- Funcional - Direito - Direito do Trabalho - Relação de Emprego (Subordinação, Habitualidade, Onerosidade, Pessoalidade)]]",
        "[[- Funcional - Direito - Direito do Trabalho - Remuneração e Salário (Adicionais de Insalubridade e Periculosidade)]]",
        "[[- Funcional - Direito - Direito do Trabalho - Rescisão do Contrato de Trabalho e Aviso Prévio]]",
        "[[- Funcional - Direito - Direito Tributário - Princípios Tributários (Legalidade, Anterioridade, Noventena)]]",
        "[[- Funcional - Direito - Direito Tributário - Suspensão, Extinção e Exclusão do Crédito Tributário]]",
        "[[- Funcional - Direito - Direitos Difusos e Coletivos - Direito do Consumidor (CDC) e Responsabilidade do Fornecedor]]",
        "[[- Funcional - Direito - Direitos Difusos e Coletivos - Estatuto da Criança e do Adolescente (ECA) e Medidas Socioeducativas]]",

        // =====================================================================
        // 350 - ADMINISTRAÇÃO PÚBLICA, GESTÃO E ADMINISTRAÇÃO GERAL
        // =====================================================================
        "[[- Elementar - Administração - Teorias da Administração - Administração Científica (Taylor) e Clássica (Fayol)]]",
        "[[- Elementar - Administração - Teorias da Administração - Teoria das Relações Humanas (Experiência de Hawthorne)]]",
        "[[- Funcional - Administração - Planejamento Estratégico - Análise de Cenários (Matriz SWOT) e Balanced Scorecard (BSC)]]",
        "[[- Funcional - Administração - Gestão de Projetos - Guia PMBOK (Escopo, Tempo, Custos e Qualidade)]]",
        "[[- Funcional - Administração - Gestão de Processos - Mapeamento de Processos (BPMN) e Ciclo PDCA]]",
        "[[- Funcional - Administração - Gestão de Pessoas - Avaliação de Desempenho (360 Graus e 9-Box)]]",
        "[[- Funcional - Administração - Gestão de Pessoas - Políticas de Remuneração e Benefícios Flexíveis]]",
        "[[- Funcional - Administração - Logística e Supply Chain - Gestão de Estoques (Curva ABC e Just-in-Time)]]",
        "[[- Funcional - Administração Pública - Modelos de Gestão Pública - Administração Patrimonialista, Burocrática e Gerencial]]",
        "[[- Funcional - Administração Pública - Políticas Públicas - Ciclo das Políticas Públicas (Agenda, Formulação, Implementação, Avaliação)]]",

        // =====================================================================
        // CONTABILIDADE (Geralmente incluída nas Sociais Aplicadas)
        // =====================================================================
        "[[- Funcional - Contabilidade - Contabilidade Geral - Princípios Contábeis (Entidade, Competência, Continuidade)]]",
        "[[- Funcional - Contabilidade - Contabilidade Geral - Plano de Contas e Escrituração (Método das Partidas Dobradas)]]",
        "[[- Funcional - Contabilidade - Demonstrações Contábeis - Balanço Patrimonial (Ativo, Passivo e Patrimônio Líquido)]]",
        "[[- Funcional - Contabilidade - Demonstrações Contábeis - Demonstração do Resultado do Exercício (DRE) e EBITDA]]",
        "[[- Funcional - Contabilidade - Contabilidade Pública - Orçamento Público (PPA, LDO, LOA e Lei de Responsabilidade Fiscal)]]",
        "[[- Funcional - Contabilidade - Contabilidade Pública - Estágios da Receita e da Despesa Pública (Empenho, Liquidação, Pagamento)]]",
        "[[- Funcional - Contabilidade - Auditoria e Perícia - Papéis de Trabalho, Parecer de Auditoria e Risco de Imagem]]",

        // =====================================================================
        // 360 - SERVIÇO SOCIAL, PATOLOGIA SOCIAL E CRIMINOLOGIA
        // =====================================================================
        "[[- Elementar - Assistência Social - Fundamentos Históricos - Origem do Serviço Social no Brasil (Ação Católica e Estado Varguista)]]",
        "[[- Funcional - Assistência Social - Políticas Sociais - Sistema Único de Assistência Social (SUAS) e CRAS/CREAS]]",
        "[[- Funcional - Assistência Social - Seguridade Social - Previdência Social, Assistência Social e Saúde (Art. 194 da CF)]]",
        "[[- Funcional - Criminologia - Vitimologia - Vitimização Primária, Secundária e Terciária]]",
        "[[- Funcional - Criminologia - Teorias Sociológicas da Criminalidade - Teoria da Anomia, Escola de Chicago e Labelling Approach]]",

        // =====================================================================
        // 370 - EDUCAÇÃO E PEDAGOGIA
        // =====================================================================
        "[[- Elementar - Educação - História da Educação - Educação Jesuítica e Reformas Pombalinas no Brasil]]",
        "[[- Elementar - Educação - Tendências Pedagógicas - Pedagogia Liberal (Tradicional, Renovada, Tecnicista)]]",
        "[[- Elementar - Educação - Tendências Pedagógicas - Pedagogia Progressista (Libertadora de Paulo Freire, Crítico-Social dos Conteúdos)]]",
        "[[- Elementar - Educação - Psicologia da Educação - Construtivismo (Piaget), Sociointeracionismo (Vygotsky) e Aprendizagem Significativa (Ausubel)]]",
        "[[- Funcional - Educação - Legislação Educacional - Lei de Diretrizes e Bases da Educação Nacional (LDB - Lei 9.394/96)]]",
        "[[- Funcional - Educação - Legislação Educacional - Base Nacional Comum Curricular (BNCC) e Novo Ensino Médio]]",
        "[[- Funcional - Educação - Didática e Metodologia - Planejamento de Ensino (Plano de Aula, de Unidade e de Curso)]]",
        "[[- Funcional - Educação - Didática e Metodologia - Instrumentos de Avaliação (Diagnóstica, Formativa e Somativa)]]",
        "[[- Funcional - Educação - Gestão Escolar - Projeto Político-Pedagógico (PPP) e Gestão Democrática]]",
        "[[- Funcional - Educação - Educação Especial e Inclusiva - Atendimento Educacional Especializado (AEE) e Tecnologias Assistivas]]",

        // =====================================================================
        // 400 / 410 - LINGUÍSTICA GERAL, TEORIA DA LINGUAGEM E FILOLOGIA
        // =====================================================================
        "[[- Elementar - Linguística - Fundamentos da Linguística - Estruturalismo (Signo, Significante e Significado de Saussure)]]",
        "[[- Elementar - Linguística - Fundamentos da Linguística - Gramática Gerativa (A Estrutura Profunda de Chomsky)]]",
        "[[- Elementar - Linguística - Fonética e Fonologia - Aparelho Fonador e Ponto de Articulação das Consoantes]]",
        "[[- Elementar - Linguística - Fonética e Fonologia - Fonemas, Alones e Arquifonemas]]",
        "[[- Elementar - Linguística - Morfologia Linguística - Morfemas Lexicais e Gramaticais (Raiz, Afixos, Desinências)]]",
        "[[- Elementar - Linguística - Sintaxe Teórica - Árvores Sintáticas e Teoria X-barra]]",
        "[[- Elementar - Linguística - Semântica - Relações Semânticas (Sinonímia, Antonímia, Polissemia, Homonímia)]]",
        "[[- Elementar - Linguística - Pragmática - Teoria dos Atos de Fala (Locucionário, Ilocucionário, Perlocucionário)]]",
        "[[- Elementar - Linguística - Pragmática - Máximas Conversacionais de Grice (Quantidade, Qualidade, Relação, Modo)]]",
        "[[- Elementar - Linguística - Sociolinguística - Variação Linguística (Diatópica, Diastrática, Diafásica, Diacrônica)]]",
        "[[- Elementar - Linguística - Sociolinguística - Preconceito Linguístico e Diglossia]]",
        "[[- Elementar - Linguística - Psicolinguística - Aquisição da Linguagem e Bilinguismo Cognitivo]]",
        "[[- Elementar - Filologia - História da Língua - Etimologia e Evolução do Latim Vulgar para as Línguas Românicas]]",

        // =====================================================================
        // LINGUÍSTICA APLICADA E TRADUÇÃO (APLICAÇÃO FUNCIONAL)
        // =====================================================================
        "[[- Funcional - Linguística - Análise do Discurso - Análise Crítica do Discurso (ACD) e Ideologia Midiática]]",
        "[[- Funcional - Linguística - Análise do Discurso - Formações Discursivas e Interdiscursividade (Pêcheux)]]",
        "[[- Funcional - Linguística - Tradução e Interpretação - Técnicas de Tradução (Empréstimo, Calque, Transposição, Modulação)]]",
        "[[- Funcional - Linguística - Tradução e Interpretação - Uso de CAT Tools (Trados, MemoQ) e Tradução Automática]]",
        "[[- Funcional - Linguística - Ensino de Línguas - Abordagem Comunicativa e Ensino Baseado em Tarefas (TBLT)]]",

        // =====================================================================
        // 469 - LÍNGUA PORTUGUESA: FONÉTICA, ORTOGRAFIA E MORFOLOGIA
        // =====================================================================
        "[[- Elementar - Português - Fonologia e Ortografia - Encontros Vocálicos (Ditongo, Tritongo, Hiato) e Consonantais]]",
        "[[- Elementar - Português - Fonologia e Ortografia - Dígrafos Vocaicos e Consonantais]]",
        "[[- Elementar - Português - Fonologia e Ortografia - Divisão Silábica e Translineação]]",
        "[[- Elementar - Português - Fonologia e Ortografia - Acentuação Gráfica (Regras das Oxítonas, Paroxítonas e Proparoxítonas)]]",
        "[[- Elementar - Português - Fonologia e Ortografia - Novo Acordo Ortográfico (Fim do Trema, Novas Regras de Hífen)]]",
        "[[- Elementar - Português - Fonologia e Ortografia - Uso dos Porquês, Mal/Mau, Onde/Aonde, Mas/Mais]]",
        "[[- Elementar - Português - Morfologia - Formação de Palavras (Derivação Prefixal, Sufixal, Parassintética, Imprópria)]]",
        "[[- Elementar - Português - Morfologia - Classes: Substantivos (Classificação, Gênero, Número e Grau)]]",
        "[[- Elementar - Português - Morfologia - Classes: Adjetivos (Locuções Adjetivas e Superlativos)]]",
        "[[- Elementar - Português - Morfologia - Classes: Pronomes (Pessoais, Possessivos, Demonstrativos, Indefinidos)]]",
        "[[- Elementar - Português - Morfologia - Classes: Pronomes Relativos (Que, Quem, Qual, Onde, Cujo)]]",
        "[[- Elementar - Português - Morfologia - Classes: Verbos (Tempos e Modos do Indicativo, Subjuntivo e Imperativo)]]",
        "[[- Elementar - Português - Morfologia - Classes: Verbos (Vozes Verbais: Ativa, Passiva Analítica/Sintética, Reflexiva)]]",
        "[[- Elementar - Português - Morfologia - Classes: Verbos (Verbos Irregulares, Defectivos e Anômalos)]]",
        "[[- Elementar - Português - Morfologia - Classes: Conjunções (Coordenativas e Subordinativas)]]",
        "[[- Elementar - Português - Morfologia - Classes: Preposições, Advérbios e Interjeições]]",

        // =====================================================================
        // 469 - LÍNGUA PORTUGUESA: SINTAXE E ESTILÍSTICA
        // =====================================================================
        "[[- Elementar - Português - Sintaxe do Período Simples - Tipos de Sujeito (Simples, Composto, Oculto, Indeterminado, Inexistente)]]",
        "[[- Elementar - Português - Sintaxe do Período Simples - Predicação Verbal (VI, VTD, VTI, VTDI, Verbo de Ligação)]]",
        "[[- Elementar - Português - Sintaxe do Período Simples - Complementos Verbais (Objeto Direto e Indireto) e Predicativos]]",
        "[[- Elementar - Português - Sintaxe do Período Simples - Adjunto Adnominal vs. Complemento Nominal]]",
        "[[- Elementar - Português - Sintaxe do Período Simples - Adjunto Adverbial, Aposto e Vocativo]]",
        "[[- Elementar - Português - Sintaxe do Período Composto - Orações Coordenadas (Sindéticas e Assindéticas)]]",
        "[[- Elementar - Português - Sintaxe do Período Composto - Orações Subordinadas Substantivas]]",
        "[[- Elementar - Português - Sintaxe do Período Composto - Orações Subordinadas Adjetivas (Restritivas e Explicativas)]]",
        "[[- Elementar - Português - Sintaxe do Período Composto - Orações Subordinadas Adverbiais (Causais, Condicionais, Concessivas, etc.)]]",
        "[[- Elementar - Português - Sintaxe de Concordância - Concordância Verbal (Regra Geral e Casos Especiais com 'Haver' e 'Fazer')]]",
        "[[- Elementar - Português - Sintaxe de Concordância - Concordância Nominal (Casos com 'Anexo', 'Meio', 'Bastante', 'Proibido')]]",
        "[[- Elementar - Português - Sintaxe de Regência - Regência Verbal (Verbos Assistir, Visar, Aspirar, Preferir)]]",
        "[[- Elementar - Português - Sintaxe de Regência - Regência Nominal]]",
        "[[- Elementar - Português - Sintaxe - Crase (Regra Geral, Casos Proibidos, Obrigatórios e Facultativos)]]",
        "[[- Elementar - Português - Sintaxe de Colocação - Colocação Pronominal (Próclise, Mesóclise e Ênclise)]]",
        "[[- Elementar - Português - Pontuação - Uso da Vírgula (Deslocamento de Adjuntos, Enumerações, Vocativos)]]",
        "[[- Elementar - Português - Pontuação - Uso do Ponto e Vírgula, Dois-Pontos e Aspas]]",

        // =====================================================================
        // INTERPRETAÇÃO, REDAÇÃO E COMUNICAÇÃO APLICADA
        // =====================================================================
        "[[- Elementar - Português - Interpretação de Texto - Tipologia Textual (Narrações, Descrições, Dissertações, Injunções)]]",
        "[[- Elementar - Português - Interpretação de Texto - Gêneros Textuais (Notícia, Crônica, Artigo de Opinião, Editorial)]]",
        "[[- Elementar - Português - Interpretação de Texto - Coesão Referencial (Anáfora e Catáfora)]]",
        "[[- Elementar - Português - Interpretação de Texto - Coesão Sequencial (Uso de Conectivos e Articuladores do Discurso)]]",
        "[[- Elementar - Português - Interpretação de Texto - Coerência Textual (Princípio da Não Contradição)]]",
        "[[- Elementar - Português - Interpretação de Texto - Funções da Linguagem (Referencial, Emotiva, Conativa, Metalinguística, Fática, Poética)]]",
        "[[- Elementar - Português - Interpretação de Texto - Figuras de Linguagem (Metáfora, Metonímia, Paradoxo, Ironia, Eufemismo)]]",
        "[[- Elementar - Português - Interpretação de Texto - Níveis de Leitura (Compreensão Literal, Inferencial e Crítica)]]",
        "[[- Funcional - Português - Redação Oficial - Manual de Redação da Presidência da República (Pronomes de Tratamento)]]",
        "[[- Funcional - Português - Redação Oficial - Estrutura do Padrão Ofício (Aviso, Memorando e Ofício)]]",
        "[[- Funcional - Português - Redação Oficial - Atributos da Redação Oficial (Clareza, Concisão, Impessoalidade)]]",
        "[[- Funcional - Português - Comunicação Empresarial - Elaboração de E-mails Corporativos, Relatórios e Atas]]",

        // =====================================================================
        // LÍNGUAS DE SINAIS E INCLUSÃO (LIBRAS)
        // =====================================================================
        "[[- Funcional - LIBRAS - Estrutura da Língua - Parâmetros da LIBRAS (Configuração de Mão, Ponto de Articulação, Movimento)]]",
        "[[- Funcional - LIBRAS - Estrutura da Língua - Orientação/Direcionalidade e Expressões Não Manuais (Facial/Corporal)]]",
        "[[- Funcional - LIBRAS - Gramática - Estrutura Sintática (Tópico-Comentário e Ordem SOV/SVO)]]",
        "[[- Funcional - LIBRAS - Vocabulário - Alfabeto Manual (Datilologia) e Numerais em LIBRAS]]",
        "[[- Funcional - LIBRAS - Legislação e Inclusão - Lei de LIBRAS (Lei nº 10.436/2002) e o Papel do Intérprete (TILS)]]",

        // =====================================================================
        // 420 - LÍNGUA INGLESA E IDIOMAS ESTRANGEIROS
        // =====================================================================
        "[[- Elementar - Inglês - Gramática - Simple Present e Present Continuous (Verbos Auxiliares Do/Does, Am/Is/Are)]]",
        "[[- Elementar - Inglês - Gramática - Simple Past e Past Continuous (Verbos Regulares e Irregulares)]]",
        "[[- Elementar - Inglês - Gramática - Present Perfect e Present Perfect Continuous (Uso de Since, For, Yet, Already)]]",
        "[[- Elementar - Inglês - Gramática - Future Tenses (Will, Going to, Future Continuous)]]",
        "[[- Elementar - Inglês - Gramática - Modal Verbs (Can, Could, May, Might, Must, Should)]]",
        "[[- Elementar - Inglês - Gramática - Conditionals (Zero, First, Second, Third and Mixed Conditionals)]]",
        "[[- Elementar - Inglês - Gramática - Passive Voice (Voz Passiva em Diferentes Tempos Verbais)]]",
        "[[- Elementar - Inglês - Gramática - Reported Speech (Discurso Indireto e Mudança de Tempos Verbais)]]",
        "[[- Elementar - Inglês - Gramática - Nouns and Pronouns (Countable/Uncountable, Quantifiers: Much/Many, Some/Any)]]",
        "[[- Elementar - Inglês - Gramática - Adjectives and Adverbs (Comparatives and Superlatives)]]",
        "[[- Elementar - Inglês - Vocabulário - Phrasal Verbs, Idioms e Collocations Comuns]]",
        "[[- Elementar - Inglês - Vocabulário - Falsos Cognatos (False Friends: Pretend, Actually, Fabric)]]",
        "[[- Funcional - Inglês - Inglês Instrumental - Técnicas de Leitura Rápida (Skimming e Scanning)]]",
        "[[- Funcional - Inglês - Inglês Instrumental - Inferência Lexical e Marcadores Discursivos (Linkers)]]",
        "[[- Funcional - Inglês - Business English - Terminologia Corporativa, E-mails Formais e Apresentações Profissionais]]",
        "[[- Funcional - Inglês - Certificações - Preparação Estratégica para TOEFL, IELTS e Cambridge Exams]]",

        // =====================================================================
        // 460 - LÍNGUA ESPANHOLA E OUTRAS LÍNGUAS CLÁSSICAS
        // =====================================================================
        "[[- Elementar - Espanhol - Gramática e Ortografia - Regras de Acentuação (Agudas, Llanas, Esdrújulas)]]",
        "[[- Elementar - Espanhol - Gramática e Ortografia - Artigos (El, La, Los, Las, Lo Neutro) e Contrações (Al, Del)]]",
        "[[- Elementar - Espanhol - Gramática e Ortografia - Verbos Regulares e Irregulares no Presente de Indicativo (Diptongación)]]",
        "[[- Elementar - Espanhol - Gramática e Ortografia - Pretérito Indefinido vs. Pretérito Perfecto Compuesto]]",
        "[[- Elementar - Espanhol - Vocabulário - Heterossemânticos (Falsos Amigos: Embarazada, Exquisito, Pelado)]]",
        "[[- Elementar - Espanhol - Vocabulário - Heterogenéricos (El Viaje, La Leche, La Sangre)]]",
        "[[- Elementar - Espanhol - Vocabulário - Heterotônicos (Magia, Cerebro, Policía)]]",
        "[[- Funcional - Espanhol - Espanhol Instrumental - Compreensão de Textos Acadêmicos e Jornalísticos (DELE/SIELE)]]",
        "[[- Elementar - Línguas Clássicas - Latim - Casos Gramaticais (Nominativo, Genitivo, Dativo, Acusativo, Ablativo, Vocativo)]]",
        "[[- Elementar - Línguas Clássicas - Latim - As Cinco Declinações Nominativas e Radicais Etimológicos]]",
        "[[- Elementar - Línguas Clássicas - Grego Antigo - O Alfabeto Grego e a Formação de Prefixos e Sufixos Científicos]]",

        // =====================================================================
        // 510 - MATEMÁTICA: ARITMÉTICA, ÁLGEBRA E GEOMETRIA
        // =====================================================================
        "[[- Elementar - Matemática - Aritmética - Operações Básicas, Frações e Números Decimais]]",
        "[[- Elementar - Matemática - Aritmética - Mínimo Múltiplo Comum (MMC) e Máximo Divisor Comum (MDC)]]",
        "[[- Elementar - Matemática - Aritmética - Razão, Proporção e Regra de Três (Simples e Composta)]]",
        "[[- Elementar - Matemática - Teoria dos Números - Números Primos, Fatoração e Crivo de Eratóstenes]]",
        "[[- Elementar - Matemática - Álgebra Elementar - Produtos Notáveis e Fatoração Algébrica]]",
        "[[- Elementar - Matemática - Álgebra Elementar - Equações e Inequações do 1º e 2º Grau (Fórmula de Bhaskara)]]",
        "[[- Elementar - Matemática - Funções - Domínio, Imagem e Estudo do Sinal (Função Afim e Quadrática)]]",
        "[[- Elementar - Matemática - Funções - Funções Exponenciais, Logarítmicas e Propriedades dos Logaritmos]]",
        "[[- Elementar - Matemática - Álgebra Linear - Matrizes (Determinantes, Teorema de Laplace e Regra de Sarrus)]]",
        "[[- Elementar - Matemática - Álgebra Linear - Sistemas Lineares (Regra de Cramer e Escalonamento)]]",
        "[[- Elementar - Matemática - Números Complexos - Forma Algébrica, Forma Trigonométrica e Fórmula de Moivre]]",
        "[[- Elementar - Matemática - Geometria Plana - Teorema de Pitágoras, Semelhança de Triângulos e Relações Métricas]]",
        "[[- Elementar - Matemática - Geometria Plana - Polígonos Regulares, Ângulos Internos e Cálculo de Áreas]]",
        "[[- Elementar - Matemática - Geometria Plana - Circunferência e Círculo (Comprimento, Área e Setor Circular)]]",
        "[[- Elementar - Matemática - Geometria Espacial - Poliedros de Platão e Relação de Euler]]",
        "[[- Elementar - Matemática - Geometria Espacial - Prismas, Pirâmides e Cilindros (Área Superficial e Volume)]]",
        "[[- Elementar - Matemática - Geometria Espacial - Cones e Esferas (Troncos e Volumes)]]",
        "[[- Elementar - Matemática - Geometria Analítica - Estudo do Ponto e da Reta (Coeficiente Angular e Linear)]]",
        "[[- Elementar - Matemática - Geometria Analítica - Cônicas (Circunferência, Elipse, Parábola e Hipérbole)]]",
        "[[- Elementar - Matemática - Trigonometria - Ciclo Trigonométrico (Seno, Cosseno e Tangente)]]",
        "[[- Elementar - Matemática - Trigonometria - Lei dos Senos, Lei dos Cossenos e Relação Fundamental]]",

        // =====================================================================
        // 510 - MATEMÁTICA: CÁLCULO, PROBABILIDADE E ESTATÍSTICA APLICADA
        // =====================================================================
        "[[- Elementar - Matemática - Análise Combinatória - Princípio Fundamental da Contagem (Fatorial)]]",
        "[[- Elementar - Matemática - Análise Combinatória - Permutações, Arranjos e Combinações Simples/Com Repetição]]",
        "[[- Elementar - Matemática - Probabilidade - Espaço Amostral, Eventos e Probabilidade Condicional]]",
        "[[- Elementar - Matemática - Cálculo Diferencial e Integral - Limites e Continuidade de Funções]]",
        "[[- Elementar - Matemática - Cálculo Diferencial e Integral - Derivadas (Regra da Cadeia e Regra do Produto/Quociente)]]",
        "[[- Elementar - Matemática - Cálculo Diferencial e Integral - Integrais Indefinidas e Definidas (Teorema Fundamental do Cálculo)]]",
        "[[- Elementar - Matemática - Cálculo Diferencial e Integral - Equações Diferenciais Ordinárias (EDOs)]]",
        "[[- Funcional - Matemática - Estatística Descritiva - Medidas de Tendência Central (Média, Moda e Mediana)]]",
        "[[- Funcional - Matemática - Estatística Descritiva - Medidas de Dispersão (Variância, Desvio Padrão e Coeficiente de Variação)]]",
        "[[- Funcional - Matemática - Estatística Inferencial - Distribuições de Probabilidade (Normal/Gaussiana, Binomial e Poisson)]]",
        "[[- Funcional - Matemática - Estatística Inferencial - Testes de Hipótese (Testes T de Student, Qui-Quadrado e ANOVA)]]",
        "[[- Funcional - Matemática - Matemática Financeira - Juros Simples, Compostos e Taxas Equivalentes]]",
        "[[- Funcional - Matemática - Matemática Financeira - Sistemas de Amortização (SAC, Tabela Price e SAM)]]",

        // =====================================================================
        // 520 - ASTRONOMIA E ASTROFÍSICA
        // =====================================================================
        "[[- Elementar - Astronomia - Mecânica Celeste - Leis de Kepler (Órbitas, Áreas e Períodos)]]",
        "[[- Elementar - Astronomia - Sistema Solar - Planetas Rochosos, Gasosos e Corpos Menores (Asteroides e Cometas)]]",
        "[[- Elementar - Astronomia - Astrofísica Estelar - Ciclo de Vida das Estrelas (Anãs Brancas, Supernovas e Buracos Negros)]]",
        "[[- Elementar - Astronomia - Cosmologia - Teoria do Big Bang, Radiação Cósmica de Fundo e Expansão do Universo]]",

        // =====================================================================
        // 530 - FÍSICA: MECÂNICA, TERMODINÂMICA E ELETROMAGNETISMO
        // =====================================================================
        "[[- Elementar - Física - Cinemática - Movimento Retilíneo Uniforme (MRU) e Uniformemente Variado (MRUV)]]",
        "[[- Elementar - Física - Cinemática - Lançamento Oblíquo, Horizontal e Queda Livre]]",
        "[[- Elementar - Física - Cinemática - Movimento Circular Uniforme (MCU) e Aceleração Centrípeta]]",
        "[[- Elementar - Física - Dinâmica - As Três Leis de Newton e Força de Atrito (Estático e Cinético)]]",
        "[[- Elementar - Física - Dinâmica - Força Elástica (Lei de Hooke) e Plano Inclinado]]",
        "[[- Elementar - Física - Trabalho e Energia - Energia Cinética, Potencial (Gravitacional e Elástica) e Teorema do Trabalho]]",
        "[[- Elementar - Física - Impulso e Quantidade de Movimento - Conservação da Quantidade de Movimento e Colisões]]",
        "[[- Elementar - Física - Gravitação Universal - Força Gravitacional de Newton e Campo Gravitacional]]",
        "[[- Elementar - Física - Estática e Hidrostática - Torque (Momento de uma Força) e Centro de Massa]]",
        "[[- Elementar - Física - Estática e Hidrostática - Pressão Atmosférica, Teorema de Stevin, Pascal e Empuxo (Arquimedes)]]",
        "[[- Elementar - Física - Termologia - Escalas Termométricas (Celsius, Kelvin, Fahrenheit) e Dilatação Térmica]]",
        "[[- Elementar - Física - Calorimetria - Calor Sensível, Calor Latente e Trocas de Calor]]",
        "[[- Elementar - Física - Termodinâmica - Estudo dos Gases Ideais (Equação de Clapeyron e Transformações Gasosas)]]",
        "[[- Elementar - Física - Termodinâmica - Primeira Lei (Conservação de Energia) e Segunda Lei (Entropia e Máquinas Térmicas)]]",
        "[[- Elementar - Física - Óptica Geométrica - Reflexão da Luz, Espelhos Planos e Esféricos (Côncavos e Convexos)]]",
        "[[- Elementar - Física - Óptica Geométrica - Refração da Luz (Lei de Snell-Descartes), Lentes e Instrumentos Ópticos]]",
        "[[- Elementar - Física - Ondulatória - Natureza e Propagação das Ondas (Frequência, Comprimento, Velocidade)]]",
        "[[- Elementar - Física - Ondulatória - Acústica (Qualidades Fisiológicas do Som e Efeito Doppler)]]",
        "[[- Elementar - Física - Eletrostática - Carga Elétrica, Processos de Eletrização e Lei de Coulomb]]",
        "[[- Elementar - Física - Eletrostática - Campo Elétrico, Potencial Elétrico e Superfícies Equipotenciais]]",
        "[[- Elementar - Física - Eletrodinâmica - Corrente Elétrica, Tensão, Resistência e as Leis de Ohm]]",
        "[[- Elementar - Física - Eletrodinâmica - Associação de Resistores (Série, Paralelo e Mista) e Potência Elétrica]]",
        "[[- Elementar - Física - Eletrodinâmica - Geradores, Receptores, Capacitores e Leis de Kirchhoff]]",
        "[[- Elementar - Física - Eletromagnetismo - Campo Magnético gerado por Corrente (Fio, Espira e Solenoide)]]",
        "[[- Elementar - Física - Eletromagnetismo - Força Magnética (Regra da Mão Direita) e Indução Eletromagnética (Lei de Faraday-Lenz)]]",
        "[[- Elementar - Física - Física Moderna - Relatividade Restrita (Dilatação do Tempo e Contração do Espaço)]]",
        "[[- Elementar - Física - Física Moderna - Mecânica Quântica (Dualidade Onda-Partícula e Efeito Fotoelétrico)]]",
        "[[- Elementar - Física - Física Moderna - Física Nuclear (Radioatividade, Fissão e Fusão Nuclear)]]",

        // =====================================================================
        // 540 - QUÍMICA: GERAL, FÍSICO-QUÍMICA E ORGÂNICA
        // =====================================================================
        "[[- Elementar - Química - Estrutura Atômica - Modelos Atômicos (Dalton, Thomson, Rutherford, Bohr e Sommerfeld)]]",
        "[[- Elementar - Química - Estrutura Atômica - Partículas Subatômicas (Prótons, Nêutrons, Elétrons, Isótopos)]]",
        "[[- Elementar - Química - Tabela Periódica - Classificação dos Elementos e Propriedades Periódicas (Eletronegatividade e Raio Atômico)]]",
        "[[- Elementar - Química - Ligações Químicas - Regra do Octeto, Ligações Iônicas, Covalentes e Metálicas]]",
        "[[- Elementar - Química - Geometria Molecular - Teoria da Repulsão (VSEPR), Polaridade e Forças Intermoleculares]]",
        "[[- Elementar - Química - Funções Inorgânicas - Ácidos, Bases, Sais e Óxidos (Nomenclatura e Classificação)]]",
        "[[- Elementar - Química - Estequiometria - Leis Ponderais (Lavoisier e Proust) e Cálculo Estequiométrico (Mol, Massa Molar)]]",
        "[[- Elementar - Química - Soluções - Tipos de Soluções, Concentração Comum, Molaridade e Título]]",
        "[[- Elementar - Química - Propriedades Coligativas - Ebulioscopia, Crioscopia, Tonoscopia e Pressão Osmótica]]",
        "[[- Elementar - Química - Termoquímica - Reações Exotérmicas, Endotérmicas, Entalpia e Lei de Hess]]",
        "[[- Elementar - Química - Cinética Química - Velocidade das Reações, Energia de Ativação e Catalisadores]]",
        "[[- Elementar - Química - Equilíbrio Químico - Constante de Equilíbrio (Kc e Kp) e Princípio de Le Chatelier]]",
        "[[- Elementar - Química - Equilíbrio Químico - Equilíbrio Iônico, Cálculo de pH e pOH, e Soluções Tampão]]",
        "[[- Elementar - Química - Eletroquímica - Pilhas (Células Galvânicas, Ânodo, Cátodo e DDP)]]",
        "[[- Elementar - Química - Eletroquímica - Eletrólise Ígnea e Aquosa (Leis de Faraday)]]",
        "[[- Elementar - Química - Química Orgânica - Postulados de Kekulé, Hibridização do Carbono e Cadeias Carbônicas]]",
        "[[- Elementar - Química - Química Orgânica - Funções Hidrocarbonetos (Alcanos, Alcenos, Alcinos, Aromáticos)]]",
        "[[- Elementar - Química - Química Orgânica - Funções Oxigenadas (Álcoois, Fenóis, Éteres, Aldeídos, Cetonas, Ácidos Carboxílicos e Ésteres)]]",
        "[[- Elementar - Química - Química Orgânica - Funções Nitrogenadas (Aminas, Amidas, Nitrilas e Nitrocompostos)]]",
        "[[- Elementar - Química - Química Orgânica - Isomeria Plana (Cadeia, Posição, Função, Compensação e Tautomeria)]]",
        "[[- Elementar - Química - Química Orgânica - Isomeria Espacial (Geométrica Cis-Trans/E-Z e Isomeria Óptica/Quiralidade)]]",
        "[[- Elementar - Química - Química Orgânica - Reações Orgânicas (Adição, Substituição, Eliminação e Oxidação)]]",
        "[[- Funcional - Química - Química Analítica - Métodos Volumétricos (Titulação Ácido-Base e Redox)]]",
        "[[- Funcional - Química - Química Analítica - Métodos Instrumentais (Cromatografia e Espectrofotometria)]]",

        // =====================================================================
        // 550 / 560 - CIÊNCIAS DA TERRA, GEOCIÊNCIAS E PALEONTOLOGIA
        // =====================================================================
        "[[- Elementar - Geociências - Geologia - Estrutura Interna da Terra (Crosta, Manto, Núcleo)]]",
        "[[- Elementar - Geociências - Geologia - Tectônica de Placas, Deriva Continental e Sismos]]",
        "[[- Elementar - Geociências - Mineralogia e Petrologia - Rochas Ígneas, Metamórficas, Sedimentares e o Ciclo das Rochas]]",
        "[[- Funcional - Geociências - Meteorologia e Climatologia - Circulação Atmosférica, Massas de Ar e Previsão do Tempo]]",
        "[[- Elementar - Geociências - Oceanografia - Relevo Submarino, Correntes Marítimas e Marés]]",
        "[[- Elementar - Geociências - Paleontologia - Fósseis, Tafonomia e a Escala de Tempo Geológico (Éons, Eras, Períodos)]]",

        // =====================================================================
        // 570 / 580 / 590 - CIÊNCIAS BIOLÓGICAS (BIOLOGIA, BOTÂNICA E ZOOLOGIA)
        // =====================================================================
        "[[- Elementar - Biologia - Bioquímica - Água, Sais Minerais, Carboidratos e Lipídios]]",
        "[[- Elementar - Biologia - Bioquímica - Proteínas (Estrutura, Função e Enzimas)]]",
        "[[- Elementar - Biologia - Bioquímica - Ácidos Nucleicos (DNA, RNA e Replicação)]]",
        "[[- Elementar - Biologia - Citologia - Membrana Plasmática (Estrutura e Transportes: Osmose, Difusão, Bomba de Sódio/Potássio)]]",
        "[[- Elementar - Biologia - Citologia - Organelas Citoplasmáticas (Mitocôndrias, Ribossomos, Lisossomos, Complexo de Golgi)]]",
        "[[- Elementar - Biologia - Citologia - Bioenergética (Respiração Celular, Fermentação e Fotossíntese)]]",
        "[[- Elementar - Biologia - Citologia - Divisão Celular (Ciclo Celular, Mitose e Meiose)]]",
        "[[- Elementar - Biologia - Histologia Animal - Tecidos Epitelial, Conjuntivo, Muscular e Nervoso]]",
        "[[- Elementar - Biologia - Genética - Primeira e Segunda Lei de Mendel (Monoibridismo e Diibridismo)]]",
        "[[- Elementar - Biologia - Genética - Alelos Múltiplos (Sistema ABO e Fator Rh) e Herança Ligada ao Sexo]]",
        "[[- Elementar - Biologia - Genética - Linkage (Ligação Gênica) e Mutações Cromossômicas (Aneuploidias)]]",
        "[[- Elementar - Biologia - Evolução - Evidências da Evolução (Órgãos Homólogos/Análogos e Órgãos Vestigiais)]]",
        "[[- Elementar - Biologia - Evolução - Teorias Evolutivas (Lamarckismo, Darwinismo e Teoria Sintética/Neodarwinismo)]]",
        "[[- Elementar - Biologia - Evolução - Genética de Populações (Teorema de Hardy-Weinberg) e Especiação]]",
        "[[- Elementar - Biologia - Ecologia - Conceitos Básicos (Habitat, Nicho Ecológico, População e Comunidade)]]",
        "[[- Elementar - Biologia - Ecologia - Cadeias e Teias Alimentares, e Níveis Tróficos]]",
        "[[- Elementar - Biologia - Ecologia - Dinâmica de Populações e Relações Ecológicas (Harmônicas e Desarmônicas)]]",
        "[[- Elementar - Biologia - Ecologia - Ciclos Biogeoquímicos (Ciclo da Água, Carbono, Nitrogênio e Fósforo)]]",
        "[[- Elementar - Biologia - Ecologia - Sucessão Ecológica e Biomas Terrestres/Brasileiros]]",
        "[[- Funcional - Biologia - Biotecnologia - Tecnologia do DNA Recombinante, Clonagem e Transgênicos]]",
        "[[- Funcional - Biologia - Virologia e Microbiologia - Estrutura Viral, Bacterioses e Resistência a Antibióticos]]",
        "[[- Elementar - Botânica - Taxonomia Vegetal - Briófitas, Pteridófitas, Gimnospermas e Angiospermas]]",
        "[[- Elementar - Botânica - Morfologia Vegetal - Raiz, Caule, Folha, Flor, Fruto e Semente]]",
        "[[- Elementar - Botânica - Fisiologia Vegetal - Condução de Seiva (Xilema e Floema) e Transpiração]]",
        "[[- Elementar - Botânica - Fisiologia Vegetal - Hormônios Vegetais (Auxinas, Giberelinas, Etileno) e Fototropismo]]",
        "[[- Elementar - Zoologia - Invertebrados - Poríferos, Cnidários, Platelmintos e Nematelmintos]]",
        "[[- Elementar - Zoologia - Invertebrados - Moluscos, Anelídeos, Artrópodes (Insetos, Aracnídeos, Crustáceos) e Equinodermos]]",
        "[[- Elementar - Zoologia - Cordados e Vertebrados - Peixes (Condrictes e Osteíctes) e Anfíbios]]",
        "[[- Elementar - Zoologia - Cordados e Vertebrados - Répteis, Aves e Mamíferos (Anatomia Comparada)]]",

        // =====================================================================
        // 610 - MEDICINA, ENFERMAGEM E SAÚDE PÚBLICA
        // =====================================================================
        "[[- Elementar - Medicina - Anatomia Humana - Sistema Nervoso Central e Periférico]]",
        "[[- Elementar - Medicina - Anatomia Humana - Sistema Cardiovascular e Circulação Sistêmica/Pulmonar]]",
        "[[- Elementar - Medicina - Fisiologia Humana - Sistema Endócrino e Eixo Hipotálamo-Hipófise]]",
        "[[- Elementar - Medicina - Fisiologia Humana - Fisiologia Renal e Néfrons]]",
        "[[- Funcional - Medicina - Saúde Pública - Epidemiologia, Endemias e Notificação Compulsória]]",
        "[[- Funcional - Medicina - Saúde Pública - Sistema Único de Saúde (SUS) e Princípios Doutrinários (Lei 8.080/90)]]",
        "[[- Funcional - Medicina - Clínica Médica - Semiologia, Anamnese e Exame Físico]]",
        "[[- Funcional - Medicina - Clínica Médica - Interpretação de Exames Laboratoriais (Hemograma, Lipidograma)]]",
        "[[- Funcional - Medicina - Pediatria - Puericultura e Marcos do Desenvolvimento Infantil]]",
        "[[- Funcional - Medicina - Ginecologia e Obstetrícia - Ciclo Menstrual e Assistência ao Pré-Natal]]",
        "[[- Funcional - Medicina - Cirurgia Geral - Assepsia, Antissepsia e Tempos Cirúrgicos (Diérese, Hemostasia, Síntese)]]",
        "[[- Funcional - Enfermagem - Fundamentos de Enfermagem - Sistematização da Assistência de Enfermagem (SAE)]]",
        "[[- Funcional - Enfermagem - Urgência e Emergência - Protocolo de Manchester e Suporte Básico de Vida (BLS)]]",
        "[[- Funcional - Enfermagem - Centro Cirúrgico - Instrumentação Cirúrgica e Cuidados Pós-Anestésicos (RPA)]]",
        "[[- Funcional - Farmácia - Farmacologia Básica - Farmacocinética (Absorção, Distribuição, Metabolismo, Excreção)]]",
        "[[- Funcional - Farmácia - Farmacologia Clínica - Antibioticoterapia e Mecanismos de Resistência Bacteriana]]",
        "[[- Funcional - Farmácia - Farmacotécnica - Formas Farmacêuticas Líquidas e Sólidas (Xaropes, Cápsulas, Pomadas)]]",
        "[[- Funcional - Odontologia - Cariologia - Formação do Biofilme Dental e Prevenção com Flúor]]",
        "[[- Funcional - Odontologia - Periodontia - Doença Periodontal e Raspagem Supra/Subgengival]]",
        "[[- Funcional - Odontologia - Endodontia - Acesso, Instrumentação e Obturação do Canal Radicular]]",
        "[[- Funcional - Nutrição - Nutrição Clínica - Avaliação Antropométrica e Cálculo de IMC]]",
        "[[- Funcional - Nutrição - Dietoterapia - Terapia Nutricional Enteral e Parenteral em Pacientes Críticos]]",
        "[[- Funcional - Fisioterapia - Ortopedia e Traumatologia - Reabilitação de Fraturas e Lesões Ligamentares]]",
        "[[- Funcional - Fisioterapia - Fisioterapia Respiratória - Ventilação Mecânica Não Invasiva (VNI)]]",

        // =====================================================================
        // 620 - ENGENHARIA CIVIL, MECÂNICA, ELÉTRICA E AFINS
        // =====================================================================
        "[[- Elementar - Engenharia - Ciência dos Materiais - Estruturas Cristalinas e Defeitos em Metais]]",
        "[[- Funcional - Engenharia Civil - Resistência dos Materiais - Tensão, Deformação e Lei de Hooke Aplicada]]",
        "[[- Funcional - Engenharia Civil - Estruturas de Concreto - Dimensionamento de Vigas e Pilares (NBR 6118)]]",
        "[[- Funcional - Engenharia Civil - Estruturas Metálicas - Ligações Parafusadas e Soldadas (NBR 8800)]]",
        "[[- Funcional - Engenharia Civil - Geotecnia - Mecânica dos Solos, Sondagem SPT e Muros de Arrimo]]",
        "[[- Funcional - Engenharia Civil - Instalações Prediais - Instalações Hidrossanitárias (Água Fria, Quente e Esgoto)]]",
        "[[- Funcional - Engenharia Mecânica - Termodinâmica Aplicada - Ciclos de Potência (Otto, Diesel, Brayton)]]",
        "[[- Funcional - Engenharia Mecânica - Mecânica dos Fluidos - Equação de Bernoulli e Perda de Carga em Tubulações]]",
        "[[- Funcional - Engenharia Mecânica - Elementos de Máquinas - Dimensionamento de Eixos, Engrenagens e Rolamentos]]",
        "[[- Funcional - Engenharia Mecânica - Refrigeração e Ar Condicionado - Ciclo de Compressão a Vapor e Carta Psicrométrica]]",
        "[[- Funcional - Engenharia Elétrica - Eletrotécnica - Circuitos de Corrente Alternada e Sistemas Trifásicos]]",
        "[[- Funcional - Engenharia Elétrica - Eletrotécnica - Triângulo de Potências e Correção do Fator de Potência]]",
        "[[- Funcional - Engenharia Elétrica - Máquinas Elétricas - Motores de Indução Trifásicos e Transformadores]]",
        "[[- Funcional - Engenharia Elétrica - Instalações Elétricas - Projeto Luminotécnico e Quadro de Distribuição (NBR 5410)]]",
        "[[- Funcional - Engenharia Eletrônica - Eletrônica Analógica - Diodos, Amplificadores Operacionais e Transistores (BJT/MOSFET)]]",
        "[[- Funcional - Engenharia Eletrônica - Eletrônica Digital - Portas Lógicas, Flip-Flops e Microcontroladores (Arduino/PIC)]]",
        "[[- Funcional - Engenharia de Telecomunicações - Redes de Transmissão - Modulação AM/FM, Multiplexação e Fibras Ópticas]]",

        // =====================================================================
        // 630 - AGRICULTURA, ZOOTECNIA E MEDICINA VETERINÁRIA
        // =====================================================================
        "[[- Funcional - Agronomia - Ciência do Solo - Pedologia, Perfil do Solo e Classificação Brasileira de Solos]]",
        "[[- Funcional - Agronomia - Fertilidade do Solo - Correção de Acidez (Calagem) e Adubação NPK]]",
        "[[- Funcional - Agronomia - Fitotecnia - Manejo Integrado de Pragas (MIP) e Uso de Defensivos Agrícolas]]",
        "[[- Funcional - Agronomia - Engenharia Agrícola - Sistemas de Irrigação (Gotejamento, Aspersão, Pivô Central)]]",
        "[[- Funcional - Medicina Veterinária - Clínica de Pequenos Animais - Doenças Infecciosas (Cinomose, Parvovirose, Leishmaniose)]]",
        "[[- Funcional - Medicina Veterinária - Clínica de Grandes Animais - Cólica Equina e Mastite Bovina]]",
        "[[- Funcional - Zootecnia - Nutrição Animal - Formulação de Rações para Monogástricos e Ruminantes]]",
        "[[- Funcional - Zootecnia - Melhoramento Genético Animal - Inseminação Artificial (IA) e Transferência de Embriões (TE)]]",
        "[[- Funcional - Zootecnia - Avicultura e Suinocultura - Manejo de Instalações, Ambiência e Bem-Estar Animal]]",

        // =====================================================================
        // 640 - ECONOMIA DOMÉSTICA, GASTRONOMIA E HOTELARIA
        // =====================================================================
        "[[- Funcional - Gastronomia - Técnicas Culinárias - Cortes Clássicos de Vegetais (Julienne, Brunoise, Mirepoix)]]",
        "[[- Funcional - Gastronomia - Técnicas Culinárias - Bases Culinárias (Fundos de Cozimento, Roux e Molhos Mãe)]]",
        "[[- Funcional - Gastronomia - Segurança Alimentar - Boas Práticas de Manipulação e Controle de Temperatura]]",
        "[[- Funcional - Hotelaria - Gestão Hoteleira - Front Office (Recepção), Governança e Revenue Management]]",
        "[[- Funcional - Moda e Vestuário - Design de Moda - Modelagem Plana Industrial e Moulage (Draping)]]",

        // =====================================================================
        // 650 - ADMINISTRAÇÃO CORPORATIVA E NEGÓCIOS (APLICAÇÃO DE MERCADO)
        // =====================================================================
        "[[- Funcional - Gestão de Negócios - Marketing Estratégico - Composto de Marketing (Os 4 Ps) e Segmentação de Mercado]]",
        "[[- Funcional - Gestão de Negócios - Marketing Digital - Inbound Marketing, Funil de Vendas e Copywriting]]",
        "[[- Funcional - Gestão de Negócios - Recursos Humanos - Recrutamento, Seleção e Onboarding de Talentos]]",
        "[[- Funcional - Gestão de Negócios - Recursos Humanos - Avaliação de Desempenho (360 Graus) e Plano de Cargos e Salários]]",
        "[[- Funcional - Gestão de Negócios - Gestão de Projetos - Metodologias Ágeis (Scrum: Sprints, Product Backlog, Daily)]]",
        "[[- Funcional - Gestão de Negócios - Gestão de Projetos - Metodologias Preditivas (Guia PMBOK e Gráfico de Gantt)]]",
        "[[- Funcional - Gestão de Negócios - Logística Empresarial - Gestão de Estoques, Curva ABC e Cross-Docking]]",
        "[[- Funcional - Contabilidade Gerencial - Análise de Custos - Custeio Baseado em Atividades (ABC) e Ponto de Equilíbrio]]",
        "[[- Funcional - Relações Públicas - Comunicação Corporativa - Gestão de Crise de Imagem e Assessoria de Imprensa]]",

        // =====================================================================
        // 660 A 690 - INDÚSTRIA, ENGENHARIA QUÍMICA E CONSTRUÇÃO
        // =====================================================================
        "[[- Funcional - Engenharia Química - Operações Unitárias - Destilação Fracionada, Extração Líquido-Líquido e Filtração]]",
        "[[- Funcional - Engenharia Química - Cinética de Reatores - Reatores Batelada, CSTR e PFR]]",
        "[[- Funcional - Ciência dos Alimentos - Tecnologia de Alimentos - Processos Térmicos (Pasteurização, UHT, Liofilização)]]",
        "[[- Funcional - Engenharia de Produção - Gestão da Qualidade - Ferramentas Lean Six Sigma (DMAIC e Diagrama de Ishikawa)]]",
        "[[- Funcional - Engenharia de Produção - Controle de Produção - Planejamento e Controle da Produção (PCP), MRP e Kanban]]",
        "[[- Funcional - Manufatura - Usinagem e Fabricação - Processos de Torneamento, Fresamento e Soldagem (TIG/MIG)]]",
        "[[- Funcional - Construção Civil - Orçamentação de Obras - Composição de Custos Unitários, BDI e Tabela SINAPI]]",
        "[[- Funcional - Construção Civil - Planejamento de Obras - Cronograma Físico-Financeiro e Curva S]]",

        // =====================================================================
        // 700 - TEORIA DA ARTE, ESTÉTICA E HISTÓRIA DA ARTE
        // =====================================================================
        "[[- Elementar - Artes Visuais - Teoria da Arte - Estética Filosófica e o Conceito do Belo]]",
        "[[- Elementar - Artes Visuais - Teoria da Arte - Semiótica da Imagem e Análise Visual]]",
        "[[- Elementar - Artes Visuais - História da Arte - Arte Pré-Histórica (Pinturas Rupestres)]]",
        "[[- Elementar - Artes Visuais - História da Arte - Antiguidade Clássica (Arte Grega e Romana)]]",
        "[[- Elementar - Artes Visuais - História da Arte - Idade Média (Arte Românica, Gótica e Bizantina)]]",
        "[[- Elementar - Artes Visuais - História da Arte - Renascimento Cultural (Perspectiva e Esfumato)]]",
        "[[- Elementar - Artes Visuais - História da Arte - Barroco e Rococó (Tenebrismo e Exagero Dramático)]]",
        "[[- Elementar - Artes Visuais - História da Arte - Século XIX (Neoclassicismo, Romantismo e Realismo)]]",
        "[[- Elementar - Artes Visuais - História da Arte - Vanguardas Europeias (Cubismo, Expressionismo, Surrealismo)]]",
        "[[- Elementar - Artes Visuais - História da Arte - Arte Moderna Brasileira (Semana de Arte de 22)]]",
        "[[- Elementar - Artes Visuais - História da Arte - Arte Contemporânea (Pop Art, Minimalismo, Arte Conceitual)]]",

        // =====================================================================
        // 710 / 720 - URBANISMO E ARQUITETURA
        // =====================================================================
        "[[- Funcional - Arquitetura - Planejamento Urbano - Plano Diretor, Zoneamento e Uso do Solo]]",
        "[[- Funcional - Arquitetura - Planejamento Urbano - Mobilidade Urbana e Cidades Inteligentes (Smart Cities)]]",
        "[[- Funcional - Arquitetura - Paisagismo - Projeto Paisagístico, Espécies Nativas e Arborização Urbana]]",
        "[[- Elementar - Arquitetura - História da Arquitetura - Ordens Clássicas (Dórica, Jônica, Coríntia)]]",
        "[[- Elementar - Arquitetura - História da Arquitetura - Arquitetura Moderna (Le Corbusier e Oscar Niemeyer)]]",
        "[[- Funcional - Arquitetura - Projeto Arquitetônico - Representação Gráfica (Planta Baixa, Cortes, Fachadas)]]",
        "[[- Funcional - Arquitetura - Projeto Arquitetônico - Metodologia BIM (Building Information Modeling)]]",
        "[[- Funcional - Arquitetura - Conforto Ambiental - Conforto Térmico (Ventilação Cruzada, Brise-Soleil, Inércia Térmica)]]",
        "[[- Funcional - Arquitetura - Conforto Ambiental - Conforto Acústico e Luminotécnico (Iluminação Natural e Zenital)]]",
        "[[- Funcional - Arquitetura - Legislação e Normas - Acessibilidade a Edificações (Norma NBR 9050)]]",
        "[[- Funcional - Arquitetura - Legislação e Normas - Código de Obras e Posturas Municipais]]",

        // =====================================================================
        // 730 / 740 / 750 / 760 - ARTES PLÁSTICAS, DESENHO E DESIGN (UX/UI)
        // =====================================================================
        "[[- Funcional - Artes Plásticas - Escultura - Técnicas de Modelagem, Entalhe e Fundição (Bronze/Gesso)]]",
        "[[- Funcional - Artes Plásticas - Pintura - Técnicas (Óleo sobre Tela, Aquarela, Acrílica, Fresco)]]",
        "[[- Funcional - Artes Plásticas - Gravura - Xilogravura, Litogravura e Serigrafia]]",
        "[[- Funcional - Design - Teoria das Cores - Círculo Cromático, RGB/CMYK e Psicologia das Cores]]",
        "[[- Funcional - Design - Design Gráfico - Tipografia (Serif, Sans-Serif, Kerning, Tracking)]]",
        "[[- Funcional - Design - Design Gráfico - Identidade Visual, Branding e Design de Embalagens]]",
        "[[- Funcional - Design - Desenho Técnico - Perspectiva Isométrica, Cavaleira e Ponto de Fuga]]",
        "[[- Funcional - Design - Desenho Industrial - Ergonomia e Design de Produto]]",
        "[[- Funcional - Design - UX Design - Pesquisa de Usuário (User Research) e Criação de Personas]]",
        "[[- Funcional - Design - UX Design - Arquitetura da Informação e Jornada do Usuário]]",
        "[[- Funcional - Design - UI Design - Wireframing, Prototipagem e Design Systems]]",
        "[[- Funcional - Design - UI Design - Heurísticas de Usabilidade (Jakob Nielsen) e Acessibilidade Digital]]",

        // =====================================================================
        // 770 - FOTOGRAFIA E CINEMA (AUDIOVISUAL)
        // =====================================================================
        "[[- Funcional - Audiovisual - Fotografia - Triângulo de Exposição (ISO, Diafragma/Abertura e Obturador/Velocidade)]]",
        "[[- Funcional - Audiovisual - Fotografia - Composição Fotográfica (Regra dos Terços, Linhas Guia e Profundidade de Campo)]]",
        "[[- Funcional - Audiovisual - Fotografia - Iluminação (Luz Dura/Suave, Key Light, Fill Light, Backlight)]]",
        "[[- Elementar - Audiovisual - História do Cinema - Cinema Mudo, Expressionismo Alemão e Neorrealismo Italiano]]",
        "[[- Elementar - Audiovisual - Teoria do Cinema - Linguagem Cinematográfica e Montagem Soviética (Eisenstein)]]",
        "[[- Funcional - Audiovisual - Produção Cinematográfica - Roteiro (A Jornada do Herói e Estrutura de Três Atos)]]",
        "[[- Funcional - Audiovisual - Produção Cinematográfica - Direção de Arte e Decupagem de Cenas]]",
        "[[- Funcional - Audiovisual - Produção Cinematográfica - Edição e Pós-Produção (Corte Seco, Fade, Correção de Cor/Color Grading)]]",

        // =====================================================================
        // 780 - MÚSICA E PRODUÇÃO MUSICAL
        // =====================================================================
        "[[- Elementar - Música - História da Música - Períodos Barroco, Clássico e Romântico (Bach, Mozart, Beethoven)]]",
        "[[- Elementar - Música - Teoria Musical - Pauta, Claves (Sol, Fá, Dó) e Figuras Rítmicas]]",
        "[[- Elementar - Música - Teoria Musical - Escalas Maiores, Menores e Modos Gregos]]",
        "[[- Elementar - Música - Harmonia e Contraponto - Formação de Acordes, Tríades, Tétrades e Campo Harmônico]]",
        "[[- Funcional - Música - Prática Instrumental - Técnicas de Execução (Corda, Sopro, Percussão, Teclas)]]",
        "[[- Funcional - Música - Canto e Técnica Vocal - Apoio Diafragmático, Registros Vocais e Afinação]]",
        "[[- Funcional - Música - Produção Musical - Gravação em Home Studio e Uso de DAWs (Pro Tools, Logic, Ableton)]]",
        "[[- Funcional - Música - Produção Musical - Engenharia de Áudio (Mixagem, Masterização, Compressores e EQs)]]",

        // =====================================================================
        // 790 - ARTES CÊNICAS, ESPORTES, RECREAÇÃO E EDUCAÇÃO FÍSICA
        // =====================================================================
        "[[- Elementar - Artes Cênicas - História do Teatro - Tragédia/Comédia Grega e Teatro Elisabetano (Shakespeare)]]",
        "[[- Funcional - Artes Cênicas - Atuação - O Método (Stanislavski) e Expressão Corporal/Vocal]]",
        "[[- Funcional - Esportes - Educação Física Escolar - Jogos Cooperativos, Lúdico e Psicomotricidade Infantil]]",
        "[[- Funcional - Esportes - Fisiologia do Exercício - Vias Energéticas (Anaeróbia Alática/Lática, Aeróbia) e VO2 Máx]]",
        "[[- Funcional - Esportes - Cinesiologia e Biomecânica - Análise do Movimento Humano, Alavancas e Contração Muscular]]",
        "[[- Funcional - Esportes - Treinamento Desportivo - Princípios do Treinamento (Sobrecarga, Especificidade, Reversibilidade)]]",
        "[[- Funcional - Esportes - Treinamento Desportivo - Periodização do Treinamento (Microciclo, Mesociclo, Macrociclo)]]",
        "[[- Funcional - Esportes - Regras Desportivas - Esportes Coletivos (Futebol, Vôlei, Basquete, Handebol)]]",
        "[[- Funcional - Esportes - Regras Desportivas - Esportes Individuais (Atletismo, Natação, Ginástica Olímpica)]]",

        // =====================================================================
        // 800 / 801 - TEORIA LITERÁRIA, POÉTICA E NARRATOLOGIA
        // =====================================================================
        "[[- Elementar - Literatura - Teoria Literária - Gêneros Literários (Épico, Lírico e Dramático)]]",
        "[[- Elementar - Literatura - Teoria Literária - Narratologia (Foco Narrativo, Tempo Psicológico/Cronológico, Espaço)]]",
        "[[- Elementar - Literatura - Teoria Literária - Tipos de Personagem (Plana, Redonda, Protagonista, Antagonista)]]",
        "[[- Elementar - Literatura - Teoria Literária - Intertextualidade (Paródia, Paráfrase, Epígrafe, Citação)]]",
        "[[- Elementar - Literatura - Teoria Literária - Escolas da Crítica (Formalismo Russo, Estruturalismo, Estética da Recepção)]]",
        "[[- Elementar - Literatura - Poética - Versificação (Métrica, Escansão, Sílabas Poéticas)]]",
        "[[- Elementar - Literatura - Poética - Rimas (Ricas, Pobres, Raras, Emparelhadas, Cruzadas, Interpoladas)]]",
        "[[- Elementar - Literatura - Poética - Formas Fixas (Soneto, Haicai, Balada, Ode)]]",

        // =====================================================================
        // 808 - RETÓRICA, ESCRITA CRIATIVA E REDAÇÃO (FUNCIONAL)
        // =====================================================================
        "[[- Funcional - Retórica - Oratória - Técnicas de Persuasão (Ethos, Pathos, Logos)]]",
        "[[- Funcional - Retórica - Oratória - Expressão Corporal, Controle de Voz e Dicção em Apresentações]]",
        "[[- Funcional - Escrita Criativa - Produção Literária - Estrutura Dramática (Pirâmide de Freytag e Curva de Tensão)]]",
        "[[- Funcional - Escrita Criativa - Produção Literária - A Jornada do Herói (Monomito de Joseph Campbell)]]",
        "[[- Funcional - Escrita Criativa - Storytelling - Construção de Arcos de Personagem e Conflitos (Interno, Relacional, Extra-pessoal)]]",
        "[[- Funcional - Escrita Criativa - Copywriting - Gatilhos Mentais e Escrita Persuasiva para Vendas]]",
        "[[- Funcional - Escrita Criativa - Roteirização - Formatação de Roteiro Audiovisual (Padrão Master Scenes)]]",

        // =====================================================================
        // 869.3 - LITERATURA BRASILEIRA: ERA COLONIAL AO SÉCULO XIX
        // =====================================================================
        "[[- Elementar - Literatura - Literatura Brasileira - Quinhentismo (Literatura de Informação e Carta de Caminha)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Quinhentismo (Literatura de Catequese e Padre José de Anchieta)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Barroco (Poesia de Gregório de Matos - 'O Boca do Inferno')]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Barroco (Prosa e Sermões do Padre Antônio Vieira)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Arcadismo (Bucolismo, Nativismo e Tomás Antônio Gonzaga)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Arcadismo (Poesia Épica: O Uraguai e Caramuru)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Romantismo (1ª Geração: Nacionalismo, Indianismo e Gonçalves Dias)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Romantismo (2ª Geração: Mal do Século, Ultrarromantismo e Álvares de Azevedo)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Romantismo (3ª Geração: Condoreirismo, Abolicionismo e Castro Alves)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Romantismo em Prosa (Romances Urbanos, Indianistas e Regionais de José de Alencar)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Realismo (Análise Psicológica e Ironia em Machado de Assis)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Realismo (Obras-primas Machadianas: Memórias Póstumas, Dom Casmurro, Quincas Borba)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Naturalismo (Determinismo Social, Zoomorfização e Aluísio Azevedo - 'O Cortiço')]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Parnasianismo (Arte pela Arte, Preciosismo Vocabular e Olavo Bilac)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Simbolismo (Sinestesia, Misticismo, Musicalidade e Cruz e Sousa)]]",

        // =====================================================================
        // 869.3 - LITERATURA BRASILEIRA: SÉCULO XX E CONTEMPORÂNEA
        // =====================================================================
        "[[- Elementar - Literatura - Literatura Brasileira - Pré-Modernismo (Sertões de Euclides da Cunha e o Brasil Profundo)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Pré-Modernismo (Crítica Social de Lima Barreto e Monteiro Lobato)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Pré-Modernismo (Poesia Sincrética de Augusto dos Anjos)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Modernismo (Semana de 22, Ruptura Estética e Manifestos Antropofágico/Pau-Brasil)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Modernismo 1ª Geração (Mário de Andrade e Oswald de Andrade)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Modernismo 2ª Geração Poesia (Carlos Drummond de Andrade, Vinicius de Moraes, Cecília Meireles)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Modernismo 2ª Geração Prosa (Romance de 30: Graciliano Ramos, Jorge Amado, Rachel de Queiroz)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Modernismo 3ª Geração (Geração de 45 e Prosa Intimista de Clarice Lispector)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Modernismo 3ª Geração (Regionalismo Universal e Neologismos de João Guimarães Rosa)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Modernismo 3ª Geração (Poesia Engajada de João Cabral de Melo Neto)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Concretismo (Poesia Visual e Vanguardas Paulistas - Década de 50)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Literatura Contemporânea (Conto Urbano, Brutalismo: Rubem Fonseca, Dalton Trevisan)]]",
        "[[- Elementar - Literatura - Literatura Brasileira - Literatura Contemporânea (Poesia Marginal - Geração Mimeógrafo)]]",

        // =====================================================================
        // 869 - LITERATURA PORTUGUESA
        // =====================================================================
        "[[- Elementar - Literatura - Literatura Portuguesa - Trovadorismo (Cantigas de Amigo, Amor, Escárnio e Maldizer)]]",
        "[[- Elementar - Literatura - Literatura Portuguesa - Humanismo (Teatro Vicentino: Auto da Barca do Inferno)]]",
        "[[- Elementar - Literatura - Literatura Portuguesa - Classicismo (Epopeia 'Os Lusíadas' de Luís Vaz de Camões)]]",
        "[[- Elementar - Literatura - Literatura Portuguesa - Barroco (Conceptismo e Cultismo na Península Ibérica)]]",
        "[[- Elementar - Literatura - Literatura Portuguesa - Arcadismo (Bocage e a Nova Arcádia)]]",
        "[[- Elementar - Literatura - Literatura Portuguesa - Romantismo (Almeida Garrett e Camilo Castelo Branco)]]",
        "[[- Elementar - Literatura - Literatura Portuguesa - Realismo/Naturalismo (Geração de 70 e a Crítica de Eça de Queirós)]]",
        "[[- Elementar - Literatura - Literatura Portuguesa - Modernismo (Geração de Orpheu, Fernando Pessoa e seus Heterônimos)]]",
        "[[- Elementar - Literatura - Literatura Portuguesa - Literatura Contemporânea (Neorrealismo e a Prosa de José Saramago)]]",

        // =====================================================================
        // 810 A 890 - LITERATURA MUNDIAL (UNIVERSAL) E CLÁSSICA
        // =====================================================================
        "[[- Elementar - Literatura - Literatura Clássica - Epopeias Gregas (A Ilíada e A Odisseia de Homero)]]",
        "[[- Elementar - Literatura - Literatura Clássica - Teatro Grego (Édipo Rei de Sófocles)]]",
        "[[- Elementar - Literatura - Literatura Clássica - Literatura Latina (A Eneida de Virgílio)]]",
        "[[- Elementar - Literatura - Literatura Italiana - Transição para o Renascimento (A Divina Comédia de Dante Alighieri)]]",
        "[[- Elementar - Literatura - Literatura Inglesa - Era Elisabetana (Tragédias e Comédias de William Shakespeare)]]",
        "[[- Elementar - Literatura - Literatura Inglesa - Era Vitoriana (Romance Social de Charles Dickens e as Irmãs Brontë)]]",
        "[[- Elementar - Literatura - Literatura Inglesa - Modernismo Britânico (Fluxo de Consciência de Virginia Woolf e James Joyce)]]",
        "[[- Elementar - Literatura - Literatura Norte-Americana - Romantismo Obscuro (Edgar Allan Poe e o Conto de Terror)]]",
        "[[- Elementar - Literatura - Literatura Norte-Americana - Geração Perdida (F. Scott Fitzgerald e Ernest Hemingway)]]",
        "[[- Elementar - Literatura - Literatura Francesa - Romantismo Francês (Victor Hugo - Os Miseráveis)]]",
        "[[- Elementar - Literatura - Literatura Francesa - Realismo e Naturalismo Francês (Flaubert, Balzac, Zola)]]",
        "[[- Elementar - Literatura - Literatura Francesa - Modernismo Francês (Em Busca do Tempo Perdido de Marcel Proust)]]",
        "[[- Elementar - Literatura - Literatura Alemã - Romantismo Alemão (Sturm und Drang e Goethe - Fausto/Werther)]]",
        "[[- Elementar - Literatura - Literatura Alemã - Existencialismo e Absurdo (A Metamorfose de Franz Kafka)]]",
        "[[- Elementar - Literatura - Literatura Russa - Realismo Psicológico (Dostoiévski - Crime e Castigo, Os Irmãos Karamázov)]]",
        "[[- Elementar - Literatura - Literatura Russa - Realismo Histórico (Liev Tolstói - Guerra e Paz, Anna Kariênina)]]",
        "[[- Elementar - Literatura - Literatura Hispano-Americana - Boom Latino-Americano e Realismo Mágico (Gabriel García Márquez)]]",
        "[[- Elementar - Literatura - Literatura Hispano-Americana - Literatura Fantástica e Labirintos (Jorge Luis Borges)]]",

        // =====================================================================
        // 900 - HISTORIOGRAFIA E CIÊNCIAS AUXILIARES DA HISTÓRIA
        // =====================================================================
        "[[- Elementar - História - Historiografia - Escola dos Annales e a Nova História Cultural]]",
        "[[- Elementar - História - Historiografia - Materialismo Histórico (Marxismo Aplicado à História)]]",
        "[[- Elementar - História - Arqueologia - Métodos de Datação (Carbono-14 e Termoluminescência)]]",
        "[[- Funcional - História - Gestão de Patrimônio - Tombamento, Musealização e Preservação (IPHAN/UNESCO)]]",

        // =====================================================================
        // 910 - GEOGRAFIA FÍSICA E CARTOGRAFIA
        // =====================================================================
        "[[- Elementar - Geografia - Cartografia Básica - Projeções Cartográficas (Mercator, Peters, Cilíndricas e Cônicas)]]",
        "[[- Elementar - Geografia - Cartografia Básica - Escalas (Gráfica e Numérica), Fusos Horários e Coordenadas]]",
        "[[- Funcional - Geografia - Cartografia Digital - Sensoriamento Remoto, GPS e Sistemas de Informação Geográfica (SIG)]]",
        "[[- Elementar - Geografia - Geologia e Geomorfologia - Estrutura Interna da Terra e Tectônica de Placas]]",
        "[[- Elementar - Geografia - Geologia e Geomorfologia - Agentes Endógenos (Tectonismo/Vulcanismo) e Exógenos (Intemperismo/Erosão)]]",
        "[[- Elementar - Geografia - Geologia e Geomorfologia - Formas de Relevo (Planaltos, Planícies, Depressões e Montanhas)]]",
        "[[- Elementar - Geografia - Climatologia - Elementos e Fatores Climáticos (Latitude, Altitude, Maritimidade/Continentalidade)]]",
        "[[- Elementar - Geografia - Climatologia - Fenômenos Atmosféricos (El Niño, La Niña e Inversão Térmica)]]",
        "[[- Elementar - Geografia - Climatologia - Classificação Climática Global e Tipos de Clima do Brasil (Köppen e Strahler)]]",
        "[[- Elementar - Geografia - Hidrografia - Bacias Hidrográficas Brasileiras (Amazônica, Tocantins-Araguaia, São Francisco, Prata)]]",
        "[[- Elementar - Geografia - Hidrografia - Águas Subterrâneas (Aquífero Guarani e Sistema Aquífero Grande Amazônia)]]",
        "[[- Elementar - Geografia - Biogeografia - Biomas Terrestres Mundiais (Tundra, Taiga, Florestas Temperadas/Tropicais, Desertos)]]",
        "[[- Elementar - Geografia - Biogeografia - Domínios Morfoclimáticos Brasileiros (Aziz Ab'Sáber: Amazônico, Cerrado, Mares de Morros, Caatinga, Araucárias, Pradarias)]]",

        // =====================================================================
        // GEOGRAFIA HUMANA, ECONÔMICA, GEOPOLÍTICA E TURISMO
        // =====================================================================
        "[[- Elementar - Geografia - Geografia da População - Transição Demográfica, Pirâmides Etárias e Envelhecimento Populacional]]",
        "[[- Elementar - Geografia - Geografia da População - Teorias Demográficas (Malthusiana, Neomalthusiana e Reformista/Marxista)]]",
        "[[- Elementar - Geografia - Geografia da População - Movimentos Migratórios (Migração Pendular, Transumância, Êxodo Rural, Refugiados)]]",
        "[[- Elementar - Geografia - Geografia Urbana - Processos de Urbanização, Metropolização e Conurbação]]",
        "[[- Elementar - Geografia - Geografia Urbana - Problemas Urbanos (Segregação Socioespacial, Favelização, Ilhas de Calor e Gentrificação)]]",
        "[[- Elementar - Geografia - Geografia Agrária - Estrutura Fundiária Brasileira, Latifúndios e Reforma Agrária]]",
        "[[- Elementar - Geografia - Geografia Agrária - Revolução Verde, Agronegócio e Agricultura Familiar]]",
        "[[- Funcional - Geopolítica - Ordem Mundial - Nova Ordem Mundial, Multipolaridade e Guerra Comercial (EUA vs. China)]]",
        "[[- Funcional - Geopolítica - Blocos Econômicos - Tipos de Integração (Área de Livre Comércio, União Aduaneira, Mercado Comum, União Monetária)]]",
        "[[- Funcional - Geopolítica - Blocos Econômicos - União Europeia, NAFTA/USMCA, Mercosul, ASEAN e BRICS]]",
        "[[- Funcional - Geopolítica - Conflitos Contemporâneos - Questão Palestina (Israel vs. Hamas/OLP), Primavera Árabe e Guerra da Síria]]",
        "[[- Funcional - Geopolítica - Conflitos Contemporâneos - Conflitos no Leste Europeu (Rússia vs. Ucrânia e OTAN)]]",
        "[[- Funcional - Turismo - Gestão Turística - Planejamento Estratégico do Turismo, Ecoturismo e Turismo Sustentável]]",

        // =====================================================================
        // 930 - HISTÓRIA GERAL: ANTIGUIDADE CLÁSSICA E ORIENTAL
        // =====================================================================
        "[[- Elementar - História - Pré-História - Paleolítico, Neolítico, Idade dos Metais e Revolução Agrícola]]",
        "[[- Elementar - História - Antiguidade Oriental - Mesopotâmia (Sumérios, Acádios, Babilônicos e Código de Hamurábi)]]",
        "[[- Elementar - História - Antiguidade Oriental - Egito Antigo (Faraós, Sociedade Teocrática e o Rio Nilo)]]",
        "[[- Elementar - História - Antiguidade Oriental - Hebreus, Fenícios (Comércio Marítimo/Alfabeto) e Persas]]",
        "[[- Elementar - História - Antiguidade Clássica - Grécia Antiga (Período Homérico, Pólis: Atenas e Esparta)]]",
        "[[- Elementar - História - Antiguidade Clássica - Grécia Antiga (Guerras Médicas, Guerra do Peloponeso e Helenismo)]]",
        "[[- Elementar - História - Antiguidade Clássica - Roma Antiga (Monarquia, República, Lutas Plebeias e Triunviratos)]]",
        "[[- Elementar - História - Antiguidade Clássica - Roma Antiga (Império Romano, Pax Romana, Crise do Século III e Invasões Bárbaras)]]",

        // =====================================================================
        // 940 - HISTÓRIA GERAL: IDADE MÉDIA E IDADE MODERNA
        // =====================================================================
        "[[- Elementar - História - Idade Média - Alta Idade Média (Império Bizantino, Expansão Islâmica e Império Carolíngio)]]",
        "[[- Elementar - História - Idade Média - Feudalismo (Relações de Suserania e Vassalagem, Servidão, Três Ordens)]]",
        "[[- Elementar - História - Idade Média - Baixa Idade Média (As Cruzadas, Renascimento Comercial e Urbano)]]",
        "[[- Elementar - História - Idade Média - Baixa Idade Média (Crise do Século XIV: Peste Negra, Guerra dos Cem Anos e Fome)]]",
        "[[- Elementar - História - Idade Moderna - Formação dos Estados Nacionais e Absolutismo Monárquico (Maquiavel, Hobbes, Bossuet)]]",
        "[[- Elementar - História - Idade Moderna - Expansão Marítima Europeia (Grandes Navegações e Tratado de Tordesilhas)]]",
        "[[- Elementar - História - Idade Moderna - Capitalismo Comercial (Mercantilismo: Metalismo, Balança Favorável e Pacto Colonial)]]",
        "[[- Elementar - História - Idade Moderna - Renascimento Cultural e Científico (Antropocentrismo, Racionalismo)]]",
        "[[- Elementar - História - Idade Moderna - Reformas Religiosas (Luteranismo, Calvinismo, Anglicanismo) e Contrarreforma Católica (Concílio de Trento)]]",

        // =====================================================================
        // HISTÓRIA GERAL: IDADE CONTEMPORÂNEA (SÉCULOS XVIII AO XX)
        // =====================================================================
        "[[- Elementar - História - Idade Contemporânea - Iluminismo (Enciclopedismo, Voltaire, Montesquieu, Rousseau e Despotismo Esclarecido)]]",
        "[[- Elementar - História - Idade Contemporânea - Revoluções Inglesas (Revolução Puritana e Revolução Gloriosa de 1688)]]",
        "[[- Elementar - História - Idade Contemporânea - Revolução Industrial (Fases I, II e III, Movimento Ludista e Cartista)]]",
        "[[- Elementar - História - Idade Contemporânea - Independência dos EUA (A Guerra das Treze Colônias e a Constituição de 1787)]]",
        "[[- Elementar - História - Idade Contemporânea - Revolução Francesa (Queda da Bastilha, Fase Jacobina/Terror e Diretório)]]",
        "[[- Elementar - História - Idade Contemporânea - Era Napoleônica (Consulado, Império, Bloqueio Continental) e Congresso de Viena]]",
        "[[- Elementar - História - Idade Contemporânea - Unificações Tardias (Unificação da Itália e da Alemanha - Otto von Bismarck)]]",
        "[[- Elementar - História - Idade Contemporânea - Imperialismo e Neocolonialismo (Conferência de Berlim e Partilha da África e Ásia)]]",

        // =====================================================================
        // HISTÓRIA GERAL: O BREVE SÉCULO XX
        // =====================================================================
        "[[- Elementar - História - Século XX - Primeira Guerra Mundial (Tríplice Aliança vs. Entente, Guerra de Trincheiras, Tratado de Versalhes)]]",
        "[[- Elementar - História - Século XX - Revolução Russa (Mencheviques vs. Bolcheviques, Lênin, Stalinismo e a URSS)]]",
        "[[- Elementar - História - Século XX - Período Entre Guerras (Crise de 1929, New Deal e Fascismo na Itália)]]",
        "[[- Elementar - História - Século XX - Nazismo (Ascensão de Hitler, Antissemitismo e Espaço Vital)]]",
        "[[- Elementar - História - Século XX - Segunda Guerra Mundial (Eixo vs. Aliados, Batalha de Stalingrado, Dia D e Bombas Atômicas)]]",
        "[[- Elementar - História - Século XX - Guerra Fria (Doutrina Truman, Plano Marshall, OTAN vs. Pacto de Varsóvia)]]",
        "[[- Elementar - História - Século XX - Guerra Fria (Corrida Espacial, Crise dos Mísseis em Cuba e Muro de Berlim)]]",
        "[[- Elementar - História - Século XX - Descolonização Afro-Asiática (Conferência de Bandung, Guerra do Vietnã e Apartheid na África do Sul)]]",

        // =====================================================================
        // 970 / 980 - HISTÓRIA DA AMÉRICA E HISTÓRIA DO BRASIL
        // =====================================================================
        "[[- Elementar - História - História da América - Povos Pré-Colombianos (Maias, Astecas e Incas)]]",
        "[[- Elementar - História - História da América - Colonização Espanhola (Mita, Encomienda, Chapetones e Criollos)]]",
        "[[- Elementar - História - História da América - Independência da América Espanhola (Simon Bolívar, San Martín e Caudilhismo)]]",
        "[[- Elementar - História - História do Brasil - Período Pré-Colonial (Extração de Pau-Brasil e Escambo)]]",
        "[[- Elementar - História - História do Brasil - Brasil Colônia (Capitanias Hereditárias, Governo Geral, Economia Açucareira e Escravidão)]]",
        "[[- Elementar - História - História do Brasil - Brasil Colônia (Invasões Holandesas e Insurreição Pernambucana)]]",
        "[[- Elementar - História - História do Brasil - Brasil Colônia (Bandeirantes, Ciclo do Ouro e Tratado de Madri)]]",
        "[[- Elementar - História - História do Brasil - Brasil Colônia (Revoltas Nativistas: Beckman, Mascates, Emboabas e Vila Rica)]]",
        "[[- Elementar - História - História do Brasil - Brasil Colônia (Revoltas Separatistas: Inconfidência Mineira e Conjuração Baiana)]]",
        "[[- Elementar - História - História do Brasil - Período Joanino (Abertura dos Portos em 1808 e Elevação a Reino Unido)]]",
        "[[- Elementar - História - História do Brasil - Brasil Império (Primeiro Reinado: D. Pedro I, Constituição de 1824 e Poder Moderador)]]",
        "[[- Elementar - História - História do Brasil - Brasil Império (Período Regencial: Avanço Liberal, Ato Adicional e Revoltas: Farroupilha, Cabanagem, Sabinada, Balaiada)]]",
        "[[- Elementar - História - História do Brasil - Brasil Império (Segundo Reinado: D. Pedro II, Parlamentarismo às Avessas e Economia Cafeeira)]]",
        "[[- Elementar - História - História do Brasil - Brasil Império (Segundo Reinado: Leis Abolicionistas e a Guerra do Paraguai)]]",
        "[[- Elementar - História - História do Brasil - Brasil República (República da Espada: Deodoro e Floriano, Encilhamento)]]",
        "[[- Elementar - História - História do Brasil - Brasil República (República Oligárquica: Política do Café com Leite, Coronelismo, Voto de Cabresto)]]",
        "[[- Elementar - História - História do Brasil - Brasil República (Revoltas na Primeira República: Canudos, Contestado, Vacina e Chibata)]]",
        "[[- Elementar - História - História do Brasil - Brasil República (A Era Vargas: Governo Provisório, Constitucional e Estado Novo)]]",
        "[[- Elementar - História - História do Brasil - Brasil República (República Populista: Dutra, Vargas, JK e Plano de Metas, Jânio Quadros, João Goulart)]]",
        "[[- Elementar - História - História do Brasil - Brasil República (Ditadura Militar/Civil: Castelo Branco, Costa e Silva, AI-5, Médici/Milagre Econômico, Geisel e Figueiredo)]]",
        "[[- Elementar - História - História do Brasil - Brasil República (Redemocratização: Diretas Já, Constituição Cidadã de 1988, Plano Real e Governos Contemporâneos)]]"
    };

}
