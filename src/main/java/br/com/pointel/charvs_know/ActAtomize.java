package br.com.pointel.charvs_know;

import br.com.pointel.charvs_know.desk.HelperAtomize;

public class ActAtomize implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperAtomize(workRef).setVisible(true);
    }

}
