package MessageTypes;

public class Interested extends Message{
    
    public Interested()
    {
        this.type = Type.CHOKE;
        this.payload = null;
    }    

}
