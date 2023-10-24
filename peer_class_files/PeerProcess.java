package peer_class_files;
import java.io.File;
import java.io.FileWriter;
//import java.io.IOError;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.List;

public class PeerProcess {
    
    byte[] handshake = new byte[32];
    boolean[] fileTracker;

    int peerId;
    private String peerLogDirectory;
    private String peerLogFile;
    
    CommonBlock commonBlock;
    //unchokingInterval, optimisticUnchokingInterval, NumberOfPreferredNeighbors, fileName, fileSize, and pieceSize can all be pulled from commonBlock

    //tracker (peerInfo.cfg) passed to each of the peers so they can check if all the others have completed the file
    //additionally used to fill in constructor details
    List<PeerInfoBlock> peerInfo;

    //the following will be pulled from the peer info block
    String hostName;
    int portNum;
    boolean hasFile;

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
        fileTracker = new boolean[commonBlock.fileSize];

        //peer checks info block to see if it has the entire file. If it does, fill its filetracker array
        if(thisPeer.hasFile){
            for(int i=0;i<fileTracker.length;++i){
                fileTracker[i]=true;
            }
        }
    }

    public int getPeerId(){
        return this.peerId;
    }

    public static void checkPeerInfo(PeerProcess p){
        System.out.print("Peer with peer ID "+p.peerId+" has the file: ");
        String yesorno;
        if(p.fileTracker[0]) yesorno = "yes";
        else yesorno="no";
        System.out.println(yesorno);

    }

    private void writeToLog(String logMessage){
        try{
            FileWriter logger = new FileWriter(peerLogDirectory+"/"+peerLogFile);
            logger.write(logMessage);
            logger.close();
        }
        catch (IOException e){
            System.out.println("Error: ");
            e.printStackTrace();
        }
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