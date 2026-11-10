
import com.rabbitmq.client.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MessageQueueWorker {

    private static final Path INDEX_PATH =
            Path.of("../index/Index.txt");

    public static void start() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(
                MessageQueueServer.QUEUE_NAME,
                false, false, false, null
        );

        channel.basicConsume(
                MessageQueueServer.QUEUE_NAME,
                false,
                (consumerTag, delivery) -> {
                    String message = new String(
                            delivery.getBody(),
                            StandardCharsets.UTF_8
                    );

                    String[] parts = message.split("\\|", 2);

                    if (parts.length != 2) {
                        channel.basicAck(
                                delivery.getEnvelope().getDeliveryTag(),
                                false
                        );
                        return;
                    }

                    String requestId = parts[0];
                    String keyword = parts[1];

                    try {
                        String result = searchTermInIndex(keyword);

                        MessageQueueServer.results.put(
                                requestId,
                                result.isEmpty() ? "Keyword not found" : result
                        );

                        System.out.println(
                                "Processed request " + requestId +
                                " for keyword: " + keyword
                        );

                        channel.basicAck(
                                delivery.getEnvelope().getDeliveryTag(),
                                false
                        );

                    } catch (Exception e) {
                        System.err.println(
                                "Worker error: " + e.getMessage()
                        );

                        channel.basicNack(
                                delivery.getEnvelope().getDeliveryTag(),
                                false,
                                true
                        );
                    }
                },
                consumerTag -> {}
        );

        System.out.println("RabbitMQ search worker started.");
    }

    private static String searchTermInIndex(String keyword)
            throws Exception {

        try (var reader = Files.newBufferedReader(INDEX_PATH)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\.\\|");

                if (parts.length > 0 && parts[0].equals(keyword)) {
                    return line;
                }
            }
        }

        return "";
    }
}
