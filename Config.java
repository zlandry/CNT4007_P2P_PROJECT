//import java.net.*;
import java.io.*;
//import java.nio.*;
//import java.nio.channels.*;
import java.util.*;
//import java.lang.*;

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

public class Config {

	static int prefNeighborCount;
	static int unchokeIntv;
	static int optmUnchokeIntv;

	static String fileName;
	static int fileSize;
	static int pieceSize;

	static DummyPeer[] peers;

	public static void initalizeCommon() throws Exception{
		FileWriter writer = new FileWriter("Common.cfg");
		writer.write("NumberOfPreferredNeighbors 2\n");
		writer.write("UnchokingInterval 5\n");
		writer.write("OptimisticUnchokingInterval 15\n");
		writer.write("FileName TheFile.dat\n");
		writer.write("FileSize 10000232\n");
		writer.write("PieceSize 32768\n");
		writer.close();
	
		System.out.println("Default Common.cfg file created.\n");
		System.out.println("You may want to update the settings to better match what you maybe looking for.");
	}

	public static void initalizePeerInfo() throws Exception{
		FileWriter writer = new FileWriter("PeerInfo.cfg");
		writer.write("1001 lin114-00.cise.ufl.edu 6008 1\n");
		writer.write("1002 lin114-00.cise.ufl.edu 6008 0\n");
		writer.write("1003 lin114-00.cise.ufl.edu 6008 0\n");
		writer.close();

		System.out.println("Default PeerInfo.cfg file created.\n");
		System.out.println("You may want to update the settings to better match the peers you plan on engaging with.");
	}

	public static void main(String[] args) throws Exception
	{
		File common = new File("Common.cfg");
		if(common.exists())
		{
			Scanner scanner = new Scanner(common);
			boolean correct = true;
			while(scanner.hasNextLine() && correct)
			{
				String line = scanner.nextLine();

				int spacePos = line.indexOf(' ');
				String substr = line.substring(0, spacePos);
				String remainder = line.substring(spacePos, line.length());
				remainder = remainder.trim();

				//System.out.println("|" + remainder + "|");
				switch(substr)
				{
					case "NumberOfPreferredNeighbors":
						prefNeighborCount = Integer.parseInt(remainder);
						break;
					
					case "UnchokingInterval" :
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
						System.out.println("Common.cfg is malformed - " + line + " is not formatted correctly.");
						File oldfile = new File("Common.cfg.old");
						correct = false;
						break;
				}

			}
			if(!correct){
				initalizeCommon();
			}
			scanner.close();
		}
		else
		{
			initalizeCommon();
		}

		File peerinfo = new File("PeerInfo.cfg");
		DummyPeer[] peertemp = new DummyPeer[1];
		if(peerinfo.exists())
		{
			Scanner scanner = new Scanner(peerinfo);
			while(scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				String[] parts = line.split(" ", 4);
				//System.out.println("parts: " + parts);
				//foreach()
				System.out.println("|0|" + parts[0]);
				System.out.println("|1|" + parts[1]);
				System.out.println("|2|" + parts[2]);
				System.out.println("|3|" + parts[3]);
				try{

				}
			}
			scanner.close();
		}
		else
		{
			FileWriter writer = new FileWriter("PeerInfo.cfg");
			writer.write("1001 lin114-00.cise.ufl.edu 6008 1\n");
			writer.write("1002 lin114-00.cise.ufl.edu 6008 0\n");
			writer.write("1003 lin114-00.cise.ufl.edu 6008 0\n");
			writer.close();
		}
	}

}