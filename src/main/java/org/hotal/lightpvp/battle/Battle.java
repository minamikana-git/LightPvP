package org.hotal.lightpvp.battle;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.hotal.lightpvp.LightPvP;
import org.hotal.lightpvp.game.GameManager;
import org.hotal.lightpvp.tournament.TournamentEntry;
import org.hotal.lightpvp.tournament.WinnerType;
import org.hotal.lightpvp.tournament.impl.MatchNode;

import java.util.HashMap;
import java.util.Map;

public class Battle {

    @Getter
    private final MatchNode node;
    private final Map<WinnerType, TournamentEntry> playerEntryMap = new HashMap<>();
    private Player leftPlayer;
    private Player rightPlayer;
    private boolean isStarted = false;

    public Battle(MatchNode node) {
        this.node = node;
    }

    public void start() {

        playerEntryMap.put(WinnerType.LEFT, node.getLeft().getPlayerEntry());
        playerEntryMap.put(WinnerType.RIGHT, node.getRight().getPlayerEntry());

        if (node.getLeft() == null || node.getRight() == null) {
            return;
        }

        leftPlayer = Bukkit.getPlayer(node.getLeft().getPlayerEntry().getUuid());
        rightPlayer = Bukkit.getPlayer(node.getRight().getPlayerEntry().getUuid());

        if (leftPlayer == null && rightPlayer == null) {
            return;
        }
        if (leftPlayer == null) {
            win(WinnerType.RIGHT);
            return;
        }
        if (rightPlayer == null) {
            win(WinnerType.LEFT);
            return;
        }

        resetPlayer(leftPlayer);
        resetPlayer(rightPlayer);

        leftPlayer.teleport(GameManager.getLeftSpawnLocation());
        rightPlayer.teleport(GameManager.getRightSpawnLocation());

        Bukkit.broadcast(Component.text("次の試合は..."));
        Bukkit.broadcast(Component.text(String.format("§c§l%s §rvs §9§l%s", node.getLeft().getPlayerEntry().getName(), node.getRight().getPlayerEntry().getName())));

        new BukkitRunnable() {
            int count = 5;
            @Override
            public void run() {
                if (count == 0) {
                    cancel();
                    onStart();
                    return;
                }
                Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1));
                Bukkit.broadcast(Component.text("§6§l" + count + "§e§l秒前"));
                count--;
            }
        }.runTaskTimer(LightPvP.getPlugin(), 0, 20);
    }

    private void onStart() {

        initPlayer(leftPlayer);
        initPlayer(rightPlayer);

        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1));

        isStarted = true;
    }

    public void handlePlayerDeath(PlayerDeathEvent event) {
        if (!isStarted) {
            return;
        }
        playerEntryMap.forEach((key, value) -> {
            if (value.getUuid().equals(event.getEntity().getUniqueId())) {
                win(opposingSideOf(key));
            }
        });
    }

    public void handlePlayerQuit(PlayerQuitEvent event) {
        if (!isStarted) {
            return;
        }
        playerEntryMap.forEach((key, value) -> {
            if (value.getUuid().equals(event.getPlayer().getUniqueId())) {
                win(opposingSideOf(key));
            }
        });
    }

    public void win(WinnerType type) {
        resetPlayer(leftPlayer);
        resetPlayer(rightPlayer);
        leftPlayer.teleport(GameManager.getLobbyLocation());
        rightPlayer.teleport(GameManager.getLobbyLocation());
        node.setWinnerType(type);
        GameManager.updateLeaderboards();
        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1, 1));
        Bukkit.broadcast(Component.text("§a" + playerEntryMap.get(type).getName() + "の勝利！"));
        isStarted = false;
    }

    private void initPlayer(Player player) {
        player.getInventory().addItem(
                new ItemStack(Material.IRON_SWORD),
                new ItemStack(Material.IRON_AXE),
                new ItemStack(Material.BOW),
                new ItemStack(Material.ARROW, 64),
                new ItemStack(Material.COOKED_BEEF, 64),
                new ItemStack(Material.SHIELD, 1));
        player.getInventory().setArmorContents(
                new ItemStack[]{
                        new ItemStack(Material.IRON_BOOTS),
                        new ItemStack(Material.IRON_LEGGINGS),
                        new ItemStack(Material.IRON_CHESTPLATE),
                        new ItemStack(Material.IRON_HELMET)});
        player.setGameMode(GameMode.ADVENTURE);
    }

    private void resetPlayer(Player player) {
        player.getInventory().clear();
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(1.0f);
    }

    private WinnerType opposingSideOf(WinnerType type) {
        return switch (type) {
            case LEFT -> WinnerType.RIGHT;
            case RIGHT -> WinnerType.LEFT;
            case NONE -> WinnerType.NONE;
        };
    }

}
