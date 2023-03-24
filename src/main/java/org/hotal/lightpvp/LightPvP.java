package org.hotal.lightpvp;

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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


public final class LightPvP extends JavaPlugin implements Listener {

        @Override
        public void onEnable() {
            getServer().getPluginManager().registerEvents(this, this);
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
