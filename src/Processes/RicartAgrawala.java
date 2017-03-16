package Processes;

import ClocksClass.LamportClock;
import Conexiones.Conections;
import Conexiones.Message;

import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.util.LinkedList;

/**
 * Created by Marchu on 31/12/16.
 */
public class RicartAgrawala implements Runnable{

    private int identificador;
    private int portPropi;
    private Message msg;
    private DatagramSocket dSocket;
    private DatagramSocket dSocketHeavy;

    int myts;
    int numOkay = 0;
    LamportClock c = new LamportClock();
    LinkedList<Integer> pendingQ = new LinkedList<>();

    public RicartAgrawala(int identificador, int portPropi) throws  Exception{
        this.identificador = identificador;
        this.portPropi = portPropi;

        msg = new Message();
        dSocketHeavy = new DatagramSocket(9005+identificador);
        dSocket = new DatagramSocket(portPropi); //8004 + id

        myts = -1;
    }

    public synchronized void requestCS() throws Exception{
        c.tick();
        myts = c.getValue();
        msg.broadcastMsgRA("request", myts, dSocket, (8007+identificador), identificador);
        numOkay = 0;
        while(numOkay < 2-1){ //2 perque es el numero de processos
            numOkay = msg.myWaitRA(dSocket, c, myts, numOkay, pendingQ, identificador);
        }
    }

    public synchronized void releaseCS() throws Exception{
        myts = -1;
        while(!pendingQ.isEmpty()){
            int pid = pendingQ.removeFirst();
            String missatge = "okay-" + c.getValue() + "-" + pid;
            msg.enviaOK((8007+pid), missatge, dSocket);
        }
    }

    public void criticalSection() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println("Soc el proces lightweight B" + identificador);
            Thread.sleep(500);
        }
    }

    public void run(){

        Conections conect = new Conections();

        try {
            while(true) {

                //Rebem el missatge conforme estem actius
                conect.receiveMessage(dSocketHeavy);

                //Faig cosetes maques i divertides
                requestCS();
                criticalSection();
                releaseCS();

                //Envio cap al process A que ja he acabat
                conect.sendMessage("Finished LightWeight Agrawala", dSocketHeavy, 9001);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
