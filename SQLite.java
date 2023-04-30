import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SQLite {

    private Connection connection;

    public SQLite(String database) throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC"); // Load the JDBC driver for SQLite
        connection = DriverManager.getConnection("jdbc:sqlite:" + database);
    }

    public void createMoviesTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS movies ( id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, released TEXT, year TEXT, imdbID TEXT, type TEXT, poster TEXT, runtime TEXT, genre TEXT, director TEXT, writer TEXT, actors TEXT, plot TEXT, language TEXT, country TEXT, awards TEXT, rated TEXT, metascore TEXT, imdbRating TEXT, imdbVotes TEXT)");
            System.out.println("Table 'movies' has been created.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addMovie(Movie movie) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO movies (title, released, year, imdbID, type, poster, runtime, genre, director, writer, actors, plot, language, country, awards, rated, metascore, imdbRating, imdbVotes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, movie.getTitle());
            preparedStatement.setString(2, movie.getReleased());
            preparedStatement.setString(3, movie.getYear());
            preparedStatement.setString(4, movie.getImdbID());
            preparedStatement.setString(5, movie.getType());
            preparedStatement.setString(6, movie.getPoster());
            preparedStatement.setString(7, movie.getRuntime());
            preparedStatement.setString(8, movie.getGenre());
            preparedStatement.setString(9, movie.getDirector());
            preparedStatement.setString(10, movie.getWriter());
            preparedStatement.setString(11, movie.getActors());
            preparedStatement.setString(12, movie.getPlot());
            preparedStatement.setString(13, movie.getLanguage());
            preparedStatement.setString(14, movie.getCountry());
            preparedStatement.setString(15, movie.getAwards());
            preparedStatement.setString(16, movie.getRated());
            preparedStatement.setString(17, movie.getMetascore());
            preparedStatement.setString(18, movie.getImdbRating());
            preparedStatement.setString(19, movie.getImdbVotes());
            preparedStatement.executeUpdate();
            System.out.println("Movie added successfully");
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                System.out.println("Movie already exists");
            } else {
                System.out.println(e.getMessage());
            }
        }
    }

    public Movie[] getMovie(String title) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM movies WHERE title LIKE ?");
            preparedStatement.setString(1, "%" + title + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Movie> movies = new ArrayList<>();
            while (resultSet.next()) {
                Movie movie = createMovieFromResultSet(resultSet);
                movies.add(movie);
            }
            return movies.toArray(new Movie[0]);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Movie[0];
        }
    }






    public Movie[] getMovie(String title, String year)  {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM movies WHERE title LIKE ? AND year = ?");
            preparedStatement.setString(1, "%" + title + "%");
            preparedStatement.setString(2, year);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Movie> movies = new ArrayList<>();
            while (resultSet.next()) {
                Movie movie = createMovieFromResultSet(resultSet);
                movies.add(movie);
            }
            return movies.toArray(new Movie[0]);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Movie[0];
        }
    }



    public Movie[] getMovie(String title, String year, String type) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM movies WHERE title LIKE ? AND year LIKE ? AND type LIKE ?\n");
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, year);
            preparedStatement.setString(3, type);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Movie> movies = new ArrayList<>();
            while (resultSet.next()) {
                Movie movie = createMovieFromResultSet(resultSet);
                movies.add(movie);
            }
            return movies.toArray(new Movie[0]);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Movie[0];
        }
    }

    public Movie[] getActor(String actor) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM movies WHERE actors LIKE ?");
            preparedStatement.setString(1, "%" + actor + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Movie> movies = new ArrayList<>();
            while (resultSet.next()) {
                Movie movie = createMovieFromResultSet(resultSet);
                movies.add(movie);
            }
            return movies.toArray(new Movie[0]);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Movie[0];
        }
    }



    public Movie[] getDirector(String director) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM movies WHERE director LIKE ?");
            preparedStatement.setString(1, "%" + director + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Movie> movies = new ArrayList<>();
            while (resultSet.next()) {
                Movie movie = createMovieFromResultSet(resultSet);
                movies.add(movie);
            }
            return movies.toArray(new Movie[0]);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Movie[0];
        }
    }

    public Movie[] getGenre(String genre) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM movies WHERE genre LIKE ?");
            preparedStatement.setString(1, "%" + genre + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Movie> movies = new ArrayList<>();
            while (resultSet.next()) {
                Movie movie = createMovieFromResultSet(resultSet);
                movies.add(movie);
            }
            return movies.toArray(new Movie[0]);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Movie[0];
        }
    }

    public Movie[] getYear(String year) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM movies WHERE year = ?");
            preparedStatement.setString(1, year);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Movie> movies = new ArrayList<>();
            while (resultSet.next()) {
                Movie movie = createMovieFromResultSet(resultSet);
                movies.add(movie);
            }
            return movies.toArray(new Movie[0]);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return new Movie[0];
        }
    }

    private Movie createMovieFromResultSet(ResultSet resultSet) throws SQLException {
        Movie movie = new Movie( null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        movie.setTitle(resultSet.getString("title"));
        movie.setReleased(resultSet.getString("released"));
        movie.setYear(resultSet.getString("year"));
        movie.setImdbID(resultSet.getString("imdbID"));
        movie.setType(resultSet.getString("type"));
        movie.setPoster(resultSet.getString("poster"));
        movie.setRuntime(resultSet.getString("runtime"));
        movie.setGenre(resultSet.getString("genre"));
        movie.setDirector(resultSet.getString("director"));
        movie.setWriter(resultSet.getString("writer"));
        movie.setActors(resultSet.getString("actors"));
        movie.setPlot(resultSet.getString("plot"));
        movie.setLanguage(resultSet.getString("language"));
        movie.setCountry(resultSet.getString("country"));
        movie.setAwards(resultSet.getString("awards"));
        movie.setRated(resultSet.getString("rated"));
        movie.setMetascore(resultSet.getString("metascore"));
        movie.setImdbRating(resultSet.getString("imdbRating"));
        movie.setImdbVotes(resultSet.getString("imdbVotes"));
        return movie;
    }

    public void deleteMovie(Movie movie) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM movies WHERE imdbID = ?");
            preparedStatement.setString(1, movie.getImdbID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}








