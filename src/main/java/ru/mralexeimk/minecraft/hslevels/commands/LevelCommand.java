package ru.mralexeimk.minecraft.hslevels.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.mralexeimk.minecraft.hslevels.HSLevels;
import ru.mralexeimk.minecraft.hslevels.builders.MessageConstructor;
import ru.mralexeimk.minecraft.hslevels.configs.Config;
import ru.mralexeimk.minecraft.hslevels.configs.Message;
import ru.mralexeimk.minecraft.hslevels.services.LevelService;

public class LevelCommand implements CommandExecutor {
    private final LevelService levelService = HSLevels.getInstance().getLevelService();
    private final Config config = HSLevels.getInstance().config();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("level")) return false;
        if (args.length == 0) {
            if (!(sender instanceof Player p)) {
                MessageConstructor.of(Message.ONLY_FROM_PLAYER).send(sender);
                return false;
            }
            if (!sender.hasPermission("hslevels.level")) {
                MessageConstructor.of(Message.NO_PEX).send(sender);
                return false;
            }

            MessageConstructor
                    .of(Message.LEVEL_COMMAND)
                    .replace("%level%", String.valueOf(levelService.getLevel(p)))
                    .replace("%xp_bar%", levelService.getExpBar(p))
                    .replace("%xp%", String.valueOf(levelService.getExp(p)))
                    .replace("%xp_needed%", String.valueOf(levelService.getExpNeeded(p)))
                    .send(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help" -> printHelp(sender);
            case "add" -> {
                if (!sender.hasPermission("hslevels.add")) {
                    MessageConstructor
                            .of(Message.NO_PEX)
                            .send(sender);
                    return false;
                }

                Player p = isOk(sender, args);
                if (p == null) return false;

                try {
                    int oldLevel = p.getLevel();
                    int levels = Math.max(0, Integer.parseInt(args[2]));

                    levelService.giveLevels(p, levels);
                    int sumExp = 0;
                    for (int level = oldLevel + 1; level <= p.getLevel(); ++level) {
                        sumExp += config.getXpToUp(level);
                    }

                    levelService.alertExp(p, sumExp);

                    MessageConstructor
                            .of(Message.ADD_LEVEL_COMMAND)
                            .replace("%level%", String.valueOf(levels))
                            .replace("%player%", sender.getName())
                            .send(p);
                    MessageConstructor
                            .of(Message.SUCCESS)
                            .send(sender);
                } catch (Exception ex) {
                    printHelp(sender);
                    return false;
                }
            }
            case "set" -> {
                if (!sender.hasPermission("hslevels.set")) {
                    MessageConstructor
                            .of(Message.NO_PEX)
                            .send(sender);
                    return false;
                }

                Player p = isOk(sender, args);
                if (p == null) return false;

                try {
                    int level = Math.min(config.getMaxLevel(), Math.max(0, Integer.parseInt(args[2])));
                    p.setLevel(level);

                    MessageConstructor
                            .of(Message.SET_LEVEL_COMMAND)
                            .replace("%level%", String.valueOf(level))
                            .replace("%player%", sender.getName())
                            .send(p);
                    MessageConstructor
                            .of(Message.SUCCESS)
                            .send(sender);
                } catch (Exception ex) {
                    printHelp(sender);
                    return false;
                }
            }
            case "addxp" -> {
                if (!sender.hasPermission("hslevels.addxp")) {
                    MessageConstructor
                            .of(Message.NO_PEX)
                            .send(sender);
                    return false;
                }

                Player p = isOk(sender, args);
                if (p == null) return false;

                try {
                    if(p.getLevel() != config.getMaxLevel()) {
                        int xp = Integer.parseInt(args[2]);
                        levelService.giveExp(p, xp);
                        levelService.alertExp(p, xp);

                        MessageConstructor
                                .of(Message.ADD_XP_COMMAND)
                                .replace("%xp%", String.valueOf(xp))
                                .replace("%player%", sender.getName())
                                .send(p);
                    }
                    MessageConstructor
                            .of(Message.SUCCESS)
                            .send(sender);
                } catch (Exception ex) {
                    printHelp(sender);
                    return false;
                }
            }
            case "setxp" -> {
                if (!sender.hasPermission("hslevels.setxp")) {
                    MessageConstructor
                            .of(Message.NO_PEX)
                            .send(sender);
                    return false;
                }

                Player p = isOk(sender, args);
                if (p == null) return false;

                try {
                    float xp = Math.min(1, Math.max(0, Float.parseFloat(args[2])));
                    p.setExp(xp);

                    MessageConstructor
                            .of(Message.SET_XP_COMMAND)
                            .replace("%xp%", String.valueOf(xp))
                            .replace("%player%", sender.getName())
                            .send(p);
                    MessageConstructor
                            .of(Message.SUCCESS)
                            .send(sender);
                } catch (Exception ex) {
                    printHelp(sender);
                    return false;
                }
            }
            case "take" -> {
                if (!sender.hasPermission("hslevels.take")) {
                    MessageConstructor
                            .of(Message.NO_PEX)
                            .send(sender);
                    return false;
                }

                Player p = isOk(sender, args);
                if (p == null) return false;

                try {
                    int level = Math.min(p.getLevel(), Math.max(0, Integer.parseInt(args[2])));
                    p.giveExpLevels(-level);

                    MessageConstructor
                            .of(Message.TAKE_LEVEL_COMMAND)
                            .replace("%level%", String.valueOf(level))
                            .replace("%player%", sender.getName())
                            .send(p);
                    MessageConstructor
                            .of(Message.SUCCESS)
                            .send(sender);
                } catch (Exception ex) {
                    printHelp(sender);
                    return false;
                }
            }
            case "takexp" -> {
                if (!sender.hasPermission("hslevels.takexp")) {
                    MessageConstructor
                            .of(Message.NO_PEX)
                            .send(sender);
                    return false;
                }

                Player p = isOk(sender, args);
                if (p == null) return false;

                try {
                    int xp = Math.max(0, Integer.parseInt(args[2]));
                    levelService.takeExp(p, xp);
                    levelService.alertExp(p, -xp);

                    MessageConstructor
                            .of(Message.TAKE_XP_COMMAND)
                            .replace("%xp%", String.valueOf(xp))
                            .replace("%player%", sender.getName())
                            .send(p);
                    MessageConstructor
                            .of(Message.SUCCESS)
                            .send(sender);
                } catch (Exception ex) {
                    printHelp(sender);
                    return false;
                }
            }
            case "reload" -> {
                if (!sender.hasPermission("hslevels.reload")) {
                    MessageConstructor
                            .of(Message.NO_PEX)
                            .send(sender);
                    return false;
                }

                HSLevels.getInstance().config().reloadConfig();
                HSLevels.getInstance().getMessage().reloadConfig();
                HSLevels.getInstance().config().saveConfig();
                HSLevels.getInstance().getMessage().saveConfig();

                MessageConstructor
                        .of(Message.SUCCESS)
                        .send(sender);
            }
            default -> {
                if (!sender.hasPermission("hslevels.player")) {
                    MessageConstructor
                            .of(Message.NO_PEX)
                            .send(sender);
                    return false;
                }

                Player p = Bukkit.getPlayer(args[0]);
                if (p == null || !p.isOnline()) {
                    MessageConstructor
                            .of(Message.NOT_FOUND)
                            .send(sender);
                    return false;
                }

                MessageConstructor
                        .of(Message.LEVEL_PLAYER_COMMAND)
                        .replace("%player%", p.getName())
                        .replace("%level%", String.valueOf(levelService.getLevel(p)))
                        .replace("%xp_bar%", levelService.getExpBar(p))
                        .replace("%xp%", String.valueOf(levelService.getExp(p)))
                        .replace("%xp_needed%", String.valueOf(levelService.getExpNeeded(p)))
                        .send(sender);
            }
        }

        return true;
    }

    private void printHelp(CommandSender sender) {
        if (sender.hasPermission("hslevels.level")) MessageConstructor.of(Message.HELP_LEVEL).send(sender);
        if (sender.hasPermission("hslevels.player")) MessageConstructor.of(Message.HELP_LEVEL_PLAYER).send(sender);
        if (sender.hasPermission("hslevels.add")) MessageConstructor.of(Message.HELP_ADD_LEVEL).send(sender);
        if (sender.hasPermission("hslevels.set")) MessageConstructor.of(Message.HELP_SET_LEVEL).send(sender);
        if (sender.hasPermission("hslevels.addxp")) MessageConstructor.of(Message.HELP_ADD_XP).send(sender);
        if (sender.hasPermission("hslevels.setxp")) MessageConstructor.of(Message.HELP_SET_XP).send(sender);
        if (sender.hasPermission("hslevels.take")) MessageConstructor.of(Message.HELP_TAKE_LEVEL).send(sender);
        if (sender.hasPermission("hslevels.takexp")) MessageConstructor.of(Message.HELP_TAKE_XP).send(sender);
        if (sender.hasPermission("hslevels.reload")) MessageConstructor.of(Message.HELP_RELOAD).send(sender);
    }

    private Player isOk(CommandSender sender, String... args) {
        if (args.length < 3) {
            printHelp(sender);
            return null;
        }

        Player p = Bukkit.getPlayer(args[1]);
        if (p == null || !p.isOnline()) {
            MessageConstructor
                    .of(Message.NOT_FOUND)
                    .send(sender);
            return null;
        }

        return p;
    }
}
