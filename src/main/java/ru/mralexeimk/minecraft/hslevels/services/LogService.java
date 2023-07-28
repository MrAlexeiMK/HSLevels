package ru.mralexeimk.minecraft.hslevels.services;

import org.bukkit.Bukkit;
import ru.mralexeimk.minecraft.hslevels.builders.MessageConstructor;

public class LogService {

    public void info(String msg) {
        Bukkit.getLogger().info("[HSLevels] " +
                MessageConstructor.of("&a" + msg).get());
    }

    public void warning(String msg) {
        Bukkit.getLogger().warning("[HSLevels] " +
                MessageConstructor.of("&e" + msg).get());
    }

    public void severe(String msg) {
        Bukkit.getLogger().severe("[HSLevels] " +
                MessageConstructor.of("&c" + msg).get());
    }

    public void debug(String msg) {
        Bukkit.getLogger().info("[HSLevels] [DEBUG] " +
                MessageConstructor.of("&c&l" + msg).get());
    }
}
