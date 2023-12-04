import peer_class_files.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import cnt.*;

public class peerProcess {
    public static void main(String args[]) throws Exception{
        
        PeerProcess peer = new PeerProcess(Integer.parseInt(args[0]));

        
        peer.buildPeerProcess();
        peer.initializePeerProcess();
       
        
        // peerProcess.startServer();

        
        // TODO: separate listener and sender into different methods
        // TODO: handshake
        // TODO: bitfield after that i guess lol    
        
        /*
         * start up listener server
         */
        

         /*
         * read peerblock info and find peers started before this one
         * the list of information about those peers will be stored in peerIdsToConnectTo
         */
        ArrayList<PeerInfoBlock> peerIdsToConnectTo = new ArrayList<>();
        List<PeerInfoBlock> allPeers = peer.getPeerInfoBlocks();
        for(PeerInfoBlock b: allPeers){
            if(b.getPeerId() != peer.getPeerId()){
                peerIdsToConnectTo.add(b);
            }

            else{
                break;
            }
        }
        // make this wait for a connection from a peer
        // peerProcess.startClient();
        peer.newPeer(peerIdsToConnectTo);
         /*

         * connect to each previous peer
         * 
         *          send handshake
         *          receive handshake
         *          validate handshake
         * 
         *          if has file, send bitfield message
         */

         /*
         * randmoly choose k neighbors
         * 
         * spin up thread to wait for m seconds and reselect the new neighbor
         */

         /*
         * begin timing loop
         */

            /*
             * pass messages to neighbors
             */

        /*
         * Time expires
         * 
         */

            /*
            * Calculate message speeds for all neighbors, choose new neighbors
            */
    }
}

/*

for timing, the loop which accepts connections from other peers must keep track of how much time has passed
for optimistic unchoking and selecting new neighbors.  Blocking operations must be passed the shorter of the two timeouts

*/

/*

    wait for connection
    accept connection between two peers
    each peer starts a new thread with a new socket which waits for a new incoming connection
    after each connection, check to see if there are any peers missing
        if not, don't start up a new connection
*/