package br.com.pointel.charvs_know;

public class ActDidactic implements Act {

    @Override
    public void execute(SelectedRef selectedRef) throws Exception {
        new HelperDidactic(selectedRef).setVisible(true);
    }

}
