package Conexiones;

import Processes.ProcessA;
import Processes.ProcessB;

/**
 * Created by Marchu on 31/12/16.
 */
public class Main {

    protected int nLightweights;
    protected int idProcess;

    public Main (int nLightweights, int idProcess) throws Exception {
        this.nLightweights = nLightweights;
        this.idProcess = idProcess;
    }


    public static void main(String[] args) throws Exception {

        new Thread(new ProcessA(3)).start();
        new Thread(new ProcessB(2)).start();

    }

}
