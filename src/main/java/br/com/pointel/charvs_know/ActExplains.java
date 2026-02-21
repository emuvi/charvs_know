package br.com.pointel.charvs_know;

public class ActExplains implements Act {

    @Override
    public void execute(SelectedRef selectedRef) throws Exception {
        new HelperExplains(selectedRef).setVisible(true);
    }

}
