package store.j3studios.plugin.spaziorpg.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.database.SQL;


public class BankRunnable {
    
    private final RPG core;
    public BankRunnable (RPG core) {
        this.core = core;
    }
	
    private final List<String> paydayHours = new ArrayList<>();
    private final List<String> resetHours = new ArrayList<>();
	
    public void loadRunnable() {
        String hour;
        for (int i = 0; i < 24; ++i) {
            if (i < 10) {
                hour = "0" + i;
            } else { hour = ""+i; }
            paydayHours.add(hour + ":10:00");
        }
        for (int i = 0; i < 24; ++i) {
            if (i < 10) {
                hour = "0" + i;
            } else { hour = ""+i; }
            resetHours.add(hour + ":30:00");
        }
        this.startPaydayRunnable();
    }
	
    @SuppressWarnings("deprecation")
    public void startPaydayRunnable () {
        RPG.get().getServer().getScheduler().scheduleAsyncRepeatingTask(RPG.get(), () -> {
            if (paydayHours.contains(getHour())) {
                for (Player p1 : Bukkit.getOnlinePlayers()) {
                    //MS.getBank().playerBankInterests(p1);
                }
                Date now = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");                
                String fecha = sdf.format(now);
                String hora = sdf2.format(now);
                Double price = (double)SQL.get().getGoldStats(SQL.UpdateType.GOLD_STATS_GOLD_PRICE);  
                ArrayList<String> historial = SQL.get().getGoldHistory(1);
                Double last_price = Double.valueOf(historial.get(0).split(" / ")[3]);
                Double diffPrice = price - last_price;
                Double diffPercent = (diffPrice / last_price) * 100;                
                SQL.get().saveGoldHistory(fecha, hora, price, last_price, diffPrice, diffPercent);
            }
            if (resetHours.contains(getHour())) {
                //MS.getBank().getPagosDiarios().clear();
            }
        }, 0L, 20L);
    }	

    public String getHour() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss"); 
        return format.format(now);
    }
    
    public RPG getCore() { return core; }
    
    
}
