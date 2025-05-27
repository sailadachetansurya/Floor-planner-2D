import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.*;

public class Room extends JPanel{
    private int width;
    private int height;
    private Point position;
    public int roomtype; // 0-livingroom, 1-DiningRoom, 2-Kitchen, 3-Bedroom, 4-Bathroom
    public Color roomcolor;
    protected ArrayList<Furniture> Furniture; 

    @SuppressWarnings("unused")
    private int wallThickness = 15;
    
    
    // Door and window data structures
    private static class Opening implements Serializable{
        int x, y, width, height;
        @SuppressWarnings("unused")
        String position; // "North", "South", "East", "West"
        
        Opening(int x, int y, int width, int height, String position) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.position = position;
        }
    }
    
    private ArrayList<Opening> doors = new ArrayList<>();
    private ArrayList<Opening> windows = new ArrayList<>();
    private static final int MIN_DOOR_SIZE = 2;
    
    private boolean checkOverlap(int x, int y, int width, int height, ArrayList<Opening> openings) {
        Rectangle newOpening = new Rectangle(x, y, width, height);
        for (Opening existing : openings) {
            Rectangle existingOpening = new Rectangle(existing.x, existing.y, existing.width, existing.height);
            if (newOpening.intersects(existingOpening)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isOverlapping(int x, int y, int width, int height) {
        // Check overlap with both doors and windows
        return checkOverlap(x, y, width, height, doors) || 
               checkOverlap(x, y, width, height, windows);
    }

    public ArrayList<Opening> getDoors() {
        return doors;
    }
    
    public void removeAllDoors() {
        doors.clear();
        repaint();
    }
    
    public void removeAllWindows() {
        windows.clear();
        repaint();
    }
    
    public void removeAllOpenings() {
        removeAllDoors();
        removeAllWindows();
    }
    private static final int WINDOW_WIDTH = 20;
    private static final int WINDOW_HEIGHT = 3;
    private static final int WALL_THICKNESS = 4;


    public Room(int roomtype) {
        this.roomtype = roomtype;
        // setinitialcolor(roomtype); // This should initialize roomcolor based on
        // roomtype
        this.setLayout(null);
        this.setOpaque(false);
    }

    public Room(int x, int y, int width, int height, int roomtype) {
        this.roomtype = roomtype;
        this.width = width;
        this.height = height;
        this.position = new Point(x, y);
        this.setBounds(x, y, width, height);
        this.setLayout(null);
        // setinitialcolor(roomtype);
        this.setOpaque(false);
    }

    private int getRoomDimension(String dimensionName, int maxValue) {
        int dimension = -1;
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(null, "Please enter your Room " + dimensionName + ":");

                if (input == null || input.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Empty input: Set to default value(100).");
                    dimension = 100;
                    break;
                }

                dimension = Integer.parseInt(input);

                if (dimension > maxValue) {
                    JOptionPane.showMessageDialog(null,
                            dimensionName + " cannot exceed " + maxValue + ". Please try again.");
                    continue;
                } else if (dimension < 0) {
                    JOptionPane.showMessageDialog(null, dimensionName + " cannot be negative. Please try again.");
                    continue;
                }

                break; // Valid input, exit the loop
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input: Please enter a valid number.");
            }
        }
        return dimension;
    }

    public int setwidth() {
        return getRoomDimension("width", canvasPanel.canvasWidth);
    }

    public int setheight() {
        return getRoomDimension("height", canvasPanel.canvasHeight);
    }

    public void setLocation(int x, int y) {
        this.position = new Point(x, y);
        this.setBounds(x, y, width, height); // Update JPanel bounds when position changes
    }

    public int getX() {
        return position.x;
    }

    public int getY() {
        return position.y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Point getPosition() {
        return position;
    }

    private static final int MAX_DOORS = 4;
    
    private int getDoorDimension(String dimensionName, String position) {
        int maxSize = (position.equals("North") || position.equals("South")) ? getWidth() : getHeight();
        
        while (true) {
            try {
                String input = JOptionPane.showInputDialog(this,
                    "Enter door " + dimensionName + " (between " + MIN_DOOR_SIZE + " and " + 
                    maxSize + "):");
                
                if (input == null) {
                    return MIN_DOOR_SIZE; // Default size if cancelled
                }
                
                int size = Integer.parseInt(input);
                
                if (size < MIN_DOOR_SIZE) {
                    JOptionPane.showMessageDialog(this,
                        dimensionName + " must be at least " + MIN_DOOR_SIZE);
                    continue;
                }
                
                if (size > maxSize) {
                    JOptionPane.showMessageDialog(this,
                        dimensionName + " must be less than " + maxSize);
                    continue;
                }
                
                return size;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid number");
            }
        }
    }

    public void addDoor() {
        // For bedrooms and bathrooms, only allow doors to adjacent rooms
     

        if (doors.size() >= MAX_DOORS) {
            JOptionPane.showMessageDialog(this,
                "Maximum number of doors (" + MAX_DOORS + ") reached",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show dialog for door position
        String[] positions = {"North", "South", "East", "West"};
        String position = (String) JOptionPane.showInputDialog(
            this,
            "Select door position:\nDoors: " + doors.size() + "/4",
            "Add Door",
            JOptionPane.QUESTION_MESSAGE,
            null,
            positions,
            positions[0]
        );

        if ((roomtype == 3 || roomtype == 4) && !isRoomAdjacent(position)) {
            JOptionPane.showMessageDialog(this,
                "Bedrooms and bathrooms can only have doors to adjacent rooms",
                "Invalid Door Position",
                JOptionPane.ERROR_MESSAGE);
            return;
        }


        if (position != null) {
            int width = getDoorDimension("width", position);
            int height = 3;
            
            // For East/West walls, swap width/height
            if (position.equals("East") || position.equals("West")) {
                int temp = width;
                width = height;
                height = temp;
            }
            
            // Initialize coordinates
            int x = -1, y = -1;
            boolean validCoord = false;
            
            while (!validCoord) {
                try {
                    // Set fixed coordinate based on wall position
                    switch (position) {
                        case "North":
                            y = 0;
                            String xInput = JOptionPane.showInputDialog(this,
                                "Enter X coordinate (0-" + (getWidth() - width) + "):");
                            if (xInput == null) return; // User cancelled
                            x = Integer.parseInt(xInput);
                            if (x < 0 || x > getWidth() - width) {
                                JOptionPane.showMessageDialog(this, "Invalid X coordinate");
                                continue;
                            }
                            break;
                            
                        case "South":
                            y = getHeight() - height;
                            xInput = JOptionPane.showInputDialog(this,
                                "Enter X coordinate (0-" + (getWidth() - width) + "):");
                            if (xInput == null) return; // User cancelled
                            x = Integer.parseInt(xInput);
                            if (x < 0 || x > getWidth() - width) {
                                JOptionPane.showMessageDialog(this, "Invalid X coordinate");
                                continue;
                            }
                            break;
                            
                        case "East":
                            x = getWidth() - width;
                            String yInput = JOptionPane.showInputDialog(this,
                                "Enter Y coordinate (0-" + (getHeight() - height) + "):");
                            if (yInput == null) return; // User cancelled
                            y = Integer.parseInt(yInput);
                            if (y < 0 || y > getHeight() - height) {
                                JOptionPane.showMessageDialog(this, "Invalid Y coordinate");
                                continue;
                            }
                            break;
                            
                        case "West":
                            x = 0;
                            yInput = JOptionPane.showInputDialog(this,
                                "Enter Y coordinate (0-" + (getHeight() - height) + "):");
                            if (yInput == null) return; // User cancelled
                            y = Integer.parseInt(yInput);
                            if (y < 0 || y > getHeight() - height) {
                                JOptionPane.showMessageDialog(this, "Invalid Y coordinate");
                                continue;
                            }
                            break;
                    }
                    validCoord = true;
                    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number");
                }
            }
            
            // Check for overlaps before adding the door
            if (isOverlapping(x, y, width, height)) {
                JOptionPane.showMessageDialog(this,
                    "Cannot add door here - it would overlap with existing door or window",
                    "Overlap Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            doors.add(new Opening(x, y, width, height, position));
            repaint();
        }
    }


    public void addWindow() {
        
        // Show dialog for window position
        String[] positions = {"North", "South", "East", "West"};
        String position = (String) JOptionPane.showInputDialog(
            this,
            "Select window position:",
            "Add Window",
            JOptionPane.QUESTION_MESSAGE,
            null,
            positions,
            positions[0]
        );

        // Check if this window would be between rooms
        if (isRoomAdjacent(position)) {
            JOptionPane.showMessageDialog(this,
                "Windows cannot be placed between rooms",
                "Invalid Window Position",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (position != null) {
            int width = WINDOW_WIDTH;
            int height = WINDOW_HEIGHT;
            
            // For East/West walls, swap width/height
            if (position.equals("East") || position.equals("West")) {
                int temp = width;
                width = height;
                height = temp;
            }
            
            // Initialize coordinates
            int x = -1, y = -1;
            boolean validCoord = false;
            
            while (!validCoord) {
                try {
                    // Set fixed coordinate based on wall position
                    switch (position) {
                        case "North":
                            y = 0;
                            String xInput = JOptionPane.showInputDialog(this,
                                "Enter X coordinate (0-" + (getWidth() - width) + "):");
                            if (xInput == null) return; // User cancelled
                            x = Integer.parseInt(xInput);
                            if (x < 0 || x > getWidth() - width) {
                                JOptionPane.showMessageDialog(this, "Invalid X coordinate");
                                continue;
                            }
                            break;
                            
                        case "South":
                            y = getHeight() - height;
                            xInput = JOptionPane.showInputDialog(this,
                                "Enter X coordinate (0-" + (getWidth() - width) + "):");
                            if (xInput == null) return; // User cancelled
                            x = Integer.parseInt(xInput);
                            if (x < 0 || x > getWidth() - width) {
                                JOptionPane.showMessageDialog(this, "Invalid X coordinate");
                                continue;
                            }
                            break;
                            
                        case "East":
                            x = getWidth() - width;
                            String yInput = JOptionPane.showInputDialog(this,
                                "Enter Y coordinate (0-" + (getHeight() - height) + "):");
                            if (yInput == null) return; // User cancelled
                            y = Integer.parseInt(yInput);
                            if (y < 0 || y > getHeight() - height) {
                                JOptionPane.showMessageDialog(this, "Invalid Y coordinate");
                                continue;
                            }
                            break;
                            
                        case "West":
                            x = 0;
                            yInput = JOptionPane.showInputDialog(this,
                                "Enter Y coordinate (0-" + (getHeight() - height) + "):");
                            if (yInput == null) return; // User cancelled
                            y = Integer.parseInt(yInput);
                            if (y < 0 || y > getHeight() - height) {
                                JOptionPane.showMessageDialog(this, "Invalid Y coordinate");
                                continue;
                            }
                            break;
                    }
                    validCoord = true;
                    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number");
                }
            }
            if (isOverlapping(x, y, width, height)) {
                JOptionPane.showMessageDialog(this,
                    "Cannot add Window here - it would overlap with existing door or window",
                    "Overlap Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            windows.add(new Opening(x, y, width, height, position));
            repaint();
        }
    }

    private boolean isRoomAdjacent(String position) {
        Rectangle bounds = getBounds();
        for (Room room : Frame.getCanvasPanel().getRooms()) {
            if (room != this) {
                Rectangle otherBounds = room.getBounds();
                switch (position) {
                    case "North":
                        if (bounds.y == otherBounds.y + otherBounds.height &&
                            bounds.x < otherBounds.x + otherBounds.width &&
                            bounds.x + bounds.width > otherBounds.x)
                            return true;
                        break;
                    case "South":
                        if (bounds.y + bounds.height == otherBounds.y &&
                            bounds.x < otherBounds.x + otherBounds.width &&
                            bounds.x + bounds.width > otherBounds.x)
                            return true;
                        break;
                    case "East":
                        if (bounds.x + bounds.width == otherBounds.x &&
                            bounds.y < otherBounds.y + otherBounds.height &&
                            bounds.y + bounds.height > otherBounds.y)
                            return true;
                        break;
                    case "West":
                        if (bounds.x == otherBounds.x + otherBounds.width &&
                            bounds.y < otherBounds.y + otherBounds.height &&
                            bounds.y + bounds.height > otherBounds.y)
                            return true;
                        break;
                }
            }
        }
        return false;
    }

    // private boolean hasAdjacentRoom() {
    //     return isRoomAdjacent("North") || isRoomAdjacent("South") ||
    //            isRoomAdjacent("East") || isRoomAdjacent("West");
    // }



    private static final int FURNITURE_SIZE = 40;
    private int nextFurnitureX = 10;
    private int nextFurnitureY = 10;
    
    public void addFurnishing(String furnitureType) {
        try {
          
            // Check if there's enough space for new furniture
            if (nextFurnitureX + FURNITURE_SIZE > getWidth()) {
                nextFurnitureX = 10;
                nextFurnitureY += FURNITURE_SIZE + 10;
            }
            
            // Check if we've run out of vertical space
            if (nextFurnitureY + FURNITURE_SIZE > getHeight()) {
                JOptionPane.showMessageDialog(this,
                    "Not enough room space for more furniture!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Furniture auxPanel = new Furniture(this, furnitureType);
            auxPanel.setBounds(nextFurnitureX, nextFurnitureY, FURNITURE_SIZE, FURNITURE_SIZE);
            this.add(auxPanel);
            
            // Update position for next furniture
            nextFurnitureX += FURNITURE_SIZE + 10;
            
            this.revalidate();
            this.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "WARNING: No Room Selected",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(4));
        GradientPaint gradient = null;
        switch (roomtype) {
            case 0:
                gradient = new GradientPaint(
                        0, 0, new Color(190, 125, 159, 128),
                        0, (int) getHeight(), new Color(150, 125, 159, 128));
                break;
            case 1:
                gradient = new GradientPaint(
                        0, 0, new Color(255, 223, 0, 128),
                        0, (int) getHeight(), new Color(255, 223, 0, 128));
                break;
            case 2:
                gradient = new GradientPaint(
                        0, 0, new Color(255, 36, 0, 68),
                        0, (int) getHeight(), new Color(255, 36, 0, 68));
                break;
            case 3:
                gradient = new GradientPaint(
                        0, 0, new Color(124, 185, 232, 128),
                        0, (int) getHeight(), new Color(124, 185, 232, 128));
                break;
            case 4:
                gradient = new GradientPaint(
                        0, 0, new Color(34, 139, 34, 128),
                        0, (int) getHeight(), new Color(34, 139, 34, 128));
                break;
        }

        // Apply the gradient to the room's rectangle
        if (gradient != null) {
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Draw room outline
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(WALL_THICKNESS));
        
        // Draw walls in segments, skipping door areas
        drawWallsWithOpenings(g2d);

        

        // Draw windows with dashed lines
        float[] dash = { 5.0f, 5.0f };
        g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g2d.setColor(new Color(173, 216, 230, 180)); // Light blue with transparency
        
        for (Opening window : windows) {
            g2d.setColor(new Color(173, 216, 230, 180));
            g2d.fillRect(window.x, window.y, window.width, window.height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(window.x, window.y, window.width, window.height);
            // Draw window panes
            g2d.drawLine(window.x, window.y + window.height/2, 
                        window.x + window.width, window.y + window.height/2);
            g2d.drawLine(window.x + window.width/2, window.y,
                        window.x + window.width/2, window.y + window.height);
        }

        

        // Draw windows with dashed lines
        g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
        g2d.setColor(new Color(173, 216, 230, 180)); // Light blue with transparency
        
        for (Opening window : windows) {
            g2d.setColor(new Color(173, 216, 230, 180));
            g2d.fillRect(window.x, window.y, window.width, window.height);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(window.x, window.y, window.width, window.height);
            // Draw window panes
            g2d.drawLine(window.x, window.y + window.height/2, 
                        window.x + window.width, window.y + window.height/2);
            g2d.drawLine(window.x + window.width/2, window.y,
                        window.x + window.width/2, window.y + window.height);
        }

        if (this == canvasPanel.selected) {
            g2d.setStroke(new BasicStroke(2));
            // g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2d.setColor(Color.BLUE);
            // g2d.fillRect(0, 0, (int) this.getWidth(), (int) this.getHeight());
            g2d.drawRect(0, 0, (int) this.getWidth(), (int) this.getHeight());
        }
    }

    private void drawWallsWithOpenings(Graphics2D g2d) {
        // Draw the walls of the room
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(WALL_THICKNESS));
    
        // Draw the four walls, skipping door areas
        g2d.drawLine(0, 0, getWidth(), 0); // Top wall
        g2d.drawLine(getWidth(), 0, getWidth(), getHeight()); // Right wall
        g2d.drawLine(getWidth(), getHeight(), 0, getHeight()); // Bottom wall
        g2d.drawLine(0, getHeight(), 0, 0); // Left wall
    
        // Draw doors
        for (Opening door : doors) {
            g2d.setColor(Color.WHITE); // Set color to match the background for door openings
            g2d.fillRect(door.x, door.y, door.width, door.height); // Draw door opening
        }
    }

    public boolean intersects(Room r) {
        return this.getBounds().intersects(r.getBounds());
    }

    public void addRoomRelative() {
        // Dialog start
        Room selectedRoom = Frame.CanvasPanel.selectedRoom;
        String y = JOptionPane.showInputDialog(null,
                "Please enter your Roomtype number:" + "\n"
                        + " 0-livingroom "+"\n"+"1-DiningRoom"+"\n"+" 2-Kitchen"+"\n"+" 3-Bedroom"+"\n"+"4-Bathroom");
        int roomType = Integer.parseInt(y);
        Room newRoom = new Room(roomType);
        int width = newRoom.setwidth();
        int height = newRoom.setheight();
        newRoom.setSize(width, height);

        // Set up dialog for direction and orientation selection
        JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setTitle("Relative Addition Of Rooms: (>__<)");
        dialog.setSize(300, 200);
        dialog.setBackground(Color.LIGHT_GRAY);
        dialog.setForeground(Color.BLACK);
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(getParent());

        JButton okButton;
        JButton cancelButton;

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2)); // 3 rows, 2 columns

        inputPanel.add(new JLabel("Direction:"));
        JPanel radioPanel = new JPanel();
        JRadioButton North = new JRadioButton("North");
        JRadioButton South = new JRadioButton("South");
        JRadioButton East = new JRadioButton("East");
        JRadioButton West = new JRadioButton("West");
        ButtonGroup Direction = new ButtonGroup();
        Direction.add(North);
        Direction.add(South);
        Direction.add(East);
        Direction.add(West);

        radioPanel.add(North);
        radioPanel.add(South);
        radioPanel.add(East);
        radioPanel.add(West);
        inputPanel.add(radioPanel);

        inputPanel.add(new JLabel("Orientation:"));
        JPanel orientation = new JPanel();
        JRadioButton Right = new JRadioButton("Right / Top");
        JRadioButton Left = new JRadioButton("Left / Bottom");
        JRadioButton Center = new JRadioButton("Center");
        ButtonGroup Orientation = new ButtonGroup();
        Orientation.add(Right);
        Orientation.add(Left);
        Orientation.add(Center);

        orientation.add(Right);
        orientation.add(Left);
        orientation.add(Center);
        inputPanel.add(orientation);

        // OK and Cancel buttons
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        canvasPanel canvasPanel = Frame.getCanvasPanel();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected direction from the ButtonGroup
                String selectedDirection = null;
                if (North.isSelected()) {
                    selectedDirection = "North";
                } else if (South.isSelected()) {
                    selectedDirection = "South";
                } else if (East.isSelected()) {
                    selectedDirection = "East";
                } else if (West.isSelected()) {
                    selectedDirection = "West";
                }

                // Get the selected orientation from the ButtonGroup
                String selectedOrientation = null;
                if (Right.isSelected()) {
                    selectedOrientation = "Right / Top";
                } else if (Left.isSelected()) {
                    selectedOrientation = "Left / Bottom";
                } else if (Center.isSelected()) {
                    selectedOrientation = "Center";
                }

                if (selectedDirection != null && selectedOrientation != null) {
                    // Now selectedDirection and selectedOrientation will hold the chosen options

                    int newX = selectedRoom.getX();
                    int newY = selectedRoom.getY();

                    // Direction switch case to set the new room's position
                    switch (selectedDirection) {
                        case "North":
                            newX = selectedRoom.getX();
                            newY = selectedRoom.getY() - height; // Position above the selected room
                            break;
                        case "South":
                            newX = selectedRoom.getX();
                            newY = selectedRoom.getY() + selectedRoom.getHeight(); // Position below the selected room
                            break;
                        case "East":
                            newX = selectedRoom.getX() + selectedRoom.getWidth(); // Position to the right of the
                                                                                  // selected room
                            newY = selectedRoom.getY();
                            break;
                        case "West":
                            newX = selectedRoom.getX() - width; // Position to the left of the selected room
                            newY = selectedRoom.getY();
                            break;
                        default:
                            newX = selectedRoom.getX();
                            newY = selectedRoom.getY();
                            break;
                    }

                    // Orientation switch case to adjust positioning
                    switch (selectedOrientation) {
                        case "Right / Top":
                            if (selectedDirection.equals("North") || selectedDirection.equals("South")) {
                                // No change to X
                            } else {
                                newY = newY + newRoom.getWidth(); // Adjust Y for East/West direction
                            }
                            break;
                        case "Left / Bottom":
                            if (selectedDirection.equals("North") || selectedDirection.equals("South")) {
                                newX = newX + selectedRoom.getWidth() - newRoom.getWidth(); // Adjust X for North/South
                            } else {
                                newY = newY + selectedRoom.getHeight() - newRoom.getHeight(); // Adjust Y for East/West
                            }
                            break;
                        case "Center":
                            if (selectedDirection.equals("North") || selectedDirection.equals("South")) {
                                newX = selectedRoom.getX() + (selectedRoom.getWidth() / 2) - (newRoom.getWidth() / 2); // Center
                                                                                                        // horizontally
                            } else {
                                newY = newY + (selectedRoom.getHeight() / 2) - (newRoom.getHeight() / 2); // Center
                                                                                                          // vertically
                            }
                            break;
                        default:
                            break;
                    }

                    // Create the new room and check for collisions with existing rooms
                    newRoom.setLocation(newX, newY);
                    Room newRoom1 = new Room(newX, newY, width, height, roomType);

                    // Collision detection loop
                    boolean overlaps = false; // Flag to track overlaps
                    for (Room existingRoom : canvasPanel.getRooms()) {
                        if (newRoom1.getBounds().intersects(existingRoom.getBounds())) {
                            overlaps = true; // Collision detected
                            break; // Exit the loop immediately after detecting overlap
                        }
                    }

                    if (overlaps) {
                        // Show an error message and cancel the room addition
                        JOptionPane.showMessageDialog(null,
                                "The room cannot be added due to overlap. Please try again.",
                                "Collision Error", JOptionPane.ERROR_MESSAGE);
                        dialog.dispose();
                        return; // Stop the room addition process

                    }

                    // If no overlap, add the room

                    canvasPanel.getRooms().add(newRoom1);
                    canvasPanel.add(newRoom1);
                    newRoom1.setVisible(true);
                    newRoom1.repaint();
                    newRoom1.revalidate(); // Add to the canvas or container
                    canvasPanel.repaint();
                    canvasPanel.revalidate(); // Refresh the canvas

                    System.out.println("New Room Position: (" + newX + ", " + newY + ")");

                }

                dialog.dispose(); // Close dialog after confirmation
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Close dialog without saving
            }
        });

        // Panel to hold buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        // dialogbox end
    }

}