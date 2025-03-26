# LiteMQ - Lightweight Java Message Queue

LiteMQ is a simple file-based message queue system built in Java, inspired by Kafka-style architecture.

It supports producers, consumers, logging to files, message polling, offset tracking, and multi-consumer coordination.

## Architecture Overview:
![Workflow](docs/LiteMq-Arch.png)

## Components:
- LiteMQ: Creates producers and consumers, manages subscriptions.
- Producer: Sends messages to a topic using LogWriter.
- Consumer: Subscribes to a topic and reads messages using LogReader.
- LogWriter: Writes messages to log files.
- LogReader: Reads messages from log files based on offset.
- SubscriptionManager: Tracks topic subscriptions and delivers messages.

## Features Completed:
✅ File-based message logging  
✅ Consumer polling with WatchService  
✅ Offset tracking for message reading  
✅ Round-robin delivery to multiple consumers  
✅ Graceful shutdown and cleanup  
✅ Buffered messages for offline consumers  
✅ Manual testing apps working successfully  

## Manual Testing Setup:
Use the following test apps (can run from one JVM or use a unified test class):
1. LiteMQTestApp - Starts the system.
2. ConsumerTestApp - Subscribes to a topic.
3. ProducerTestApp - Sends messages.

## Next Steps:
- Finalize and fix automated test cases  
- Improve performance (read/write speed)  
- Add better logging and debugging messages  
- (Optional) Docker support  
- (Optional) CLI interface for interaction  




