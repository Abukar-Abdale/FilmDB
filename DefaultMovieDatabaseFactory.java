
import java.sql.SQLException;
import java.util.Scanner;

public class DefaultMovieDatabaseFactory implements MovieDatabaseFactory {
    public Scanner createScanner() {
        return new Scanner(System.in);
    }

    public SQLite createSQLite(String dbName) throws ClassNotFoundException, SQLException {
        SQLite database = new SQLite(dbName);
        database.createMoviesTable();
        return database;
    }

    public OMDBApi createOMDBApi(String apiKey) {
        return new OMDBApi(apiKey);
    }
}
