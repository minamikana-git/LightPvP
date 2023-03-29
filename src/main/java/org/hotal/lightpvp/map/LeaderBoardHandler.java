package org.hotal.lightpvp.map;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.hotal.lightpvp.game.GameManager;

public class LeaderBoardHandler implements Listener {

    @EventHandler
    public void on(HangingBreakByEntityEvent event) {
        if (event.getEntity() instanceof GlowItemFrame itemFrame && event.getRemover() instanceof Player player) {
            if (player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
                if (!player.isOp()) {
                    return;
                }
                if (GameManager.addItemFrame(itemFrame)) {
                    event.setCancelled(true);
                    player.sendMessage(Component.text("マップの場所を登録しました"));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                }
            }
        }
    }

}
