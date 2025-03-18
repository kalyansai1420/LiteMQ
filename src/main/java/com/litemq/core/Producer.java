package com.litemq.core;

import java.io.IOException;
import java.util.List;

import com.litemq.log.LogWriter;

public class Producer {

    private final LogWriter logWriter;
    private final SubscriptionManager subscriptionManager;

    public Producer(LogWriter logWriter, SubscriptionManager subscriptionManager) {
        this.logWriter = logWriter;
        this.subscriptionManager = subscriptionManager;
    }

    public void sendMessage(String topic, String message) throws IOException{
        logWriter.writeMessage(topic,message);
        subscriptionManager.notifySubscribers(topic,message);
    }

    

}
