package global.training;

import java.time.LocalDateTime;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

/**
 * Pubsub envelope subscriber
 */

public class SubEnv
{

    public static void main(String[] args)
    {
        LocalDateTime start = LocalDateTime.now();

        // Prepare our context and subscriber
        try (ZContext context = new ZContext()) {
            Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://" + Util.HOST_NAME + ":5563");
            subscriber.subscribe("B".getBytes(ZMQ.CHARSET));

            while (!Thread.currentThread().isInterrupted()) {
                // Read envelope with address
                String topic = subscriber.recvStr();
                // Read message contents
                String contents = subscriber.recvStr();
                System.out.println(String.format("[%s] received from topic: %s, process started at: %s", LocalDateTime.now(), topic, start));
            }
        }
    }
}