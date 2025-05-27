import java.util.ArrayList;
import java.awt.*;


public class SpatialGrid {
    private ArrayList<Rectangle>[][] grid; // 2D array of lists for rectangles
    private int cellSize;

    @SuppressWarnings("unchecked")
    public SpatialGrid(int width, int height, int cellSize) {
        this.cellSize = cellSize;
        int cols = width / cellSize;
        int rows = height / cellSize;
        grid = new ArrayList[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j] = new ArrayList<>();
            }
        }
    }

    public void addRoom(Rectangle room) {
        // Determine the grid cells occupied by the room and add it to those cells
        int minCol = room.x / cellSize;
        int maxCol = (room.x + room.width) / cellSize;
        int minRow = room.y / cellSize;
        int maxRow = (room.y + room.height) / cellSize;

        for (int col = minCol; col <= maxCol; col++) {
            for (int row = minRow; row <= maxRow; row++) {
                grid[col][row].add(room);
            }
        }
    }

    public ArrayList<Rectangle> getPotentialCollisions(Rectangle room) {
        ArrayList<Rectangle> collisions = new ArrayList<>();
        int minCol = room.x / cellSize;
        int maxCol = (room.x + room.width) / cellSize;
        int minRow = room.y / cellSize;
        int maxRow = (room.y + room.height) / cellSize;

        for (int col = minCol; col <= maxCol; col++) {
            for (int row = minRow; row <= maxRow; row++) {
                collisions.addAll(grid[col][row]);
            }
        }
        return collisions;
    }
}
