import java.awt.*;

public class App {
    public static void main(String[] args) throws Exception {


        Frame frame = new Frame("2D Floor Planner");
        frame.add(Frame.getControlPanel(), BorderLayout.WEST);
        frame.add(Frame.CanvasPanel,BorderLayout.CENTER);
        int x = frame.getWidth();
        int y = frame.getHeight();
        Frame.CanvasPanel.setBounds(x/4,0,3*x/4,y);
        Frame.controlPanel.setBounds(0,0,x/4,y);
        Frame.CanvasPanel.getHeight();
        frame.repaint();
        frame.revalidate();
        
    }
}
