package br.com.pointel.charvs_know;

public class ActOrganize implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperOrganize(workRef).setVisible(true);
    }

}
