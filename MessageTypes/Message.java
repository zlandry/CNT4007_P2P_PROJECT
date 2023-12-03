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

}
