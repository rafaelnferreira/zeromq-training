package global.training;

import java.util.Random;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZThread;
import org.zeromq.ZThread.IAttachedRunnable;




public class Main {

    


    private static class Listener implements IAttachedRunnable
{
    @Override
    public void run(Object[] args, ZContext ctx, Socket pipe)
    {
        //  Print everything that arrives on pipe
        while (true) {
            ZFrame frame = ZFrame.recvFrame(pipe);
            if (frame == null)
                break; //  Interrupted
            frame.print(null);
            frame.destroy();
        }
    }
}

 //  .split publisher thread
    //  The publisher sends random messages starting with A-J:
    private static class Publisher implements IAttachedRunnable
    {
        private final int port;

        private Publisher(int port) {
            this.port = port;
        }

        @Override
        public void run(Object[] args, ZContext ctx, Socket pipe)
        {
            Socket publisher = ctx.createSocket(SocketType.PUB);
            publisher.connect("tcp://localhost:" + port);

            while (!Thread.currentThread().isInterrupted()) {
                
                java.util.Scanner scanner = new java.util.Scanner(System.in);

                while (true) {
                    System.out.print("Enter topic (empty to exit): ");
                    String topicInput = scanner.nextLine().trim();

                    if (topicInput.isEmpty()) {
                        break;
                    }

                    System.out.print("Enter message: ");
                    String messageInput = scanner.nextLine();

                    // Publish the message with the specified topic
                    publisher.sendMore(topicInput);
                    publisher.send(messageInput);
                }

                scanner.close();

            }

            notifyAll();
            
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 3) {
            System.out.println("Usage: java Main <mode> <pubPort> <subPort> <topic1> <topic2> ...");
            return;
        }

        String mode = args[0];

        int pubPort = Integer.parseInt(args[1]);
        int subPort = Integer.parseInt(args[2]);

        System.out.println("PubPort is: "+ pubPort);
        System.out.println("SubPort is: "+ subPort);


        String[] topics = new String[args.length - 3];
        System.arraycopy(args, 3, topics, 0, topics.length);

        // Create a ZeroMQ context
        ZContext context = new ZContext();

        ZThread.fork(context, new Subscriber(pubPort, topics));
        ZThread.fork(context, new Publisher(subPort));

        if ("broker".equalsIgnoreCase((mode))) {
            // Create the xsub socket
            ZMQ.Socket subscriber = context.createSocket(SocketType.XSUB);
            subscriber.bind("tcp://localhost:" + subPort);

            // Create the xpub socket
            ZMQ.Socket publisher = context.createSocket(SocketType.XPUB);
            publisher.bind("tcp://localhost:" + pubPort);

            Socket listener = ZThread.fork(context, new Listener());
            ZMQ.proxy(subscriber, publisher, listener);

            subscriber.close();
            publisher.close();
            System.out.println(" interrupted");
        } 

        System.out.println("finishing main");


        //context.close();
        

    }
}
