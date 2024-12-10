import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.io.IOException;
public interface Process {
    Record insert(Record rec) throws InvalidKeyException, IOException;

    boolean contains(String field, String value);

    Record[] update(Field search, Field modify) throws IOException;

    Record[] select(Field search) throws FileNotFoundException;

    Record[] delete(Field search) throws FileNotFoundException;
}
