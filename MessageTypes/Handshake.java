package MessageTypes;

public class Handshake extends Message
{

    int peerID;

    public Handshake(int id)
    {
        type = Type.HANDSHAKE;
        peerID = id;
        setupPayload();
    }    

    public void setupPayload(){
        byte[] message = new byte[32];

        String hsheader = "P2PFILESHARINGPROJ";
        char[] splitstr = hsheader.toCharArray();

        for(int i = 0; i < splitstr.length; ++i)
        {
            message[i] = (byte)splitstr[i];
        }

        for(int i = 18; i < 28; ++i)
        {
            message[i] = (byte)0;
        }

        String str = String.valueOf(peerID);

        for(int i = 0; i < str.length(); ++i)
        {
            message[i] = (byte)str.charAt(i);
        }
        payload = message;
    }

}
