import javax.swing.*;
import java.awt.*;

public class Controlpanel extends JPanel {
    Toolbar toolbar;
    JPanel toolbarPanel;
    Drop drop, drop2;

    Controlpanel() {
        this.setBackground(Color.black);
        this.setLayout(null);

        toolbar = new Toolbar();
        toolbar.setBounds(0, 0, 390, 40);
        add(toolbar);

        drop = new Drop(0);
        drop.setBounds(0, 40, 390, 550);
        add(drop);
        drop.repaint();
        drop.revalidate();


        JToggleButton snapButton = new JToggleButton("Snap to Grid: ON");
        snapButton.setSelected(true); // Start with snap enabled or disabled
        snapButton.addActionListener(e -> {
            Frame.getCanvasPanel().toggleSnapToGrid();
            updateSnapButtonText(snapButton);
        });
        snapButton.setBounds(190, 740, 190, 40);
        drop.styleButton(snapButton);
        add(snapButton);

    }


    private void updateSnapButtonText(JToggleButton button) {
        if (button.isSelected()) {
            button.setText("Snap to Grid: ON");
        } else {
            button.setText("Snap to Wall: ON");
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (getParent() == null)
            return new Dimension(100, 100);
        int parentWidth = getParent().getWidth();
        return new Dimension(parentWidth / 4, getParent().getHeight());
    }

}
