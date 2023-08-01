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

    public void giveLevels(Player p, int levels) {
        if(p.getLevel() > config.getMaxLevel()) {
            p.setLevel(config.getMaxLevel());
            p.setExp(0f);
            return;
        }
        p.giveExpLevels(p.getLevel() + levels > config.getMaxLevel() ?
                config.getMaxLevel() - p.getLevel() : levels);
        if(p.getLevel() == config.getMaxLevel()) p.setExp(0f);
    }

    public void giveExp(Player p, int amount) {
        if(p.getLevel() >= config.getMaxLevel()) {
            p.setLevel(config.getMaxLevel());
            p.setExp(0f);
            return;
        }
        if(getExp(p) + amount > config.getXpToUp(p.getLevel() + 1)) {
            p.giveExpLevels(1);
        }
    }

    public void alertExpByVanilla(Player p, int amount) {
        int neededExp = config.getXpToUp(p.getLevel() + 1);
        int neededExpVanilla = getVanillaExpToUp(p.getLevel() + 1);
        int predAmount = amount;

        amount = Math.max(predAmount > 0 ? 1 : 0, (neededExpVanilla * amount) / neededExp);

        logService.debug("exp custom change: " + amount + "/" + neededExpVanilla + ", " +
                predAmount + "/" + neededExp);

        if (amount == 0) return;

        p.sendActionBar(Component.text(MessageConstructor
                .of(Message.COMMON_PLUS_XP)
                .replace("%xp%", String.valueOf(amount))
                .get()
        ));
    }

    public void alertExp(Player p, int amount) {
        if(amount > 0) {
            p.sendActionBar(Component.text(MessageConstructor
                    .of(Message.COMMON_PLUS_XP)
                    .replace("%xp%", String.valueOf(amount))
                    .get()
            ));
        }
        else if(amount < 0) {
            p.sendActionBar(Component.text(MessageConstructor
                    .of(Message.COMMON_MINUS_XP)
                    .replace("%xp%", String.valueOf(-amount))
                    .get()
            ));
        }
    }

    /**
     * x/neededExpVanilla = amount/neededExp => x = (neededExpVanilla * amount)/neededExp
     */
    public int toVanillaExp(Player p, int amount) {
        int neededExp = config.getXpToUp(p.getLevel() + 1);
        int neededExpVanilla = getVanillaExpToUp(p.getLevel() + 1);
        return (neededExpVanilla * amount) / neededExp;
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();

        if(p.getLevel() > config.getMaxLevel()) {
            p.setLevel(config.getMaxLevel());
            p.setExp(0f);
            return;
        }
        if(p.getLevel() == config.getMaxLevel()) {
            if(p.getExp() > 0f) p.setExp(0f);
            e.setAmount(0);
            return;
        }
        int neededExp = config.getXpToUp(p.getLevel() + 1);
        int neededExpVanilla = getVanillaExpToUp(p.getLevel() + 1);
        int predAmount = e.getAmount();

        e.setAmount(Math.max(predAmount > 0 ? 1 : 0, (neededExpVanilla * e.getAmount()) / neededExp));

        logService.debug("exp vanilla change: " + e.getAmount() + "/" + neededExpVanilla + ", " +
                predAmount + "/" + neededExp);

        if (e.getAmount() == 0) return;

        p.sendActionBar(Component.text(MessageConstructor
                .of(Message.COMMON_PLUS_XP)
                .replace("%xp%", String.valueOf(e.getAmount()))
                .get()
        ));
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

            int xp = (int) ((oldExp - p.getExp()) * getVanillaExpToUp(p.getLevel() + 1));
            p.getKiller().giveExp(xp);
            alertExpByVanilla(p.getKiller(), xp);
        }
    }
}
