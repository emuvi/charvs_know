package br.com.pointel.charvs_know;

import java.util.Date;

import br.com.pointel.jarch.mage.WizInteger;
import br.com.pointel.jarch.mage.WizUtilDate;

public class ActRevisedAtNow implements Act {

    @Override
    public void execute(SelectedRef selectedRef) throws Exception {
        selectedRef.ref.props.revisedAt = WizUtilDate.formatDateMach(new Date());
        Integer revisedCount = WizInteger.get(selectedRef.ref.props.revisedCount, 0);
        revisedCount++;
        selectedRef.ref.props.revisedCount = revisedCount.toString();
        selectedRef.write();
    }

}
