package csci305.javalab;

public class Lizard extends Element{
    
    public Lizard(String n){
        super(n);
    }
    
    public Outcome compareTo(Element that){
        return new Outcome(this.get_name(), that.get_name());
        
    }
}