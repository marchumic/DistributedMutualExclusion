package Processes;

import Conexiones.Conections;

import java.net.DatagramSocket;

/**
 * Created by Marchu on 29/10/16.
 */
public class ProcessA implements Runnable{

    private static int MAIN_PORT = 8000;
    private int numLightWeights;
    private DatagramSocket dSocket, dSocketHeavy;
    private Lamport[] aLamport;

    public ProcessA(int numProcessosLight) throws Exception{

        this.numLightWeights = numProcessosLight;

        //Iniciem el socket per el que enviarem/arriba la informació
        dSocketHeavy = new DatagramSocket(9000);
        dSocket = new DatagramSocket((MAIN_PORT+numLightWeights)); //Port 8003

        //Iniciem Lights, encenem threads
        aLamport = new Lamport[3];
        for (int i = 0; i < numProcessosLight; i++) {
            //Inicialitzem cada Lamport
            aLamport[i] = new Lamport(i, (8004+i)); //id, port
            new Thread(aLamport[i]).start();
        }

    }

    public void run(){
        String receivedMessage = "";
        //Instanciem la classe de connexions
        Conections conect = new Conections();

        try {
            while(true) {
                //Enviar missatge als lightweight conforme ja poden començar a fer coses
                conect.sendActiveLightWeights(dSocketHeavy, numLightWeights, 9002, "Active");

                //Esperar a que els lightweight acabin de fer les seves cosetes
                conect.waitLightweightFinish(dSocketHeavy, numLightWeights);
                System.out.println("S'han acabat els light");

                //Envio al process B que ja he acabat de fer les coses
                conect.sendMessage("uToken", dSocketHeavy, 9001);

                //M'espero a que el process B hagi acabat per tornar a fer jo les meves coses
                conect.receiveMessage(dSocketHeavy);
                //Thread.sleep(1000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
