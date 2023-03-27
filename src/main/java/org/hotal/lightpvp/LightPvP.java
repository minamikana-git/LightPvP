package org.hotal.lightpvp;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.hotal.lightpvp.map.LeaderBoard;
import org.hotal.lightpvp.tournament.Tournament;
import org.hotal.lightpvp.tournament.TournamentEntry;
import org.hotal.lightpvp.tournament.TournamentNode;
import org.hotal.lightpvp.tournament.WinnerType;

import java.util.*;


public class LightPvP extends JavaPlugin implements Listener {

    @Getter
    private static LightPvP plugin;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        saveResource("leaderboard.png", false);

        getServer().getPluginManager().registerEvents(this, this);

        new CommandAPICommand("test")
                .withArguments(new IntegerArgument("num"))
                .executesPlayer((sender, args) -> {
                    final List<TournamentEntry> uuidList = new ArrayList<>();
                    for (int i = 0; i < (int) args[0]; i++) {
                        uuidList.add(new TournamentEntry(UUID.randomUUID(), "Username" + i));
                    }
                    Tournament tournament = Tournament.create(uuidList);
                    tournament.getRoot().setWinnerType(WinnerType.values()[new Random().nextInt(2)]);
                    Queue<Map.Entry<TournamentNode, String>> visitQueue = new ArrayDeque<>();
                    visitQueue.add(new AbstractMap.SimpleEntry<>(tournament.getRoot().getLeft(), "L"));
                    visitQueue.add(new AbstractMap.SimpleEntry<>(tournament.getRoot().getRight(), "R"));
                    while (!visitQueue.isEmpty()) {
                        Map.Entry<TournamentNode, String> entry = visitQueue.poll();
                        TournamentNode node = entry.getKey();
                        String nav = entry.getValue();
                        if (node.isMatch()) {
                            visitQueue.add(new AbstractMap.SimpleEntry<>(node.getLeft(), nav + "L"));
                            visitQueue.add(new AbstractMap.SimpleEntry<>(node.getRight(), nav + "R"));
                            node.setWinnerType(WinnerType.values()[new Random().nextInt(2)]);
                        }
                    }

                    List<MapRenderer> tournamentMap = LeaderBoard.createMap(tournament);
                    tournamentMap.forEach(mapRenderer -> {
                        MapView view = Bukkit.createMap(Bukkit.getWorlds().get(0));
                        view.getRenderers().clear();
                        view.addRenderer(mapRenderer);
                        ItemStack map = new ItemStack(Material.FILLED_MAP);
                        MapMeta mapMeta = (MapMeta) map.getItemMeta();
                        mapMeta.setMapView(view);
                        map.setItemMeta(mapMeta);
                        sender.getInventory().addItem(map);
                    });
                })
                .register();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("start")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("このコマンドはプレイヤーからのみ実行できます。");
                return true;
            }
            startGame((Player) sender);
            return true;
        }
        return false;
    }

    private void startGame(Player player) {
        player.sendMessage("§6§l5§e§l秒前");
        new BukkitRunnable() {
            int count = 4;

            @Override
            public void run() {
                if (count > 0) {
                    player.sendMessage("§6§l" + count + "§e§l秒前");
                    count--;
                } else {
                    player.sendMessage("§6§lスタート！");
                    startPvP(player);
                    cancel();
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    private void startPvP(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage("§cPvPモードをオンにしてください。");
            return;
        }
        player.sendMessage("§aゲームスタート！");
        giveItems(player);
    }

        private void giveItems(Player player) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                player.getInventory().clear();
                player.getInventory().addItem(
                        new ItemStack(Material.IRON_SWORD),
                        new ItemStack(Material.IRON_AXE),
                        new ItemStack(Material.BOW),
                        new ItemStack(Material.ARROW, 64),
                        new ItemStack(Material.COOKED_BEEF, 64));
                        new ItemStack(Material.SHIELD,1);
                player.getInventory().setArmorContents(
                        new ItemStack[]{
                                new ItemStack(Material.IRON_BOOTS),
                                new ItemStack(Material.IRON_LEGGINGS),
                                new ItemStack(Material.IRON_CHESTPLATE),
                                new ItemStack(Material.IRON_HELMET)});
            } else {
                player.getInventory().clear();
            }
        }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 4));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player =
                event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        player.sendMessage("§c負けてしまった...");
        Player killer = player.getKiller();
        if (killer != null) {
            killer.sendMessage("§aあなたの勝利です！");
        }
        resetPlayer(player);
        resetPlayer(killer);
    }

    private void resetPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
    }
}
