import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Toolbar extends JToolBar {
    private static final String[] BUTTON_TEXTS = {"OPEN", "SAVE", "CLEAR", "DELETE"};
    private static final int BUTTON_HEIGHT = 80;
    private static final Font CUSTOM_FONT = new Font("Roboto", Font.PLAIN, 14);
    // If Roboto is not available, use: new Font("Sans-Serif", Font.PLAIN, 14);

    public Toolbar() {
        super();
        initializeToolbar();
    }

    private void initializeToolbar() {
        setFloatable(false);
        setLayout(new GridLayout(1, BUTTON_TEXTS.length, 0, 0));
        setBorder(null);
        setBackground(Color.BLACK);

        for (String text : BUTTON_TEXTS) {
            add(createStyledButton(text));
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(CUSTOM_FONT);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);

        //switch break for each of the  buttons
        switch (text) {
            case "OPEN":
            button.addActionListener(e -> new planFilehandler().open());
            break;
        case "SAVE":
            button.addActionListener(e -> new planFilehandler().Save());
            break;
        case "CLEAR":
            button.addActionListener(e -> new planFilehandler().New());
            break;
        case  "DELETE":
            button.addActionListener(e -> new planFilehandler().help());
            break;
        }

        
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



    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width, BUTTON_HEIGHT);
    }
}