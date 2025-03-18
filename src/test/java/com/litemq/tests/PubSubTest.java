// package com.litemq.tests;

// import java.util.function.Consumer;

// import com.litemq.core.Producer;
// import com.litemq.core.SubscriptionManager;
// import com.litemq.log.LogWriter;

// public class PubSubTest {
//     public static void main(String[] args) {
//         SubscriptionManager manager = new SubscriptionManager();
//         LogWriter logWriter = new LogWriter("logs/", 1024);
//         Producer producer = new Producer(logWriter, manager);

//         Consumer consumer1 = new Consumer("Consumer-1");
//         Consumer consumer2 = new Consumer("Consumer-2");

//         manager.subscribe("orders", consumer1);
//         manager.subscribe("orders", consumer2);

//         producer.publish("orders", "Order #123 created");
//         producer.publish("orders", "Order #124 created");
//     }
// }
