import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.Arrays;

public class Main{
    public static void main(String[] args) throws IOException, InvalidKeyException {
        //Not a UI - just testing code
        DBMS test = new DBMS(); // new dbms instance

        Field search = new Field("name","grape");
        Field modify = new Field("number","4242");
        Field delete = new Field("name","peanut");
        Field num = new Field("number", "pizza");
        Field select = new Field("name", "machete");
        Field id = new Field("id","20");

        Field[] newRec = {id, search, num};

        Record record = new Record(newRec);

        // testing connect and disconnect methods and their error handling
        test.connect("C:/Users/ellie/OneDrive/Desktop/test.txt");

        //System.out.println(test.insert(record)); // works!

        //System.out.println(test.contains("id","20")); // works!

        //System.out.println(Arrays.toString(test.update(search, modify))); //works!

        //System.out.println(Arrays.toString(test.select(select))); // works!

        System.out.println(Arrays.toString(test.delete(delete))); // works!

        test.disconnect("C:/Users/ellie/OneDrive/Desktop/test.txt");

    }
}