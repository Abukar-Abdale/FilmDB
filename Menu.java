import org.json.JSONException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class Menu {
    private Scanner scanner;
    private SQLite database;
    private OMDBApi omdbAPI;

    private KeyReader keyReader = new KeyReader();

    public Menu() throws ClassNotFoundException, SQLException, IOException {
        scanner = new Scanner(System.in);
        database = new SQLite( "film.db");
        database.createMoviesTable();
        String apiKey = keyReader.getApiKey();
        omdbAPI = new OMDBApi(apiKey);

    }


    public void run() throws SQLException {
        System.out.println("Welcome to the Movie Database!");
        boolean quit = false;
        while (!quit) {
            System.out.println("\nPlease select an option:");

            System.out.println("1. Search for a movie");
            System.out.println("2. Add a movie");
            System.out.println("3. Show all movies ");
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
                     addMovie();
                    break;
                case 3:
                    showAllMovies();
                    break;
                case 4:
                case 8:
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid input. Please enter a number between 1 and 8.");
                    break;
            }
        }

    }

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



    private void search() throws SQLException {
        System.out.println("\nWhat do you want to search for?");
        System.out.println("1. Movies");
        System.out.println("2. Actors");
        System.out.println("3. Directors");
        System.out.println("4. Genres");
        System.out.println("5. Years");
        System.out.println("6. Back");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline character

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

    public void searchMovies() throws SQLException {
        System.out.println("\nEnter movie title:");
        String title = scanner.nextLine();
        Movie[] movies = Movie.getMovie(title);
        if (movies.length == 0) {
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
        } else {
            // Movie found in local database, display results
            System.out.println("Results from local database:");
            displayResult(movies);
        }
    }




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

        }
    }

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






    private void searchActors() throws SQLException{
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




