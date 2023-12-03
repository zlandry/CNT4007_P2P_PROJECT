//import java.net.*;
import java.io.*;
//import java.nio.*;
//import java.nio.channels.*;
import java.util.*;
//import java.lang.*;
import peer_class_files.*;
/* 
class DummyPeer {
    int id;
    String hostname;
    int port;
    int hasFile;

    public DummyPeer() {
        id = 0000;
        hostname = "";
        port = 2020;
        hasFile = 0;
    }

	public DummyPeer(int pid, String name, int portnum, int has){
		id = pid;
		hostname = name;
		port = portnum;
		hasFile = has;
	}

    void all_out() {
        System.out.println(id + " " + hostname + " " + port + " " + hasFile);
    }

    void set_id(int id) {
        this.id = id;
    }

    void get_id() {
        System.out.println("Peer id: " + this.id);
    }
}

*/

public class Config {

	static boolean VERBOSE = true;

	 
	static int prefNeighborCount;
	static int unchokeIntv;
	static int optmUnchokeIntv;

	static String fileName;
	static int fileSize;
	static int pieceSize;
 
	static CommonBlock commonBlock;
	static List<PeerInfoBlock> peerInfoBlock;
	static List<PeerProcess> peers;

	public static void initalizeCommon() throws Exception{
		FileWriter writer = new FileWriter("Common.cfg");
		writer.write("NumberOfPreferredNeighbors 2\n");
		writer.write("UnchokingInterval 5\n");
		writer.write("OptimisticUnchokingInterval 15\n");
		writer.write("FileName TheFile.dat\n");
		writer.write("FileSize 10000232\n");
		writer.write("PieceSize 32768\n");
		writer.close();
		
		if(VERBOSE)
		{
			System.out.println("Default Common.cfg file created.");
			System.out.println("You may want to update the settings to better match what you maybe looking for.\n");
		}

	}

	public static void initalizePeerInfo() throws Exception{
		FileWriter writer = new FileWriter("PeerInfo.cfg");
		writer.write("1001 lin114-00.cise.ufl.edu 6008 1\n");
		writer.write("1002 lin114-00.cise.ufl.edu 6008 0\n");
		writer.write("1003 lin114-00.cise.ufl.edu 6008 0\n");
		writer.close();

		if(VERBOSE)
		{
			System.out.println("Default PeerInfo.cfg file created.");
			System.out.println("You may want to update the settings to better match the peers you plan on engaging with.\n");
		}
	}

	private static void buildPeerDirectory(PeerProcess pp) throws Exception{
		if(VERBOSE){
			System.out.println("Building directory for peer "+pp.getPeerId()+"...");
		}
		File thisDirectory = new File("peer_"+pp.getPeerId());
		if(!thisDirectory.exists()){
			if(!thisDirectory.mkdir()) throw new Exception("Failed to build directory for process "+pp.getPeerId());
		}
		if(VERBOSE){
			System.out.println("Success");
		}
	}

	//creates the list of peerProcesses and builds a directory for each of them to write their log files to
	private static void buildPeerArray() throws Exception{
		if(VERBOSE){
			System.out.println("Building Peer Processes...");
		}
		peers = new ArrayList<PeerProcess>(); 
		for(PeerInfoBlock p:peerInfoBlock){
			peers.add(new PeerProcess(p.getPeerId()));
			buildPeerDirectory(peers.get(peers.size()-1));
		}

	}

	private static void initializePeerProcesses() throws Exception{
		if(VERBOSE){
			System.out.println("Initializing Peer Processes...");
		}; 
		for(PeerProcess p:peers){
			p.initializePeerProcess();
		}

	}



	public static void main(String[] args) throws Exception
	{
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

							File oldfile = new File("Common.cfg.old");
							if(oldfile.exists())
							{
								oldfile.delete();
								if(VERBOSE)
								{
									System.out.println("Deleting old Common.cfg.old.");
								}
							}

							boolean success = common.renameTo(oldfile);

							if(success){
								System.out.println("Successfully renamed invalid Common.cfg to Common.cfg.old. Generating default Common.cfg...");
								initalizeCommon();
							}
							else
							{
								System.out.println("Failed to rename invalid Common.cfg. You may need to delete it yourself.");
							}

							correct = false;
							break;
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

					File oldfile = new File("Common.cfg.old");
					boolean success = common.renameTo(oldfile);

					if(success){
						System.out.println("Successfully renamed invalid Common.cfg to Common.cfg.old. Generating default Common.cfg...");
						initalizeCommon();
					}
					else
					{
						System.out.println("Failed to rename invalid Common.cfg. You may need to delete it yourself.");
					}

					correct = false;
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
		else
		{
			System.out.println("PeerInfo.cfg does not exist - Generating default Common.cfg...");
			initalizeCommon();
		}

		commonBlock = new CommonBlock(prefNeighborCount,unchokeIntv,optmUnchokeIntv,fileName,fileSize,pieceSize);

		File peerinfo = new File("PeerInfo.cfg");
		peerInfoBlock = new ArrayList<PeerInfoBlock>();

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

					//DummyPeer temp = new DummyPeer(pid, name, port, has);
					peerInfoBlock.add(new PeerInfoBlock(pid, name, port, (has==1) ));
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

					File oldfile = new File("PeerInfo.cfg.old");
					if(oldfile.exists())
					{
						oldfile.delete();
						if(VERBOSE)
						{
							System.out.println("Deleting old PeerInfo.cfg.old.");
						}
					}
					
					boolean success = peerinfo.renameTo(oldfile);

					if(success){
						System.out.println("Successfully renamed invalid PeerInfo.cfg to PeerInfo.cfg.old. Generating default PeerInfo.cfg...\n");
						initalizePeerInfo();
						System.out.println("Did not populate peer list with peers from PeerInfo.cfg. Run this program again to do so.");
					}
					else
					{
						System.out.println("Failed to rename invalid PeerInfo.cfg. You may need to delete it yourself.");
					}

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
					System.out.println("Finished reading peers from PeerInfo. Found " + peerInfoBlock.size() + " entries.\n");
				}

				buildPeerArray();
				initializePeerProcesses();
			}
		}
		else
		{
			System.out.println("PeerInfo.cfg does not exist - Generating default PeerInfo.cfg...\n");
			initalizePeerInfo();
			System.out.println("Did not populate peer list with peers from PeerInfo.cfg. Run this program again to do so.");
		}
	}
}