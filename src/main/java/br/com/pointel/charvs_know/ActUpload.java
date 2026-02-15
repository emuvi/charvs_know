package br.com.pointel.charvs_know;

import java.util.Date;

import br.com.pointel.jarch.mage.WizGUI;
import br.com.pointel.jarch.mage.WizUtilDate;

public class ActUpload implements Act {

    @Override
    public void execute(SelectedRef selectedRef) throws Exception {
        RefFTP.upload(selectedRef.sourceFile, selectedRef.refWithExtension);
        selectedRef.ref.props.uploadedAt = WizUtilDate.formatDateMach(new Date());
        selectedRef.write();
        WizGUI.showInfo("Upload executed.");
    }

}
