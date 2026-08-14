import javax.swing.*;
import java.awt.*;

public class Main extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw a square
        g.drawRect(100, 100, 200, 200);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Square");

        Main square = new Main();

        frame.add(square);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}