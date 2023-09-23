package dev.math3w.playerstash.configs;

import dev.math3w.playerstash.configs.annotations.ConfigField;
import dev.math3w.playerstash.configs.annotations.ConfigInfo;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public interface Config {

    default YamlConfiguration generateDefaultConfiguration() {
        YamlConfiguration yaml = new YamlConfiguration();

        List<Field> fields = Arrays.stream(this.getClass().getFields())
                .filter((field) -> field.isAnnotationPresent(ConfigField.class)).collect(Collectors.toList());

        fields.forEach((field) -> {
            try {
                ConfigField annotation = field.getAnnotation(ConfigField.class);

                String path = annotation.path();
                Object defaultValue = field.get(this);
                String[] comments = annotation.comments();

                if (defaultValue == null) return;
                yaml.set(path, defaultValue);

                if (comments.length == 0) return;
                yaml.setComments(path, Arrays.stream(comments).collect(Collectors.toList()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return yaml;
    }

    default void loadConfiguration(JavaPlugin plugin) {
        ConfigInfo configInfo = this.getClass().getAnnotation(ConfigInfo.class);

        File file = new File(plugin.getDataFolder(), configInfo.fileName());
        if (!file.exists()) {
            YamlConfiguration yaml = generateDefaultConfiguration();
            yaml.setComments("", Arrays.stream(configInfo.headerComments()).collect(Collectors.toList()));
            try {
                yaml.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        List<Field> fields = Arrays.stream(this.getClass().getDeclaredFields())
                .filter((field) -> field.isAnnotationPresent(ConfigField.class)).collect(Collectors.toList());

        AtomicBoolean isUpdated = new AtomicBoolean(false);
        fields.forEach((field) -> {
            try {
                ConfigField annotation = field.getAnnotation(ConfigField.class);

                field.setAccessible(true);
                String path = annotation.path();
                Object defaultValue = field.get(this);

                if (!yaml.contains(path)) {
                    if (defaultValue.getClass().isAssignableFrom(Material.class)) {
                        yaml.set(path, defaultValue.toString());
                    } else {
                        yaml.set(path, defaultValue);
                    }

                    isUpdated.set(true);
                }

                if (field.getType().isAssignableFrom(Material.class)) {
                    Material material = Material.matchMaterial(Objects.requireNonNull(yaml.getString(path), "Material cannot be null"));
                    field.set(this, material);
                    return;
                }

                field.set(this, yaml.get(path));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        if (isUpdated.get()) {
            try {
                yaml.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
