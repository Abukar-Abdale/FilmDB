import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public interface MovieDatabaseFactory {
    Scanner createScanner();
    SQLite createSQLite(String dbName) throws ClassNotFoundException, SQLException, IOException;
    OMDBApi createOMDBApi(String apiKey);
}

