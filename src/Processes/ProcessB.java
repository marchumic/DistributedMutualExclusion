package Processes;

import Conexiones.Conections;

import java.net.DatagramSocket;

/**
 * Created by Marchu on 31/12/16.
 */
public class ProcessB implements Runnable{

    private static int MAIN_PORT = 8000;
    private int numLightWeights;
    private DatagramSocket dSocket;
    private  DatagramSocket dSocketHeavy;
    private RicartAgrawala[] aAgrawala;


    public ProcessB(int numProcessosLight) throws Exception{

        this.numLightWeights = numProcessosLight;
        dSocketHeavy = new DatagramSocket(9001);
        dSocket = new DatagramSocket((MAIN_PORT+numLightWeights)); //Port 8002

        //Iniciem Lights, encenem threads
        aAgrawala = new RicartAgrawala[2];
        for (int i = 0; i < numProcessosLight; i++) {
            //Inicialitzem cada Lamport
            aAgrawala[i] = new RicartAgrawala(i, (8007+i));
            new Thread(aAgrawala[i]).start();
        }

    }

    public void run(){
        String receivedMessage = "";
        //Instanciem la classe de conexions
        Conections conect = new Conections();

        try {

            while(true) {
                //M'espero a que el Process A hagi acabat
                conect.receiveMessage(dSocketHeavy);

                //Enviar missatge als lightweight conforme ja poden començar a fer coses
                conect.sendActiveLightWeights(dSocketHeavy, numLightWeights, 9005, "Active");

                //Esperar a que els lightweight acabin de fer les seves cosetes
                conect.waitLightweightFinish(dSocketHeavy, numLightWeights);

                //Envio al process A que ja he acabat
                conect.sendMessage("uToken", dSocketHeavy, 9000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
