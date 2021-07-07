import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GPanel extends JPanel {

    private final int RADIUS = 20;
    private final ArrayList<Pair<Integer, Integer>> circles;

    public GPanel(){
        super();

        circles = new ArrayList<Pair<Integer, Integer>>(2);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if((e.getClickCount() == 2) && (e.getButton() == 1)){

                    circles.add(new Pair(e.getX(), e.getY()));
                }
                getParent().repaint();
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
            }
        });
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        for(Pair<Integer, Integer> p: circles) {
            g.setColor(Color.GREEN);
            g.fillOval(p.first() - RADIUS, p.second() - RADIUS, RADIUS*2, RADIUS*2);
            g.setColor(Color.BLACK);
            g.drawOval(p.first() - RADIUS, p.second() - RADIUS, RADIUS*2, RADIUS*2);
            String s = Integer.toString(circles.indexOf(p) + 1);
            g.drawString(s, p.first() - 3*(s.length()), p.second() + 4);
        }
    }
}
