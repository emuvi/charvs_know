package br.com.pointel.charvs_know;

import br.com.pointel.charvs_know.desk.HelperIdentify;

public class ActIdentify implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperIdentify(workRef).setVisible(true);
    }

}
