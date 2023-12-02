package peer_class_files;

public class CommonBlock {

    int numberOfPreferredNeighbors;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    String fileName;
    int fileSize;
    int pieceSize;

    public CommonBlock(int numberOfPreferredNeighbors, int unchokingInterval, int optimisticUnchokingInterval, String fileName, int fileSize, int pieceSize){
        this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
        this.unchokingInterval=unchokingInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.fileName=fileName;
        this.fileSize=fileSize;
        this.pieceSize=pieceSize;
    }

    public int getNumberOfPreferredNeighbors(){
        return this.numberOfPreferredNeighbors;
    }

    public int getUnchokingInterval(){
        return this.unchokingInterval;
    }

    public int getoptimisticUnchokingInterval(){
        return this.optimisticUnchokingInterval;
    }

    public String getFileName(){
        return this.fileName;
    }

    public int getFileSize(){
        return this.fileSize;
    }

    public int getPieceSize(){
        return this.pieceSize;
    }

}
