import java.io.FileNotFoundException;
public interface Connection {
    void connect(String filename) throws FileNotFoundException;
    void disconnect(String filename) throws FileNotFoundException;
}
