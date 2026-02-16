package br.com.pointel.charvs_know;

import java.util.Date;

import br.com.pointel.jarch.mage.WizUtilDate;

public class ActDoneAtNow implements Act {

    @Override
    public void execute(SelectedRef selectedRef) throws Exception {
        selectedRef.ref.props.doneAt = WizUtilDate.formatDateMach(new Date());
        selectedRef.write();
    }

}
