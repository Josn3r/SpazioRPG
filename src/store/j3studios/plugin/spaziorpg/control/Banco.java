package store.j3studios.plugin.spaziorpg.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
    
    private final Double DEPOSIT_COMM = 2.35D;
    private final Double WITHDRAW_COMM = 4.70D;
    private final Double TRANSFER_COMM = 3.14D;
    
    public Double getCommission(TransactionType type) {
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
    
    public enum TransactionType {
        DEPOSIT,
        WITHDRAW,
        TRANSFER_POSITIVE,
        TRANSFER_NEGATIVE,
        BONO,
        PAYDAY;
    }
    
    public void createTransaction (String uuid, TransactionType type, Double value, String transferAccount) {
        byte[] transList64 = Base64.getDecoder().decode(SQL.get().getPlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_TRANSACTIONS, SQL.UpdateType.PLAYER_STATS_UUID, uuid).toString());
        String transListArray = new String(transList64);
        ArrayList<String> transList = new ArrayList<>();
        if (transListArray.contains(" /// ")) {
            transList.addAll(Arrays.asList(transListArray.split(" /// ")));
        }        
        String timeStamp = (new SimpleDateFormat("dd/MM/yyyy (HH:mm:ss)")).format(Calendar.getInstance().getTime());
        String text = "";                
        if (null != type) switch (type) {
            case DEPOSIT -> {
                Double calcComision = Tools.get().calcPercent(value, getCommission(type));
                Double balance = 0.0D;
                Double total = balance + (value-calcComision);
                // save
                SQL.get().updatePlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, total, uuid);
                text = value + " : " + type.toString() + " : " + timeStamp;                
            }
            case WITHDRAW -> {
                Double balance = 0.0D;
                Double total = balance - value;
                // save
                SQL.get().updatePlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, total, uuid);
                text = value + " : " + type.toString() + " : " + timeStamp; 
            }
            case TRANSFER_POSITIVE -> {
                Double balance = 0.0D;
                Double total = balance + value;
                // save
                SQL.get().updatePlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, total, uuid);
                text = value + " : " + type.toString() + " : " + transferAccount + " : " + timeStamp; 
            }
            case TRANSFER_NEGATIVE -> {
                Double balance = 0.0D;
                Double total = balance - value;
                // save
                SQL.get().updatePlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, total, uuid);
                text = value + " : " + type.toString() + " : " + transferAccount + " : " + timeStamp; 
            }
            case BONO -> {
                Double balance = 0.0D;
                Double total = balance + value;
                // save
                SQL.get().updatePlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, total, uuid);
                text = value + " : " + type.toString() + " : " + timeStamp; 
            }
            case PAYDAY -> {
                Double balance = 0.0D;
                Double total = balance + value;
                // save
                SQL.get().updatePlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_BALANCE, total, uuid);
                text = value + " : " + type.toString() + " : " + timeStamp; 
            }
        }        
        transList.add(text);
        if (transList.size() > 10) {
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
        SQL.get().updatePlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_TRANSACTIONS, transactions64, uuid);
        // save transactions64.
    }
    
    public ArrayList<String> getAccountTransactions (Player player) {
        byte[] transList64 = Base64.getDecoder().decode(SQL.get().getPlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_TRANSACTIONS, SQL.UpdateType.PLAYER_STATS_UUID, player.getUniqueId().toString()).toString());
        String transListArray = new String(transList64);
        ArrayList<String> transList = new ArrayList<>();
        if (transListArray.contains(" /// ")) {
            transList.addAll(Arrays.asList(transListArray.split(" /// ")));
        }        
        Integer listSize = transList.size() - 1;
        ArrayList<String> lista = new ArrayList<>();
        if (!transList.isEmpty()) {
            for (int i=listSize; i>= 0; --i) {
                String[] str = transList.get(i).split(" /// ");
                TransactionType type = TransactionType.valueOf(str[1]);                
                if (null != type) switch (type) {
                    case DEPOSIT -> {
                        lista.add("&7[&a&l+&7] Deposito - &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &7- " + str[2]);
                    }
                    case WITHDRAW -> {
                        lista.add("&7[&c&l-&7] Retiro - &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &7- " + str[2]);
                    }
                    case TRANSFER_POSITIVE -> {
                        lista.add("&7[&a&l>&7] Transferencia de &e" + str[2] + " &7- &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &7- " + str[3]);
                    }
                    case TRANSFER_NEGATIVE -> {
                        lista.add("&7[&c&l<&7] Transferencia para &e" + str[2] + " &7- &6&l$&e" + Tools.get().decimalFormat(Double.valueOf(str[0]), "###,###,###,###.##") + " &7- " + str[3]);
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
        String playerAccount = SQL.get().getPlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_ACCOUNT, SQL.UpdateType.PLAYER_BANK_UUID, player.getUniqueId().toString()).toString();
        String transferUUID = SQL.get().getPlayer(SQL.DataType.PLAYER_BANK, SQL.UpdateType.PLAYER_BANK_UUID, SQL.UpdateType.PLAYER_BANK_ACCOUNT, transferAccount).toString();
        if (transferUUID == null) {
            return false;
        }
        
        Player transfPlayer = Bukkit.getPlayer(UUID.fromString(transferUUID));
        if (transfPlayer == null) {
            return false;
        }
                
        createTransaction(player.getUniqueId().toString(), TransactionType.TRANSFER_NEGATIVE, value, transferAccount);        
        createTransaction(transferUUID, TransactionType.TRANSFER_POSITIVE, value, playerAccount);
        
        return true;
    }
    
}
