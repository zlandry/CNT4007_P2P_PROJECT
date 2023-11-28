package peer_class_files;
import java.io.File;
import java.io.FileWriter;
//import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import MessageTypes.Handshake;


public class PeerProcess {
    
    //byte[] handshake = new byte[32];
    byte[] fileTracker;

    int peerId;
    private String peerLogDirectory;
    private String peerLogFile;
    private Handshake handshake;
    
    CommonBlock commonBlock;
    //unchokingInterval, optimisticUnchokingInterval, NumberOfPreferredNeighbors, fileName, fileSize, and pieceSize can all be pulled from commonBlock

    //tracker (peerInfo.cfg) passed to each of the peers so they can check if all the others have completed the file
    //additionally used to fill in constructor details
    List<PeerInfoBlock> peerInfo;

    //the following will be pulled from the peer info block
    String hostName;
    int portNum;
    boolean hasFile;

    //tracks this peers preferred neighbors
    List<Integer> preferredNeighbors;

    //tracks how many peices of the file the peer has gathered. Updated by downloading a piece from another peer
    int numberOfPieces = 0;

    public PeerProcess(List<PeerInfoBlock> peerInfo, int peerId, CommonBlock commonBlock) throws Exception{
        this.peerInfo=peerInfo;
        this.peerId = peerId;

        peerLogDirectory = new String("peer_"+peerId);
        peerLogFile = new String("log_peer_"+peerId+".log");

        PeerInfoBlock thisPeer = null;
        //find the info block for this process
        for(PeerInfoBlock b: peerInfo){
            if(b.peerId == this.peerId){
                thisPeer = b;
            }
        }

        if (thisPeer == null) throw new Exception("Construction of peer process failed: could not match PeerID to one in the peer info list");

        this.commonBlock = commonBlock;
        fileTracker = new byte[(commonBlock.fileSize/8)+1];

        //peer checks info block to see if it has the entire file. If it does, fill its filetracker array
        if(thisPeer.hasFile){
            for(int i=0;i<fileTracker.length;++i){
                fileTracker[i]=0xF;
            }
        }

        handshake = new Handshake(peerId);

        /*
        //peer constructs it's handshake message in the handshake byte array
        String handshakeStarter = "P2PFILESHARINGPROJ";
        for(int i=0;i<18;++i){
            handshake[i] = (byte)handshakeStarter.charAt(i);
        }
        
        for(int i = 0;i<=3;++i){
            //splits the bytes of the int which represents the peer ID and adds them to the and of the handshake array
            handshake[i+28] = (byte) (peerId >> ( (2*(3-i))*4));
        }
        */
    }

    private boolean checkHasData(int position){
        int sector = position / 8;
        int bitPos = position % 8;
        byte data = fileTracker[sector];
        
        return ((data >> bitPos) & 1) == 1;

    }

    public int getPeerId(){
        return this.peerId;
    }

    public static void checkPeerInfo(PeerProcess p){
        System.out.print("Peer with peer ID "+p.peerId+" has the file: ");
        String yesorno;
        if(p.fileTracker[0]==0xFF) yesorno = "yes";
        else yesorno="no";
        System.out.println(yesorno);

    }

    //write to log needs access to time of some sort for logging

    private void writeToLog(String logMessage){
        try{
            FileWriter logger = new FileWriter(peerLogDirectory+"/"+peerLogFile);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
            Date date = new Date();
            logger.write(formatter.format(date));
            logger.write(": ");
            logger.write(logMessage);
            logger.write('\n');
            logger.close();
        }
        catch (IOException e){
            System.out.println("Error: ");
            e.printStackTrace();
        }
    }

    private void logConnectToPeer(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" makes a connection to Peer ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    private void logConnectedFromPeer(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" is connected from Peer ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    private void logChangedPreferredNeighbors(){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" has the preferred neighbors ");
        boolean first = true;
        for(int i:preferredNeighbors){
            if(first){
                first = false;
            }
            else{
                sb.append(',');
            }
            sb.append(i);
        }
        sb.append('.');

        writeToLog(sb.toString());
    }

    private void logChangeOptimisticallyUnchokedNeighbor(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" has the optimistically unchoked neighbor ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    private void logPeerIsUnchoked(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" is unchoked by ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    private void logPeerIsChoked(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" is choked by ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    private void logRecHaveMessage(int peerIDOther, int pieceIndex){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" received the \'have\' message from ");
        sb.append(peerIDOther);
        sb.append(" for the piece ");
        sb.append(pieceIndex);
        sb.append('.');

        writeToLog(sb.toString());
    }

    private void logRecInterestedMessage(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" received the \'interested\' message from ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

     private void logRecNotInterestedMessage(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" received the \'not interested\' message from ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    private void logRecCompletedDownload(int peerIDOther, int pieceIndex){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" has downloaded the piece ");
        sb.append(pieceIndex);
        sb.append(" from ");
        sb.append(peerIDOther);
        sb.append('.');

        sb.append(" Now the number of pieces it has is ");
        sb.append(this.numberOfPieces);

        writeToLog(sb.toString());
    }

    private void logCompletedDownload(){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" has downloaded the complete file.");

        writeToLog(sb.toString());
    }



    public void initializePeerProcess(){
        try {
            File logFile = new File(peerLogDirectory+"/"+peerLogFile);
            if(logFile.createNewFile()){
                System.out.println("Successfully created log file for peer "+this.peerId);
            }
            else{
                System.out.println("Failed to create file: log file already exists for "+this.peerId);
            }

        }
        catch(IOException e){
            System.out.println("Error in building peer log file: ");
            e.printStackTrace();
        }
        writeToLog("First log message for peer "+this.peerId);
    }

}