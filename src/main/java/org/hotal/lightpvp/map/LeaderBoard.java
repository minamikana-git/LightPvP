package org.hotal.lightpvp.map;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.hotal.lightpvp.LightPvP;
import org.hotal.lightpvp.tournament.Tournament;
import org.hotal.lightpvp.tournament.TournamentNode;
import org.hotal.lightpvp.tournament.WinnerType;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class LeaderBoard {

    public static final int ROWS = 5;
    public static final int COLUMNS = 6;

    public static List<MapRenderer> createMap(Tournament tournament) {
        BufferedImage image;
        Font font;
        try {
            image = ImageIO.read(new File(LightPvP.getPlugin().getDataFolder(), "leaderboard.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(LightPvP.getPlugin().getDataFolder(), "leaderboard.ttf"));

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);

            font = new Font(font.getName(), Font.PLAIN, 20);
        } catch (IOException | FontFormatException e) {
            font = new Font("Arial", Font.BOLD, 12);
        }
        Dimension imageDimension = new Dimension(128 * COLUMNS, 128 * ROWS);
        final int perPos = (int) (imageDimension.height / (double) (tournament.getNumOfPlayers() + 1));
        final int l = (int) (0.55 * imageDimension.width / tournament.getDepth());
        final int b = (int) (0.28 * imageDimension.width);
        final int tx = (int) (0.05 * imageDimension.width);
        final int tw = (int) (0.22 * imageDimension.width);

        Graphics2D graphics2D = image.createGraphics();
        graphics2D.setFont(font);

        if (tournament.getRoot().getWinnerType() != WinnerType.NONE) {
            graphics2D.setColor(Color.RED);
            graphics2D.setStroke(new BasicStroke(5));
        } else {
            graphics2D.setColor(Color.BLACK);
            graphics2D.setStroke(new BasicStroke(2));
        }
        graphics2D.drawLine((int) (0.83 * imageDimension.width), (int) (perPos * tournament.getRoot().getPos()), (int) (0.93 * imageDimension.getWidth()), (int) (perPos * tournament.getRoot().getPos()));

        Queue<TournamentNode> queue = new ArrayDeque<>();
        queue.add(tournament.getRoot());
        while (!queue.isEmpty()) {
            TournamentNode node = queue.poll();
            final int x = (int) (0.83 * imageDimension.width - (l * node.getDepth()));

            if (node.isMatch()) {
                final TournamentNode left = node.getLeft();
                final TournamentNode right = node.getRight();
                queue.add(left);
                queue.add(right);
                switch (node.getWinnerType()) {
                    case LEFT -> {
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(5));
                    }
                    case RIGHT, NONE -> {
                        graphics2D.setColor(Color.BLACK);
                        graphics2D.setStroke(new BasicStroke(2));
                    }
                }
                graphics2D.drawLine(x, (int) (node.getPos() * perPos), x, (int) (left.getPos() * perPos));
                switch (left.getWinnerType()) {
                    case NONE -> {
                        if (node.getWinnerType() == WinnerType.LEFT && !left.isMatch()) {
                            graphics2D.setColor(Color.RED);
                            graphics2D.setStroke(new BasicStroke(5));
                        } else {
                            graphics2D.setColor(Color.BLACK);
                            graphics2D.setStroke(new BasicStroke(2));
                        }
                    }
                    case LEFT, RIGHT -> {
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(5));
                    }
                }
                graphics2D.drawLine(x, (int) (left.getPos() * perPos), left.isMatch() ? (x - l) : b, (int) (left.getPos() * perPos));
                switch (node.getWinnerType()) {
                    case RIGHT -> {
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(5));
                    }
                    case LEFT, NONE -> {
                        graphics2D.setColor(Color.BLACK);
                        graphics2D.setStroke(new BasicStroke(2));
                    }
                }
                graphics2D.drawLine(x, (int) (node.getPos() * perPos), x, (int) (right.getPos() * perPos));
                switch (right.getWinnerType()) {
                    case NONE -> {
                        if (node.getWinnerType() == WinnerType.RIGHT && !right.isMatch()) {
                            graphics2D.setColor(Color.RED);
                            graphics2D.setStroke(new BasicStroke(5));
                        } else {
                            graphics2D.setColor(Color.BLACK);
                            graphics2D.setStroke(new BasicStroke(2));
                        }
                    }
                    case LEFT, RIGHT -> {
                        graphics2D.setColor(Color.RED);
                        graphics2D.setStroke(new BasicStroke(5));
                    }
                }
                graphics2D.drawLine(x, (int) (right.getPos() * perPos), right.isMatch() ? (x - l) : b, (int) (right.getPos() * perPos));
            } else {
                graphics2D.setColor(Color.BLACK);
                FontMetrics fontMetrics = graphics2D.getFontMetrics(graphics2D.getFont());
                final String text = node.getPlayerEntry().getName();
                final int offsetX = tw - fontMetrics.stringWidth(text);
                final int offsetY = fontMetrics.getAscent() / 2;
                final int textX = tx + offsetX;
                final int textY = (int) (perPos * node.getPos() + offsetY);
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

        int chunkWidth = image.getWidth() / LeaderBoard.COLUMNS; // determines the chunk width and height
        int chunkHeight = image.getHeight() / LeaderBoard.ROWS;
        for (int x = 0; x < LeaderBoard.ROWS; x++) {
            for (int y = 0; y < LeaderBoard.COLUMNS; y++) {
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
