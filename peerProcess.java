import peer_class_files.*;
import java.io.*;
import java.util.*;

public class peerProcess {
    public static void main(String args[]) throws Exception{
        
        PeerProcess peerProcess = new PeerProcess(Integer.parseInt(args[0]));

        /*
         * start up listener server
         */

         /*
         * read peerblock info and find peers started before this one
         */
        ArrayList<PeerInfoBlock> peerIds = new ArrayList<>();
        List<PeerInfoBlock> allPeers = peerProcess.getPeerInfoBlocks();
        for(PeerInfoBlock b: allPeers){
            if(b.getPeerId() != peerProcess.getPeerId()){
                peerIds.add(b);
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
