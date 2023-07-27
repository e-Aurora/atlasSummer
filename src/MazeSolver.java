import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class MazeSolver extends JFrame{

    private String[][] maze;
    private int x;
    private int y;
    private final ArrayList<Point> shortestPath;
    Point start;
    Point finish;
    int cell_size;

    MazeSolver() {
        shortestPath = new ArrayList<>();
        openFileChooser();
    }

    void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadMaze(selectedFile);
        }
    }

    void loadMaze(File file) {
        try {
            Scanner s = new Scanner(file);

            String startCoord = s.nextLine();
            String finishCoord = s.nextLine();

            String size = s.nextLine();
            String[] sSize = size.split(" ");
            x = Integer.parseInt(sSize[0]);
            y = Integer.parseInt(sSize[1]);

            maze = new String[x][y];

            for (int j = 0; j < y; j++) {
                for (int i = 0; i < x; i++) {
                    maze[i][j] = s.next();
                }
            }


            String[] startCoords = startCoord.split(" ");
            String[] finishCoords = finishCoord.split(" ");
            start = new Point(Integer.parseInt(startCoords[0]), Integer.parseInt(startCoords[1]));
            finish = new Point(Integer.parseInt(finishCoords[0]), Integer.parseInt(finishCoords[1]));

            findShortestPath(start, finish);

            if(shortestPath.isEmpty())
                System.out.println("Finish Point Is Unreachable!");

            DrawingPanel dp = new DrawingPanel();
            this.setTitle("Shortest Solve");
            this.setLayout(new BorderLayout());
            this.add(dp, BorderLayout.CENTER);
            this.setSize(800, 800);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setVisible(true);

            s.close();

        } catch (IOException e) {
            System.out.println("Error loading maze!");
        }
    }

    void findShortestPath(Point start, Point finish) {
        int[][] dirs = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        Queue<Point> queue = new LinkedList<>();
        boolean[][] visited = new boolean[x][y];
        Point[][] previous = new Point[x][y];

        queue.add(start);
        visited[start.x][start.y] = true;

        while (!queue.isEmpty()) {
            Point current = queue.poll();

            if (current.equals(finish)) {

                while (current != null) {
                    shortestPath.add(current);
                    current = previous[current.x][current.y];
                }
                Collections.reverse(shortestPath);
                return;
            }

            for (int[] dir : dirs) {
                int nextX = current.x + dir[0];
                int nextY = current.y + dir[1];

                Point next = new Point(nextX, nextY);

                if (isWithinBounds(next) && maze[nextX][nextY].equals(".") && !visited[nextX][nextY]) {
                    queue.add(next);
                    visited[nextX][nextY] = true;
                    previous[nextX][nextY] = current;
                }
            }
        }
    }


    boolean isWithinBounds(Point point) {
        return point.x >= 0 && point.x < x && point.y >= 0 && point.y < y;
    }

    public static void main(String[] args) {
        new MazeSolver();

    }

    class DrawingPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            cell_size = 50;

            for (int i = 0; i < maze.length; i++) {
                for (int j = 0; j < maze[i].length; j++) {
                    if (maze[i][j].equals("#")) {
                        g.setColor(Color.BLACK);
                    } else if (maze[i][j].equals(".")) {
                        g.setColor(Color.WHITE);
                    }

                    g.fillRect(i * cell_size, j * cell_size, cell_size, cell_size);
                }
            }

            g.setColor(Color.GREEN);
            g.fillRect(start.x * cell_size, start.y * cell_size, cell_size, cell_size);

            g.setColor(Color.RED);
            g.fillRect(finish.x * cell_size, finish.y * cell_size, cell_size, cell_size);

            if (shortestPath.isEmpty()) {

                g.setColor(Color.BLUE);
                Font largeFont = new Font("Arial", Font.BOLD, 30);
                g.setFont(largeFont);

                String message = "Finish Point Is Unreachable!";
                int messageWidth = g.getFontMetrics(largeFont).stringWidth(message);
                int messageX = (maze.length * cell_size - messageWidth) / 2;
                int messageY = (maze[0].length * cell_size + cell_size);
                g.drawString(message, messageX, messageY);
            } else {
                g.setColor(Color.YELLOW);
                for(Point p:shortestPath){
                    if(!p.equals(start) && !p.equals(finish))
                        g.fillRect(p.x * cell_size, p.y * cell_size, cell_size, cell_size);
                }
            }

        }
    }

}