package store.j3studios.plugin.spaziorpg.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.entity.Player;
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
        PLAYER_BANK ("PLAYER_BANK"),
        
        CLAN_STATS ("CLAN_STATS"),
                
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
                case PLAYER_BANK -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`uuid` VARCHAR(255), `username` VARCHAR(255), `account` VARCHAR(255), `level` INT NOT NULL DEFAULT '1', `balance` DOUBLE NOT NULL DEFAULT '0.0', `transactions` LONGTEXT, `claimedBonos` LONGTEXT)");
                //
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
    
    /*
        UPDATE / GET : PLAYER STATS
    */
    
    public enum UpdateType {
        PLAYER_STATS_USERNAME ("username"),
        PLAYER_STATS_LEVEL ("level"),
        PLAYER_STATS_EXP ("exp"),
        PLAYER_STATS_TOTAL_EXP ("totalExp"),
        
        PLAYER_SKILLS ("PLAYER_SKILLS"),
        PLAYER_JOBS ("PLAYER_JOBS"),
        
        CLAN_STATS ("CLAN_STATS"),
                
        BANK_ACCOUNTS ("BANK_ACCOUNTS"),
        BANK_BONOS ("BANK_BONOS"),
        BANK_STATS ("BANK_STATS");
        
        private final String dataType;
        private UpdateType (String str) { this.dataType = str; }
        public String getUpdateType() {
            return this.dataType;
        }
    }
    
    /*
        CREATE / UPDATE / GET - PLAYER STATS
    */
    
    public void createPlayer (Player player, DataType dataType) {
        try {
            PreparedStatement st1 = SQL.get().getConnection().prepareStatement("SELECT * FROM `" + dataType.getDataType() + "` WHERE `uuid` = ? LIMIT 1;");
            st1.setString(1, player.getUniqueId().toString());
            st1.executeQuery();
            ResultSet rs = st1.getResultSet();
            if (!rs.next()) {
                switch (dataType) {
                    case PLAYER_STATS ->                         {
                            PreparedStatement st2 = SQL.get().getConnection().prepareStatement("INSERT INTO `" + dataType.getDataType() + "` VALUES (?, ?, ?, ?, ?)");
                            st2.setString(1, player.getUniqueId().toString());
                            st2.setString(2, player.getName());
                            st2.setInt(3, 1);
                            st2.setInt(4, 0);
                            st2.setInt(5, 0);
                            st2.executeUpdate();
                            st2.close();
                        }
                    case PLAYER_SKILLS ->                         {
                            PreparedStatement st2 = SQL.get().getConnection().prepareStatement("INSERT INTO `" + dataType.getDataType() + "` VALUES (?, ?, ?, ?, ?, ?)");
                            st2.setString(1, player.getUniqueId().toString());
                            st2.setString(2, player.getName());
                            st2.setInt(3, 0);
                            st2.setInt(4, 0);
                            st2.setInt(5, 0);
                            st2.setInt(6, 0);
                            st2.executeUpdate();
                            st2.close();
                        }
                    case PLAYER_JOBS ->                         {
                            PreparedStatement st2 = SQL.get().getConnection().prepareStatement("INSERT INTO `" + dataType.getDataType() + "` VALUES (?, ?, ?, ?, ?, ?, ?)");
                            st2.setString(1, player.getUniqueId().toString());
                            st2.setString(2, player.getName());
                            st2.setString(3, "");
                            st2.setString(4, "");
                            st2.setString(5, "");
                            st2.setString(6, "");
                            st2.setString(7, "");
                            st2.executeUpdate();
                            st2.close();
                        }
                    case PLAYER_BANK ->                         { 
                            PreparedStatement st2 = SQL.get().getConnection().prepareStatement("INSERT INTO `" + dataType.getDataType() + "` VALUES (?, ?, ?, ?, ?, ?, ?)");
                            st2.setString(1, player.getUniqueId().toString());
                            st2.setString(2, player.getName());
                            st2.setString(3, "");
                            st2.setInt(4, 1);
                            st2.setDouble(5, 0.0);
                            st2.setString(6, "");
                            st2.setString(7, "");
                            st2.executeUpdate();
                            st2.close();
                        }
                    default -> {
                    }
                }
            }
            rs.close();
            st1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updatePlayer (Player player, DataType dataType, UpdateType updateType, Object value) {
        RPG.get().getServer().getScheduler().runTaskAsynchronously(RPG.get(), () -> {
            try {
                PreparedStatement st = SQL.get().getConnection().prepareStatement("SELECT * FROM `" + dataType.getDataType() + "` WHERE `uuid` = ? LIMIT 1;");
                st.setString(1, player.getUniqueId().toString());
                st.executeQuery();
                ResultSet rs = st.getResultSet();
                if (!rs.next()) {
                    createPlayer(player, dataType);
                } else {
                    PreparedStatement st2 = SQL.get().getConnection().prepareStatement("UPDATE `" + dataType.getDataType() + "` SET `" + updateType.getUpdateType() + "` = ? WHERE `ùuid` = ? LIMIT 1;");
                    st2.setObject(1, value);
                    st2.setString(2, player.getUniqueId().toString());
                    st2.executeUpdate();
                    st2.close();
                }
                rs.close();
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });  
    }
    
    public Object getPlayer (Player player, DataType dataType, UpdateType updateType) {
        Object value = null;
        try {
            assert (SQL.get().getConnection() != null);
            PreparedStatement st = SQL.get().getConnection().prepareStatement("");
            st.setString(1, player.getUniqueId().toString());
            st.executeQuery();
            ResultSet rs = st.getResultSet();
            value = rs.next() ? rs.getObject(updateType.getUpdateType()) : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
