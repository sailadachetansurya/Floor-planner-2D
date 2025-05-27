import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;

public class Drop extends JPanel {
    private boolean isExpandedRooms = false;
    private boolean isExpandedFurnitures = false;
    private final String[] Rooms = { "Living Room", "Dining Hall", "Kitchen", "Bathroom", "Bedroom" };
    private final String[] Furnitures = { "Bed", "Dining Set", "Table", "Chair", "Sofa", "Other Fixtures ->" };
    private final String[] Fixtures = { "commode",  "washbasin", "shower", "kitchen sink", "stove" };
    private JButton secondToggleButton, toggleButton;
    private int menuloc;

    Drop(int x) {
        if (x == 0) {
            setBackground(Color.BLACK);
            setLayout(new GridLayout(12, 1));

            // Initialize and configure toggle buttons
            toggleButton = createToggleButton("Add Rooms", e -> togglePanel());
            secondToggleButton = createToggleButton("Add Furniture", e -> togglePanel2());

            add(toggleButton);
            add(secondToggleButton);
        }
    }

    private JButton createToggleButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.PLAIN, 16));
        button.setForeground(Color.white);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(200, 70));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        button.addActionListener(action);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Color.blue);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });

        return button;
    }

    private void togglePanel() {
        isExpandedRooms = !isExpandedRooms; // Toggle rooms panel

        if (isExpandedFurnitures && !isExpandedRooms) {
            // onli furniture panel if it's open
            menuloc = 350;
        } else {
            menuloc = 550;
        }
        updatePanel();
    }

    private void togglePanel2() {
        isExpandedFurnitures = !isExpandedFurnitures; // Toggle furniture panel
        menuloc = 350;
        if (isExpandedRooms && isExpandedFurnitures) {
            // both open
            menuloc = 550;
        }
        updatePanel();
    }

    private void updatePanel() {
        this.removeAll();
        this.setLayout(new GridLayout(13, 1)); // Reset layout

        // Re-add toggle buttons
        this.add(toggleButton);

        // Add room buttons if the rooms panel is expanded
        if (isExpandedRooms) {
            for (String room : Rooms) {
                JButton roomButton = createRoomOrFurnitureButton(room);
                this.add(roomButton);
            }
        }

        this.add(secondToggleButton);

        // Add furniture buttons if the furniture panel is expanded
        if (isExpandedFurnitures) {
            for (String furniture : Furnitures) {
                JButton furnitureButton = createRoomOrFurnitureButton(furniture);
                this.add(furnitureButton);
            }
        }

        this.revalidate();
        this.repaint();
    }

    private JButton createRoomOrFurnitureButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        setfunc(text, button);
        return button;
    }

    public JButton styleButton(JButton button) {
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        Border sideBorder = BorderFactory.createEmptyBorder(2, 0, 2, 0);
        button.setBorder(sideBorder);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });
        return button;
    }

    public JToggleButton styleButton(JToggleButton button) {
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        Border sideBorder = BorderFactory.createEmptyBorder(2, 0, 2, 0);
        button.setBorder(sideBorder);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Color.DARK_GRAY);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.BLACK);
            }
        });
        return button;
    }

    private void setfunc(String text, JButton button) {
        switch (text) {

            case "Living Room":
                button.addActionListener(e -> Frame.CanvasPanel.addRoom(0));
                break;

            case "Dining Hall":
                button.addActionListener(e -> Frame.CanvasPanel.addRoom(1));
                break;
            case "Bathroom":
                button.addActionListener(e -> Frame.CanvasPanel.addRoom(3));
                break;
            case "Kitchen":
                button.addActionListener(e -> Frame.CanvasPanel.addRoom(2));
                break;
            case "Bedroom":
                button.addActionListener(e -> Frame.CanvasPanel.addRoom(4));
                break;
            case "Bed":
                button.addActionListener(e -> Frame.getCanvasPanel().addFurnishing("lib\\bed.png"));
                break;
            case "Cupboard":
                button.addActionListener(e -> Frame.getCanvasPanel().addFurnishing("lib\\cupboard.png"));

                break;
            case "Dining Set":
                button.addActionListener(e -> Frame.getCanvasPanel().addFurnishing("lib\\cupboard.png"));

                break;
            case "Table":
                button.addActionListener(e -> Frame.getCanvasPanel().addFurnishing("lib\\table.png"));

                break;
            case "Chair":
                button.addActionListener(e -> Frame.getCanvasPanel().addFurnishing("lib\\chair.png"));

                break;
            case "Sofa":
                button.addActionListener(e -> Frame.CanvasPanel.addFurnishing("lib\\sofa.png"));

                break;
            case "Other Fixtures ->":
                button.addActionListener(e -> Fixtures());
                break;

            default:
                break;
        }
    }

    public void Fixtures() {
        JPopupMenu popupmenu = createMenuItems(Fixtures);
        popupmenu.show(Frame.controlPanel, getParent().getX() + getParent().getWidth(), getParent().getY() + menuloc);
    }

    private JPopupMenu createMenuItems(String[] Fixtures){
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(Color.BLACK);
        for (String string : Fixtures) {
            JMenuItem Fixture = new JMenuItem(string);
            Fixture.setBorder(new LineBorder(Color.BLUE, 1));
            Fixture.setFocusPainted(false);
            Fixture.setBackground(Color.BLACK);
            Fixture.setForeground(Color.GRAY);
            Fixture.setBorder(new LineBorder(Color.BLUE, 1));
            Fixture.setFocusPainted(false);
            setfunc(Fixture);

            popupMenu.add(Fixture);

        }
        
     
        popupMenu.setPreferredSize(new Dimension(150, 150));
        return popupMenu;
    }
    private void setfunc(JMenuItem item) {
        switch (item.getText()) {

            case "commode":
                item.addActionListener(e -> Frame.CanvasPanel.addFurnishing("lib\\commode.png"));

                break;
            case "washbasin":
                item.addActionListener(e -> Frame.CanvasPanel.addFurnishing("lib\\washbasin.png"));

                break;
            case "shower":
                item.addActionListener(e -> Frame.CanvasPanel.addFurnishing("lib\\shower.png"));

                break;
            case "kitchen sink":
                item.addActionListener(e -> Frame.CanvasPanel.addFurnishing("lib\\sink.png"));

                break;
            case "stove":
                item.addActionListener(e -> Frame.CanvasPanel.addFurnishing("lib\\stove.png"));

                break;
            default:
                break;
        }
    }
}
