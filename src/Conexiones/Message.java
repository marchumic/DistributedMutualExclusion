package Conexiones;

import ClocksClass.DirectClock;
import ClocksClass.LamportClock;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.util.LinkedList;

/**
 * Created by Marchu on 2/1/17.
 */
public class Message {

    public Message(){}

    public void broadcastMsg(String bCastMsg, int timeStamp, DatagramSocket s, int portPropi, int identificador) throws Exception {
        String message;
        DatagramPacket packet;
        InetAddress hostAddress = InetAddress.getByName("localhost");

        message = bCastMsg + "-" + timeStamp + "-" + identificador;
        for(int i = 8004; i < 8007; i++){
            if(i != portPropi) {
                packet = new DatagramPacket(message.getBytes(), message.getBytes().length, hostAddress, i);
                s.send(packet);
            }
        }
    }

    public void broadcastMsgRA(String bCastMsg, int timeStamp, DatagramSocket s, int portPropi, int identificador) throws Exception {
        String message;
        DatagramPacket packet;
        InetAddress hostAddress = InetAddress.getByName("localhost");

        message = bCastMsg + "-" + timeStamp + "-" + identificador;
        for(int i = 8007; i < 8009; i++){
            if(i != portPropi) {
                packet = new DatagramPacket(message.getBytes(), message.getBytes().length, hostAddress, i);
                s.send(packet);
            }
        }
    }

    public void myWaitLamport(DatagramSocket s, DirectClock v, int[] q, int portPropi, int identificador) throws  Exception{
        String msg;
        String[] parts;
        byte[] buf = new byte[1000];

        DatagramPacket datagram = new DatagramPacket(buf, 100); //Rebem el missatge que ens arriba al nostre port
        s.receive(datagram);

        msg = new String(datagram.getData(), 0, datagram.getLength());
        parts = msg.split("-");

        //Aixo realment es el HandleMSg
        v.receiveAction(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]));
        if (parts[0].equals("request")) {
            q[Integer.parseInt(parts[2])] = Integer.parseInt(parts[1]); //Identificador, timestamp
            String missatge = "ack-" + v.getValue(identificador) + "-" + identificador;
            enviaOK(datagram.getPort(), missatge, s);
            v.sendAction();//sí, no????
        }else if (parts[0].equals("release")) {
            q[Integer.parseInt(parts[2])] = -1;
        }
    }

    public int myWaitRA(DatagramSocket s, LamportClock c, int myts, int numOkay, LinkedList<Integer> pendingQ, int identificador) throws Exception{
        String msg;
        String[] parts;
        byte[] buf = new byte[1000];

        DatagramPacket datagram = new DatagramPacket(buf, 100); //Rebem el missatge que ens arriba al nostre port
        s.receive(datagram);

        msg = new String(datagram.getData(), 0, datagram.getLength());
        parts = msg.split("-");

        c.receiveAction(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]));
        if(parts[0].equals("request")){
            if((myts == -1) || (Integer.parseInt(parts[1]) < myts) || ((Integer.parseInt(parts[1]) == myts) && (Integer.parseInt(parts[2]) < identificador))) {
                String missatge = "okay-" + c.getValue() + "-" + parts[2];
                enviaOK(datagram.getPort(), missatge, s);
            }else{
                pendingQ.add(Integer.parseInt(parts[2]));
            }
        }else if(parts[0].equals("okay")){
            numOkay++;
        }
        return numOkay;
    }

    public void enviaOK(int port, String msg, DatagramSocket s) throws  Exception{

        InetAddress hostAddress = InetAddress.getByName("localhost");

        DatagramPacket datagram = new DatagramPacket(msg.getBytes(), msg.getBytes().length, hostAddress, port);
        s.send(datagram);

    }



}
