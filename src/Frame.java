import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

public class Frame extends JFrame implements ActionListener{
    static Controlpanel controlPanel;
    static canvasPanel CanvasPanel;

    Frame(String a) {

        this.setTitle(a);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.getContentPane().setBackground(Color.gray);
        this.setResizable(false);
        //this.setLocationRelativeTo(null);
        this.setLayout(null);

        ImageIcon icon = new ImageIcon("lib\\icon.png");
        Image customIcon = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        this.setIconImage(customIcon);

        // addcomponents
        controlPanel = new Controlpanel();
        CanvasPanel  = new canvasPanel();
        

       JButton exportButton = new JButton("Export");
        exportButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                CanvasPanel.exportAsPNG(fileToSave);
            }
        });
        exportButton.setBounds(0, 740, 190, 40);
        controlPanel.drop.styleButton(exportButton);
        controlPanel.add(exportButton);
        
        

    }

    public static canvasPanel getCanvasPanel() {
        return CanvasPanel;
    }

    public static Controlpanel getControlPanel() {
        return controlPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("clicked!!");          }

}
