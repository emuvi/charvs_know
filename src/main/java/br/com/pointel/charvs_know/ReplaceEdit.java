package br.com.pointel.charvs_know;

import br.com.pointel.jarch.desk.DEdit;
import br.com.pointel.jarch.desk.DFieldEdit;
import br.com.pointel.jarch.desk.DLabelEdit;
import br.com.pointel.jarch.desk.DLinePane;
import br.com.pointel.jarch.desk.DPane;
import br.com.pointel.jarch.desk.DRowPane;
import br.com.pointel.jarch.desk.DStringField;

import javax.swing.Box;

import br.com.pointel.jarch.desk.DCheckEdit;
import br.com.pointel.jarch.desk.DColPane;

public class ReplaceEdit extends DEdit<Replace> {

    private DFieldEdit<String> fieldName = new DStringField().cols(12);
    private DCheckEdit fieldRegex = new DCheckEdit().text("Regex");
    private DPane paneOptions = new DRowPane().insets(3)
            .growHorizontal().put(fieldName)
            .put(fieldRegex)
            .growHorizontal().put(Box.createHorizontalBox());
    private DFieldEdit<String> fieldOf = new DStringField().cols(24);
    private DLabelEdit<String> titledOf = new DLabelEdit<>("Of:", fieldOf);
    private DFieldEdit<String> fieldTo = new DStringField().cols(24);
    private DLabelEdit<String> titledTo = new DLabelEdit<>("To:", fieldTo);
    private DPane paneReplace = new DRowPane().insets(3)
            .growHorizontal().put(titledOf)
            .put(titledTo);
    private DPane paneBody = new DColPane()
            .growBoth().put(paneOptions)
            .put(paneReplace);

    public ReplaceEdit() {
        comp(paneBody);
    }

    @Override
    public Replace getValue() {
        return new Replace(fieldName.getValue(), fieldRegex.getValue(), fieldOf.getValue(), fieldTo.getValue());
    }

    @Override
    public void setValue(Replace value) {
        fieldName.setValue(value.name);
        fieldRegex.setValue(value.regex);
        fieldOf.setValue(value.of);
        fieldTo.setValue(value.to);
    }

}
