package br.com.pointel.charvs_know;

import br.com.pointel.charvs_know.desk.HelperClassify;

public class ActClassify implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperClassify(workRef).setVisible(true);
    }

}
