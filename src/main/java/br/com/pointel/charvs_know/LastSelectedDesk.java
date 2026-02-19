package br.com.pointel.charvs_know;

import br.com.pointel.jarch.desk.SelectDesk;
import br.com.pointel.jarch.mage.WizGUI;

public class LastSelectedDesk extends SelectDesk<String> {

    private final CharvsKnowDesk parent;

    public LastSelectedDesk(CharvsKnowDesk parent) {
        super("Last Selected");
        this.parent = parent;
        initComponents();
    }
    
    private void initComponents() {
        var lastSelected = Setup.getLastSelectedRefs();
        for (var ref : lastSelected) {
            if (ref != null && !ref.isBlank()) {
                addOption(ref);
            }
        }
        onSelect(this::onSelect);
    }

    private void onSelect(String selected) {
        try {
            if (selected == null || selected.isBlank()) {
                return;
            }
            parent.selectRef(selected);
        } catch (Exception e) {
            WizGUI.showError(e);
        }
    }

}
