package peer_class_files;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
//import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import MessageTypes.Handshake;
import MessageTypes.Have;
import MessageTypes.Interested;
import MessageTypes.Message;
import MessageTypes.NotInterested;
import MessageTypes.Type;
// import cnt.Client;
// import cnt.Server;
import MessageTypes.Type;
import java.io.FileReader;

class Holder
{
    public byte[] bytes;

};

public class PeerProcess {
    
    byte[] fileTracker;

    //tracks file storage of all other peers
    //upon connection to a peer, needs to add a new byte array to the list
    //upon collection of file parts, needs to update that peers list
    List<byte[]> peerFileTracker;

    int peerId;
    private String peerLogDirectory;
    private String peerLogFile;
    private Handshake handshake;
    int destPort;

    private static boolean VERBOSE = true;

    //list of sockets to between peers
    ArrayList<Socket> communicationSockets = new ArrayList<Socket>();
    
    CommonBlock commonBlock;
    //unchokingInterval, optimisticUnchokingInterval, NumberOfPreferredNeighbors, fileName, fileSize, and pieceSize can all be pulled from commonBlock

    //tracker (peerInfo.cfg) passed to each of the peers so they can check if all the others have completed the file
    //additionally used to fill in constructor details
    List<PeerInfoBlock> peerInfo;

    //the following will be pulled from the peer info block
    String hostName;
    int portNum;
    boolean hasFile;

    int fileSizeInPieces = 0;

    //tracks this peers preferred neighbors
    List<Integer> preferredNeighbors;

    //tracks how many peices of the file the peer has gathered. Updated by downloading a piece from another peer
    int numberOfPieces = 0;

    public PeerProcess(int pid) throws Exception{
        this.peerId = pid;

        peerLogDirectory = new String("peer_"+ this.peerId);
        peerLogFile = new String("log_peer_"+peerId+".log");
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

    public void setCommonBlock(CommonBlock cb){
        this.commonBlock = cb;
    }

    public void setPeerInfoBlock(List<PeerInfoBlock> pib){
        this.peerInfo = pib;
    }

    public List<PeerInfoBlock> getPeerInfoBlocks(){
        return this.peerInfo;
    }

    public void clearPreferredNeighbors(){
        preferredNeighbors.clear();
    }

    public void setPreferredNeighbors(int newNeighberPeerId){
        preferredNeighbors.add(newNeighberPeerId);
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

    public int getPortNum(){
        return this.portNum;
    }

    public static void checkPeerInfo(PeerProcess p){
        System.out.print("Peer with peer ID "+p.peerId+" has the file: ");
        String yesorno;
        if(p.fileTracker[0]==0xFF) yesorno = "yes";
        else yesorno="no";
        System.out.println(yesorno);

    }

    public boolean checkFileValidity(ArrayList<String> messages, String filename) {
        // read filename into an arraylist of strings
        // compare the two arraylists
        // if they are the same, return true
        
        // read the file into an arraylist of strings
        ArrayList<String> file = new ArrayList<String>();
        try {
            File f = new File(filename);
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                file.add(sc.nextLine());
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // compare the two arraylists line by line
        for (int i = 0; i < file.size(); i++) {
            if (!file.get(i).equals(messages.get(i))) {
                return false;
            }
        }

        return true;
    }

    //write to log which is used by all logging functions
    //all log messages start with a timestamp, the rest of the string is left up to the functions

    private void writeToLog(String logMessage){
        try{
            FileWriter logger = new FileWriter(peerLogDirectory+"/"+peerLogFile, true);
            BufferedWriter bw = new BufferedWriter(logger);
            PrintWriter out = new PrintWriter(bw);
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date date = new Date();
            out.print(formatter.format(date));
            out.print(": ");
            out.print(logMessage);
            out.print('\n');
            out.close();
        }
        catch (IOException e){
            System.out.println("Error: ");
            e.printStackTrace();
        }
    }

    private void writeToFile(byte[] filePiece){
        try{
            FileWriter Filelogger = new FileWriter(peerLogDirectory+"/"+commonBlock.getFileName(), true);
            BufferedWriter bw = new BufferedWriter(Filelogger);
            PrintWriter out = new PrintWriter(bw);

            //System.out.println("got a file piece of size "+filePiece.length +", first hundred chars to follow...");
           // for(int i = 0; i<100;++i){
           //                 System.out.print((char)filePiece[i]);
          //              }
            out.print(filePiece);
            out.print('\n');
            out.close();
        }
        catch (IOException e){
            System.out.println("Error: ");
            e.printStackTrace();
        }
    }

    public void logConnectToPeer(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" makes a connection to Peer ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    public void logConnectedFromPeer(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" is connected from Peer ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    public void logChangedPreferredNeighbors(){
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

    public void logChangeOptimisticallyUnchokedNeighbor(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" has the optimistically unchoked neighbor ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    public void logPeerIsUnchoked(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" is unchoked by ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    public void logPeerIsChoked(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" is choked by ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    public void logRecHaveMessage(int peerIDOther, int pieceIndex){
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

    public void logRecInterestedMessage(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" received the \'interested\' message from ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

     public void logRecNotInterestedMessage(int peerIDOther){
        StringBuilder sb = new StringBuilder();
        sb.append("Peer ");
        sb.append(this.peerId);
        sb.append(" received the \'not interested\' message from ");
        sb.append(peerIDOther);
        sb.append('.');

        writeToLog(sb.toString());
    }

    public void logRecCompletedDownload(int peerIDOther, int pieceIndex){
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

    public void logCompletedDownload(){
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
        try {
            File logFile = new File(peerLogDirectory+"/"+commonBlock.getFileName());
            if(logFile.createNewFile()){
                System.out.println("Successfully created empty file for peer "+this.peerId);
            }
            else{
                System.out.println("Failed to create file: empty file already exists for "+this.peerId);
            }

        }
        catch(IOException e){
            System.out.println("Error in building peer log file: ");
            e.printStackTrace();
        }
        writeToLog("First log message for peer "+this.peerId);
    }

    public boolean validateHandshake(int id, String message){ //compare given int with last four bytres of handshake array
        String hsheader = "P2PFILESHARINGPROJ";

        for(int i = 0; i < hsheader.length(); ++i){
            if(message.charAt(i) != hsheader.charAt((i))){
                System.out.println("Header mismatch on position " + i + " which was |" + message.charAt(i) + "| and should have been |" + hsheader.charAt(i) + "|");
                return false;
            }
        }

        for(int i = 18; i < 28; ++i){
            if((byte)message.charAt(i) != 0x00){
                System.out.println("Zero buffer mismatch on position " + i + " (character: " + message.charAt(i) + ")");
                return false;
            }
        }

        byte[] idarr = ByteBuffer.allocate(4).putInt(id).array();
        String str = new String(idarr);
        for(int i = 0; i < 4; ++i)
        {
            //message[i] = (byte)str.charAt(i);
            if((byte)message.charAt(31-i) != (byte)str.charAt(i)){
                System.out.println("ID mismatch on position " + (i + 28) + ", expected " + str.charAt(i) + " but recieved " + message.charAt(i + 28));
                return false;
            }
        }

        return true;
    } 

    public boolean validateHandshakeArray(int id, char[] message){ //compare given int with last four bytres of handshake array
        String hsheader = "P2PFILESHARINGPROJ";

        for(int i = 0; i < hsheader.length(); ++i){
            if(message[i] != (byte)hsheader.charAt((i))){
                System.out.println("Header mismatch on position " + i + " which was |" + message[i] + "| and should have been |" + hsheader.charAt(i) + "|");
                return false;
            }
        }

        for(int i = 18; i < 28; ++i){
            if(message[i] != 0x00){
                System.out.println("Zero buffer mismatch on position " + i + " (character: " + message[i] + ")");
                return false;
            }
        }

        byte[] idarr = ByteBuffer.allocate(4).putInt(id).array();
        String str = new String(idarr);
        for(int i = 0; i < 4; ++i)
        {
            //message[i] = (byte)str.charAt(i);
            if((byte)message[31 - i] != (byte)str.charAt(i)){
                System.out.println("ID mismatch on position " + (i + 28) + ", expected " + str.charAt(i) + " but recieved " + message[i + 28]);
                return false;
            }
        }

        return true;
    } 

    public void buildPeerProcess() throws Exception{
        int prefNeighborCount = 0;
        int unchokeIntv = 0;
        int optmUnchokeIntv = 0;
        int fileSize = 0;
        int pieceSize = 0;
        String fileName = "";

        writeToLog("Initializing peer "+this.peerId);

        File common = new File("Common.cfg");
		if(common.exists())
		{
			if(VERBOSE)
			{
				System.out.println("Found Common.cfg. Reading...");
			}

			Scanner scanner = new Scanner(common);
			boolean correct = true;
			
			while(correct && scanner.hasNextLine())
			{
				
				String line = scanner.nextLine();

				int spacePos = line.indexOf(' ');
				String substr = line.substring(0, spacePos);
				String remainder = line.substring(spacePos, line.length());

				remainder = remainder.trim();
				
				if(VERBOSE)
				{
					System.out.println("Reading in " + substr + " with value \'" + remainder + "\'");
				}
				try
				{
					switch(substr)
					{
						case "NumberOfPreferredNeighbors":
							prefNeighborCount = Integer.parseInt(remainder);
							break;
					
						case "UnchokingInterval":
							unchokeIntv = Integer.parseInt(remainder);
							break;

						case "OptimisticUnchokingInterval":
							optmUnchokeIntv = Integer.parseInt(remainder);
							break;

						case "FileName":
							fileName = remainder;
							break;
						
						case "FileSize":
							fileSize = Integer.parseInt(remainder);
							break;
					
						case "PieceSize":
							pieceSize = Integer.parseInt(remainder);
							break;

						default:
							System.out.println("Common.cfg is malformed - the line \'" + line + "\' is not formatted correctly.");

							scanner.close();
					}
				}
				catch(Exception e)
				{
					if(VERBOSE)
					{
						System.out.println("Catching exception, closing scanner...");
					}
					scanner.close();
					System.out.println("Common.cfg is malformed - the line \'" + line + "\' is not formatted correctly.");

				}
				finally
				{
					
				}
			}

			if(correct)
			{
				scanner.close();
			}
			
			if(VERBOSE)
			{
				System.out.println("Finished reading peers from Common.cfg.\n");
			}

		}

		this.setCommonBlock(new CommonBlock(prefNeighborCount,unchokeIntv,optmUnchokeIntv,fileName,fileSize,pieceSize));

        StringBuilder commonBlockString = new StringBuilder();
        commonBlockString.append("peer ");
        commonBlockString.append(this.getPeerId());
        commonBlockString.append(" read commonblock file with: \n");
        commonBlockString.append("perferred neighbor count: ");
        commonBlockString.append(prefNeighborCount);
        commonBlockString.append('\n');

        commonBlockString.append("unchoking interval: ");
        commonBlockString.append(unchokeIntv);
        commonBlockString.append('\n');

        commonBlockString.append("optimistic unchoking interval: ");
        commonBlockString.append(optmUnchokeIntv);
        commonBlockString.append('\n');

        commonBlockString.append("filename: ");
        commonBlockString.append(fileName);
        commonBlockString.append('\n');

        commonBlockString.append("fileszie: ");
        commonBlockString.append(fileSize);
        commonBlockString.append('\n');

        commonBlockString.append("piece size: ");
        commonBlockString.append(pieceSize);

        writeToLog(commonBlockString.toString());

		File peerinfo = new File("PeerInfo.cfg");
		this.setPeerInfoBlock(new ArrayList<PeerInfoBlock>());

		if(peerinfo.exists())
		{
			if(VERBOSE)
			{
				System.out.println("Found PeerInfo.cfg. Reading...");
			}

			Scanner scanner = new Scanner(peerinfo);
			boolean correct = true;

			while(correct && scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				String[] parts = line.split(" ", 4);

				try
				{
					int pid = Integer.parseInt(parts[0]);
					String name = parts[1];
					int port = Integer.parseInt(parts[2]);
					int has = Integer.parseInt(parts[3]);

					this.peerInfo.add(new PeerInfoBlock(pid, name, port, (has==1) ));
					if(VERBOSE)
					{
						System.out.println("Added peer - " + pid + " from peer info file" );
						//temp.all_out();
					}
				}
				catch(Exception e)
				{
					if(VERBOSE)
					{
						System.out.println("Catching exception, closing scanner...");
					}
					scanner.close();
					System.out.println("PeerInfo.cfg is malformed - the line \'" + line + "\' is not formatted correctly.");

					correct = false;

				}
				finally
				{
					
				}
			}

			if(correct)
			{
				scanner.close();

				if(VERBOSE)
				{
					System.out.println("Finished reading peers from PeerInfo. Found " + this.peerInfo.size() + " entries.\n");
				}

				//initializePeerProcesses();
			}
		}
		else
		{
			System.out.println("PeerInfo.cfg does not exist\n");
		}

        PeerInfoBlock thisPeer = null;
        //find the info block for this process
        for(PeerInfoBlock b: peerInfo){
            if(b.peerId == this.peerId){
                thisPeer = b;
            }
        }

        StringBuilder peerInfoString = new StringBuilder();
        peerInfoString.append("peer ");
        peerInfoString.append(this.peerId);
        peerInfoString.append("has peer info: \n");

        peerInfoString.append("hostname: ");
        peerInfoString.append(thisPeer.getHostName());
        peerInfoString.append('\n');

         peerInfoString.append("port number: ");
        peerInfoString.append(thisPeer.getPortNum());
        peerInfoString.append('\n');

         peerInfoString.append("has file: ");
         if(thisPeer.getHasFile()) peerInfoString.append("yes");
         else peerInfoString.append("no");


        writeToLog(peerInfoString.toString());

        if (thisPeer == null) throw new Exception("Construction of peer process failed: could not match PeerID to one in the peer info list");
        else{
            this.hostName = thisPeer.getHostName();
            this.portNum = thisPeer.getPortNum();
            this.hasFile = thisPeer.getHasFile();
        }

        this.fileSizeInPieces = this.commonBlock.getFileSize()/this.commonBlock.getPieceSize();
        int fileDifference = this.commonBlock.getFileSize() % this.commonBlock.getPieceSize();

        if(fileDifference != 0){
             this.fileSizeInPieces += 1;
        }
        //file tracker 
        fileTracker = new byte[fileSizeInPieces];

        //peer checks info block to see if it has the entire file. If it does, fill its filetracker array
        if(thisPeer.hasFile){
            for(int i=0;i<fileTracker.length;++i){
                fileTracker[i]=(byte)0xff;
            }
            
        }

        this.preferredNeighbors = new ArrayList<Integer>();

        //initialize the peer file piece tracker, adding a byte array in each slot of the array list
        //which corresponds to each other peer. 
        this.peerFileTracker = new ArrayList<byte[]>();
        for(int i = 0;i<this.peerInfo.size();++i){
            peerFileTracker.add(new byte[fileSizeInPieces]);
        }

        this.handshake = new Handshake(peerId);
        this.handshake.setupPayload();
    
	
    }

    public boolean sendInterested(BufferedWriter din) throws Exception {
                            
        Message m = new Interested();
        m.setLength(5);

        char[] message = new char[]{1, 0, 0, 1, 2};
        din.write(message);
        return true;
    }

    public boolean sendNotInterested(BufferedWriter din) throws Exception {
                            
        Message m = new NotInterested();
        m.setLength(5);
        char[] message = new char[]{1, 0, 0, 1, 3};
        din.write(message);
        return true;
    }
    
  //  public static void main(String args[]) throws Exception{
  //      PeerProcess peerProcess = new PeerProcess(Integer.parseInt(args[1]));
 //       peerProcess.buildPeerProcess();
  //      peerProcess.initializePeerProcess();

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
    // }
        
    class Sender extends Thread {
        private Socket s;
        private BufferedReader in;
        private BufferedWriter dout;
    
        boolean hasSentBitfield = false;
    
        Sender(Socket socket) {
            this.s = socket;
            this.in = new BufferedReader(new InputStreamReader(System.in));
        }
    
        public void run() {
            try {
                dout = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                
                String str = new String(handshake.getPayload());
                
                //dout.write(str + "\n"); // this can write a byte array
                //dout.flush();

                //char[] charstr = str.toCharArray();

                //dout.write(charstr);
               // dout.flush();

                byte[] buf = new byte[1024];
               s.getOutputStream().write(handshake.getPayload());
               s.getInputStream().read(buf,0,1);
                // create a file reader to read alice.txt
                FileReader fr = new FileReader("2mb.txt");
                BufferedReader br = new BufferedReader(fr);
                if(!hasSentBitfield){
                    
                }
                // create HAVE message to send
                // Message m = new Have();
                // m.setLength(5);
                // m.setType(Type.HAVE);
                // m.setPayload(new byte[]{0,0,0,0,0});
                // char[] message = m.getChar();
                // dout.write(message);
                // dout.flush();
                // read alice.txt line by line add it to one string
                
                
                ArrayList<byte[]> field = new ArrayList<byte[]>(); 

                FileInputStream fis = new FileInputStream("cod.jpg");
                int next = 0;
                int i = 0;
                byte[] file = new byte[commonBlock.getFileSize()];
                fis.read(file);
                int beginning = 0;
                int end = 0;
                int startBuff=0;
                while((i+1)*commonBlock.getPieceSize()<commonBlock.getFileSize()){
                    startBuff = 0;
                    beginning = i*commonBlock.getPieceSize();
                    end = (i+1)*commonBlock.getPieceSize();
                    byte[] temp = new byte[commonBlock.getPieceSize()];
                        for(;beginning<end;beginning++){
                            temp[startBuff] = file[beginning];
                            ++startBuff;
                        }
                    
                    System.out.println("read " + i++ + " times");
                    if(next == -1){
                        break;
                    }
                    field.add(temp);
                }
                String message = "";
                for(int j = 0; j < field.size(); ++j){
                    //System.out.println("byte array "+j+" has byte "+String.format("%02x", field.get(j)[0]) + " at location 0");
                    s.getOutputStream().write(field.get(j));
                    
                    //message = new String(field.get(0));
                    //dout.write(message);
                }
                fis.close();

                int fieldsize = (int)Math.ceil(field.size() / 8);
                int rem = field.size() % 8;
                
                // char[] message = new char[5 + fieldsize];
            

                // message[4] = '5';

                // for(int j = 6; j < 5 + fieldsize - 1; ++j){
                //     message[j] = 0xFF;
                // }
                // switch(rem){
                //     case 1:
                //         message[5 + fieldsize - 1] = 0x01;
                //         break;
                //     case 2:
                //         message[5 + fieldsize - 1] = 0x03;
                //         break;
                //     case 3:
                //         message[5 + fieldsize - 1] = 0x07;
                //         break;
                //     case 4:
                //         message[5 + fieldsize - 1] = 0x0F;
                //         break;
                //     case 5:
                //         message[5 + fieldsize - 1] = 0x1F;
                //         break;
                //     case 6:
                //         message[5 + fieldsize - 1] = 0x3F;
                //         break;
                //     case 7:
                //         message[5 + fieldsize - 1] = 0x7F;
                //         break;
                // }

                
                dout.flush();
                //read in 16384 bytes of a file 

                //FileInputStream using read() 
                //0000 0000 
                //128 64 32 16 8421
                /*
                while (true) {
                    System.out.println("Enter message for another peer:");
                    str = in.readLine();
                    
                    if (str == null) break;
                    
                    dout.write(str + "\n");
                    dout.flush();


    
                    if (str.equalsIgnoreCase("bye")) {
                        break;
                    }
                }
                */
                
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    if (s != null) s.close();
                    if (dout != null) dout.close();
                    if (in != null) in.close();
                } catch (IOException e) {
                    System.out.println("Error closing resources: " + e.getMessage());
                }
            }
        }
    }
    class Receiver extends Thread {
        public ArrayList<String> messages = new ArrayList<String>();
        private int port;
        private ServerSocket ss;
        private Socket s;
        // private ByteArrayInputStream din;
        private BufferedReader din;
    
        Receiver(int port) {
            this.port = port;
        }
        Receiver(Socket s) {
            this.s = s;
        }
    
        public void run() {
            try {
                // if ss is null, then new ServerSocket at port
                if (ss == null) ss = new ServerSocket(port);
                System.out.println("Receiver created!!!");
    
                while (true) {
                    //connect to others existing
                    //for each existing, send a connection request with a new client socket
                    //start up a server socket to accept new connections

                    int connectingPeerNum;
                    if (peerId == 1001) connectingPeerNum = 1002;
                    else connectingPeerNum = 1001;

                    s = ss.accept();
                    logConnectToPeer(connectingPeerNum);
                    System.out.println("Client connected");

                    communicationSockets.add(s);

                    din = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    s.getOutputStream().write(' ');
                    // din = new ByteArrayInputStream(new InputStreamReader(s.getInputStream()).toString().getBytes());
                    // TODO: get the id from the file
                    

                    byte[] buf = new byte[1024];
                    s.getInputStream().read(buf,0,buf.length);
/*
                    int connectedPeer = 0;
                    for(int i = 28;i<32;++i){
                        connectedPeer = (connectedPeer << 8) + (buf[i] & 0xFF);
                    }
                    */

                    String hand = new String(buf);

                    System.out.println("Attempting to receive connection for peer: "+connectingPeerNum);
                    System.out.println("Handshake is valid: " + validateHandshake(connectingPeerNum,hand));

                    logConnectedFromPeer(connectingPeerNum);
                    

                    // din.read(message);
                    // if (message[4] == 0x04) {
                    //     System.out.println("Received a HAVE message");
                    // }
                    // else {
                    //     System.out.println("Received a message of type " + message[4]);
                    // }

                    String str;
                    byte[] largeBuf = new byte[commonBlock.getPieceSize()];
                    int read;
                    while (true) {
                        read = 0;
                       // System.out.println("Received: " + str);
                       s.getInputStream().read(largeBuf);
                       System.out.println("recieve piece "+numberOfPieces);
                       System.out.println("recieved byte array "+numberOfPieces+" has byte "+String.format("%02x", largeBuf[0]) );
                        writeToFile(largeBuf);
                        logRecCompletedDownload(connectingPeerNum,numberOfPieces++);
                       // messages.add(str);
                        // System.out.println("Is this what I expected? " + validateHandshake(1001,str));
    
                        if (numberOfPieces == commonBlock.getFileSize()/commonBlock.getPieceSize()-1) {
                            logCompletedDownload();
                            
                            System.out.println("Client left");
                            break;
                        }
                    }
                    /*

                    BufferedWriter dout = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                    while ((str = din.readLine()) != null) {
                    // System.out.println("Enter message for another peer:");
                    
                    System.out.println("Received: " + str);
    
                        if (str.equalsIgnoreCase("bye")) {
                            System.out.println("Client left");
                            break;
                        }
                    
                }
                */
                    
    
                    s.close();

                    if (numberOfPieces == commonBlock.getFileSize()/commonBlock.getPieceSize()-1) break;
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    if (ss != null) ss.close();
                    if (din != null) din.close();
                } catch (IOException e) {
                    System.out.println("Error closing server resources: " + e.getMessage());
                }
            }
        }
    }
    
   
    public void newPeer(ArrayList<PeerInfoBlock> peerInfoBlocks) throws Exception {
        // System.out.print("Enter port for this Peer: ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        int port = this.portNum;
        Receiver r = new Receiver(port);

        
    
        String str = new String(handshake.getPayload());

        //attempt to connect to all peers already active
        // for(PeerInfoBlock peerInfo: peerInfoBlocks){
        //     try{
        //         System.out.println("Connecting to port " + peerInfo.getPortNum());
        //         destPort = peerInfo.getPortNum();
        //         Socket socket = new Socket("localhost", peerInfo.getPortNum());
        //         communicationSockets.add(socket);
        //         BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        //         out.write(this.portNum + '\n');
        //         out.write(str + '\n');
        //         out.flush();

        //         BufferedReader handshakeIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //         String recval = ""; 
        //         recval = handshakeIn.readLine();
                
        //         validateHandshake(peerInfo.getPeerId(),recval);

        //     }
        //     catch(IOException e){
        //         e.printStackTrace();
        //     }
        // }

        // for(Socket s:communicationSockets){
        //         System.out.println("Spinning up new thread to handle communication connection to new peer...");

        //         Thread senderThread = new Thread(() -> {
        //             try {
        //                 Sender sender = new Sender(s);
        //                 sender.start();
        //             } catch (Exception e) {
        //                 e.printStackTrace();
        //             }
        //         });

        //         senderThread.start();
        // }


        // Start the server thread but don't accept connections until signaled
        r.start();
            // JEREMY: the sockets may not start correctly because .start is a thread thing i think
        System.out.println("Press Enter to start accepting connections");
        in.readLine();
        // grab the port from all other peer info blocks
        // int count = 0;
        // for(PeerInfoBlock peerInfo: peerInfoBlocks){
        //     // TODO: this for loop should connect to all other peers, not just the one with the lowest id
        //     // TODO: create a new thread for each connection
        //     if (count == 0) break;
        //     if (peerInfo.getPeerId() != this.peerId) {
                
        //         System.out.println("Connecting to port " + peerInfo.getPortNum());
        //         // if there is a socket using this port, break
        //         destPort = peerInfo.getPortNum();
        //         Thread senderThread = new Thread(() -> {
        //             try {
        //                 Socket socket = new Socket("localhost", destPort);
        //                 Sender sender = new Sender(socket);
        //                 sender.start();
        //             } catch (IOException e) {
        //                 e.printStackTrace();
        //             }
        //         });
    
        //         Thread receiverThread = new Thread(() -> {
        //             try {
        //                 ServerSocket serverSocket = new ServerSocket(peerInfo.getPortNum());
        //                 Socket clientSocket = serverSocket.accept();
        //                 Receiver receiver = new Receiver(clientSocket);
        //                 receiver.start();
        //             } catch (IOException e) {
        //                 e.printStackTrace();
        //             }
        //         });
    
        //         senderThread.start();
        //         receiverThread.start();
        //         count ++;
        //     }
            
            
        // }
        String other = "";
        if (this.peerId == 1001){
            destPort = 6009;
            other = "lin114-01.cise.ufl.edu";
        } 
        if (this.peerId == 1002){
             destPort = 6008;
             other = "lin114-00.cise.ufl.edu";
        }
    
        // Now that the user has signaled to start accepting connections, create the client
        // System.out.println("Enter destination port for client:");
        // int destPort = Integer.parseInt(in.readLine());
        
        System.out.println("Connecting to port " + destPort);
        Socket socket = new Socket("localhost", destPort);
        Sender s = new Sender(socket);
        s.start(); // Start the client thread
        
    }

}