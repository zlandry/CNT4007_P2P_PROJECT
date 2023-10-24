package MessageTypes;

public enum Type
{
    HANDSHAKE,
    CHOKE,
    UNCHOKE,
    INTERESTED,
    NOTINTERESTED,
    HAVE,
    BITFIELD,
    REQUEST,
    PIECE;

    public static byte getTypeInt(Type msg)
    {
        switch(msg)
        {
            case HANDSHAKE:
                return -1;

            case CHOKE:
                return 0;
            
            case UNCHOKE:
                return 1;
            
            case INTERESTED:
                return 2;
            
            case NOTINTERESTED:
                return 3;
            
            case HAVE:
                return 4;
            
            case BITFIELD:
                return 5;
            
            case REQUEST:
                return 6;
            
            case PIECE:
                return 7;
                
            default:
                return -99;

        }
    }
}