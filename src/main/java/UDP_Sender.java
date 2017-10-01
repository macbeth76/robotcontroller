import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


public class UDP_Sender {
    private static final int SERVER_PORT = 5000;
    private InetAddress broadcastAddress;


    public UDP_Sender() {


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
        UDP_Sender udp_sender = new UDP_Sender();
        udp_sender.sendMessage("Hello" + Math.random());

    }
}