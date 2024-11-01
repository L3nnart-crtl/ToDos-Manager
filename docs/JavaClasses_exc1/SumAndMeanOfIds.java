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
 * This class contains examples of using the JDBC API to connect to a local MariaDB Server
 */
public class SumAndMeanOfIds {
    private static Logger LOGGER = Logger.getLogger(SumAndMeanOfIds.class.getName());
    private ConnectionSource connection;

    public static void main(String args[]) {
        SumAndMeanOfIds program = new SumAndMeanOfIds();
        program.run();
    }
    /**
     * Connects to MariaDB, and executes the program
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
            ConnectionSource connection = new JdbcConnectionSource(connectionString, user, password);
            this.connection = connection;
            return true;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error code: " + exception.getErrorCode());
            LOGGER.log(Level.SEVERE, "Error message: " + exception.getMessage());
        }
        return false;
    }

    /*
     * Gets the Sum and Mean of ids
     */
    private void interactWithDB() {
        try {
            //Create dao
            Dao<Letter, Integer> dao = DaoManager.createDao(this.connection, Letter.class);

            //Task 2.2 c)
            List<Letter> allLetters = dao.queryForAll();
            Integer sum = 0;
            for (Letter letter : allLetters) {
                sum += letter.getId();
            }
            String sumIds = "Sum of all IDs: " + sum;
            String meanIds = "Mean of all IDs: " + (sum / allLetters.size());

            //Print of all Solutions
            System.out.println("The Solution for c) is:");
            System.out.println(sumIds);
            System.out.println(meanIds);

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
