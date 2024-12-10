public class Field {
    private String name;
    private String value;

    public Field(){
        this.name = null;
        this.value = null;
    }
    public Field(String n, String v){
        this.name = n;
        this.value = v;
    }
    //Field Getter and Setters
    public String getName(){
        return name;
    }

    public void setName(String n){
        name = n;
    }

    //Value Getter and Setters
    public String getValue(){
        return value;
    }

    public void setValue(String v){
        value = v;
    }
}
