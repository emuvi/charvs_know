package br.com.pointel.charvs_know;

import br.com.pointel.charvs_know.desk.HelperDidactic;

public class ActDidactic implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperDidactic(workRef).setVisible(true);
    }

}
