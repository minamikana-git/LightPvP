package org.hotal.lightpvp.battle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hotal.lightpvp.game.GameManager;

public class BattleListener implements Listener {

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (GameManager.getCurrentBattle() != null) {
            GameManager.getCurrentBattle().handlePlayerDeath(event);
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        if (GameManager.getCurrentBattle() != null) {
            GameManager.getCurrentBattle().handlePlayerQuit(event);
        }
    }

}
