import javax.swing.*;
import java.awt.*;

public class GPanel extends JPanel {
    public GPanel(){

    }

    @Override
    public void paintComponent(Graphics g){
        g.drawRect(10, 10, 60, 40);
    }
}
