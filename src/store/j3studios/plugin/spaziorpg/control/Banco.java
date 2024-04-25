package store.j3studios.plugin.spaziorpg.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.database.SQL;
import store.j3studios.plugin.spaziorpg.utils.Tools;

public class Banco {
    
    private static Banco ins;    
    public static Banco get() {
        if (ins == null) { ins = new Banco(); }
        return ins;
    }
    
    //
    
    private final Map<Player, Double> paydayLog = new HashMap<>();
    public Map<Player, Double> getPaydayLog() { return paydayLog; }
    
    private final Double DEPOSIT_COMM = 3.25D;
    private final Double WITHDRAW_COMM = 2.75D;
    private final Double TRANSFER_COMM = 4.65D;
    
    public Double getCommission(CommissionType type) {
        if (null != type) switch (type) {
            case DEPOSIT -> {
                return DEPOSIT_COMM;          
            }
            case WITHDRAW -> {
                return WITHDRAW_COMM;          
            }
            case TRANSFER_POSITIVE -> {
                return TRANSFER_COMM;          
            }
            case TRANSFER_NEGATIVE -> {
                return TRANSFER_COMM;          
            }
        }
        return 0.0D;
    }
    
    public enum CommissionType {
        DEPOSIT,
        WITHDRAW,
        TRANSFER_POSITIVE,
        TRANSFER_NEGATIVE;
    }
    
    public enum TransactionType {
        DEPOSIT,
        WITHDRAW,
        TRANSFER_POSITIVE,
        TRANSFER_NEGATIVE,
        BONO,
        PAYDAY;
    }
    
    public void createTransaction (String uuid, TransactionType type, Double value, String transferAccount) {
        byte[] transList64 = Base64.getDecoder().decode(SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_TRANSACTIONS, "uuid", uuid).toString());
        String transListArray = new String(transList64);
        ArrayList<String> transList = new ArrayList<>();
        if (transListArray.contains(" /// ")) {
            transList.addAll(Arrays.asList(transListArray.split(" /// ")));
        } else {
            transList.add(transListArray);
        }      
        String timeStamp = (new SimpleDateFormat("dd/MM/yyyy (HH:mm:ss)")).format(Calendar.getInstance().getTime());
        String text = "";                
        
        Double balance = (double)SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", uuid);                
        if (null != type) switch (type) {
            case DEPOSIT -> {
                Double calcComision = Tools.get().calcPercent(value, getCommission(CommissionType.valueOf(type.toString())));
                Double total = balance + (value-calcComision);
                SQL.get().updateData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", uuid, total);
                text = value + " : " + type.toString() + " : " + timeStamp;                
            }
            case WITHDRAW -> {
                Double total = balance - value;
                SQL.get().updateData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", uuid, total);
                text = value + " : " + type.toString() + " : " + timeStamp; 
            }
            case TRANSFER_POSITIVE -> {
                Double total = balance + value;
                SQL.get().updateData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", uuid, total);
                text = value + " : " + type.toString() + " : " + transferAccount + " : " + timeStamp; 
            }
            case TRANSFER_NEGATIVE -> {
                Double calcComision = Tools.get().calcPercent(value, getCommission(CommissionType.valueOf(type.toString())));
                Double total = balance - (value+calcComision);
                SQL.get().updateData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", uuid, total);
                text = value + " : " + type.toString() + " : " + transferAccount + " : " + timeStamp; 
            }
            case BONO -> {
                Double total = balance + value;
                SQL.get().updateData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", uuid, total);
                text = value + " : " + type.toString() + " : " + timeStamp; 
            }
            case PAYDAY -> {
                Double total = balance + value;
                SQL.get().updateData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, "uuid", uuid, total);
                text = value + " : " + type.toString() + " : " + timeStamp; 
            }
        }        
        transList.add(text);
        if (transList.size() > 15) {
            transList.remove(0);
        }        
        String transactions = "";
        if (!transList.isEmpty()) {
            for (String str : transList) {
                transactions = transactions + str + " /// ";
            }
            transactions = transactions.substring(0, transactions.length()-5);
        }
        String transactions64 = Base64.getEncoder().encodeToString(transactions.getBytes());
        SQL.get().updateData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_TRANSACTIONS, "uuid", uuid, transactions64);
                // save transactions64.
    }
    
    public ArrayList<String> getAccountTransactions (Player player) {
        byte[] transList64 = Base64.getDecoder().decode(SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_TRANSACTIONS, "uuid", player.getUniqueId().toString()).toString());
        String transListArray = new String(transList64);
        if (transListArray.startsWith(" /// ")) {
            transListArray = transListArray.substring(5, transListArray.length());
        }
        ArrayList<String> transList = new ArrayList<>();
        if (!transListArray.isEmpty()) {
            if (transListArray.contains(" /// ")) {
                transList.addAll(Arrays.asList(transListArray.split(" /// ")));
            } else {
                transList.add(transListArray);
            } 
        }               
        Integer listSize = transList.size() - 1;
        ArrayList<String> lista = new ArrayList<>();
        if (!transList.isEmpty()) {
            for (int i=listSize; i>= 0; --i) {
                String[] str = transList.get(i).split(" : ");
                TransactionType type = TransactionType.valueOf(str[1]);                
                if (null != type) switch (type) {
                    case DEPOSIT -> {
                        Double calcComision = Tools.get().calcPercent(Double.valueOf(str[0]), getCommission(CommissionType.DEPOSIT));
                        lista.add("&7[&a&l+&7] Deposito - &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &8(-$"+Tools.get().decimalFormat(calcComision, "###,###,###,###.##")+") &7- " + str[2]);
                    }
                    case WITHDRAW -> {
                        Double calcComision = Tools.get().calcPercent(Double.valueOf(str[0]), getCommission(CommissionType.WITHDRAW));
                        lista.add("&7[&c&l-&7] Retiro - &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &8(-$"+Tools.get().decimalFormat(calcComision, "###,###,###,###.##")+") &7- " + str[2]);
                    }
                    case TRANSFER_POSITIVE -> {
                        Object playerName = SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_USERNAME, "account", str[2]);
                        lista.add("&7[&a&l>&7] Transf de &e" + playerName + " &7- &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &7- " + str[3]);
                    }
                    case TRANSFER_NEGATIVE -> {
                        Double calcComision = Tools.get().calcPercent(Double.valueOf(str[0]), getCommission(CommissionType.TRANSFER_NEGATIVE));
                        Object playerName = SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_USERNAME, "account", str[2]);
                        lista.add("&7[&c&l<&7] Transf para &e" + playerName + " &7- &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &8(-$"+Tools.get().decimalFormat(calcComision, "###,###,###,###.##")+") &7- " + str[3]);
                    }
                    case BONO -> {
                        lista.add("&7[&a&l+&7] Cobro bono - &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &7- " + str[2]);
                    }
                    case PAYDAY -> {
                        lista.add("&7[&a&l+&7] Cobro intereses - &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &7- " + str[2]);
                    }
                }
            }
        } else {
            lista.add("&7- Ningun movimiento...");
        }
        return lista;
    }
    
    public boolean createTransferencia (Player player, String transferAccount, Double value) {
        String playerAccount = SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_ACCOUNT, "uuid", player.getUniqueId().toString()).toString();
        String transferUUID = SQL.get().getData(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_UUID, "account", transferAccount).toString();
        if (transferUUID == null) {
            return false;
        }                  
        createTransaction(player.getUniqueId().toString(), TransactionType.TRANSFER_NEGATIVE, value, transferAccount);        
        createTransaction(transferUUID, TransactionType.TRANSFER_POSITIVE, value, playerAccount);
        
        Player target = Bukkit.getPlayer(UUID.fromString(transferUUID));
        if (target != null) {
            
        }
        return true;
    }
    
    public void giveBankInterest() {
        
    }
    
    /*
        SISTEMA DE BONOS
    */
    
    public void createBono() {
        
    }
        
    /*
        SISTEMA DE GOLDS
        - Se encargará de definir el precio del gold basado en la oferta y demanda existente.
        - Los golds se podrán canjear por items de alto valor, así como también por monedas "sCoins".
        - El valor del exchange de Golds -> sCoins será de 20 Golds x 1 sCoins.
    */
      
    public void comprarOro(String uuid, int unidades) {
        Double precioOro = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);
        Double picoMaximo = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MAX_PRICE);
        Double picoMinimo = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MIN_PRICE);
        Integer goldBank = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_BANK_GOLDS);
        Integer circulatingGold = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_CIRCULATING_GOLDS);        
        Integer goldPlayer = (int)SQL.get().getData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", uuid);
                
        goldBank -= unidades;
        circulatingGold += unidades; 
        goldPlayer += unidades;
        
        for (int i = 0; i<unidades; ++i) {
            precioOro += new Random().nextDouble(2.25, 3.35);
        }
        if (precioOro > picoMaximo) {
            picoMaximo = precioOro;
        }
        if (precioOro < picoMinimo) {
            picoMinimo = precioOro;
        }
        
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE, precioOro);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MAX_PRICE, picoMaximo);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MIN_PRICE, picoMinimo);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_BANK_GOLDS, goldBank);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_CIRCULATING_GOLDS, circulatingGold);
        
        SQL.get().updateData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", uuid, goldPlayer);
    }
    
    public void venderOro(String uuid, int unidades) {
        Double precioOro = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);
        Double picoMaximo = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MAX_PRICE);
        Double picoMinimo = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MIN_PRICE);
        Integer goldBank = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_BANK_GOLDS);
        Integer circulatingGold = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_CIRCULATING_GOLDS);
        Integer goldPlayer = (int)SQL.get().getData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", uuid);
        
        circulatingGold -= unidades;
        goldPlayer -= unidades;
        goldBank += unidades;
        for (int i = 0; i<unidades; ++i) {
            precioOro -= new Random().nextDouble(1.75, 2.65);
        }
        if (precioOro > picoMaximo) {
            picoMaximo = precioOro;
        }
        if (precioOro < picoMinimo) {
            picoMinimo = precioOro;
        }
        
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE, precioOro);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MAX_PRICE, picoMaximo);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MIN_PRICE, picoMinimo);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_BANK_GOLDS, goldBank);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_CIRCULATING_GOLDS, circulatingGold);
        SQL.get().updateData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", uuid, goldPlayer);
    }

    public void producirOro(String uuid, int unidades) {
        Double precioOro = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);
        Double picoMaximo = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MAX_PRICE);
        Double picoMinimo = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MIN_PRICE);
        Integer totalGolds = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_TOTAL_GOLDS);
        Integer totalProduction = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_PRODUCED_GOLDS);
        Integer circulatingGold = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_CIRCULATING_GOLDS);
        Integer goldPlayer = (int)SQL.get().getData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", uuid);
        
        goldPlayer += unidades;
        circulatingGold += unidades;
        totalProduction += unidades;
        totalGolds += unidades;
        for (int i = 0; i<unidades; ++i) {
            precioOro -= new Random().nextDouble(2.75, 5.5);
        }
        if (precioOro > picoMaximo) {
            picoMaximo = precioOro;
        }
        if (precioOro < picoMinimo) {
            picoMinimo = precioOro;
        }
        
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE, precioOro);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MAX_PRICE, picoMaximo);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MIN_PRICE, picoMinimo);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_TOTAL_GOLDS, totalGolds);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_PRODUCED_GOLDS, totalProduction);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_CIRCULATING_GOLDS, circulatingGold);
        SQL.get().updateData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", uuid, goldPlayer);
    }
    
    public void quemarOro(String uuid, int unidades) {
        Double precioOro = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);
        Double picoMaximo = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MAX_PRICE);
        Double picoMinimo = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MIN_PRICE);
        Integer totalQuemas = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_BURNED_GOLDS);
        Integer circulatingGold = (int)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_CIRCULATING_GOLDS);
        Integer goldPlayer = (int)SQL.get().getData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", uuid);
        
        goldPlayer -= unidades;
        circulatingGold -= unidades;
        totalQuemas += unidades;
        for (int i = 0; i<unidades; ++i) {
            precioOro += new Random().nextDouble(2.75, 4.65);
        }
        if (precioOro > picoMaximo) {
            picoMaximo = precioOro;
        }
        if (precioOro < picoMinimo) {
            picoMinimo = precioOro;
        }
        
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE, precioOro);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MAX_PRICE, picoMaximo);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_MIN_PRICE, picoMinimo);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_BURNED_GOLDS, totalQuemas);
        SQL.get().updateGoldStats(SQL.UpdateType.GOLD_STATS_CIRCULATING_GOLDS, circulatingGold);
        SQL.get().updateData(SQL.DataType.PLAYER_GOLDS, SQL.UpdateType.PLAYER_GOLDS_GOLDS, "uuid", uuid, goldPlayer);
    }
    
    public void registerHistoryPrice() {
        
    }
    
}
