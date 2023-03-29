package org.hotal.lightpvp;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.hotal.lightpvp.battle.BattleListener;
import org.hotal.lightpvp.game.GameManager;
import org.hotal.lightpvp.map.LeaderBoardHandler;
import org.hotal.lightpvp.tournament.impl.MatchNode;
import org.hotal.lightpvp.util.Config;

import java.io.File;
import java.io.IOException;


public class LightPvP extends JavaPlugin implements Listener {

    @Getter
    private static LightPvP plugin;

    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        reloadConfig();

        saveResource("leaderboard.png", false);

        registerListeners();

        createCommands();
    }

    @Override
    public void onDisable() {
        try {
            getConfig().save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BattleListener(), this);
        getServer().getPluginManager().registerEvents(new LeaderBoardHandler(), this);
    }

    private void createCommands() {
        new CommandAPICommand("tournament")
                .withAliases("lt")
                .withPermission(CommandPermission.OP)
                .withSubcommand(
                        new CommandAPICommand("create")
                                .executes((sender, args) -> {
                                    if (!GameManager.create()) {
                                        sender.sendMessage("§cトーナメントの構築に失敗しました");
                                    } else {
                                        sender.sendMessage("§aトーナメントを構築しました");
                                    }
                                }))
                .withSubcommand(
                        new CommandAPICommand("terminate")
                                .executes((sender, args) -> {
                                    GameManager.end();
                                    sender.sendMessage("§aトーナメントを終了しました");
                                }))
                .withSubcommand(
                        new CommandAPICommand("register")
                                .withArguments(new PlayerArgument("対象"))
                                .executes((sender, args) -> {
                                    GameManager.register((Player) args[0]);
                                    sender.sendMessage("§a" + ((Player) args[0]).getName() + "を登録しました");
                                }))
                .withSubcommand(
                        new CommandAPICommand("unregister")
                                .withArguments(new PlayerArgument("対象"))
                                .executes((sender, args) -> {
                                    GameManager.unregister((Player) args[0]);
                                    sender.sendMessage("§a" + ((Player) args[0]).getName() + "の登録を解除しました");
                                }))
                .withSubcommand(
                        new CommandAPICommand("next")
                                .executes((sender, args) -> {
                                    GameManager.nextMatch();
                                }))
                .withSubcommand(
                        new CommandAPICommand("list")
                                .executes((sender, args) -> {
                                    GameManager.getEntries().forEach(entry -> sender.sendMessage(entry.getName()));
                                }))
                .withSubcommand(
                        new CommandAPICommand("info")
                                .executes((sender, args) -> {
                                    MatchNode matchNode = GameManager.getNextMatch();
                                    if (matchNode == null) {
                                        return;
                                    }
                                    if (matchNode.getLeft().getPlayerEntry() == null || matchNode.getRight().getPlayerEntry() == null) {
                                        return;
                                    }
                                    sender.sendMessage(String.format("次の試合: %s vs %s", matchNode.getLeft().getPlayerEntry().getName(), matchNode.getRight().getPlayerEntry().getName()));
                                }))
                .withSubcommand(
                        new CommandAPICommand("clear-frames")
                                .executes((sender, args) -> {
                                    Config.clearItemFrame();
                                }))
                .withSubcommand(
                        new CommandAPICommand("set")
                                .withArguments(new MultiLiteralArgument(
                                        "lobby",
                                        "left",
                                        "right"
                                ))
                                .executesPlayer((sender, args) -> {
                                    switch ((String) args[0]) {
                                        case "lobby" -> Config.setLobbyLocation(sender.getLocation());
                                        case "left" -> Config.setLeftSpawnLocation(sender.getLocation());
                                        case "right" -> Config.setRightSpawnLocation(sender.getLocation());
                                    }
                                }))
                .register();
    }

}
