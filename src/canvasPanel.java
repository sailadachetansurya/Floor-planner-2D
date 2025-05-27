import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class canvasPanel extends JPanel {
    protected ArrayList<Room> rooms;
    public static Room selected;
    public Room selectedRoom;
    public Furniture selectedFurniture;
    public static Furniture selectedFurn;
    private Point mouseOffset;
    private boolean collisionState = false;
    private final int gridSize = 20;
    private boolean snapToGridEnabled = true;
    public static int canvasWidth = 1162;
    public static int canvasHeight = 830;

    canvasPanel() {
        this.setBackground(Color.gray);
        this.setLayout(null);
        rooms = new ArrayList<>();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }

            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });
    }

    public void addRoom(int roomType) {
        Room newRoomInstance = new Room(roomType);

        System.out.println("Adding room of type: " + roomType);
        int width = newRoomInstance.setwidth();
        if (width == -1) {
            System.out.println("Width is -1, returning.");
            return;
        }
        int height = newRoomInstance.setheight();
        if (height == -1) {
            System.out.println("Height is -1, returning.");
            return;
        }

        int x = 10;
        int y = 10;

        Room newRoom = new Room(x, y, width, height, roomType);

        boolean overlaps;
        do {
            overlaps = false;
            for (Room existingRoom : rooms) {
                if (newRoom.intersects(existingRoom)) {
                    overlaps = true;
                    x += width;
                    newRoom.setLocation(x, y);
                    if (newRoom.getX() + newRoom.getWidth() > canvasWidth) {
                        x = 10;
                        y += height;
                        newRoom.setLocation(x, y);
                    }
                    System.out.println("Overlap detected. Adjusting position.");
                    break;
                }
            }
        } while (overlaps);

        System.out.println("Room added at X: " + newRoom.getX() + ", Y: " + newRoom.getY());
        rooms.add(newRoom);
        this.add(newRoom);

        checkCollisions();
        repaint();
    }

    public void deleteRoom() {
        if (selectedRoom != null) {
            System.out.println("Deleting room: " + selectedRoom);
            this.remove(selectedRoom); // Remove the room from the panel
            rooms.remove(selectedRoom); // Remove the room from the list
            selectedRoom = null; // Clear selection
            this.revalidate(); // Make sure the layout is updated
            this.repaint(); // Redraw the canvas
        } else {
            System.out.println("No room selected to delete.");
        }
    }

    public void addFurnishing(String furnitureType) {
        try {
            if (selectedRoom != null) {
                selectedRoom.addFurnishing(furnitureType);
            } else {
                new JOptionPane("WARNING: No Room Selected", JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            new JOptionPane("WARNING: No Room Selected", JOptionPane.WARNING_MESSAGE);
        }

    }

    Point initialRoomPosition;

   
    private void handleMouseClicked(MouseEvent e) {
        // First check for furniture clicks
        boolean furnitureClicked = false;
        for (Room room : rooms) {
            for (Component comp : room.getComponents()) {
                if (comp instanceof Furniture) {
                    Furniture furniture = (Furniture) comp;
                    Point relativePoint = SwingUtilities.convertPoint(this, e.getPoint(), room);
                    if (furniture.getBounds().contains(relativePoint)) {
                        selectedFurniture = furniture;
                        selectedFurn = selectedFurniture;
                        selectedRoom = room;
                        selected = selectedRoom;
                        furnitureClicked = true;
                        break;
                    }
                }
            }
            if (furnitureClicked) break;
        }

        // If no furniture was clicked, check for room clicks
        if (!furnitureClicked) {
            selectedFurniture = null;
            selectedFurn = null;
            for (Room room : rooms) {
                if (room.getBounds().contains(e.getPoint())) {
                    selectedRoom = room;
                    selected = selectedRoom;
                    mouseOffset = new Point(e.getX() - room.getX(), e.getY() - room.getY());
                }
            }
        }
    }

    private void handleMousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            // Only deselect the room if no room was clicked (i.e., left-click on empty
            // space)
            boolean roomClicked = false;
            for (Room room : rooms) {
                if (room.getBounds().contains(e.getPoint())) {
                    roomClicked = true;
                    break;
                }
            }
            if (!roomClicked) {
                selectedRoom = null; // Deselect if no room was clicked
                selected = selectedRoom;
            }
        }

        // Iterate over rooms to check if any room was clicked
        for (Room room : rooms) {
            // Check if the click is within the bounds of the room
            if (room.getBounds().contains(e.getPoint())) {
                selectedRoom = room;
                selected = selectedRoom;
                mouseOffset = new Point(e.getX() - room.getX(), e.getY() - room.getY());
                initialRoomPosition = new Point(selectedRoom.getX(), selectedRoom.getY()); // Store initial position
                break;
            }
        }

        // If a room is selected and right-click is detected, show the popup menu
        if (SwingUtilities.isRightMouseButton(e) && selectedRoom != null) {
            showPopupMenu(e); // Show context menu
        } else if (SwingUtilities.isRightMouseButton(e) && selectedRoom == null) {
            System.out.println("No room selected.");
        }

        repaint();
    }

    private void handleMouseDragged(MouseEvent e) {
        if (selectedRoom != null) {
            int newX = e.getX() - mouseOffset.x;
            int newY = e.getY() - mouseOffset.y;

            // Apply boundary restrictions (keeping the room within the canvas)
            newX = Math.max(0, Math.min(newX, canvasWidth - selectedRoom.getWidth()));
            newY = Math.max(0, Math.min(newY, canvasHeight - selectedRoom.getHeight()));

            // If snap to grid is enabled, apply grid snapping
            if (snapToGridEnabled) {
                newX = Math.round(newX / (float) gridSize) * gridSize;
                newY = Math.round(newY / (float) gridSize) * gridSize;
            } else {
                // If snap to grid is not enabled, apply wall snapping logic
                newX = snapToNearestWall(newX, selectedRoom.getWidth(), true);
                newY = snapToNearestWall(newY, selectedRoom.getHeight(), false);
            }

            // Update the selected room's new position
            selectedRoom.setLocation(newX, newY);
            repaint();
        }
    }

    private void handleMouseReleased(MouseEvent e) {
        if (selectedRoom != null) {
            // If snap to grid is enabled, apply grid snapping after drag
            if (snapToGridEnabled) {
                int newX = (selectedRoom.getX() / gridSize) * gridSize;
                int newY = (selectedRoom.getY() / gridSize) * gridSize;
                selectedRoom.setLocation(newX, newY);
            }

            // Check for collisions after releasing the mouse
            if (checkCollision()) {
                // If a collision occurred, reset the room to its initial position
                JOptionPane.showMessageDialog(this, "Collision detected! Room reset to initial position.",
                        "Collision Detected", JOptionPane.WARNING_MESSAGE);
                selectedRoom.setLocation(initialRoomPosition);

            }

            selectedRoom = null;
            selected = selectedRoom;
            repaint();
        }
    }

    public void toggleSnapToGrid() {
        snapToGridEnabled = !snapToGridEnabled;
        repaint();
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    private void showPopupMenu(MouseEvent e) {
        if (selectedRoom != null) {
            System.out.println("Selected room: " + selectedRoom);
        } else {
            System.out.println("No room selected for deletion.");
        }

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(Color.BLACK);

        JMenuItem item1 = new JMenuItem("Delete");
        JMenuItem item2 = new JMenuItem("ADD room relative");
        JMenuItem item3 = new JMenuItem("Add Door");
        JMenuItem item4 = new JMenuItem("Add Window");
        JMenuItem item5 = new JMenuItem("Remove Doors");
        JMenuItem item6 = new JMenuItem("Remove Windows");

        // Style all menu items
        for (JMenuItem item : new JMenuItem[]{item1, item2, item3, item4,item5,item6}) {
            item.setBackground(Color.BLACK);
            item.setForeground(Color.GRAY);
            item.setBorder(new LineBorder(Color.BLUE, 1));
            item.setFocusPainted(false);
        }

        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRoom != null) {
                    deleteRoom();
                }
            }
        });
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRoom != null) {
                    selectedRoom.addRoomRelative();
                }
            }
        });

        item3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRoom != null) {
                    selectedRoom.addDoor();
                }
            }
        });

        item4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRoom != null) {
                    selectedRoom.addWindow();
                }
            }
        });

        item5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRoom != null) {
                    selectedRoom.removeAllDoors();
                }
            }
        });

        item6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRoom != null) {
                    selectedRoom.removeAllWindows();
                }
            }
        });

        popupMenu.add(item1);
        popupMenu.add(item2);
        popupMenu.add(item3);
        popupMenu.add(item4);
        popupMenu.add(item5);
        popupMenu.add(item6);
        popupMenu.show(Frame.controlPanel, e.getX() + 400, e.getY());
    }

    private int snapToNearestWall(int newCoord, int roomDimension, boolean isHorizontal) {
        int nearestWall = newCoord;
        int margin = 10;

        for (Room room : rooms) {
            if (room != selectedRoom) {
                if (isHorizontal) {
                    if (newCoord + roomDimension <= room.getX() && newCoord + roomDimension >= room.getX() - margin) {
                        nearestWall = room.getX() - roomDimension;
                    } else if (newCoord >= room.getX() + room.getWidth()
                            && newCoord <= room.getX() + room.getWidth() + margin) {
                        nearestWall = room.getX() + room.getWidth();
                    }
                } else {
                    if (newCoord + roomDimension <= room.getY() && newCoord + roomDimension >= room.getY() - margin) {
                        nearestWall = room.getY() - roomDimension;
                    } else if (newCoord >= room.getY() + room.getHeight()
                            && newCoord <= room.getY() + room.getHeight() + margin) {
                        nearestWall = room.getY() + room.getHeight();
                    }
                }
            }
        }
        return nearestWall;
    }

    public void checkCollisions() {
        boolean newCollisionState = false;
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                if (rooms.get(i).intersects(rooms.get(j))) {
                    newCollisionState = true;
                    break;
                }
            }
            if (newCollisionState)
                break;
        }

        if (newCollisionState != collisionState) {
            collisionState = newCollisionState;
            if (collisionState) {
                System.out.println("Collision detected");
                JOptionPane.showMessageDialog(this, "Collision detected", "Error", JOptionPane.WARNING_MESSAGE);
            } else {
                System.out.println("No collisions");
            }
        }
    }

    private boolean checkCollision() {
        for (int i = 0; i < rooms.size(); i++) {
            for (int j = i + 1; j < rooms.size(); j++) {
                if (rooms.get(i).intersects(rooms.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // This clears the canvas before redrawing everything

        // Set up the grid pattern
        Graphics2D g2d = (Graphics2D) g;
        for (int k = 0; k < getWidth(); k += gridSize) {
            for (int k2 = 0; k2 < getHeight(); k2 += gridSize) {
                g2d.setColor(Color.green);
                g2d.fillOval(k, k2, 1, 1);
            }
        }
      

    }

    public void exportAsPNG(File file) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        paint(g2d);
        g2d.dispose();
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paintRooms(Graphics g) {
        for (Room room : rooms) {
            room.paintComponent(g);  // Assuming the Room class has a `paint(Graphics g)` method
        }
    }

}