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
        if (p.getLevel() > config.getMaxLevel()) {
            p.setLevel(config.getMaxLevel());
            p.setExp(0f);
            return;
        }
        p.giveExpLevels(p.getLevel() + levels > config.getMaxLevel() ?
                config.getMaxLevel() - p.getLevel() : levels);
        if (p.getLevel() == config.getMaxLevel()) p.setExp(0f);
    }

    public void giveExp(Player p, int amount) {
        if (p.getLevel() >= config.getMaxLevel()) {
            p.setLevel(config.getMaxLevel());
            p.setExp(0f);
            return;
        }

        int lvlRequiredExp = config.getXpToUp(p.getLevel() + 1);
        int exp = getExp(p);
        if (exp + amount > lvlRequiredExp) {
            p.setExp(0);
            p.setLevel(p.getLevel() + 1);
            giveExp(p, exp + amount - lvlRequiredExp);
        } else {
            p.setExp(Math.max(0, Math.min(1, p.getExp() +
                    (float) amount / config.getXpToUp(p.getLevel() + 1))));
        }
    }

    public void takeExp(Player p, int amount) {
        if (amount > getExp(p)) {
            p.setExp(0);
            if (p.getLevel() != 0) {
                p.setLevel(p.getLevel() - 1);
                p.setExp(1);
                takeExp(p, amount - getExp(p));
            }
        } else {
            p.setExp(Math.max(0, Math.min(1, p.getExp() -
                    (float) amount / config.getXpToUp(p.getLevel() + 1))));
        }
    }

    public void alertExp(Player p, int amount) {
        if (amount > 0) {
            p.sendActionBar(Component.text(MessageConstructor
                    .of(Message.COMMON_PLUS_XP)
                    .replace("%xp%", String.valueOf(amount))
                    .get()
            ));
        } else if (amount < 0) {
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
    @Deprecated
    public int toVanillaExp(Player p, int amount) {
        int neededExp = config.getXpToUp(p.getLevel() + 1);
        int neededExpVanilla = getVanillaExpToUp(p.getLevel() + 1);
        return (neededExpVanilla * amount) / neededExp;
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();
        int amount = e.getAmount();
        if (amount <= 0) return;
        e.setAmount(0);

        if (p.getLevel() > config.getMaxLevel()) {
            p.setLevel(config.getMaxLevel());
            p.setExp(0f);
            return;
        }
        if (p.getLevel() == config.getMaxLevel() && p.getExp() > 0f) {
            p.setExp(0f);
            return;
        }

        giveExp(p, amount);

        p.sendActionBar(Component.text(MessageConstructor
                .of(Message.COMMON_PLUS_XP)
                .replace("%xp%", String.valueOf(amount))
                .get()
        ));
    }

    @EventHandler
    public void onLvlUp(PlayerLevelChangeEvent e) {
        Player p = e.getPlayer();
        if (e.getNewLevel() <= e.getOldLevel()) return;

        for (int lvl = e.getOldLevel(); lvl <= e.getNewLevel(); ++lvl) {
            for (String cmdDesk : config.getUpLevelCommands().getOrDefault(lvl, new ArrayList<>())) {
                String byWho = cmdDesk.split(":")[0];
                String cmd = cmdDesk.split(":")[1]
                        .replace("%player%", p.getName())
                        .replace("%level%", String.valueOf(getLevel(p)));

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
            int oldExp = getExp(p);
            p.setExp(p.getExp() * (1f - (float) (config.getPvpXpLoose() / 100)));

            int xp = Math.max(0, oldExp - getExp(p));
            giveExp(p.getKiller(), xp);

            if (xp == 0) return;
            p.sendActionBar(Component.text(MessageConstructor
                    .of(Message.COMMON_MINUS_XP)
                    .replace("%xp%", String.valueOf(xp))
                    .get()
            ));
            MessageConstructor
                    .of(Message.COMMON_PVP_DEATH)
                    .replace("%player", p.getKiller().getName())
                    .replace("%xp%", String.valueOf(xp))
                    .send(p);

            p.getKiller().sendActionBar(Component.text(MessageConstructor
                    .of(Message.COMMON_PLUS_XP)
                    .replace("%xp%", String.valueOf(xp))
                    .get()
            ));
            MessageConstructor
                    .of(Message.COMMON_PVP_KILL)
                    .replace("%player", p.getName())
                    .replace("%xp%", String.valueOf(xp))
                    .send(p.getKiller());
        }
    }
}
