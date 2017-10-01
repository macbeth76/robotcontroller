import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.studiohartman.jamepad.*;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


public class UDP_Sender_Controller {
    private static final int SERVER_PORT = 5000;
    private InetAddress broadcastAddress;


    public UDP_Sender_Controller() {


    }

    public List<InetAddress> findBroadcastAddress() {
        List<InetAddress> broadcastAddress = new ArrayList<InetAddress>();


        Enumeration<NetworkInterface> en = null;
        try {
            en = NetworkInterface.getNetworkInterfaces();

            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                System.out.println(" Display Name = " + ni.getDisplayName());

                List<InterfaceAddress> list = ni.getInterfaceAddresses();
                Iterator<InterfaceAddress> it = list.iterator();

                while (it.hasNext()) {
                    InterfaceAddress ia = it.next();
                    System.out.println(" Broadcast = " + ia.getBroadcast());
                    if (ia.getBroadcast() != null) {
                        broadcastAddress.add(ia.getBroadcast());
                    }

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return broadcastAddress;

    }


    public void sendMessage(String message) {

        DatagramSocket ds = null;
        List<InetAddress> broadcastAddress = findBroadcastAddress();
        if (broadcastAddress.isEmpty()) {
            System.out.println("No Broadcast Address Found");
            System.exit(1);
        }

        try {
            for (InetAddress broadcast : broadcastAddress) {
                System.out.println("Sending out on " + broadcast);
                ds = new DatagramSocket();
                DatagramPacket dp;
                dp = new DatagramPacket(message.getBytes(), message.length(), broadcast, SERVER_PORT);
                ds.setBroadcast(true);
                ds.send(dp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ds != null) {
                ds.close();
            }
        }


    }

    public static void main(String[] argv) throws Exception {
        UDP_Sender_Controller udp_sender = new UDP_Sender_Controller();

        String command = "";
        ControllerManager controllers = new ControllerManager();
        controllers.initSDLGamepad();
        ControllerIndex currController = controllers.getControllerIndex(0);

        while (!currController.isButtonPressed(ControllerButton.START)) {
            controllers.update(); //If using ControllerIndex, you should call update() to check if a new controller
            //was plugged in or unplugged at this index.
            try {
                if (currController.isButtonPressed(ControllerButton.A)) {
                    System.out.println("\"A\" on \"" + currController.getName() + "\" is pressed");
                    command = "A";
                    udp_sender.sendMessage(command);
                }
                if (currController.isButtonPressed(ControllerButton.B)) {
                    System.out.println("\"B\" on \"" + currController.getName() + "\" is pressed");
                    command = "B";
                    udp_sender.sendMessage(command);
                }
                if (currController.isButtonPressed(ControllerButton.X)) {
                    System.out.println("\"X\" on \"" + currController.getName() + "\" is pressed");
                    command = "X";
                    udp_sender.sendMessage(command);
                }
                if (currController.isButtonPressed(ControllerButton.Y)) {
                    System.out.println("\"Y\" on \"" + currController.getName() + "\" is pressed");
                    command = "Y";
                    udp_sender.sendMessage(command);
                }
                float x = currController.getAxisState(ControllerAxis.LEFTX);
                float y = currController.getAxisState(ControllerAxis.LEFTY);

                if (x > .01 || x < -.01) {
                    System.out.println("\"left x \" on \"" + currController.getName() + "\" is " + x);
                    command = "x=" + x;
                    udp_sender.sendMessage(command);
                }
                if (y > 0.01 || x < -.01) {
                    System.out.println("\"left y \" on \"" + currController.getName() + "\" is " + y);
                    command = "y=" + y;
                    udp_sender.sendMessage(command);
                }

            } catch (ControllerUnpluggedException e) {
                System.out.println("Controller Unplugged...");
                Thread.sleep(1000);
            }
        }

    }

}
