package ru.mralexeimk.minecraft.hslevels.configs;

import java.util.List;

public class Message extends AbstractConfig {
    public static String COMMON_PLUS_XP;
    public static String COMMON_MINUS_XP;
    public static String COMMON_XP_BAR;
    public static String HELP_LEVEL;
    public static String HELP_LEVEL_PLAYER;
    public static String HELP_ADD_LEVEL;
    public static String HELP_SET_LEVEL;
    public static String HELP_ADD_XP;
    public static String HELP_SET_XP;
    public static String HELP_TAKE_LEVEL;
    public static String HELP_TAKE_XP;
    public static String HELP_RELOAD;
    public static List<String> LEVEL_COMMAND;
    public static List<String> LEVEL_PLAYER_COMMAND;
    public static String ADD_LEVEL_COMMAND;
    public static String SET_LEVEL_COMMAND;
    public static String ADD_XP_COMMAND;
    public static String SET_XP_COMMAND;
    public static String TAKE_LEVEL_COMMAND;
    public static String TAKE_XP_COMMAND;
    public static String SUCCESS;
    public static String NO_PEX;
    public static String NOT_FOUND;
    public static String ONLY_FROM_PLAYER;

    public Message() {
        super("messages.yml");
        init();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        init();
    }

    public void init() {
        COMMON_PLUS_XP = getString("common.plus_xp");
        COMMON_MINUS_XP = getString("common.minus_xp");
        COMMON_XP_BAR = getString("common.xp_bar");
        HELP_LEVEL = getString("help.level");
        HELP_LEVEL_PLAYER = getString("help.level-player");
        HELP_ADD_LEVEL = getString("help.add-level");
        HELP_SET_LEVEL = getString("help.set-level");
        HELP_ADD_XP = getString("help.add-xp");
        HELP_SET_XP = getString("help.set-xp");
        HELP_TAKE_LEVEL = getString("help.take-level");
        HELP_TAKE_XP = getString("help.take-xp");
        HELP_RELOAD = getString("help.reload");
        LEVEL_COMMAND = getStringList("commands.level");
        LEVEL_PLAYER_COMMAND = getStringList("commands.level-player");
        ADD_LEVEL_COMMAND = getString("commands.add-level");
        SET_LEVEL_COMMAND = getString("commands.set-level");
        ADD_XP_COMMAND = getString("commands.add-xp");
        SET_XP_COMMAND = getString("commands.set-xp");
        TAKE_LEVEL_COMMAND = getString("commands.take-level");
        TAKE_XP_COMMAND = getString("commands.take-xp");
        SUCCESS = getString("commands.success");
        NO_PEX = getString("commands.no-pex");
        NOT_FOUND = getString("commands.not-found");
        ONLY_FROM_PLAYER = getString("commands.only-from-player");
    }
}
