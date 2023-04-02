package org.hotal.lightpvp.map;

import lombok.Getter;
import org.bukkit.entity.GlowItemFrame;
import org.hotal.lightpvp.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegistrationSession {

    @Getter
    private final UUID player;
    private final LeaderboardSize size;
    private final List<GlowItemFrame> itemFrames = new ArrayList<>();

    public RegistrationSession(UUID player, LeaderboardSize size) {
        this.player = player;
        this.size = size;
    }

    public void addItemFrame(GlowItemFrame itemFrame) {
        itemFrames.add(itemFrame);
    }

    public boolean isFull() {
        return itemFrames.size() >= size.getColumns() * size.getRows();
    }

    public void save() {
        Config.saveLeaderboard(new Leaderboard(size, itemFrames));
    }

}
