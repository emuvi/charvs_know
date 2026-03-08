package br.com.pointel.charvs_know;

import br.com.pointel.charvs_know.desk.HelperStructure;

public class ActStructure implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperStructure(workRef).setVisible(true);
    }

}
