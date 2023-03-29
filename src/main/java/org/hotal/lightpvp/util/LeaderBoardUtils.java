package org.hotal.lightpvp.util;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.hotal.lightpvp.LightPvP;
import org.hotal.lightpvp.tournament.INode;
import org.hotal.lightpvp.tournament.Tournament;
import org.hotal.lightpvp.tournament.WinnerType;
import org.hotal.lightpvp.tournament.impl.MatchNode;
import org.hotal.lightpvp.tournament.impl.PlayerNode;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class LeaderBoardUtils {

    public static final int ROWS = 5;
    public static final int COLUMNS = 6;

    public static List<MapRenderer> createMap(Tournament tournament) {
        BufferedImage image;
        Font font;
        try {
            image = ImageIO.read(new File(LightPvP.getPlugin().getDataFolder(), "leaderboard.png"));
        } catch (Exception e) {
            image = new BufferedImage(128 * COLUMNS, 128 * ROWS, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setBackground(Color.WHITE);
            graphics2D.fillRect(0, 0, 128 * COLUMNS, 128 * ROWS);
            graphics2D.dispose();
        }
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(LightPvP.getPlugin().getDataFolder(), "leaderboard.ttf"));

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            font = new Font(font.getName(), Font.PLAIN, 20);
        } catch (Exception e) {
            font = new Font("Arial", Font.BOLD, 12);
        }
        Dimension imageDimension = new Dimension(128 * COLUMNS, 128 * ROWS);
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

        return splitImage(image).stream()
                .map(bi -> new MapRenderer() {
                    @Override
                    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
                        canvas.drawImage(0, 0, bi);
                        map.setTrackingPosition(false);
                    }
                })
                .collect(Collectors.toList());
    }

    private static List<BufferedImage> splitImage(BufferedImage image) {

        final List<BufferedImage> response = new ArrayList<>();

        int chunkWidth = image.getWidth() / LeaderBoardUtils.COLUMNS; // determines the chunk width and height
        int chunkHeight = image.getHeight() / LeaderBoardUtils.ROWS;
        for (int x = 0; x < LeaderBoardUtils.ROWS; x++) {
            for (int y = 0; y < LeaderBoardUtils.COLUMNS; y++) {
                int imageType = image.getType();
                if (imageType == 0) {
                    imageType = 5;
                }

                BufferedImage splitImage = new BufferedImage(chunkWidth, chunkHeight, imageType);

                Graphics2D gr = splitImage.createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x,
                        chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();

                response.add(splitImage);
            }
        }

        return response;

    }

}
