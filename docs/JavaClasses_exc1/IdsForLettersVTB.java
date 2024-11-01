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
 * This programm gets the Ids for the letters V,T,B and lowercase
 */
public class IdsForLettersVTB {
    private static Logger LOGGER = Logger.getLogger(IdsForLettersVTB.class.getName());
    private ConnectionSource connection;

    /**
     * Connects to MariaDB, executes the program
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
     * Gets the Solution and Prints it
     */
    private void interactWithDB() {
        try {
            //Create dao
            Dao<Letter, Integer> dao = DaoManager.createDao(this.connection, Letter.class);

            //Task 2.2 b)
            String idsT = findIdsOfSpecifiedLetter(dao,"t");
            String idsB = findIdsOfSpecifiedLetter(dao,"b");
            String idsV = findIdsOfSpecifiedLetter(dao,"v");

            //Print of all Solutions
            System.out.println("The Solution for b) is:");
            System.out.println("IdsV: " + idsV);
            System.out.println("IdsB: " + idsB);
            System.out.println("IdsT: " + idsT + "\n");

        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error code: " + exception.getErrorCode());
            LOGGER.log(Level.SEVERE, "Error message: " + exception.getMessage());
        }
    }

    private String findIdsOfSpecifiedLetter(Dao<Letter, Integer> dao, String letter) throws SQLException {
        List<Letter> ids = dao.queryForEq("letter",letter);
        ids.addAll(dao.queryForEq("letter",letter.toUpperCase()));

        StringBuilder idsList = new StringBuilder();
        for (Letter l : ids) {
            idsList.append(l.getId()).append(", ");
        }
        return idsList.toString();
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

    public static void main(String args[]) {
        IdsForLettersVTB program = new IdsForLettersVTB();
        program.run();
    }
}
