import cnt.Peer;

class MultiPeer extends Thread {
    private volatile Peer peer;
    
    public Peer getPeer() {
        return peer;
    }
    
    public void run()
    {
        try {
            // Displaying the thread that is running
            System.out.println(
                "Thread " + Thread.currentThread().getId()
                + " is running");
                peer = new Peer((int)Thread.currentThread().getId(), "localhost", 8000);
                peer.run();
        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }
}

public class PeerThreads {
    public static void main(String[] args) {
        int n = 8; // Number of threads
        MultiPeer[] threads = new MultiPeer[n];

        //init thread
        for (int i = 0; i < n; i++) {
            threads[i] = new MultiPeer();
            threads[i].start();
        }

            //poll for peer completion

        for (int i = 0; i < n; i++) {
            MultiPeer thread = threads[i];
            while (thread.getPeer() == null) {
                try {
                    // Wait for the peer object to be initialized
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Handle the InterruptedException if it occurs
                    e.printStackTrace();
                }
            }
            Peer peer = thread.getPeer();
            System.out.println("Peer " + peer.get_id() + " is running");
        }

    }        
}
