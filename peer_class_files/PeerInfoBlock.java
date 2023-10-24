package peer_class_files;

public class PeerInfoBlock {
    int peerId;
    String hostName;
    int portNum;
    boolean hasFile;

    public PeerInfoBlock(int peerId, String hostName, int portNum, boolean hasFile){
        this.peerId=peerId;
        this.hostName=hostName;
        this.portNum=portNum;
        this.hasFile=hasFile;
    }

    public int getPeerId(){
        return this.peerId;
    }
    public String getHostName(){
        return this.hostName;
    }
    public int getPortNum(){
        return this.portNum;
    }
    public boolean getHasFile(){
        return this.hasFile;
    }
}
