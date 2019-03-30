package csci305.javalab;

public class Scissors extends Element{
    
    public Scissors(String n){
        super(n);
    }
    
    public Outcome compareTo(Element that){
        return new Outcome(this.get_name(), that.get_name());
    }
}