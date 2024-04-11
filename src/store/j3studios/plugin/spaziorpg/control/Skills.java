package store.j3studios.plugin.spaziorpg.control;

import org.bukkit.entity.Player;
import store.j3studios.plugin.spaziorpg.RPG;
import store.j3studios.plugin.spaziorpg.database.SQL;

public class Skills {
    
    private static Skills ins;
    public static Skills get() {
        if (ins == null) {
            ins = new Skills();
        }
        return ins;
    }
    
    /*
        CONTROL PRINCIPAL DE LAS HABILIDADES.
        - Este será el principal control de las skills de la modalidad.
    */
    
    public enum StatsType {
        MANA, LIFE;
    }
    
    public enum SkillType {
        STRENGTH, AGILITY,
        LIFE, DEFENSE;
    }
    
    public Double getStats (Player player, StatsType type) {
        Double startMana = RPG.get().getConfig().getDouble("config.statistics.started-stats.mana");
        Double startLife = RPG.get().getConfig().getDouble("config.statistics.started-stats.life");
        
        Integer strength = getSkillPoint(player, SkillType.STRENGTH);
        Integer agility = getSkillPoint(player, SkillType.AGILITY);
        Integer life = getSkillPoint(player, SkillType.LIFE);
        Integer defense = getSkillPoint(player, SkillType.DEFENSE);
        
        if (type == StatsType.MANA) {
            Double calculoStrength = 0.80D * strength;
            Double calculoAgility = 0.80D * agility;
            Double totalCalculo = startMana + calculoStrength + calculoAgility;
            return totalCalculo;
        } else if (type == StatsType.LIFE) {
            Double calculoLife = 0.80D * life;
            Double calculoDefense = 0.80D * defense;
            Double totalCalculo = startLife + calculoLife + calculoDefense;
            return totalCalculo;
        }
        return 20.0;
    }
    
    public Integer getSkillPoint (Player player, SkillType type) {
        return (Integer)SQL.get().getPlayer(player, SQL.DataType.PLAYER_SKILLS, SQL.UpdateType.valueOf("PLAYER_SKILLS_" + type.toString()));
    }
    
    public void addSkillPoint (Player player, SkillType type, Integer value) {
        Integer skillPoint = getSkillPoint(player, type);
        SQL.get().updatePlayer(player, SQL.DataType.PLAYER_SKILLS, SQL.UpdateType.valueOf("PLAYER_SKILLS_" + type.toString()), skillPoint+value);
    }
    
    public void setSkillPoint (Player player, SkillType type, Integer value) {
        SQL.get().updatePlayer(player, SQL.DataType.PLAYER_SKILLS, SQL.UpdateType.valueOf("PLAYER_SKILLS_" + type.toString()), value);
    }
    
}
