import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class SecondUser {

    public static void main(String[] args) throws Exception {

        // -------- PRODUCER --------
        Properties pProps = new Properties();
        pProps.put("bootstrap.servers", "localhost:9092");
        pProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        pProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(pProps);

        // -------- CONSUMER --------
        Properties cProps = new Properties();
        cProps.put("bootstrap.servers", "localhost:9092");
        cProps.put("group.id", "user2-group");
        cProps.put("key.deserializer", StringDeserializer.class.getName());
        cProps.put("value.deserializer", StringDeserializer.class.getName());
        cProps.put("auto.offset.reset", "latest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(cProps);
        consumer.subscribe(Collections.singletonList("chat-topic"));

        // -------- THREAD FOR RECEIVING --------
        new Thread(() -> {
            while (true) {
                ConsumerRecords<String, String> records =
                        consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.value());
                }
            }
        }).start();

        // -------- SENDING --------
        Scanner sc = new Scanner(System.in);

        while (true) {
            String msg = sc.nextLine();
            producer.send(new ProducerRecord<>("chat-topic", "User2: " + msg)).get();
        }
    }
}