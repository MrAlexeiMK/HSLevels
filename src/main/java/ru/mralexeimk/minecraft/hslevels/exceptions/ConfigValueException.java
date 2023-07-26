package ru.mralexeimk.minecraft.hslevels.exceptions;

public class ConfigValueException extends RuntimeException {
    public ConfigValueException(String fileName, String configPath) {
        super("Can't get value from '" + fileName + "' at '" + configPath + "'. Check that value is exist!");
    }
}
