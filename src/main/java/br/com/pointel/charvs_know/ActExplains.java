package br.com.pointel.charvs_know;

import br.com.pointel.charvs_know.desk.HelperExplains;

public class ActExplains implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperExplains(workRef).setVisible(true);
    }

}
