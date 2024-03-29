package dev.skyphi.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.skyphi.SootCTF;
import dev.skyphi.Models.CTFConfig;
import dev.skyphi.Models.Pickups.ItemSpawner;

public class SetupListener implements Listener {
    
    public enum SetupType {
        FLAG_1,
        FLAG_2,
        SPAWNER
    };

    private Player player;
    private SetupType setupType;

    public SetupListener(Player player, SetupType setupType) {
        this.player = player;
        this.setupType = setupType;

        SootCTF.INSTANCE.getServer().getPluginManager().registerEvents(this, SootCTF.INSTANCE);
    }

    public Player getPlayer() { return player; }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(block == null || setupType == null || player == null || !player.equals(event.getPlayer())) return;
        event.setCancelled(true);

        if(setupType == SetupType.SPAWNER) {
            // set item spawner
            SootCTF.PICKUP_MANAGER.addSpawner(new ItemSpawner(block));
            player.sendMessage(ChatColor.AQUA + "Item spawner set!");
        }else {
            Location newFlagLoc = block.getLocation();

            // set flag block on appropriate team
            (setupType == SetupType.FLAG_1 ? SootCTF.TEAM1 : SootCTF.TEAM2).setFlag(block);

            player.sendMessage(ChatColor.AQUA + "Flag set for team "
                            + (setupType == SetupType.FLAG_1 ? "one" : "two")
                            + "!");
            
            // save to config
            if(setupType == SetupType.FLAG_1) CTFConfig.FLAG_ONE = newFlagLoc;
            else if(setupType == SetupType.FLAG_2) CTFConfig.FLAG_TWO = newFlagLoc;
            CTFConfig.save();
        }

        HandlerList.unregisterAll(this);
    }

}
