package bbk_beam.mtRooms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public interface IDatabase {
    public bool connect( String host, String username, String password );
    public bool disconnect();
    public ResultSet queryDB( String query );
}
