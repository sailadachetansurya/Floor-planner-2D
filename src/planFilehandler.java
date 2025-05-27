import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class planFilehandler {
    private static final String LOAD_DIALOG_TITLE = "Load Floor Plan";

    
    public void open() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(LOAD_DIALOG_TITLE);
    
        int userSelection = fileChooser.showOpenDialog(Frame.getControlPanel());
    
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            if (!fileToLoad.exists()) {
                JOptionPane.showMessageDialog(Frame.getControlPanel(), 
                    "File does not exist: " + fileToLoad.getAbsolutePath());
                return;
            }
            
            try (FileInputStream fis = new FileInputStream(fileToLoad);
                 ObjectInputStream in = new ObjectInputStream(fis)) {
                @SuppressWarnings("unchecked")
                ArrayList<Room> rooms = (ArrayList<Room>) in.readObject();
                Frame.getCanvasPanel().getRooms().clear(); // Clear existing rooms
                Frame.getCanvasPanel().getRooms().addAll(rooms); // Add loaded rooms
                
                // Optionally, update the canvas panel to reflect the loaded rooms
                Frame.getCanvasPanel().removeAll(); // Clear the current display
                for (Room room : rooms) {
                    Frame.getCanvasPanel().add(room); // Add each room to the panel
                }
                Frame.getCanvasPanel().revalidate(); // Refresh the layout
                Frame.getCanvasPanel().repaint(); // Repaint the canvas
                
                JOptionPane.showMessageDialog(Frame.getControlPanel(),
                        "Floor plan loaded successfully from " + fileToLoad.getAbsolutePath());
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(Frame.getControlPanel(), "Error loading floor plan: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("file opened");
    }
    public void New() {
        // some code
        Frame.getCanvasPanel().rooms.clear();
        Frame.getCanvasPanel().removeAll();
        Frame.getCanvasPanel().revalidate();
        Frame.getCanvasPanel().repaint();
        System.out.println("Canvas cleared");
    }

    public void Save() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Floor Plan");

        int userSelection = fileChooser.showSaveDialog(Frame.getControlPanel());

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                ArrayList<Room> rooms = Frame.getCanvasPanel().getRooms();
                out.writeObject(rooms);
                JOptionPane.showMessageDialog(Frame.getControlPanel(),
                        "Floor plan saved successfully to " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Frame.getControlPanel(), "Error saving floor plan: " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("file saved");
    }

    public void help() {
        // new button functionality
        Frame.CanvasPanel.deleteRoom();
        System.out.println("help on the way ... ");
    }

    public void export() {
        // export
    }

}