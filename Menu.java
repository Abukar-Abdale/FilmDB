import org.json.JSONException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


// Modify Menu class to use the factory
public class Menu {
    private Scanner scanner;
    private SQLite database;
    private OMDBApi omdbAPI;

    private KeyReader keyReader = new KeyReader();
    private MovieDatabaseFactory factory = new DefaultMovieDatabaseFactory();

    public Menu() throws ClassNotFoundException, SQLException, IOException {
        scanner = factory.createScanner();
        database = factory.createSQLite("film.db");
        String apiKey = keyReader.getApiKey();
        omdbAPI = factory.createOMDBApi(apiKey);
    }




    public void run() throws SQLException {
        System.out.println("Welcome to the Movie Database!");
        boolean quit = false;
        while (!quit) {
            System.out.println("\nPlease select an option:");

            System.out.println("1. Search");
            System.out.println("2. Show all movies ");
            System.out.println("3. Add a movie");
            System.out.println("4. Delete a movie");
            System.out.println("4. Quit");

            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 9.");
                scanner.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    search();
                  break;
                case 2:
                    showAllMovies();
                    break;
                 case 3:
                     addMovie();
                    break;

                case 4:
                    deleteMovie();
                    break;
                case 5:
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid input. Please enter a number between 1 and 8.");
                    break;
            }
        }

    }

    /**

     * @throws SQLException
     */
    public void showAllMovies() {
        try {
            // Retrieve all movies from the database
            Movie[] movies = database.getMovie("");

            // Display the movies to the user
            if (movies.length == 0) {
                System.out.println("No movies found");
                System.out.println("Please add a movie first or search for a movie.");
            } else {
                System.out.println("All movies:\n");
                for (Movie movie : movies) {
                    System.out.println(movie.getTitle() + " (" + movie.getYear() + ")");
                    System.out.println("Genre: " + movie.getGenre());
                    System.out.println("Actors: " + movie.getActors());
                    System.out.println("Director: " + movie.getDirector());
                    System.out.println("Type: " + movie.getType());
                    System.out.println("Plot: " + movie.getPlot() + "\n");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving movies: " + e.getMessage());
        }
    }

    /**

     Prompts the user to enter a movie title and adds it to the local database if it is not already present.

     If the movie is not found in the local database, the method makes an API request to search for the movie online.

     If the movie is found online, the user is prompted to add the movie to the local database.

     If the movie is already present in the local database, a message is displayed indicating that the movie is already in the database.

     @throws SQLException if there is an error accessing the database
     */

    private void addMovie() throws SQLException {
        System.out.println("\nEnter movie title:");
        String title = scanner.nextLine();

        Movie[] movies = database.getMovie(title);
        if (movies.length == 0) {
            // Movie not found in local database
            System.out.println("Movie not found in database. Searching online...");
            try {
                OMDBApi api = new OMDBApi(keyReader.getApiKey());
                Movie movie = api.getMovie(title);
                if (movie == null) {
                    System.out.println("Movie not found online. Please try again later.");
                    return;
                }
                System.out.println(movie);

                System.out.println("Do you want to add this movie to the database? (y/n)");
                String answer = scanner.nextLine();
                if (answer.equals("y")) {
                    database.addMovie(movie);
                    System.out.println("Movie added to database");
                } else {
                    System.out.println("Movie not added");
                }

            } catch (JSONException e) {
                System.err.println("Error fetching movie from OMDb API: " + e.getMessage());
                System.out.println("Could not find movie online. Please try again later.");
            }
        } else {
            // Movie found in local database
            Movie movie = movies[0];
            System.out.println(movie.toString());
            System.out.println("Movie already in database");
        }
    }

    /**
     * Deletes a movie from the database based on the user input of the movie title.
     * If the movie is found in the database, it prompts the user for confirmation before deleting.
     */
    private void deleteMovie() {
        try {
            System.out.println("\nEnter movie title:");
            String title = scanner.nextLine();
            Movie[] movies = database.getMovie(title);
            if (movies.length == 0) {
                System.out.println("Movie not found in database.");
            } else {
                Movie movie = movies[0];
                System.out.println(movie.toString());
                System.out.println("Do you want to delete this movie from the database? (y/n)");
                String answer = scanner.nextLine();
                if (answer.equals("y")) {
                    database.deleteMovie(movie);
                    System.out.println("Movie deleted from database");
                } else {
                    System.out.println("Movie not deleted");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting movie: " + e.getMessage());
        }
    }


    /**
     * Searches for movies in the database based on the user input of the movie title.
     * If the movie is found in the database, it displays the movie information.
     */

    private void search() throws SQLException {
        System.out.println("\nWhat do you want to search for?");
        System.out.println("1. Movies");
        System.out.println("2. Actors");
        System.out.println("3. Directors");
        System.out.println("4. Genres");
        System.out.println("5. Years");
        System.out.println("6. Back");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                searchMovies();
               break;
           case 2:
                searchActors();
                break;
           case 3:
                searchDirectors();
                break;
            case 4:
                searchGenres();
                break;
            case 5:
                searchYears();
                break;
            case 6:
                break;
            default:
                System.out.println("Invalid input");
        }

    }
/**   * Searches for movies in the database based on the user input of the movie title.
     * If the movie is not found in the database, it makes an API request to search for the movie online.
     * If the movie is found online, it prompts the user for confirmation before adding the movie to the database.
     */
    public void searchMovies() {
        System.out.println("\nEnter movie title:");
        String title = scanner.nextLine();
        Movie[] movies = Movie.getMovie(title);
        if (movies.length == 0) {
            try {
                // Movie not found in local database, make API request
                OMDBApi omdbApi = new OMDBApi(keyReader.getApiKey());
                Movie[] apiMovies = omdbApi.getMovie(title, keyReader.getApiKey());

                if (apiMovies.length == 0) {
                    System.out.println("No movies found");
                } else {
                    System.out.println("Results from OMDB API:");
                    displayResult(apiMovies);
                    for (Movie movie : apiMovies) {
                        System.out.println("Do you want to add this movie to the database? (Y/N)");
                        String answer = scanner.nextLine();
                        if (answer.equalsIgnoreCase("Y")) {
                            database.addMovie(movie); // add movie to database
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error while searching for movie: " + e.getMessage());
            }
        } else {
            // Movie found in local database, display results
            System.out.println("Results from local database:");
            displayResult(movies);
        }
    }

/**   * Displays the results of a movie search.
     * @param movies the movies to display
     */

    private void displayResult(Movie[] movies) {
        System.out.println("Results:\n");

        // Add each movie to the table as a row
        for (Movie movie : movies) {
            System.out.print("Title: ");
            typeOut(movie.getTitle());
            System.out.print("Year: ");
            typeOut(movie.getYear());
            System.out.print("Genre: ");
            typeOut(movie.getGenre());
            System.out.print("Type: ");
            typeOut(movie.getType());
            System.out.print("Plot: ");
            typeOut(movie.getPlot());

        }
    }
/**   * Types out the given text to the console.
     * @param text the text to type out slowly
     */
    private void typeOut(String text) {
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.err.println("Interrupted while typing: " + e.getMessage());
                Thread.currentThread().interrupt();
                return;
            }
        }
        System.out.println();
    }



    /**
     * Searches for actors in the database based on the user input of the actors name.
     */


    private void searchActors(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter actor name: ");
        String actor = scanner.nextLine().trim();

        try {
            // Retrieve all movies that feature the given actor
            Movie[] movies = database.getActor( actor);

            // Display the movies to the user
            if (movies.length == 0) {
                System.out.println(actor + " has not acted in any movies");
            } else {
                System.out.println("Movies featuring " + actor + ":");
                for (Movie movie : movies) {
                    System.out.println(movie.getTitle() + " (" + movie.getYear() + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving movies: " + e.getMessage());
        }

    }
    /**
     * Searches for directors in the database based on the user input of the directors name.
     */
    private void searchDirectors(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter director name: ");
        String director = scanner.nextLine().trim();

        try {
            // Retrieve all movies directed by the given director
            Movie[] movies = database.getDirector(director);

            // Display the movies to the user
            if (movies.length == 0) {
                System.out.println(director + " has not directed any movies");
            } else {
                System.out.println("Movies directed by " + director + ":");
                for (Movie movie : movies) {
                    System.out.println(movie.getTitle() + " (" + movie.getYear() + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving movies: " + e.getMessage());
        }



    }
    /**
     * Searches for genres in the database based on the user input of the genre.
     */
    private void searchGenres(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        try {
            // Retrieve all movies that feature the given genre
            Movie[] movies = database.getGenre(genre);

            // Display the movies to the user
            if (movies.length == 0) {
                System.out.println("No movies found in the " + genre + " genre");
            } else {
                System.out.println("Movies in the " + genre + " genre:");
                for (Movie movie : movies) {
                    System.out.println(movie.getTitle() + " (" + movie.getYear() + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving movies: " + e.getMessage());
        }


    }
    /**
     * Searches for years in the database based on the user input of the year.
     */
    private void searchYears(){
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter year: ");
        String year = scanner.nextLine().trim();

        try {
            // Retrieve all movies that were released in the given year
            Movie[] movies = database.getYear(year);

            // Display the movies to the user
            if (movies.length == 0) {
                System.out.println("No movies found in " + year);
            } else {
                System.out.println("Movies released in " + year + ":");
                for (Movie movie : movies) {
                    System.out.println(movie.getTitle() + " (" + movie.getYear() + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving movies: " + e.getMessage());


    }


}
}




