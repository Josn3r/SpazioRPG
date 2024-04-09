package store.j3studios.plugin.spaziorpg.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import store.j3studios.plugin.spaziorpg.RPG;

public class Config {
    
    private FileConfiguration config;
    private File file;
    private final RPG ins;
    
    public Config (RPG instance, String yamlName) {
        this.ins = instance;
        this.file = new File(this.ins.getDataFolder(), yamlName + ".yml");
        this.config = YamlConfiguration.loadConfiguration(this.file);
        InputStream readConfig = this.ins.getResource(yamlName + ".yml");
        YamlConfiguration setDefaults = YamlConfiguration.loadConfiguration(new InputStreamReader(readConfig));
        try {
            if (!this.file.exists()) {
                this.config.addDefaults(setDefaults);
                this.config.options().copyDefaults(true);
                this.config.save(this.file);
            } else {
                this.config.load(this.file);
            }
        } catch (IOException | InvalidConfigurationException ex) {
            // empty catch
        }
    }
    
    public void setDefault (String path, Object value) {
        if (!this.config.contains(path)) {
            this.config.set(path, value);
            this.save();
        }
    }
    
    public FileConfiguration getConfig() {
        return this.config;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public List<String> getStringList (String path) {
        return this.config.getStringList(path);
    }
    
    public String getString (String path) {
        return this.config.getString(path);
    }
    
    public Integer getInt (String path) {
        return this.config.getInt(path);
    }
    
    public Double getDouble (String path) {
        return this.config.getDouble(path);
    }
    
    public Boolean getBoolean (String path) {
        return this.config.getBoolean(path);
    }
    
    public Boolean isSet (String path) {
        return this.config.isSet(path);
    }
    
    public void set (String path, Object value) {
        this.config.set(path, value);
        this.save();
    }
    
    public FileConfigurationOptions options() {
        return this.config.options();
    }
    
    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException ex) {
            //
        }
    }
    
}
