package test.java.com.litemq.tests;

import main.java.com.litemq.core.LiteMQ;
import main.java.com.litemq.core.Producer;
import main.java.com.litemq.core.Consumer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class LiteMQTest {
    private LiteMQ liteMQ;

    @BeforeEach
    void setUp() throws IOException {
        liteMQ = new LiteMQ();
    }

    @AfterEach
    void tearDown() throws IOException {
        liteMQ.shutdown();
    }

    @Test
    void testMessageFlow() throws IOException {
        Producer producer = liteMQ.createProducer();
        Consumer consumer = liteMQ.createConsumer();

        producer.sendMessage("test_topic", "Hello LiteMQ!");
        producer.sendMessage("test_topic", "Another message");

        assertEquals("Hello LiteMQ!", consumer.consumeMessage().split(" ", 4)[3]);
        assertEquals("Another message", consumer.consumeMessage().split(" ", 4)[3]);
    }

    @Test
    void testConcurrentProducersConsumers() throws IOException {
        Producer producer1 = liteMQ.createProducer();
        Producer producer2 = liteMQ.createProducer();
        Consumer consumer1 = liteMQ.createConsumer();
        Consumer consumer2 = liteMQ.createConsumer();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.execute(() -> {
            try {
                producer1.sendMessage("test_topic", "P1-Message 1");
                producer1.sendMessage("test_topic", "P1-Message 2");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        executor.execute(() -> {
            try {
                producer2.sendMessage("test_topic", "P2-Message 1");
                producer2.sendMessage("test_topic", "P2-Message 2");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        executor.execute(() -> {
            try {
                assertNotNull(consumer1.consumeMessage());
                assertNotNull(consumer1.consumeMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        executor.execute(() -> {
            try {
                assertNotNull(consumer2.consumeMessage());
                assertNotNull(consumer2.consumeMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        executor.shutdown();
    }
}
