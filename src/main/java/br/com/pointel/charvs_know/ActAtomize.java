package br.com.pointel.charvs_know;

public class ActAtomize implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperAtomize(workRef).setVisible(true);
    }

}
