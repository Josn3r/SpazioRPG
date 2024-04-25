package store.j3studios.plugin.spaziorpg.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class SQL {

    private static SQL ins;
    private Connection con;
    
    private final String username = RPG.get().getConfig().getString("mySQL.username");
    private final String password = RPG.get().getConfig().getString("mySQL.password");
    private final String hostname = RPG.get().getConfig().getString("mySQL.hostname");
    private final Integer port = RPG.get().getConfig().getInt("mySQL.port");
    private final String database = RPG.get().getConfig().getString("mySQL.database");
    
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
        PLAYER_JOBS ("PLAYER_JOBS"),
        PLAYER_BANK ("PLAYER_BANK"),
        PLAYER_GOLDS ("PLAYER_GOLDS"),
        
        BANK_BONOS ("BANK_BONOS"),
        BANK_STATS ("BANK_STATS"),
        
        GOLDS_STATS ("GOLDS_STATS"),
        GOLDS_MOVEMENTS ("GOLDS_MOVEMENTS"),
        GOLDS_PRICE_HISTORY ("GOLDS_PRICE_HISTORY");
                       
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
                PreparedStatement keepAlive = con1.prepareStatement("SELECT 1 FROM `" + DataType.PLAYER_STATS.getDataType() + "`");
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
                case PLAYER_JOBS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`uuid` VARCHAR(255), `username` VARCHAR(255), `job_1` VARCHAR(255), `job_2` VARCHAR(255), `jobs_level` LONGTEXT, `jobs_exp` LONGTEXT, `jobs_totalExp` LONGTEXT)");
                case PLAYER_BANK -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`uuid` VARCHAR(255), `username` VARCHAR(255), `account` VARCHAR(255), `level` INT NOT NULL DEFAULT '1', `balance` DOUBLE NOT NULL DEFAULT '0.0', `transactions` LONGTEXT, `claimedBonos` LONGTEXT)");
                case PLAYER_GOLDS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`uuid` VARCHAR(255), `username` VARCHAR(255), `golds` INT NOT NULL DEFAULT '0')");
                //
                case BANK_BONOS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, `name` VARCHAR(255), `stock` INT, `level` INT, `value` DOUBLE)");
                case BANK_STATS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`accounts` INT NOT NULL DEFAULT '0', `totalMoney` DOUBLE NOT NULL DEFAULT '0.0', `bankProfits` DOUBLE NOT NULL DEFAULT '0.0', `partners` LONGTEXT)");
                //
                case GOLDS_STATS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`total_golds` INT NOT NULL DEFAULT '0', `bank_golds` INT NOT NULL DEFAULT '0', `circulating_golds` INT NOT NULL DEFAULT '0', `produced_golds` INT NOT NULL DEFAULT '0', `burned_golds` INT NOT NULL DEFAULT '0', `gold_price` DOUBLE NOT NULL DEFAULT '0.0', `gold_max_price` DOUBLE NOT NULL DEFAULT '0.0', `gold_min_price` DOUBLE NOT NULL DEFAULT '0.0')");
                case GOLDS_MOVEMENTS -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, `type` VARCHAR(255), `date` VARCHAR(255), `uuid` VARCHAR(255), `amount` INT NOT NULL DEFAULT '0', `gold_price` DOUBLE NOT NULL DEFAULT '0.0')");
                case GOLDS_PRICE_HISTORY -> st.executeUpdate("CREATE TABLE IF NOT EXISTS `" + dataType + "` (`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY, `fecha` VARCHAR(255), `hora` VARCHAR(255), `gold_price` DOUBLE NOT NULL DEFAULT '0.0', `last_gold_price` DOUBLE NOT NULL DEFAULT '0.0', `diff_price` DOUBLE NOT NULL DEFAULT '0.0', `diff_percent` DOUBLE NOT NULL DEFAULT '0.0')");
                
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
        PLAYER_STATS_UUID ("uuid"),
        PLAYER_STATS_USERNAME ("username"),
        PLAYER_STATS_LEVEL ("level"),
        PLAYER_STATS_EXP ("exp"),
        PLAYER_STATS_TOTAL_EXP ("totalExp"),
               
        PLAYER_JOBS_UUID ("uuid"),
        PLAYER_JOBS_USERNAME ("username"),
        PLAYER_JOBS_JOB1 ("job_1"),
        PLAYER_JOBS_JOB2 ("job_2"),
        PLAYER_JOBS_LEVELS ("jobs_level"),
        PLAYER_JOBS_EXPS ("jobs_exp"),
        PLAYER_JOBS_TOTAL_EXPS ("jobs_totalExp"),
        
        PLAYER_BANK_UUID ("uuid"),
        PLAYER_BANK_USERNAME ("username"),
        PLAYER_BANK_ACCOUNT ("account"),
        PLAYER_BANK_LEVEL ("level"),
        PLAYER_BANK_BALANCE ("balance"),
        PLAYER_BANK_TRANSACTIONS ("transactions"),
        PLAYER_BANK_CLAIMED_BONOS ("claimedBonos"),
            
        PLAYER_GOLDS_UUID ("uuid"),
        PLAYER_GOLDS_USERNAME ("username"),
        PLAYER_GOLDS_GOLDS ("golds"),
        
        BANK_BONOS ("BANK_BONOS"),
        BANK_STATS ("BANK_STATS"),
        
        GOLD_STATS_TOTAL_GOLDS ("TOTAL_GOLDS"),
        GOLD_STATS_BANK_GOLDS ("BANK_GOLDS"),
        GOLD_STATS_CIRCULATING_GOLDS ("CIRCULATING_GOLDS"),
        GOLD_STATS_PRODUCED_GOLDS ("PRODUCED_GOLDS"),
        GOLD_STATS_BURNED_GOLDS ("BURNED_GOLDS"),
        GOLD_STATS_GOLD_PRICE ("GOLD_PRICE"),
        GOLD_STATS_GOLD_MAX_PRICE ("GOLD_MAX_PRICE"),
        GOLD_STATS_GOLD_MIN_PRICE ("GOLD_MIN_PRICE");
        
        private final String dataType;
        
        private UpdateType (String str) { this.dataType = str; }
        public String getUpdateType() {
            return this.dataType;
        }
    }
    
    /*
        CREATE / UPDATE / GET - PLAYER STATS
    */
    
    public void createPlayer (DataType dataType, String uuid) {
        try {
            PreparedStatement st1 = SQL.get().getConnection().prepareStatement("SELECT * FROM `" + dataType.getDataType() + "` WHERE `uuid` = ? LIMIT 1;");
            st1.setString(1, uuid);
            st1.executeQuery();
            ResultSet rs = st1.getResultSet();
            if (!rs.next()) {
                switch (dataType) {
                    case PLAYER_STATS ->                         {
                            PreparedStatement st2 = SQL.get().getConnection().prepareStatement("INSERT INTO `" + dataType.getDataType() + "` VALUES (?, ?, ?, ?, ?)");
                            st2.setString(1, uuid);
                            st2.setString(2, Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                            st2.setInt(3, 1);
                            st2.setInt(4, 0);
                            st2.setInt(5, 0);
                            st2.executeUpdate();
                            st2.close();
                        }
                    case PLAYER_JOBS ->                         {
                            PreparedStatement st2 = SQL.get().getConnection().prepareStatement("INSERT INTO `" + dataType.getDataType() + "` VALUES (?, ?, ?, ?, ?, ?, ?)");
                            st2.setString(1, uuid);
                            st2.setString(2, Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
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
                            st2.setString(1, uuid);
                            st2.setString(2, Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                            st2.setString(3, Tools.get().getNumSerie());
                            st2.setInt(4, 1);
                            st2.setDouble(5, 0.0);
                            st2.setString(6, "");
                            st2.setString(7, "");
                            st2.executeUpdate();
                            st2.close();
                        }
                    case PLAYER_GOLDS ->                         { 
                            PreparedStatement st2 = SQL.get().getConnection().prepareStatement("INSERT INTO `" + dataType.getDataType() + "` VALUES (?, ?, ?)");
                            st2.setString(1, uuid);
                            st2.setString(2, Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                            st2.setInt(3, 0);
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
    
    public void updateData (DataType dataType, UpdateType updateType, String where, String sql, Object value) {
        try {
            PreparedStatement st = SQL.get().getConnection().prepareStatement("SELECT * FROM `" + dataType.getDataType() + "` WHERE `" + where + "` = ? LIMIT 1;");
            st.setString(1, sql);
            st.executeQuery();
            ResultSet rs = st.getResultSet();
            if (!rs.next()) {
                if (sql.length()>30) {
                    createPlayer(dataType, sql);
                }
            } else {
                PreparedStatement st2 = SQL.get().getConnection().prepareStatement("UPDATE `" + dataType.getDataType() + "` SET `" + updateType.getUpdateType() + "` = ? WHERE `" + where + "` = ? LIMIT 1;");
                st2.setObject(1, value);
                st2.setString(2, sql);
                st2.executeUpdate();
                st2.close();
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
    
    public Object getData (DataType dataType, UpdateType updateType, String where, String sql) {
        Object value = null;
        try {
            assert (SQL.get().getConnection() != null);
            PreparedStatement st = SQL.get().getConnection().prepareStatement("SELECT `" + updateType.getUpdateType() + "` FROM `" + dataType.getDataType() + "` WHERE `" + where + "` = ?;");
            st.setString(1, sql);
            st.executeQuery();
            ResultSet rs = st.getResultSet();
            if (!rs.next()) {
                if (sql.length()>30) {
                    createPlayer(dataType, sql);
                }
            } else {
                value = rs.getObject(updateType.getUpdateType());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }

    /*
        START GOLDS / UPDATE / GET - GOLDS STATS
    */
    
    public void startGoldStats (Integer startedGolds, Double startedPrice) {
        try {
            PreparedStatement st = SQL.get().getConnection().prepareStatement("INSERT INTO `" + DataType.GOLDS_STATS + "` VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            st.setInt(1, startedGolds);
            st.setInt(2, startedGolds);
            st.setInt(3, 0);
            st.setInt(4, 0);
            st.setInt(5, 0);
            st.setDouble(6, startedPrice);
            st.setDouble(7, startedPrice);
            st.setDouble(8, startedPrice);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateGoldStats (UpdateType updateType, Object value) {
        try {
            PreparedStatement st2 = SQL.get().getConnection().prepareStatement("UPDATE `" + DataType.GOLDS_STATS + "` SET `" + updateType.getUpdateType() + "` = ? LIMIT 1;");
            st2.setObject(1, value);
            st2.executeUpdate();
            st2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
    
    public Object getGoldStats (UpdateType updateType) {
        Object value = null;
        try {
            assert (SQL.get().getConnection() != null);
            PreparedStatement st = SQL.get().getConnection().prepareStatement("SELECT `" + updateType.getUpdateType() + "` FROM `" + DataType.GOLDS_STATS + "`;");
            st.executeQuery();
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                value = rs.getObject(updateType.getUpdateType());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return value;
    }
    
    public void saveGoldHistory (String fecha, String hora, Double price, Double last_price, Double diff_price, Double diff_percent) {
        try {
            PreparedStatement st = SQL.get().getConnection().prepareStatement("INSERT INTO `" + DataType.GOLDS_PRICE_HISTORY + "` (`fecha`, `hora`, `gold_price`, `last_gold_price`, `diff_price`, `diff_percent`) VALUES ('"+fecha+"', '"+hora+"', "+price+", '"+last_price+"', '"+diff_price+"', '"+diff_percent+"');");
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<String> getGoldHistory(Integer limit) {
        ArrayList<String> list = new ArrayList<>();
        try {
            assert (SQL.get().getConnection() != null);
            PreparedStatement st = SQL.get().getConnection().prepareStatement("SELECT * FROM `" + DataType.GOLDS_PRICE_HISTORY + "` ORDER BY id DESC LIMIT "+limit+";");            
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                String fecha = rs.getString("fecha");
                String hora = rs.getString("hora");
                Double price = rs.getDouble("gold_price");
                Double last_price = rs.getDouble("last_gold_price");
                Double diff_price = rs.getDouble("diff_price");
                Double diff_percent = rs.getDouble("diff_percent");
                list.add(fecha + " / " + hora + " / " + price + " / " + last_price + " / " + diff_price + " / " + diff_percent);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
}
