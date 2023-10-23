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

	public static void main(String[] args) throws Exception
	{
		File common = new File("Common.cfg");

		if(common.exists())
		{
			Scanner scanner = new Scanner(common);
			while(scanner.hasNextLine())
			{
				String line = scanner.nextLine();

				int spacePos = line.indexOf(' ');
				String substr = line.substring(0, spacePos);
				String remainder = line.substring(spacePos, line.length());
				remainder = remainder.trim();

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
						throw new IOException("Common.cfg is malformed - " + line + " is not formatted correctly.");
				}

			}
			scanner.close();
		}
		else
		{
			FileWriter writer = new FileWriter("Common.cfg");
			writer.write("NumberOfPreferredNeighbors 2\n");
			writer.write("UnchokingInterval 5\n");
			writer.write("OptimisticUnchokingInterval 15\n");
			writer.write("FileName TheFile.dat\n");
			writer.write("FileSize 10000232\n");
			writer.write("PieceSize 32768\n");
			writer.close();
		}

		File peerinfo = new File("PeerInfo.cfg");
		if(peerinfo.exists())
		{
			Scanner scanner = new Scanner(common);
			while(scanner.hasNextLine())
			{

			}
			scanner.close();
		}
		else
		{
			FileWriter writer = new FileWriter("PeerInfo.cfg");
			writer.write("NumberOfPreferredNeighbors 2\n");
			writer.write("UnchokingInterval 5\n");
			writer.write("OptimisticUnchokingInterval 15\n");
			writer.write("FileName TheFile.dat\n");
			writer.write("FileSize 10000232\n");
			writer.write("PieceSize 32768\n");
			writer.close();
		}
	}

}