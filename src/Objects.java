import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class Objects extends JPanel {
    private JLabel label;
    private Point initialClick;
    private int originalX, originalY;
    private int lastValidX, lastValidY;
    private int rotationAngle = 0;
    private boolean isSelected = false;

    public Objects(String imagePath) {
        ImageIcon image = new ImageIcon(imagePath);
        Image scaledImage = image.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaledImage);

        label = new JLabel(resizedIcon);
        label.setBounds(0, 0, 40, 40);
        this.setLayout(null);
        this.setOpaque(false);
        this.add(label);
        this.setBounds(0, 0, 40, 40);

        lastValidX = originalX = getX();
        lastValidY = originalY = getY();

        addMouseListeners();
    }

    private void addMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                originalX = getX();
                originalY = getY();
                lastValidX = originalX;
                lastValidY = originalY;
                setSelected(!isSelected);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentPt = e.getPoint();
                int newX = getX() + currentPt.x - initialClick.x;
                int newY = getY() + currentPt.y - initialClick.y;

                Container parent = getParent();
                if (parent != null) {
                    int maxX = parent.getWidth() - getWidth();
                    int maxY = parent.getHeight() - getHeight();
                    newX = Math.max(0, Math.min(newX, maxX));
                    newY = Math.max(0, Math.min(newY, maxY));
                }

                setLocation(newX, newY);

                if (checkCollision()) {
                    setLocation(lastValidX, lastValidY);
                } else {
                    lastValidX = newX;
                    lastValidY = newY;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (checkCollision()) {
                    setLocation(lastValidX, lastValidY);
                    JOptionPane.showMessageDialog(null,
                        "Cannot place here due to overlap!",
                        "Invalid Position",
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private boolean checkCollision() {
        if (getParent() instanceof Room) {
            Room parentRoom = (Room) getParent();
            Component[] components = parentRoom.getComponents();
            Rectangle bounds = getBounds();
            
            for (Component comp : components) {
                if (comp != this && comp instanceof Objects) {
                    if (bounds.intersects(comp.getBounds())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void rotate() {
        rotationAngle = (rotationAngle + 90) % 360;
        if (rotationAngle == 90 || rotationAngle == 270) {
            int temp = getWidth();
            setSize(getHeight(), temp);
        } else {
            int temp = getHeight();
            setSize(temp, getWidth());
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        g2d.rotate(Math.toRadians(rotationAngle), centerX, centerY);
        
        if (label != null && label.getIcon() != null) {
            label.getIcon().paintIcon(this, g2d, 0, 0);
        }

        if (isSelected) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        g2d.dispose();
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        repaint();
    }
}