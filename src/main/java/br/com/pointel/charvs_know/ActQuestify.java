package br.com.pointel.charvs_know;

public class ActQuestify implements Act {

    @Override
    public void execute(SelectedRef selectedRef) throws Exception {
        new HelperQuestify(selectedRef).setVisible(true);
    }

}
