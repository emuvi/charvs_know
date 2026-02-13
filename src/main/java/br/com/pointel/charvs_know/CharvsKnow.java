package br.com.pointel.charvs_know;

import br.com.pointel.jarch.flow.App;
import br.com.pointel.jarch.flow.AppGUI;

public class CharvsKnow {

    public static void main(String[] args) throws Exception {
        new App(new AppGUI( CharvsKnowDesk.class)).start("CharvsKnow", args);
    }

}
