package ru.mralexeimk.minecraft.hslevels.configs;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Config extends AbstractConfig {
    private Map<Integer, List<String>> upLevelCommands;
    private Map<Integer, Integer> xpToUpByLevel;
    private int maxLevel;
    private double pvpXpLoose;
    private double pveXpLoose;

    public Config() {
        super("config.yml");
        init();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        init();
    }

    public int getXpToUp(int level) {
        if (level > this.maxLevel) return 0;
        return this.xpToUpByLevel.get(level);
    }

    private void init() {
        this.upLevelCommands = new HashMap<>();
        this.xpToUpByLevel = new HashMap<>();

        List<String> commands = getStringList("up-level-commands");
        commands.forEach(cmd -> this.upLevelCommands.getOrDefault(
                Integer.valueOf(cmd.split(", ")[0]),
                new ArrayList<>()).add(cmd.split(", ")[1]));
        ConfigurationSection cs = getConfigurationSection("levels");
        for (String key : cs.getKeys(false)) {
            this.xpToUpByLevel.put(Integer.valueOf(key), getInt("levels." + key));
            this.maxLevel = Integer.parseInt(key);
        }
        this.pvpXpLoose = Math.max(0.0, Math.min(100.0, getDouble("pvp-xp-loose")));
        this.pveXpLoose = Math.max(0.0, Math.min(100.0, getDouble("pve-xp-loose")));
    }
}
