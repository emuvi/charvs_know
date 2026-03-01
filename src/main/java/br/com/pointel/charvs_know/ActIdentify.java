package br.com.pointel.charvs_know;

public class ActIdentify implements Act {

    @Override
    public void execute(WorkRef workRef) throws Exception {
        new HelperIdentify(workRef).setVisible(true);
    }

}
