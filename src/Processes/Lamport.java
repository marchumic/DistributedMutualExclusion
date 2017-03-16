package Processes;

import ClocksClass.DirectClock;
import Conexiones.Conections;
import Conexiones.Message;

import java.net.DatagramSocket;

/**
 * Created by Marchu on 31/12/16.
 */
public class Lamport implements Runnable{

    private int identificador;
    private int portPropi;
    private DatagramSocket dSocket;
    volatile DirectClock v;
    private int[] q;
    private Message msg;
    private DatagramSocket dSocketHeavy;

    public Lamport(int identificador, int portPropi) throws  Exception{
        this.identificador = identificador;
        this.portPropi = portPropi;

        msg = new Message();
        dSocketHeavy = new DatagramSocket(9002+identificador); //HEm de crear aquest socket per poder parlar amb el heavy, sino se'ns solapen els missatges
        dSocket = new DatagramSocket(portPropi); //8004 + id

        v = new DirectClock(3, identificador);
        q = new int[3];
        for(int j = 0; j < 3; j++){
            q[j] = -1;
        }

    }

    public synchronized void requestCS() throws Exception{
        v.tick();
        q[identificador] = v.getValue(identificador);
        msg.broadcastMsg("request", q[identificador], dSocket, (8004+identificador), identificador);
        while(!okayCS()){
            msg.myWaitLamport(dSocket, v, q, portPropi, identificador);
        }
    }

    public synchronized void releaseCS() throws Exception{
        q[identificador] = -1;
        msg.broadcastMsg("release", v.getValue(identificador), dSocket, (8004+identificador), identificador);
    }

    boolean okayCS(){
        for(int i = 0; i < 3; i++){
            if(isGreater(q[identificador], identificador, q[i], i)) return false;
            if(isGreater(q[identificador], identificador, v.getValue(i), i)) return false;
        }
        return true;
    }

    boolean isGreater(int entry1, int pid1, int entry2, int pid2){
        if(entry2 == -1) return false;
        return((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
    }

    public void criticalSection() throws Exception {
        for (int i = 0; i < 10; i++) {
            System.out.println("Soc el proces lightweight A" + identificador);
            Thread.sleep(500);
        }
    }

    public void restart(){
        v = new DirectClock(3, identificador);
        q = new int[3];
        for(int j = 0; j < 3; j++){
            q[j] = -1;
        }
    }

    public void run(){

        Conections conect = new Conections();

        try {
            while(true) {
                //Rebem el missatge conforme estem actius
                conect.receiveMessage(dSocketHeavy);

                //Fem reset(Sobretot per la segona volta)
                restart();

                //Fem de les nostres
                requestCS();
                criticalSection();
                releaseCS();

                //Enviem missatge conforme el process A ja ha acabat
                conect.sendMessage("Finished LightWeight Lamport", dSocketHeavy, 9000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
