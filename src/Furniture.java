import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Furniture extends JPanel {
    private JLabel label;
    private boolean selected = false;
    public static boolean isSelected = false;
    private Room parentRoom;

    public Furniture(Room room, String Path) {
        this.parentRoom = room;
        this.setLayout(null);
        setPreferredSize(new Dimension(40, 40));
        if (canvasPanel.selected != null) {
            setFocusable(true);
        }

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setImage(Path);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    setSelected(true);
                    showPopupMenu(e);
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    setSelected(!isSelected);
                }
            }
        });
    }

    public void setImage(String imagePath) {
        ImageIcon image = new ImageIcon(imagePath);
        Image scaledImage = image.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon1 = new ImageIcon(scaledImage);
        label = new JLabel(resizedIcon1);
        label.setBounds(2, 2, 36, 36);  // Add 2px padding on all sides

        this.setOpaque(false);
        this.add(label);
        this.revalidate();
        this.repaint();
    }

    public Room getParentRoom() {
        return parentRoom;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        Furniture.isSelected = selected;
        setBorder(selected ? BorderFactory.createLineBorder(Color.BLUE, 2) : BorderFactory.createLineBorder(Color.BLACK));
        repaint();
    }

    public void showPopupMenu(MouseEvent e) {
        selected = true;
        isSelected = true;
        System.out.println("Showing furniture menu");

        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(Color.BLACK);

        JMenuItem deleteItem = new JMenuItem("Delete Furniture");
        JMenuItem rotateCWItem = new JMenuItem("Rotate 90° Right");
        JMenuItem rotateACWItem = new JMenuItem("Rotate 90° Left");

        // Style menu items
        for (JMenuItem item : new JMenuItem[]{deleteItem, rotateCWItem, rotateACWItem}) {
            item.setBackground(Color.BLACK);
            item.setForeground(Color.GRAY);
            item.setBorder(new LineBorder(Color.BLUE, 1));
            item.setFocusPainted(false);
        }

        deleteItem.addActionListener(e1 -> {
           delete();
        });

        rotateCWItem.addActionListener(e1 -> {
            rotateCW();
            revalidate();
            repaint();
        });

        rotateACWItem.addActionListener(e1 -> {
            rotateACW();
            revalidate();
            repaint();
        });

        popupMenu.add(deleteItem);
        popupMenu.add(rotateCWItem);
        popupMenu.add(rotateACWItem);
        popupMenu.show(getParent(), e.getX(), e.getY());
    }

    private int rotationAngle = 0;

    private void rotateCW() {
        rotationAngle = (rotationAngle + 90) % 360;
        updateRotation();
    }

    private void rotateACW() {
        rotationAngle = (rotationAngle - 90) % 360;
        if (rotationAngle < 0) {
            rotationAngle += 360;
        }
        updateRotation();
    }

    private void updateRotation() {
        if (label != null) {
            ImageIcon icon = (ImageIcon) label.getIcon();
            Image img = icon.getImage();
            BufferedImage bufferedImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            
            // Set rendering hints for better quality
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Rotate around the center
            g2d.rotate(Math.toRadians(rotationAngle), 20, 20);
            g2d.drawImage(img, 0, 0, 40, 40, null);
            g2d.dispose();

            label.setIcon(new ImageIcon(bufferedImage));
        }
        repaint();
    }

    public void delete(){
        Container parent = getParent();
        if (parent instanceof Room) {
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if (selected) {
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLUE);
            g2d.drawRect(1, 1, getWidth()-3, getHeight()-3);
            g2d.setColor(new Color(0, 0, 255, 50));
            g2d.fillRect(2, 2, getWidth()-4, getHeight()-4);
        }
    }
}