package org.hotal.lightpvp.map.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.hotal.lightpvp.LightPvP;
import org.hotal.lightpvp.map.IMapProvider;
import org.hotal.lightpvp.map.LeaderboardSize;
import org.hotal.lightpvp.tournament.INode;
import org.hotal.lightpvp.tournament.Tournament;
import org.hotal.lightpvp.tournament.WinnerType;
import org.hotal.lightpvp.tournament.impl.MatchNode;
import org.hotal.lightpvp.tournament.impl.PlayerNode;
import org.hotal.lightpvp.util.ImageUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class NormalMapProvider implements IMapProvider {

    @Override
    public LeaderboardSize getTargetSize() {
        return LeaderboardSize.NORMAL;
    }

    @Override
    public List<MapRenderer> provide(Tournament tournament) {
        BufferedImage image;
        Font font;
        try {
            image = ImageIO.read(new File(LightPvP.getPlugin().getDataFolder(), "leaderboard.png"));
        } catch (Exception e) {
            image = new BufferedImage(128 * getTargetSize().getColumns(), 128 * getTargetSize().getRows(), BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setBackground(Color.WHITE);
            graphics2D.fillRect(0, 0, 128 * getTargetSize().getColumns(), 128 * getTargetSize().getRows());
            graphics2D.dispose();
        }
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(LightPvP.getPlugin().getDataFolder(), "leaderboard.ttf"));

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            font = new Font(font.getName(), Font.PLAIN, 18);
        } catch (Exception e) {
            font = new Font("Arial", Font.BOLD, 18);
        }
        Dimension imageDimension = new Dimension(128 * getTargetSize().getColumns(), 128 * getTargetSize().getRows());
        final int l = (int) (0.55 * imageDimension.width / tournament.getDepth());
        final int b = (int) (0.28 * imageDimension.width);
        final int tx = (int) (0.05 * imageDimension.width);
        final int tw = (int) (0.22 * imageDimension.width);

        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setFont(font);

        Queue<INode> queue = new ArrayDeque<>();
        queue.add(tournament.getRoot());
        while (!queue.isEmpty()) {
            INode node = queue.poll();
            final int x = (int) (0.83 * imageDimension.width - (l * node.getDepth()));

            if (node instanceof MatchNode matchNode) {
                final INode left = matchNode.getLeft();
                final INode right = matchNode.getRight();
                queue.add(left);
                queue.add(right);
                switch (matchNode.getWinnerType()) {
                    case LEFT -> {
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(5));
                    }
                    case RIGHT, NONE -> {
                        graphics2D.setColor(Color.BLACK);
                        graphics2D.setStroke(new BasicStroke(2));
                    }
                }
                graphics2D.drawLine(x, (int) (node.getPos() * imageDimension.height), x, (int) (left.getPos() * imageDimension.height));
                switch (matchNode.getWinnerType()) {
                    case RIGHT -> {
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(5));
                    }
                    case LEFT, NONE -> {
                        graphics2D.setColor(Color.BLACK);
                        graphics2D.setStroke(new BasicStroke(2));
                    }
                }
                graphics2D.drawLine(x, (int) (node.getPos() * imageDimension.height), x, (int) (right.getPos() * imageDimension.height));
                switch (matchNode.getWinnerType()) {
                    case LEFT, RIGHT -> {
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(5));
                    }
                    case NONE -> {
                        graphics2D.setColor(Color.BLACK);
                        graphics2D.setStroke(new BasicStroke(2));
                    }
                }
                graphics2D.drawLine(x, (int) (node.getPos() * imageDimension.height), x + l, (int) (node.getPos() * imageDimension.height));

                if (left instanceof PlayerNode) {
                    if (matchNode.getWinnerType() == WinnerType.LEFT) {
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(5));
                    } else {
                        graphics2D.setColor(Color.BLACK);
                        graphics2D.setStroke(new BasicStroke(2));
                    }
                    graphics2D.drawLine(x, (int) (left.getPos() * imageDimension.height), b, (int) (left.getPos() * imageDimension.height));
                }
                if (right instanceof PlayerNode) {
                    if (matchNode.getWinnerType() == WinnerType.RIGHT) {
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(5));
                    } else {
                        graphics2D.setColor(Color.BLACK);
                        graphics2D.setStroke(new BasicStroke(2));
                    }
                    graphics2D.drawLine(x, (int) (right.getPos() * imageDimension.height), b, (int) (right.getPos() * imageDimension.height));
                }
            } else {
                graphics2D.setColor(Color.BLACK);
                FontMetrics fontMetrics = graphics2D.getFontMetrics(graphics2D.getFont());
                final String text = node.getPlayerEntry().getName();
                final int offsetX = tw - fontMetrics.stringWidth(text);
                final int offsetY = fontMetrics.getAscent() / 2;
                final int textX = tx + offsetX;
                final int textY = (int) (node.getPos() * imageDimension.height + offsetY);
                graphics2D.drawString(text, textX, textY);
            }
        }

        graphics2D.dispose();

        try {
            ImageIO.write(image, "png", new File("image.png"));
        } catch (Exception ignored) {
        }

        return ImageUtils.splitImage(image, getTargetSize().getRows(), getTargetSize().getColumns()).stream()
                .map(bi -> new MapRenderer() {
                    @Override
                    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
                        canvas.drawImage(0, 0, bi);
                        map.setTrackingPosition(false);
                    }
                })
                .collect(Collectors.toList());
    }

}
