package br.com.pointel.charvs_know;

import java.io.File;

import br.com.pointel.jarch.flow.Datex;
import br.com.pointel.jarch.flow.DatexNode;
import br.com.pointel.jarch.flow.DatexToken;
import br.com.pointel.jarch.mage.WizProps;
import br.com.pointel.jarch.mage.WizText;

public class RefDatex {

    
    private static final String propsName = "props";
    private static final String propsStart = "---";
    private static final String propsEnd = "---";
    private static final String propsSeparator = ": ";

    private static final String memoaName = "memoa";
    private static final String memoaStart = "## Memoa";
    private static final String memoaEnd = "^^^";

    private static final String group01Name = "group01";
    private static final String group01Start = "## Grupo 01";
    private static final String group01End = "^^^";

    private static final String group02Name = "group02";
    private static final String group02Start = "## Grupo 02";
    private static final String group02End = "^^^";

    private static final String group03Name = "group03";
    private static final String group03Start = "## Grupo 03";
    private static final String group03End = "^^^";

    private static final String group04Name = "group04";
    private static final String group04Start = "## Grupo 04";
    private static final String group04End = "^^^";

    private static final String group05Name = "group05";
    private static final String group05Start = "## Grupo 05";
    private static final String group05End = "^^^";

    private static final String group06Name = "group06";
    private static final String group06Start = "## Grupo 06";
    private static final String group06End = "^^^";

    private static final String group07Name = "group07";
    private static final String group07Start = "## Grupo 07";
    private static final String group07End = "^^^";

    private static final String group08Name = "group08";
    private static final String group08Start = "## Grupo 08";
    private static final String group08End = "^^^";

    private static final String group09Name = "group09";
    private static final String group09Start = "## Grupo 09";
    private static final String group09End = "^^^";

    private static final String group10Name = "group10";
    private static final String group10Start = "## Grupo 10";
    private static final String group10End = "^^^";

    private static final String group11Name = "group11";
    private static final String group11Start = "## Grupo 11";
    private static final String group11End = "^^^";

    private static final String group12Name = "group12";
    private static final String group12Start = "## Grupo 12";
    private static final String group12End = "^^^";

    private static final DatexNode nodeProps = DatexNode.must(propsName, DatexToken.literal(propsStart), DatexToken.literal(propsEnd));
    private static final DatexNode nodeMemoa = DatexNode.may(memoaName, DatexToken.literal(memoaStart), DatexToken.literal(memoaEnd));
    private static final DatexNode nodeGroup01 = DatexNode.may(group01Name, DatexToken.literal(group01Start), DatexToken.literal(group01End));
    private static final DatexNode nodeGroup02 = DatexNode.may(group02Name, DatexToken.literal(group02Start), DatexToken.literal(group02End));
    private static final DatexNode nodeGroup03 = DatexNode.may(group03Name, DatexToken.literal(group03Start), DatexToken.literal(group03End));
    private static final DatexNode nodeGroup04 = DatexNode.may(group04Name, DatexToken.literal(group04Start), DatexToken.literal(group04End));
    private static final DatexNode nodeGroup05 = DatexNode.may(group05Name, DatexToken.literal(group05Start), DatexToken.literal(group05End));
    private static final DatexNode nodeGroup06 = DatexNode.may(group06Name, DatexToken.literal(group06Start), DatexToken.literal(group06End));
    private static final DatexNode nodeGroup07 = DatexNode.may(group07Name, DatexToken.literal(group07Start), DatexToken.literal(group07End));
    private static final DatexNode nodeGroup08 = DatexNode.may(group08Name, DatexToken.literal(group08Start), DatexToken.literal(group08End));
    private static final DatexNode nodeGroup09 = DatexNode.may(group09Name, DatexToken.literal(group09Start), DatexToken.literal(group09End));
    private static final DatexNode nodeGroup10 = DatexNode.may(group10Name, DatexToken.literal(group10Start), DatexToken.literal(group10End));
    private static final DatexNode nodeGroup11 = DatexNode.may(group11Name, DatexToken.literal(group11Start), DatexToken.literal(group11End));
    private static final DatexNode nodeGroup12 = DatexNode.may(group12Name, DatexToken.literal(group12Start), DatexToken.literal(group12End));
    
    private static final Datex datexRoot = new Datex(
            DatexNode.of(nodeProps, nodeMemoa,
                    nodeGroup01, nodeGroup02, nodeGroup03,
                    nodeGroup04, nodeGroup05, nodeGroup06,
                    nodeGroup07, nodeGroup08, nodeGroup09,
                    nodeGroup10, nodeGroup11, nodeGroup12));


    private static final String organizationName = "organization";
    private static final String organizationStart = "### Organização";
    private static final String organizationEnd = "~--";

    private static final String topicsName = "topics";
    private static final String topicsStart = "### Tópicos";
    private static final String topicsEnd = "~--";

    private static final String realizationName = "realization";
    private static final String realizationStart = "### Realização";
    private static final String realizationEnd = "~--";

    private static final DatexNode nodeOrganization = DatexNode.must(organizationName, DatexToken.literal(organizationStart), DatexToken.literal(organizationEnd));
    private static final DatexNode nodeTopics = DatexNode.must(topicsName, DatexToken.literal(topicsStart), DatexToken.literal(topicsEnd));
    private static final DatexNode nodeRealization = DatexNode.must(realizationName, DatexToken.literal(realizationStart), DatexToken.literal(realizationEnd));

    private static final Datex datexGroup = new Datex(DatexNode.of(nodeOrganization, nodeTopics, nodeRealization));
    

    public static final synchronized Ref read(File file) throws Exception {
        var ref = new Ref();
        read(ref, file);
        return ref;
    }

    public static final synchronized void read(Ref ref, File file) throws Exception {
        var source = WizText.read(file);
        datexRoot.parse(source);
        parseProps(ref);
        parseMemoa(ref);
        parseGroup(nodeGroup01, ref.group01);
        parseGroup(nodeGroup02, ref.group02);
        parseGroup(nodeGroup03, ref.group03);
        parseGroup(nodeGroup04, ref.group04);
        parseGroup(nodeGroup05, ref.group05);
        parseGroup(nodeGroup06, ref.group06);
        parseGroup(nodeGroup07, ref.group07);
        parseGroup(nodeGroup08, ref.group08);
        parseGroup(nodeGroup09, ref.group09);
        parseGroup(nodeGroup10, ref.group10);
        parseGroup(nodeGroup11, ref.group11);
        parseGroup(nodeGroup12, ref.group12);
    }

    private static void parseProps(Ref ref) {
        var props = WizProps.getOf(nodeProps.getValue(), propsSeparator);
        ref.props.hashMD5 = props.getOrDefault("hash-md5", "");
        ref.props.createdAt = props.getOrDefault("created-at", "");
        ref.props.revisedOn = props.getOrDefault("revised-on", "");
        ref.props.revisedCount = props.getOrDefault("revised-count", "");
        ref.props.uploadedAt = props.getOrDefault("uploaded-at", "");
    }

    private static void parseMemoa(Ref ref) {
        if (nodeMemoa.isPresent()) {
            ref.memoa.text = nodeMemoa.getValue().trim();
        }
    }

    private static void parseGroup(DatexNode node, RefGroup group) throws Exception {
        if (node.isPresent()) {
            datexGroup.parse(node.getValue());
            var propsOrganization = WizProps.getOf(nodeOrganization.getValue(), propsSeparator);
            group.classification = propsOrganization.getOrDefault("Classificação", "");
            group.titration = propsOrganization.getOrDefault("Titulação", "");
            group.topics = nodeTopics.getValue().trim();
            var propsRealization = WizProps.getOf(nodeRealization.getValue(), propsSeparator);
            group.statusNotes = propsRealization.getOrDefault("Notas", "");
            group.statusQuests = propsRealization.getOrDefault("Questões", "");
            group.statusTexts = propsRealization.getOrDefault("Redações", "");
        }
    }

    public static final void write(Ref ref, File file) throws Exception {
        WizText.write(file, getRefSource(ref));
    }

    public static String getRefSource(Ref ref) {
        return getRefSource(ref, true);
    }

    public static String getRefSource(Ref ref, boolean withMemoa) {
        var builder = new StringBuilder();
        builder.append(propsStart).append("\n");
        builder.append("hash-md5").append(propsSeparator).append(ref.props.hashMD5).append("\n");
        builder.append("created-at").append(propsSeparator).append(ref.props.createdAt).append("\n");
        builder.append("revised-on").append(propsSeparator).append(ref.props.revisedOn).append("\n");
        builder.append("revised-count").append(propsSeparator).append(ref.props.revisedCount).append("\n");
        builder.append("uploaded-at").append(propsSeparator).append(ref.props.uploadedAt).append("\n");
        builder.append(propsEnd).append("\n");
        if (withMemoa && ref.memoa.isPresent()) {
            builder.append(memoaStart).append("\n");
            builder.append(ref.memoa.text).append("\n");
            builder.append(memoaEnd).append("\n");
        }
        if (ref.group01.isPresent()) {
            builder.append(group01Start).append("\n");
            writeGroup(ref.group01, builder);
            builder.append(group01End).append("\n");    
        }
        if (ref.group02.isPresent()) {
            builder.append(group02Start).append("\n");
            writeGroup(ref.group02, builder);
            builder.append(group02End).append("\n");
        }
        if (ref.group03.isPresent()) {
            builder.append(group03Start).append("\n");
            writeGroup(ref.group03, builder);
            builder.append(group03End).append("\n");
        }
        if (ref.group04.isPresent()) {
            builder.append(group04Start).append("\n");
            writeGroup(ref.group04, builder);
            builder.append(group04End).append("\n");
        }
        if (ref.group05.isPresent()) {
            builder.append(group05Start).append("\n");
            writeGroup(ref.group05, builder);
            builder.append(group05End).append("\n");
        }
        if (ref.group06.isPresent()) {
            builder.append(group06Start).append("\n");
            writeGroup(ref.group06, builder);
            builder.append(group06End).append("\n");
        }
        if (ref.group07.isPresent()) {
            builder.append(group07Start).append("\n");
            writeGroup(ref.group07, builder);
            builder.append(group07End).append("\n");
        }
        if (ref.group08.isPresent()) {
            builder.append(group08Start).append("\n");
            writeGroup(ref.group08, builder);
            builder.append(group08End).append("\n");
        }
        if (ref.group09.isPresent()) {
            builder.append(group09Start).append("\n");
            writeGroup(ref.group09, builder);
            builder.append(group09End).append("\n");
        }
        if (ref.group10.isPresent()) {
            builder.append(group10Start).append("\n");
            writeGroup(ref.group10, builder);
            builder.append(group10End).append("\n");
        }
        if (ref.group11.isPresent()) {
            builder.append(group11Start).append("\n");
            writeGroup(ref.group11, builder);
            builder.append(group11End).append("\n");
        }
        if (ref.group12.isPresent()) {
            builder.append(group12Start).append("\n");
            writeGroup(ref.group12, builder);
            builder.append(group12End).append("\n");
        }
        return builder.toString();
    }

    private static void writeGroup(RefGroup group, StringBuilder builder) {
        builder.append(organizationStart).append("\n");
        builder.append("Classificação: ").append(group.classification).append("\n");
        builder.append("Titulação: ").append(group.titration).append("\n");
        builder.append(organizationEnd).append("\n");
        builder.append(topicsStart).append("\n");
        builder.append(group.topics).append("\n");
        builder.append(topicsEnd).append("\n");
        builder.append(realizationStart).append("\n");
        builder.append("Notas: ").append(group.statusNotes).append("\n");
        builder.append("Questões: ").append(group.statusQuests).append("\n");
        builder.append("Redações: ").append(group.statusTexts).append("\n");
        builder.append(realizationEnd).append("\n");
    }
    
}
