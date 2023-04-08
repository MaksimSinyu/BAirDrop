package org.by1337.bairdrop.effect.effectImpl;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.by1337.bairdrop.effect.EffectType;
import org.by1337.bairdrop.effect.IEffect;
import org.by1337.bairdrop.effect.util.RGBHelper;

import java.util.ArrayList;
import java.util.List;
import org.by1337.bairdrop.AirDrop;
import org.by1337.bairdrop.util.Message;
import org.by1337.bairdrop.BAirDrop;
public class FireworkEffect implements IEffect {
    int ticks = -1;
    int timeUpdate;
    AirDrop airDrop;
    boolean active = true;
    double startHeight;
    double endHeight;
    double stepHeight;
    FileConfiguration cs;
    Location loc;
    String name;
    List<Color> colors = new ArrayList<>();
    Vector offsets;

    public FireworkEffect(FileConfiguration cs, String name) throws NullPointerException, IllegalArgumentException {
        this.cs = cs;
        ticks = cs.getInt(String.format("effects.%s.ticks", name));
        timeUpdate = cs.getInt(String.format("effects.%s.timeUpdate", name));
        for(String pr : cs.getStringList(String.format("effects.%s.colors", name))){
            colors.add(RGBHelper.getColorWithRgb(pr));
        }
        this.name = name;

        offsets = new Vector(
                cs.getDouble(String.format("effects.%s.offset-x", name)),
                cs.getDouble(String.format("effects.%s.offset-y", name)),
                cs.getDouble(String.format("effects.%s.offset-z", name)));

        startHeight = cs.getDouble(String.format("effects.%s.start-height", name));
        endHeight = cs.getDouble(String.format("effects.%s.end-height", name));
        stepHeight = cs.getDouble(String.format("effects.%s.step-height", name));


    }

    @Override
    public void Start(AirDrop airDrop) {
        this.airDrop = airDrop;
        if (airDrop.getAirLoc() == null) {
            if (airDrop.getFutureLocation() == null) {
                Message.error("Локация для аирдропа ещё не сгенерирована");
                Message.error("Эффект не может появится");
                Message.error("аирдроп " + airDrop.getAirId());
                return;
            } else loc = airDrop.getFutureLocation().clone();
        } else loc = airDrop.getAirLoc().clone();
        run();

    }

    @Override
    public void End() {
        active = false;
    }

    @Override
    public void setLifetime(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location location = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
                location.add(offsets);
             //   y += 1;
                Firework firework = (Firework) location.getWorld().spawnEntity(location.add(0.0D, startHeight, 0.0D), EntityType.FIREWORK);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                fireworkMeta.addEffect(org.bukkit.FireworkEffect.builder()
                        .withColor(colors).flicker(true)
                        .with(org.bukkit.FireworkEffect.Type.BALL_LARGE)
                        .build());
                firework.setFireworkMeta(fireworkMeta);
                fireworkMeta.setPower(20);
                firework.detonate();

                startHeight += stepHeight;
                if (startHeight >= endHeight)
                    cancel();
                if (!isActive())
                    cancel();
                if (ticks != -1) {
                    if ((ticks - timeUpdate) > 0) {
                        ticks -= timeUpdate;
                    } else {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(BAirDrop.instance, timeUpdate, timeUpdate);





    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EffectType getType() {
        return EffectType.FIREWORK;
    }

    @Override
    public IEffect clone() {
        return new FireworkEffect(cs, name);

    }
}
