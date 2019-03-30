package csci305.javalab;

public class Spock extends Element{
    
    public Spock(String n){
        super(n);
    }
    
    public Outcome compareTo(Element that){
        return new Outcome(this.get_name(), that.get_name());
    }
}