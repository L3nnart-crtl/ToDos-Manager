package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This programm gets the word of the provided array.
 */
public class WordOfArrayIndex {
    private static Logger LOGGER = Logger.getLogger(WordOfArrayIndex.class.getName());
    private ConnectionSource connection;

    public static void main(String args[]) {
        WordOfArrayIndex program = new WordOfArrayIndex();
        program.run();
    }

    /**
     * Connects to MariaDB, executes the program and closes connection
     */
    public void run() {

        // creates connection to a MariaDB installed on localhost
        boolean connected = this.connectToDB("jdbc:mariadb://bilbao.informatik.uni-stuttgart.de/pe2-db-a1", "pe2-nutzer", "esJLtFm6ksCT4mCyOS");

        if (connected) {
            this.interactWithDB();
            this.closeConnectionToDB();
        }
    }

    /*
     * Connects to a database
     */
    private boolean connectToDB(String connectionString, String user, String password) {
        try {
            ConnectionSource connection = new JdbcConnectionSource(connectionString,user, password);
            this.connection = connection;
            return true;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error code: " + exception.getErrorCode());
            LOGGER.log(Level.SEVERE, "Error message: " + exception.getMessage());
        }
        return false;
    }

    /*
     * Gets the word and prints it
     */
    private void interactWithDB() {
        try {
            //Create dao
            Dao<Letter, Integer> dao = DaoManager.createDao(this.connection, Letter.class);

            // Task 2.2 a)
            int[] arrayIndexes = {
                    20, 44, 50, 13, 17, 33, 41,
                    68, 77, 44, 29, 72, 48, 71,
                    37, 48, 11, 69, 5, 65, 65
            };
            StringBuilder word = new StringBuilder();
            //QueryForId and append the letter
            for (int index : arrayIndexes) {
                Letter letter = dao.queryForId(index);
                if (letter != null) {
                    word.append(letter.getLetter());
                }
            }

            //Print the word
            System.out.println("The Solution for a) is:");
            System.out.println(word + "\n");

        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error code: " + exception.getErrorCode());
            LOGGER.log(Level.SEVERE, "Error message: " + exception.getMessage());
        }
    }

    /*
     * Closes connection to the database
     */
    private void closeConnectionToDB() {
        try {
            this.connection.close();
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, "Error code: " + exception.getMessage());
            LOGGER.log(Level.SEVERE, "Error message: " + exception.getMessage());
        }
    }
}
