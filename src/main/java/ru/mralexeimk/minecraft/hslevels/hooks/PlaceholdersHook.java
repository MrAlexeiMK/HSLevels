package ru.mralexeimk.minecraft.hslevels.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.mralexeimk.minecraft.hslevels.HSLevels;

public class PlaceholdersHook extends PlaceholderExpansion {

    public PlaceholdersHook() {
        register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "hslevels";
    }

    @Override
    public @NotNull String getAuthor() {
        return HSLevels.getInstance().getDescription().getAuthors().isEmpty() ?
                "MrAlexeiMK" : HSLevels.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return HSLevels.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {

        if(identifier.contains("hslevels.level")) {
            return "0";
        }
        if(identifier.contains("hslevels.xp")) {
            return "0";
        }
        if(identifier.contains("hslevels.xp_needed")) {
            return "0";
        }
        if(identifier.contains("hslevels.xp_bar")) {
            return "||||||||";
        }

        return null;
    }
}
