package ru.mralexeimk.minecraft.hslevels.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.mralexeimk.minecraft.hslevels.HSLevels;
import ru.mralexeimk.minecraft.hslevels.services.LevelService;

public class PlaceholdersHook extends PlaceholderExpansion {
    private final LevelService levelService = HSLevels.getInstance().getLevelService();

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

        if (identifier.equals("level")) {
            return String.valueOf(levelService.getLevel(p));
        }
        if (identifier.equals("xp")) {
            return String.valueOf(levelService.getExp(p));
        }
        if (identifier.equals("xp_needed")) {
            return String.valueOf(levelService.getExpNeeded(p));
        }
        if (identifier.equals("xp_bar")) {
            return levelService.getExpBar(p);
        }

        return null;
    }
}
