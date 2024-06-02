package global.training;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZThread.IAttachedRunnable;

public class Subscriber implements IAttachedRunnable {
    private final int port;
    private final String[] topics;

    public Subscriber(int port, String... topics) {
        this.port = port;
        this.topics = topics;
    }

    @Override
    public void run(Object[] args, ZContext ctx, Socket pipe) {
        Socket subscriber = ctx.createSocket(SocketType.SUB);
        subscriber.connect("tcp://MACBOOK37.local:" + port);

        int count = 0;

        for (String topic : topics) {
            System.out.println("Subscribing to: " + topic);
            subscriber.subscribe(topic.getBytes(ZMQ.CHARSET));
        }

        while (!Thread.currentThread().isInterrupted()) {
            String topic = subscriber.recvStr();
            String message = subscriber.recvStr();
            System.out.println("\nReceived: Topic - " + topic + ", Message - " + message + " count is: " + ++count);
        }

    }
}
