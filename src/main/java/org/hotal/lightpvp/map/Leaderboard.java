package org.hotal.lightpvp.map;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.List;

public class Leaderboard {

    @Getter
    private final LeaderboardSize size;
    @Getter
    private final List<GlowItemFrame> itemFrames;

    public Leaderboard(LeaderboardSize size, List<GlowItemFrame> frames) {
        this.size = size;
        this.itemFrames = frames;
    }

    public void update() {
        List<MapRenderer> maps = LeaderboardManager.getMap(size);
        if (maps == null) return;
        for (int i = 0; i < maps.size(); i++) {
            MapView view = Bukkit.createMap(Bukkit.getWorlds().get(0));
            view.getRenderers().clear();
            view.addRenderer(maps.get(i));
            ItemStack map = new ItemStack(Material.FILLED_MAP);
            MapMeta mapMeta = (MapMeta) map.getItemMeta();
            mapMeta.setMapView(view);
            map.setItemMeta(mapMeta);
            itemFrames.get(i).setItem(map);
        }
    }

}
