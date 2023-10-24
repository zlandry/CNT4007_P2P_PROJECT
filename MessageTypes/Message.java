package MessageTypes;

public abstract class Message
{
    protected int length;
    protected Type type;
    protected byte[] payload;

    int getLength()
    {
        return length;
    }

    Type getType()
    {
        return type;
    }

    byte[] getPayload()
    {
        return payload;
    }

    void setLength(int len)
    {
        length = len;
    }

    void setType(Type mtype)
    {
        type = mtype;
    }

    void setPayload(byte[] pld)
    {
        payload = pld;
    }

}
