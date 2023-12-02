package peer_class_files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
//import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import MessageTypes.Handshake;


public class PeerProcess {
    
    //byte[] handshake = new byte[32];
    byte[] fileTracker;

    int peerId;
    private String peerLogDirectory;
    private String peerLogFile;
    private Handshake handshake;

    private static boolean VERBOSE = true;
    
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

    public static void checkPeerInfo(PeerProcess p){
        System.out.print("Peer with peer ID "+p.peerId+" has the file: ");
        String yesorno;
        if(p.fileTracker[0]==0xFF) yesorno = "yes";
        else yesorno="no";
        System.out.println(yesorno);

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
        writeToLog("First log message for peer "+this.peerId);
    }

    public boolean validateHandshake(int id, byte[] message){ //compare given int with last four bytres of handshake array
        String hsheader = "P2PFILESHARINGPROJ";

        for(int i = 0; i < hsheader.length(); ++i){
            if(message[i] != hsheader.charAt((i))){
                System.out.println("Header mismatch on position " + i + " which was |" + message[i] + "| and should have been |" + hsheader.charAt(i) + "|");
                return false;
            }
        }

        for(int i = 18; i < 28; ++i){
            if(message[i] != '0'){
                System.out.println("Zero buffer mismatch on position " + i);
                return false;
            }
        }

        byte[] idarr = ByteBuffer.allocate(4).putInt(id).array();

        for(int i = 0; i < 4; ++i)
        {
            //message[i] = (byte)str.charAt(i);
            if(message[i + 28] != idarr[i]){
                System.out.println("ID mismatch on position " + i + ", expected " + id);
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

        if (thisPeer == null) throw new Exception("Construction of peer process failed: could not match PeerID to one in the peer info list");

        //file tracker must be filesize/8 because each bit in the tracker tracks a byte in the file
        fileTracker = new byte[(commonBlock.fileSize/8)+1];

        //peer checks info block to see if it has the entire file. If it does, fill its filetracker array
        if(thisPeer.hasFile){
            for(int i=0;i<fileTracker.length-1;++i){
                fileTracker[i]=(byte)0xff;
            }
            if(fileTracker.length % 8 == 0) fileTracker[fileTracker.length-1] = (byte)0xff;

            //handles the case where the file length is not a multiple of 8: leaves the last bits as 0 in the tracker
            else{
                int leftover = fileTracker.length/8;
                byte lastb = (byte)0x00;
                for(int i=0;i<leftover;++i){
                    lastb = (byte)(lastb | 0x01);
                    lastb = (byte)(lastb << 1);
                }
            }
        }

        preferredNeighbors = new ArrayList<Integer>();

        handshake = new Handshake(peerId);
    
	
    }
/*
    public static void main(String args[]) throws Exception{
        PeerProcess peerProcess = new PeerProcess(Integer.parseInt(args[1]));
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