package br.com.pointel.charvs_know;

public class ActClassify implements Act {

    @Override
    public void execute(SelectedRef selectedRef) throws Exception {
        new HelperClassify(selectedRef).setVisible(true);
    }

}
