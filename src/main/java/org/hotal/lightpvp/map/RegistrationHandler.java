package org.hotal.lightpvp.map;

import org.bukkit.Sound;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegistrationHandler implements Listener {

    private static final Map<UUID, RegistrationSession> sessions = new HashMap<>();

    public static void register(RegistrationSession session) {
        sessions.put(session.getPlayer(), session);
    }

    @EventHandler
    public void on(HangingBreakByEntityEvent event) {
        if (event.getEntity() instanceof GlowItemFrame itemFrame && event.getRemover() instanceof Player player) {
            if (!player.isOp()) {
                return;
            }
            if (sessions.containsKey(player.getUniqueId())) {
                final RegistrationSession session = sessions.get(player.getUniqueId());
                event.setCancelled(true);
                session.addItemFrame(itemFrame);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                if (session.isFull()) {
                    player.sendMessage("§a新しいリーダーボードを作成しました");
                    session.save();
                    sessions.remove(player.getUniqueId());
                }
            }
        }
    }

}
