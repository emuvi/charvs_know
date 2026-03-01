package br.com.pointel.charvs_know;

public class ActDidactic implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperDidactic(workRef).setVisible(true);
    }

}
