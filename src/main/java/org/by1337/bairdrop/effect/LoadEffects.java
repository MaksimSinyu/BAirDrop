package org.by1337.bairdrop.effect;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

import org.by1337.bairdrop.ConfigManager.Config;
import org.by1337.bairdrop.effect.effectImpl.*;
import org.by1337.bairdrop.util.Message;

public class LoadEffects {
    /**
     * загрузка эффектов в мапу откуда они будут клонироваться
     */
    public static void LoadEffect(FileConfiguration file){
        if(file.getConfigurationSection("effects") == null)
            return;
        for(String key : file.getConfigurationSection("effects").getKeys(false)) {
            try {
                @Deprecated //todo сделал так из-за поддержки старых конфигураций
                String effType = file.getString(String.format("effects.%s.type", key)).replace("-", "_").toLowerCase();

                EffectType effectType = EffectType.getByKey(NamespacedKey.fromString(effType));

                if (effectType == EffectType.CIRCLE){ EffectFactory.EffectList.put(key, new circle(file, key)); continue;}
                if (effectType == EffectType.HELIX){ EffectFactory.EffectList.put(key, new Helix(file, key)); continue;}
                if (effectType == EffectType.EXPANDING_CIRCLE){ EffectFactory.EffectList.put(key, new ExpandingCircle(file, key)); continue;}
                if (effectType == EffectType.RANDOM_PARTICLE){ EffectFactory.EffectList.put(key, new RandomParticle(file, key)); continue;}
                if (effectType == EffectType.SPAWN_GUARD){ EffectFactory.EffectList.put(key, new Guard(file, key)); continue;}
                if (effectType == EffectType.FIREWORK){ EffectFactory.EffectList.put(key, new FireworkEffect(file, key)); continue;}
                if (effectType == EffectType.TORUS){ EffectFactory.EffectList.put(key, new Torus(file, key)); continue;}
                if (effectType == EffectType.WRITHING_HELIX){ EffectFactory.EffectList.put(key, new WrithingHelix(file, key)); continue;}

            } catch (NullPointerException e) {
                Message.error(String.format(Config.getMessage("effect-error"), key));
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                Message.error(String.format(Config.getMessage("effect-error-unknown-type"), key));
            }
        }
    }
}
