import peer_class_files.*;
import java.io.*;
import java.util.*;

public class peerProcess {
    public static void main(String args[]) throws Exception{
        //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PeerProcess peerProcess = new PeerProcess(Integer.parseInt(args[0]));

        


        peerProcess.buildPeerProcess();
        peerProcess.initializePeerProcess();

        /*
         * start up listener server
         */

         /*
         * read peerblock info and find peers started before this one
         */

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
