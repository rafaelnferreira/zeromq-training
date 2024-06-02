package global.training;

import org.zeromq.ZContext;
import org.zeromq.ZThread;

public class SubscriberMain {
    
    public static void main(String ... args) {
        ZContext context = new ZContext();
        ZThread.fork(context, new Subscriber(19000, "^CLUSTER_OUTBOUND"));

        System.out.println("finishing main");
    }

}
