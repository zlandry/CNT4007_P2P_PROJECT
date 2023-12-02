import peer_class_files.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import cnt.*;

public class peerProcess {
    public static void main(String args[]) throws Exception{
        
        PeerProcess peerProcess = new PeerProcess(Integer.parseInt(args[0]));

        


        peerProcess.buildPeerProcess();
        peerProcess.initializePeerProcess();
        peerProcess.newPeer();
        


        /*
         * start up listener server
         */
        

         /*
         * read peerblock info and find peers started before this one
         * the list of information about those peers will be stored in peerIdsToConnectTo
         */
        ArrayList<PeerInfoBlock> peerIdsToConnectTo = new ArrayList<>();
        List<PeerInfoBlock> allPeers = peerProcess.getPeerInfoBlocks();
        for(PeerInfoBlock b: allPeers){
            if(b.getPeerId() != peerProcess.getPeerId()){
                peerIdsToConnectTo.add(b);
            }

            else{
                break;
            }
        }

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
