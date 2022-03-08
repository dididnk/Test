package fr.iut.td.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionFactory {
  String dbFolder;
  String login;
  String password;

  public ConnectionFactory(String jdbcDriver, String dbFolder, String login, String password)
      throws ClassNotFoundException, SQLException, IOException {
    //System.out.println("ConnectionFactory.initDBIfEmpty()" + Paths.get(dbFolder).toAbsolutePath().toString());
    this.dbFolder = dbFolder.replace('\\', '/');
    if (!dbFolder.endsWith("/"))
      dbFolder += "/";
    this.login = login;
    this.password = password;
    Class.forName(jdbcDriver);

    initDatabase();
  }

  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection("jdbc:h2:" + dbFolder + "db", login, password);
  }

  private void initDatabase() throws SQLException, IOException {
    try (
        Connection con = getConnection(); ) {

      createDB(con);
      initDBIfEmpty(con);
    }
  }

  private void initDBIfEmpty(Connection con) throws SQLException, IOException {
    try (
        PreparedStatement checkNotEmptyStmt = con.prepareStatement("SELECT idWUser FROM WUser LIMIT 1 ");
        ResultSet checkNotEmptyRs = checkNotEmptyStmt.executeQuery();
        PreparedStatement inserStmt = con
            .prepareStatement(
                "INSERT INTO "
                    + " WUSER(created,isDead,birthDate,size,firstname,lastname)"
                    + " VALUES (now(),?,?, ?,  ?,?)"); ) {
      if (checkNotEmptyRs.next())
        return;

      Path dataFile=Paths.get(dbFolder, "wuser.txt").toAbsolutePath();
      System.out.println("ConnectionFactory.initDBIfEmpty()" + dataFile.toString());
      for (String line : Files.readAllLines(dataFile)) {
        String[] fields = line.split("[,;]",5);
        for (int i = 0, iMax = fields.length; i < iMax; i++) {
          inserStmt.setString(i + 1, fields[i]);
        }
        inserStmt.executeLargeUpdate();
      }
    }
  }

  public void createDB(Connection con) throws SQLException {
    try (
        PreparedStatement stmt = con
            .prepareStatement(
                " CREATE TABLE IF NOT EXISTS WUser ("
                    + "       idWUser INT AUTO_INCREMENT PRIMARY KEY NOT NULL, "
                    + "       created DATETIME, "
                    + "       isDead BOOLEAN, "
                    + "       birthDate DATE, "
                    + "       size FLOAT, "
                    + "       firstname VARCHAR(80) NOT NULL,  "
                    + "       lastname VARCHAR(80) NOT NULL   "
                    + " ); "); ) {
      stmt.executeUpdate();
    }
  }
}
