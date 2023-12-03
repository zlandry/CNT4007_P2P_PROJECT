package MessageTypes;

public abstract class Message
{
    protected int length;
    protected Type type;
    protected byte[] payload;

    public int getLength()
    {
        return length;
    }

    public Type getType()
    {
        return type;
    }

    public byte[] getPayload()
    {
        return payload;
    }

    public void setLength(int len)
    {
        length = len;
    }

    public void setType(Type mtype)
    {
        type = mtype;
    }

    public void setPayload(byte[] pld)
    {
        payload = pld;
    }

    public char[] getChar() {
        // Assuming length includes 4 bytes of length itself, 1 byte of type, and payload length
        char[] message = new char[10];
        // 
        
        
    
        return message;
    }

}
