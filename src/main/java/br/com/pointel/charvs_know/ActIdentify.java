package br.com.pointel.charvs_know;

public class ActIdentify implements Act {

    @Override
    public void execute(SelectedRef selectedRef) throws Exception {
        new HelperIdentify(selectedRef).setVisible(true);
    }

}
