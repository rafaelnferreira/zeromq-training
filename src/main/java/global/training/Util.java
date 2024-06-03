package global.training;

import java.net.InetAddress;

public class Util {
    private Util() {}

    static final String HOST_NAME;

    static {
        String h;
        try {
            h = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            h = "localhost";
        }

        HOST_NAME = h;
        
    }

}
