import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class Send {

  private final static String QUEUE_NAME = "robot";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    String command = "";


    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    while ("exit".compareToIgnoreCase(command) != 0) {
      System.out.println("Enter command: ");
      Scanner scanner = new Scanner(System.in);
      command = scanner.nextLine();
      channel.basicPublish("", QUEUE_NAME, null, command.getBytes("UTF-8"));
      System.out.println(" [x] Sent '" + command + "'");
    }

    channel.close();
    connection.close();
  }
}
