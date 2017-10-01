import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.studiohartman.jamepad.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Scanner;

public class Controller {

    private final static String QUEUE_NAME = "robot";

    public static void main(String[] argv) throws Exception {
        ControllerManager controllers = new ControllerManager();
        controllers.initSDLGamepad();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String command = "";


        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        ControllerIndex currController = controllers.getControllerIndex(0);

        while (!currController.isButtonPressed(ControllerButton.START)) {
            controllers.update(); //If using ControllerIndex, you should call update() to check if a new controller
            //was plugged in or unplugged at this index.
            try {
                if (currController.isButtonPressed(ControllerButton.A)) {
                    System.out.println("\"A\" on \"" + currController.getName() + "\" is pressed");
                    command = "A";
                    sendMessage(channel, command);
                }
                if (currController.isButtonPressed(ControllerButton.B)) {
                    System.out.println("\"B\" on \"" + currController.getName() + "\" is pressed");
                    command = "B";
                    sendMessage(channel, command);
                }
                if (currController.isButtonPressed(ControllerButton.X)) {
                    System.out.println("\"X\" on \"" + currController.getName() + "\" is pressed");
                    command = "X";
                    sendMessage(channel, command);
                }
                if (currController.isButtonPressed(ControllerButton.Y)) {
                    System.out.println("\"Y\" on \"" + currController.getName() + "\" is pressed");
                    command = "Y";
                    sendMessage(channel, command);
                }
                float x = currController.getAxisState(ControllerAxis.LEFTX);
                float y = currController.getAxisState(ControllerAxis.LEFTY);

                if (x > .01 || x < -.01) {
                    System.out.println("\"left x \" on \"" + currController.getName() + "\" is " + x);
                    command = "x=" + x;
                    sendMessage(channel, command);
                }
                if (y > 0.01 || x < -.01) {
                    System.out.println("\"left y \" on \"" + currController.getName() + "\" is " + y);
                    command = "y=" + y;
                    sendMessage(channel, command);
                }

            } catch (ControllerUnpluggedException e) {
                System.out.println("Controller Unplugged...");
                Thread.sleep(1000);
            }
        }
        channel.close();
        connection.close();
    }

    private static void sendMessage(Channel channel, String command) throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        command = timestamp.toString() + ":" + command;
        channel.basicPublish("", QUEUE_NAME, null, command.getBytes("UTF-8"));
    }
}
