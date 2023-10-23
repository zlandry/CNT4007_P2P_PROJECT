import cnt.Client;
import cnt.Server;

public class Peer {
    public static void main(String args[]) {
        try {
            //Client.main(args);
            Server.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
