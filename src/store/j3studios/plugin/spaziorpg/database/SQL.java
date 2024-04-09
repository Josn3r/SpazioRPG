package store.j3studios.plugin.spaziorpg.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import store.j3studios.plugin.spaziorpg.RPG;

public class SQL {

    private static SQL ins;
    private Connection con;
    
    private final String username = "";
    private final String password = "";
    private final String hostname = "";
    private final Integer port = 3306;
    private final String database = "";
    
    public static SQL get() {
        if (ins == null) {
            ins = new SQL();
        }
        return ins;
    }
    
    public Connection getConnection() {
        return con;
    }
    
    public enum DataType {
        PLAYER_STATS ("PLAYER_ACCOUNT"),
        PLAYER_SKILLS ("PLAYER_SKILLS"),
        PLAYER_JOBS ("PLAYER_JOBS"),
        
        CLAN_STATS ("CLAN_STATS"),
                
        BANK_ACCOUNTS ("BANK_ACCOUNTS"),
        BANK_BONOS ("BANK_BONOS"),
        BANK_STATS ("BANK_STATS");
        
        private final String dataType;
        private DataType (String str) { this.dataType = str; }
        public String getDataType() {
            return this.dataType;
        }
    }
    
    public void openConnection() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true&useUnicode=yes", this.username, this.password);
            
            // Loading tables
            for (DataType data : DataType.values()) {
                this.createTable(data);
            }
            
            this.updateConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void closeConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateConnection() {
        RPG.get().getServer().getScheduler().runTaskTimerAsynchronously(RPG.get(), () -> {
            Connection con1 = SQL.get().getConnection();
            try {
                PreparedStatement keepAlive = con1.prepareStatement("SELECT 1 FROM ``");
                keepAlive.executeQuery();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }, 0, 600);
    }

    /*
        CREATE - DELETE - EDIT COLUMN | DATA BASE
    */
    
    private void createTable(DataType type) throws SQLException {
        String dataType = type.getDataType();
        
        Connection c = SQL.get().getConnection();
        Statement st = c.createStatement();
        try {            
            switch (type) {
                case PLAYER_STATS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`uuid` VARCHAR(255), `username` VARCHAR(255), `level` INT NOT NULL DEFAULT '1', `exp` INT NOT NULL DEFAULT '0', `totalExp` INT NOT NULL DEFAULT '0')");
                case PLAYER_SKILLS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`uuid` VARCHAR(255), `username` VARCHAR(255), `strength` INT NOT NULL DEFAULT '0', `agility` INT NOT NULL DEFAULT '0', `life` INT NOT NULL DEFAULT '0', `defense` INT NOT NULL DEFAULT '0')");
                case PLAYER_JOBS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`uuid` VARCHAR(255), `username` VARCHAR(255), `job_1` VARCHAR(255), `job_2` VARCHAR(255), `jobs_level` LONGTEXT, `jobs_exp` LONGTEXT, `jobs_totalExp` LONGTEXT)");
                //
                case BANK_ACCOUNTS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`uuid` VARCHAR(255), `username` VARCHAR(255), `account` VARCHAR(255), `level` INT NOT NULL DEFAULT '1', `balance` DOUBLE NOT NULL DEFAULT '0.0', `transactions` LONGTEXT, `claimedBonos` LONGTEXT)");
                case BANK_BONOS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`id` INT NOT NULL PRIMARY KEY, `name` VARCHAR(255), `stock` INT, `level` INT, `value` DOUBLE)");
                case BANK_STATS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`accounts` INT NOT NULL DEFAULT '0', `totalMoney` DOUBLE NOT NULL DEFAULT '0.0', `partners` LONGTEXT)");
            }
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
