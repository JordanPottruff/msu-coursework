package csci305.javalab;


public abstract class Element{
    private String name;
    
    public Element(String n){
        name = n;
    }
    
    public String get_name(){
        return name;
    }
    
    public abstract Outcome compareTo(Element that);
    
}