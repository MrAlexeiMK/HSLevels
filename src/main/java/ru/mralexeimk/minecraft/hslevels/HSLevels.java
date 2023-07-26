package ru.mralexeimk.minecraft.hslevels;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.mralexeimk.minecraft.hslevels.commands.LevelCommand;
import ru.mralexeimk.minecraft.hslevels.configs.Config;
import ru.mralexeimk.minecraft.hslevels.configs.Message;
import ru.mralexeimk.minecraft.hslevels.hooks.PlaceholdersHook;
import ru.mralexeimk.minecraft.hslevels.services.LevelService;
import ru.mralexeimk.minecraft.hslevels.services.LogService;

import java.util.Objects;

public final class HSLevels extends JavaPlugin {

    @Getter
    private static HSLevels instance;

    // Configs
    @Getter
    @Accessors(fluent = true)
    private Config config;
    @Getter
    private Message message;

    // Services
    @Getter
    private LogService logService;
    @Getter
    private LevelService levelService;

    // Hooks
    @Getter
    private PlaceholdersHook placeholdersHook;

    @Override
    @SneakyThrows
    public void onEnable() {
        instance = this;

        // Configs init
        this.config = new Config();
        this.message = new Message();

        // Services init
        this.logService = new LogService();
        this.levelService = new LevelService();

        // Hooks register
        if (Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
            this.placeholdersHook = new PlaceholdersHook();

        // Register commands
        Objects.requireNonNull(instance.getCommand("level")).setExecutor(new LevelCommand());

        logService.info("Plugin enabled!");
    }

    @Override
    public void onDisable() {
        logService.info("Plugin disabled!");
    }
}
