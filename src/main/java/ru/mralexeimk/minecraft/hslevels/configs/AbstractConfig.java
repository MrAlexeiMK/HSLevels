package ru.mralexeimk.minecraft.hslevels.configs;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.mralexeimk.minecraft.hslevels.HSLevels;
import ru.mralexeimk.minecraft.hslevels.builders.MessageConstructor;
import ru.mralexeimk.minecraft.hslevels.exceptions.ConfigValueException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;

@Data
public abstract class AbstractConfig {
    private String name;
    private File file;
    private FileConfiguration config;

    public AbstractConfig(String name) {
        init(name);
    }

    public void init(String name) {
        this.name = name;
        file = new File(HSLevels.getInstance().getDataFolder(), name);
        if(!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
        reloadConfig();
    }

    public void saveDefaultConfig() {
        if(file == null) {
            file = new File(HSLevels.getInstance().getDataFolder(), getName());
        }
        if(!file.exists()) {
            HSLevels.getInstance().saveResource(getName(), false);
        }
    }

    public void saveConfig() {
        if(config == null || file == null) return;
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept() {
        saveConfig();
        reloadConfig();
    }

    public void reloadConfig() {
        saveDefaultConfig();
        config = YamlConfiguration.loadConfiguration(file);
        try(Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(
                HSLevels.getInstance().getResource(getName())))) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        if(config == null) {
            reloadConfig();
        }
        return config;
    }

    public ConfigurationSection getConfigurationSection(String path) {
        try {
            return getConfig().getConfigurationSection(path);
        } catch (Exception ex) {
            throw new ConfigValueException(name, path);
        }
    }

    public void set(String path, Object value) {
        getConfig().set(path, value);
    }

    public Integer getInt(String path) {
        try {
            return getConfig().getInt(path);
        } catch (Exception ex) {
            throw new ConfigValueException(name, path);
        }
    }

    public Long getLong(String path) {
        try {
            return getConfig().getLong(path);
        } catch (Exception ex) {
            throw new ConfigValueException(name, path);
        }
    }

    public Boolean getBoolean(String path) {
        try {
            return getConfig().getBoolean(path);
        } catch (Exception ex) {
            throw new ConfigValueException(name, path);
        }
    }

    public Double getDouble(String path) {
        try {
            return getConfig().getDouble(path);
        } catch (Exception ex) {
            throw new ConfigValueException(name, path);
        }
    }

    public String getString(String path) {
        try {
            return MessageConstructor.of(getConfig().getString(path)).get();
        } catch (Exception ex) {
            throw new ConfigValueException(name, path);
        }
    }

    public List<String> getStringList(String path) {
        try {
            return getConfig().getStringList(path);
        } catch (Exception ex) {
            throw new ConfigValueException(name, path);
        }
    }

    public Location getLocation(String path) {
        try {
            return getConfig().getLocation(path);
        } catch (Exception ex) {
            throw new ConfigValueException(name, path);
        }
    }
}