package Conexiones;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Marchu on 31/12/16.
 */
public class Conections {



    //Funció que enviara el missatge
    public void sendMessage(String message, DatagramSocket s, int port) throws  Exception{

        InetAddress hostAddress = InetAddress.getByName("localhost");
        DatagramPacket info = new DatagramPacket(message.getBytes(), message.getBytes().length, hostAddress, port);
        s.send(info);
        //System.out.println("Envio \"" + message + "\" al port: " + port);

    }

    //Funció que rebra del port que li diguem al socket especificat
    public void receiveMessage(DatagramSocket s) throws  Exception{

        byte[] buf = new byte[1000];

        InetAddress hostAddress = InetAddress.getByName("localhost");
        DatagramPacket info = new DatagramPacket(buf, buf.length);
        s.receive(info);

        //System.out.println("Received: \"" + new String(info.getData(), 0, info.getLength()) + "\", from port: " + info.getPort());

    }

    public void sendActiveLightWeights(DatagramSocket s, int numLights, int port, String message) throws Exception {

        //Activem els LightWeight
        for(int i = 0; i < numLights; i++){
            sendMessage(message, s, (port+i));
        }

    }

    public void waitLightweightFinish(DatagramSocket s, int numLightWeight) throws  Exception {

        byte[] buf = new byte[1000];
        InetAddress hostAddress = InetAddress.getByName("localhost");

        for (int i = 0; i < numLightWeight; i++) {
            DatagramPacket info = new DatagramPacket(buf, buf.length);
            s.receive(info);

            //System.out.println("Received: \"" + new String(info.getData(), 0, info.getLength()) + "\", from port: " + info.getPort());
        }

    }

}
