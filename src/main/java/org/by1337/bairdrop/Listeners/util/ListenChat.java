package org.by1337.bairdrop.Listeners.util;

import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.by1337.bairdrop.AirDrop;
import org.by1337.bairdrop.BAirDrop;
import org.by1337.bairdrop.ConfigManager.Config;
import org.by1337.bairdrop.menu.EditAirMenu;
import org.by1337.bairdrop.util.Message;

import static org.bukkit.Bukkit.getServer;

public class ListenChat implements Listener {
    public static ListenChat ListenChat = null;
    AirDrop airDrop;
    String changeNameString;

    Player pl;

    public void unReg(){
        HandlerList.unregisterAll(this);
        ListenChat = null;
    }
    public ListenChat(AirDrop airDrop, String changeNameString, Player pl) {
        this.airDrop = airDrop;
        this.changeNameString = changeNameString;
        this.pl = pl;
        ListenChat = this;
        Message.sendMsg(pl, Config.getMessage("edit-chat"));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().equals(pl)) {
            if (e.getMessage().equalsIgnoreCase("отмена") || e.getMessage().equalsIgnoreCase("cancel")) {
                Message.sendMsg(pl, Config.getMessage("edit-canceled"));
                HandlerList.unregisterAll(this);
                e.setCancelled(true);
                return;
            }
            if (changeNameString.equalsIgnoreCase("invname")) {
                if (airDrop.isAirDropStarted()){
                    Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                    e.setCancelled(true);
                    return;
                }
                airDrop.setInvName(e.getMessage());
                airDrop.updateInvName();
                airDrop.save();
                Message.sendMsg(pl, String.format(Config.getMessage("named-changed"), e.getMessage()));
            }
            if (changeNameString.equalsIgnoreCase("airname")) {
                if (airDrop.isAirDropStarted()){
                    Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                    e.setCancelled(true);
                    return;
                }
                airDrop.setAirName(e.getMessage());
                airDrop.save();
                Message.sendMsg(pl, String.format(Config.getMessage("named-changed"), e.getMessage()));
            }
            try {
                if (changeNameString.equalsIgnoreCase("spawnmin")) {
                    if (airDrop.isAirDropStarted()){
                        Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                        e.setCancelled(true);
                        return;
                    }
                    int x = Integer.parseInt(e.getMessage());
                    airDrop.setSpawnMin(x);
                    airDrop.save();
                    Message.sendMsg(pl, String.format(Config.getMessage("min-spawn-changed"), e.getMessage()));
                }
                if (changeNameString.equalsIgnoreCase("spawnmax")) {
                    if (airDrop.isAirDropStarted()){
                        Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                        e.setCancelled(true);
                        return;
                    }
                    int x = Integer.parseInt(e.getMessage());
                    if(airDrop.getSpawnMin() >= x){
                        Message.sendMsg(pl, Config.getMessage("max-limit"));
                        e.setCancelled(true);
                        return;
                    }
                    airDrop.setSpawnMax(x);
                    airDrop.save();
                    Message.sendMsg(pl, String.format(Config.getMessage("max-spawn-changed"), e.getMessage()));
                }
                if (changeNameString.equalsIgnoreCase("airprotect")) {
                    if (airDrop.isAirDropStarted()){
                        Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                        e.setCancelled(true);
                        return;
                    }
                    int x = Integer.parseInt(e.getMessage());
                    airDrop.setAirProtect(x);
                    airDrop.save();
                    Message.sendMsg(pl, String.format(Config.getMessage("protect-changed"), e.getMessage()));
                }
                if (changeNameString.equalsIgnoreCase("timetostart") || changeNameString.equalsIgnoreCase("timetostartcons")) {
                    if (airDrop.isAirDropStarted()){
                        Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                        e.setCancelled(true);
                        return;
                    }
                    int x = Integer.parseInt(e.getMessage());
                    airDrop.setTimeToStartCons(x);
                    airDrop.setTimeToStart(x * 60);
                    airDrop.save();
                    Message.sendMsg(pl, String.format(Config.getMessage("time-to-start-changed"), e.getMessage()));
                }
                if (changeNameString.equalsIgnoreCase("searchbeforestart") || changeNameString.equalsIgnoreCase("searchbeforestartcons")) {
                    if (airDrop.isAirDropStarted()){
                        Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                        e.setCancelled(true);
                        return;
                    }
                    int x = Integer.parseInt(e.getMessage());
                    airDrop.setSearchBeforeStartCons(x);
                    airDrop.setSearchBeforeStart(x * 60);
                    airDrop.save();
                    Message.sendMsg(pl, String.format(Config.getMessage("search-before-start-changed"), e.getMessage()));
                }
                if (changeNameString.equalsIgnoreCase("timetoopen") || changeNameString.equalsIgnoreCase("timetounlockcons")) {
                    if (airDrop.isAirDropStarted()){
                        Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                        e.setCancelled(true);
                        return;
                    }
                    int x = Integer.parseInt(e.getMessage());
                    airDrop.setTimeToUnlockCons(x);
                    airDrop.setTimeToOpen(x * 60);
                    airDrop.save();
                    Message.sendMsg(pl, String.format(Config.getMessage("time-to-open-changed"), e.getMessage()));
                }
                if (changeNameString.equalsIgnoreCase("timestop") || changeNameString.equalsIgnoreCase("timetostopcons")) {
                    if (airDrop.isAirDropStarted()){
                        Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                        e.setCancelled(true);
                        return;
                    }
                    int x = Integer.parseInt(e.getMessage());
                    airDrop.setTimeToStopCons(x);
                    airDrop.setTimeStop(x * 60);
                    airDrop.save();
                    Message.sendMsg(pl, String.format(Config.getMessage("time-to-stop-changed"), e.getMessage()));
                }
                if (changeNameString.equalsIgnoreCase("minonlineplayers")) {
                    if (airDrop.isAirDropStarted()){
                        Message.sendMsg(pl, Config.getMessage("stop-event-for-edit"));
                        e.setCancelled(true);
                        return;
                    }
                    int x = Integer.parseInt(e.getMessage());
                    airDrop.setMinOnlinePlayers(x);
                    airDrop.save();
                    Message.sendMsg(pl, String.format(Config.getMessage("min-online-players-changed"), e.getMessage()));
                }
            } catch (NumberFormatException var3) {
                Message.sendMsg(pl, Config.getMessage("isn-t-number"));
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            HandlerList.unregisterAll(this);
            new BukkitRunnable() {
                @Override
                public void run() {
                    EditAirMenu em = new EditAirMenu(airDrop);
                    getServer().getPluginManager().registerEvents(em, BAirDrop.instance);
                    airDrop.setEditAirMenu(em);
                    pl.openInventory(em.getInventory());
                    cancel();
                }
            }.runTaskTimer(BAirDrop.instance, 1, 1);
        }
    }

}

