package MessageTypes;

public class NotInterested extends Message{
    
    public NotInterested()
    {
        this.type = Type.CHOKE;
        this.payload = null;
    }    

}
