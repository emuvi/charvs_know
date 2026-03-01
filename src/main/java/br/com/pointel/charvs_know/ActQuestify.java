package br.com.pointel.charvs_know;

public class ActQuestify implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperQuestify(workRef).setVisible(true);
    }

}
