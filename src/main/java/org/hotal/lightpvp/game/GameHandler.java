package org.hotal.lightpvp.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.hotal.lightpvp.battle.Battle;

import java.util.UUID;

public class GameHandler implements Listener {

    @EventHandler
    public void on(FoodLevelChangeEvent event) {
        Battle battle = GameManager.getCurrentBattle();
        if (battle == null) return;
        if (!battle.isStarted()) {
            return;
        }
        final UUID targetUUID = battle.getNode().getLeft().getPlayerEntry().getUuid();
        final UUID left = battle.getNode().getLeft().getPlayerEntry().getUuid();
        final UUID right = battle.getNode().getRight().getPlayerEntry().getUuid();
        if (!targetUUID.equals(left) && !targetUUID.equals(right)) {
            event.setFoodLevel(20);
        }
    }

}
