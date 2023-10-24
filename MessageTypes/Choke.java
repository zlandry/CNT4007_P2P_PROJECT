package MessageTypes;

public class Choke extends Message{

    public Choke()
    {
        this.type = Type.CHOKE;
        this.payload = null;
    }    
}
