package dev.skyphi.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import dev.skyphi.CTFUtils;
import dev.skyphi.SootCTF;
import dev.skyphi.Models.CTFPlayer;

public class DeathListener implements Listener {
    
    @EventHandler
    public void on(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER) return;
        final Player player = (Player)event.getEntity();
        final CTFPlayer ctfp = CTFUtils.getCTFPlayer(player);
        if(ctfp == null) return;
        if(player.getHealth() - event.getFinalDamage() > 0) return;

        event.setCancelled(true);
        GameMode gm = player.getGameMode();
        player.setGameMode(GameMode.SPECTATOR);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.SMOKE_LARGE, player.getLocation().add(0, 1, 0), 100);
        player.sendTitle(ChatColor.RED+""+ChatColor.BOLD+"You died!", ChatColor.GRAY+"You will respawn shortly...", -1, -1, -1);

        // replace flag
        if(ctfp.hasFlag()) {
            ctfp.getEnemyTeam().getFlag().setType(SootCTF.FLAG_TYPE);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                player.setHealth(20);
                player.setSaturation(20);
                player.teleport(ctfp.getTeam().getFlag().getLocation());
                player.setGameMode(gm);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
            }
        }.runTaskLater(SootCTF.INSTANCE, 20*SootCTF.RESPAWN_TIMER);
    }

}
