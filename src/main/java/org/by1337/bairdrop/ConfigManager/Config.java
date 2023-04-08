package org.by1337.bairdrop.ConfigManager;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.by1337.bairdrop.BAirDrop;
import org.by1337.bairdrop.menu.util.MenuItem;
import org.by1337.bairdrop.util.*;

import java.io.File;
import java.util.*;

import static org.by1337.bairdrop.BAirDrop.instance;
import static org.by1337.bairdrop.effect.LoadEffects.LoadEffect;
import static org.by1337.bairdrop.util.GeneratorLoc.LoadLocations;

public class Config {
    public static FileConfiguration listeners;
    static File fileListeners;
    public static FileConfiguration effects;
    static File fileEffects;
    public static FileConfiguration locations;
    public static File fileLocations;
    static FileConfiguration menu;
    static File fileMenu;
    static FileConfiguration schemConf;
    static File fileSchemConf;
    static FileConfiguration generatorSettings;
    static File fileGeneratorSettings;
    public static HashMap<String, File> Schematics = new HashMap<>();
    private static HashMap<File, FileConfiguration> airDrops = new HashMap<>();
    public static FileConfiguration message;
    static File fileMessage;
    public static boolean isLoaded;

    public static void LoadConfiguration() {
        fileMenu = new File(instance.getDataFolder() + File.separator + "menu.yml");
        if (!fileMenu.exists()) {
            instance.saveResource("menu.yml", true);
        }
        menu = YamlConfiguration.loadConfiguration(fileMenu);

        fileGeneratorSettings = new File(instance.getDataFolder() + File.separator + "generatorSettings.yml");
        if (!fileGeneratorSettings.exists()) {
            instance.saveResource("generatorSettings.yml", true);
        }
        generatorSettings = YamlConfiguration.loadConfiguration(fileGeneratorSettings);

        File shemDir = new File(instance.getDataFolder() + File.separator + "Schematics");
        if (!shemDir.exists()) {
            shemDir.mkdir();
            //  instance.saveResource("Schematics" + File.separator + "air1.schem", true);
        }

        fileSchemConf = new File(instance.getDataFolder() + File.separator + "Schematics" + File.separator + "schemConf.yml");
        if (!fileSchemConf.exists()) {
            instance.saveResource("Schematics" + File.separator + "schemConf.yml", true);
        }
        schemConf = YamlConfiguration.loadConfiguration(fileSchemConf);


        for (File shemFile : Arrays.stream(Objects.requireNonNull(shemDir.listFiles())).toList()) {
            if (shemFile.getAbsolutePath().equals(fileSchemConf.getAbsolutePath()))
                continue;
            Message.debug("load schematics" + shemFile.getAbsolutePath());
            Schematics.put(shemFile.getName(), shemFile);
        }


        fileEffects = new File(instance.getDataFolder() + File.separator + "effects.yml");
        if (!fileEffects.exists()) {
            instance.saveResource("effects.yml", true);
        }
        effects = YamlConfiguration.loadConfiguration(fileEffects);

        fileLocations = new File(instance.getDataFolder() + File.separator + "locations.yml");
        if (!fileLocations.exists()) {
            instance.saveResource("locations.yml", true);
        }
        locations = YamlConfiguration.loadConfiguration(fileLocations);

        fileListeners = new File(instance.getDataFolder() + File.separator + "listeners.yml");
        if (!fileListeners.exists()) {
            instance.saveResource("listeners.yml", true);
        }
        listeners = YamlConfiguration.loadConfiguration(fileListeners);


        fileMessage = new File(instance.getDataFolder() + File.separator + "message.yml");
        if (!fileMessage.exists()) {
            instance.saveResource("message.yml", true);
        }
        message = YamlConfiguration.loadConfiguration(fileMessage);


        File dir = new File(instance.getDataFolder() + File.separator + "airdrops");
        if (!dir.exists()) {
            dir.mkdir();
            instance.saveResource("airdrops" + File.separator + "default.yml", true);
        }


        for (File airFile : Arrays.stream(Objects.requireNonNull(dir.listFiles())).toList()) {
            Message.debug("load " + airFile.getAbsolutePath());
            FileConfiguration fc = YamlConfiguration.loadConfiguration(airFile);
            airDrops.put(airFile, fc);
        }
        BAirDrop.internalListeners.clear();
        LoadListeners();
        LoadMenu();
        LoadLocations();
        LoadEffect(effects);
        RegionManager.LoadFlags();
        if(instance.getConfig().getBoolean("custom-crafts.enable"))
            LoadCustomCraft();
        isLoaded = true;

    }

    public static void LoadMenu() {
        MenuItem.menuItemHashMap.clear();
        for (String tag : menu.getConfigurationSection("main").getKeys(false)) {
            try {
                String name = Objects.requireNonNull(menu.getString("main." + tag + ".name"));
                String material = Objects.requireNonNull(menu.getString("main." + tag + ".material"));
                int slot = menu.getInt("main." + tag + ".slot");
                List<String> lore = getListOrEmpty("main." + tag + ".lore", menu);
                List<String> LEFT = getListOrEmpty("main." + tag + ".commands." + "LEFT-CLICK", menu);
                List<String> SHIFT_LEFT = getListOrEmpty("main." + tag + ".commands." + "SHIFT_LEFT-CLICK", menu);
                List<String> RIGHT = getListOrEmpty("main." + tag + ".commands." + "RIGHT-CLICK", menu);
                List<String> SHIFT_RIGHT = getListOrEmpty("main." + tag + ".commands." + "SHIFT_RIGHT-CLICK", menu);
                List<String> MIDDLE = getListOrEmpty("main." + tag + ".commands." + "MIDDLE-CLICK", menu);
                List<String> DROP = getListOrEmpty("main." + tag + ".commands." + "DROP-CLICK", menu);
                new MenuItem(tag, name, lore, slot, LEFT, SHIFT_LEFT, RIGHT, SHIFT_RIGHT, MIDDLE, DROP, material);
            } catch (NullPointerException e) {
                Message.error(String.format(Config.getMessage("menu-error"), tag));

            } catch (IllegalArgumentException e) {
                Message.error(String.format(Config.getMessage("menu-mat-error"), tag));
            }
        }

    }

    private static void LoadCustomCraft() {
        if (instance.getConfig().getConfigurationSection("custom-crafts.crafts") == null) {
              Message.error("Список крафтов пуст!");
            return;
        }
        main:
        for (String key : instance.getConfig().getConfigurationSection("custom-crafts.crafts").getKeys(false)) {
            try {
                String summoner = Objects.requireNonNull(instance.getConfig().getString(String.format("custom-crafts.crafts.%s.summoner", key)));
                if(!BAirDrop.summoner.getItems().containsKey(summoner)){
                    Message.error(summoner + " Неизвестный предмет!");
                    Message.error(String.format("Крафт %s был пропущен", key));
                    continue ;
                }
                String top = Objects.requireNonNull(instance.getConfig().getString(String.format("custom-crafts.crafts.%s.slots.top", key)));
                String middle = Objects.requireNonNull(instance.getConfig().getString(String.format("custom-crafts.crafts.%s.slots.middle", key)));
                String bottom = Objects.requireNonNull(instance.getConfig().getString(String.format("custom-crafts.crafts.%s.slots.bottom", key)));
                List<String> call = instance.getConfig().getStringList(String.format("custom-crafts.crafts.%s.call", key));

                if (instance.getConfig().getConfigurationSection(String.format("custom-crafts.crafts.%s.ingredients", key)) == null) {
                    Message.error("Список ингридиентов для крафта пуст!");
                    continue;
                }
                HashMap<Character, Material> ingredients = new HashMap<>();
                for (String ingred : instance.getConfig().getConfigurationSection(String.format("custom-crafts.crafts.%s.ingredients", key)).getKeys(false)) {
                    if (ingred.length() > 1) {
                        Message.error(ingred + " Может состоять только из одного символа!");
                        Message.error(String.format("Крафт %s был пропущен", key));
                        continue main;
                    }
                    try {
                        ingredients.put(ingred.charAt(0), Material.valueOf(Objects.requireNonNull(instance.getConfig().getString(String.format("custom-crafts.crafts.%s.ingredients.%s", key, ingred)))));
                    } catch (IllegalArgumentException e) {
                        Message.error(instance.getConfig().getString(String.format("custom-crafts.crafts.%s.ingredients.%s", key, ingred)) + " Неизвестный материал!");
                        Message.error(String.format("Крафт %s был пропущен", key));
                        continue main;
                    }
                }
                BAirDrop.crafts.put(key, new CustomCraft(key, summoner, call, ingredients, top, middle, bottom));
            }catch (NullPointerException e){
                Message.error(String.format("Ошибка загрузки крафта! %s", key));
            }catch (Exception e){
                Message.error("Произошла ошибка при загрузке крафта! " + key);
            }
        }
    }

    private static void LoadListeners() {
        if (listeners.getConfigurationSection("listeners") == null) {
            Message.error("Список слушателей пуст!");
            return;
        }
        for (String key : listeners.getConfigurationSection("listeners").getKeys(false)) {
            try {
                Events event = Events.valueOf(Objects.requireNonNull(listeners.getString("listeners." + key + ".event")));
                List<String> commands = listeners.getStringList("listeners." + key + ".commands");
                List<String> denyCommands = listeners.getStringList("listeners." + key + ".deny-commands");
                String description = Objects.requireNonNull(listeners.getString("listeners." + key + ".description"));
                HashMap<String, HashMap<String, String>> requirement = new HashMap<>();
                if (listeners.getConfigurationSection("listeners." + key + ".requirement") != null) {
                    for (String checkId : listeners.getConfigurationSection("listeners." + key + ".requirement").getKeys(false)) {
                        String checkType = Objects.requireNonNull(listeners.getString("listeners." + key + ".requirement." + checkId + ".type"));
                        String input = Objects.requireNonNull(listeners.getString("listeners." + key + ".requirement." + checkId + ".input"));
                        HashMap<String, String> check = new HashMap<>();
                        check.put(checkType, input);
                        requirement.put(checkId, check);
                    }
                }
                BAirDrop.internalListeners.put(key, new InternalListener(event, commands.toArray(new String[0]), requirement, description, denyCommands.toArray(new String[0])));
            } catch (NullPointerException e) {
                Message.error(Config.getMessage("listeners-error"));
            } catch (IllegalArgumentException e) {
                Message.error("Неизвестный ивент! В слушателе " + key);
            }
        }
    }

    public static String getMessage(String path) {
        if (message.getString(path) == null) {
            Message.error(path + " <- this path does not exist!");
            return Message.messageBuilder("&cСообщения с таким пути нет!, There are no messages with this path!");
        }
        return Message.messageBuilder(message.getString(path));
    }

    public static List<String> getList(String path) {
        if (message.getStringList(path).isEmpty()) {
            Message.error(path + " <- this path does not exist!");
            return Collections.singletonList(Message.messageBuilder("&cСообщения с таким пути нет!, There are no messages with this path!"));
        }
        return new ArrayList<>(message.getStringList(path));
    }

    public static List<String> getListOrEmpty(String path, FileConfiguration file) {
        if (file.getStringList(path).isEmpty()) {
            return new ArrayList<String>();
        }
        return new ArrayList<>(file.getStringList(path));
    }

    public static HashMap<File, FileConfiguration> getAirDrops() {
        return airDrops;
    }

    public static FileConfiguration getSchemConf() {
        return schemConf;
    }


    public static FileConfiguration getGeneratorSettings() {
        return generatorSettings;
    }
}
