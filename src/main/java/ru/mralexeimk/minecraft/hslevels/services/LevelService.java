package ru.mralexeimk.minecraft.hslevels.services;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import ru.mralexeimk.minecraft.hslevels.HSLevels;
import ru.mralexeimk.minecraft.hslevels.builders.MessageConstructor;
import ru.mralexeimk.minecraft.hslevels.configs.Config;
import ru.mralexeimk.minecraft.hslevels.configs.Message;

import java.util.ArrayList;

public class LevelService implements Listener {
    private final LogService logService = HSLevels.getInstance().getLogService();
    private final Config config = HSLevels.getInstance().config();

    public int getLevel(Player p) {
        return p.getLevel();
    }

    public int getExp(Player p) {
        return (int) (p.getExp() * config.getXpToUp(p.getLevel() + 1));
    }

    public int getExpNeeded(Player p) {
        return config.getXpToUp(p.getLevel() + 1);
    }

    public String getExpBar(Player p) {
        String res = Message.COMMON_XP_BAR;
        int pos = Math.min(res.length() - 1, (int) (p.getExp() * res.length()));
        return "§6" + res.substring(0, pos) + "§f" + res.substring(pos + 1);
    }

    public int getVanillaExpToUp(int level) {
        if (level >= 30) {
            return level * 9 - 158;
        }
        if (level >= 15) {
            return level * 5 - 38;
        }
        return level * 2 + 7;
    }

    public void addExp(Player p, int amount) {
        int neededExp = config.getXpToUp(p.getLevel() + 1);
        int neededExpVanilla = getVanillaExpToUp(p.getLevel() + 1);
        int predAmount = amount;

        amount = Math.max(predAmount > 0 ? 1 : 0, (neededExpVanilla * amount) / neededExp);

        logService.debug("exp change: " + amount + "/" + neededExpVanilla + ", " +
                predAmount + "/" + neededExp);

        if (amount == 0) return;
        String message = Message.COMMON_PLUS_XP;

        p.sendActionBar(Component.text(MessageConstructor
                .of(message)
                .replace("%xp%", String.valueOf(amount))
                .get()
        ));
    }

    /**
     * x/neededExpVanilla = e.getAmount()/neededExp => x = (neededExpVanilla * e.getAmount())/neededExp
     */
    @EventHandler
    public void onExpChange(PlayerExpChangeEvent e) {
        addExp(e.getPlayer(), e.getAmount());
    }

    @EventHandler
    public void onLvlUp(PlayerLevelChangeEvent e) {
        Player p = e.getPlayer();
        logService.debug("level change: " + e.getOldLevel() + " -> " + e.getNewLevel());
        if (e.getNewLevel() <= e.getOldLevel()) return;

        for (int lvl = e.getOldLevel(); lvl <= e.getNewLevel(); ++lvl) {
            for (String cmdDesk : config.getUpLevelCommands().getOrDefault(lvl, new ArrayList<>())) {
                String byWho = cmdDesk.split(":")[0];
                String cmd = cmdDesk.split(":")[1];

                if (byWho.equalsIgnoreCase("console")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                } else if (byWho.equalsIgnoreCase("player")) {
                    p.performCommand(cmd);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        e.setKeepLevel(true);
        e.setShouldDropExperience(false);

        if (p.getKiller() == null) {
            p.setExp(p.getExp() * (1f - (float) (config.getPveXpLoose() / 100)));
        } else {
            float oldExp = p.getExp();
            p.setExp(p.getExp() * (1f - (float) (config.getPvpXpLoose() / 100)));
            p.getKiller().giveExp((int) ((p.getExp() - oldExp) * getVanillaExpToUp(p.getLevel() + 1)));
        }
    }
}
