import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GPanel extends JPanel {

    private final int RADIUS = 20;
    private final ArrayList<VisualVertex> circles;
    private final ArrayList<VisualEdge> edges;
    private VisualEdge curEdge;
    boolean drawingEdge = false;

    public GPanel(Solver solver){
        super();

        circles = new ArrayList<VisualVertex>(2);
        edges = new ArrayList<VisualEdge>(2);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    drawingEdge = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(e.getKeyCode() == KeyEvent.VK_SHIFT){
                    drawingEdge = false;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {//Добавление вершин
                if((e.getClickCount() == 2) && (e.getButton() == 1)){
                    boolean intersects = false;
                    for(VisualVertex v : circles){
                        if((Math.pow((double)(e.getX() - v.getX()), 2) + Math.pow((double)(e.getY() - v.getY()), 2)) <
                            Math.pow(2*(double)RADIUS, 2) + 10){
                            intersects = true;
                            break;
                        }
                    }
                    if(!intersects && !(e.getX() < RADIUS + 10 || e.getY() < RADIUS + 10
                            || e.getX() > size().width - RADIUS - 10 || e.getY() > size().height - RADIUS - 10)) {
                        circles.add(new VisualVertex(e.getX(), e.getY(), circles.size() + 1, Color.GREEN));
                        getParent().repaint();
                    }
                }
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(drawingEdge) {
                    curEdge = new VisualEdge (e.getX(), e.getY(), e.getX(), e.getY(), e.getY(), 0);
                    getParent().repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {//Рисование прямой
                super.mouseReleased(e);
                if(drawingEdge && curEdge != null && (e.getX() != curEdge.getX1() || e.getY() != curEdge.getY1())) {
                    boolean isInFirst = false;
                    boolean isInSecond = false;
                    VisualVertex first = null;
                    VisualVertex second = null;
                    for(VisualVertex v : circles){
                        if((Math.pow((double)(curEdge.getX1() - v.getX()), 2) + Math.pow((double)(curEdge.getY1() - v.getY()), 2)) < RADIUS*RADIUS){
                            first = v;
                            isInFirst = true;
                        }
                        if((Math.pow((double)(curEdge.getX2() - v.getX()), 2) + Math.pow((double)(curEdge.getY2() - v.getY()), 2)) < RADIUS*RADIUS){
                            second = v;
                            isInSecond = true;
                        }
                    }
                    if(isInFirst && isInSecond && first != second){
                        curEdge.setId1(first.getId());
                        curEdge.setId2(second.getId());
                        boolean exists = false;
                        for(VisualEdge edge: edges){
                            if(curEdge.equals(edge)){
                                exists = true;
                                break;
                            }
                        }
                        if(!exists){
                            edges.add(new VisualEdge(first.getX(), first.getY(), second.getX(), second.getY(), first.getId(), second.getId()));
                        }
                    }
                }
                curEdge = null;
                getParent().repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e){
                super.mouseEntered(e);
                setFocusable(true);
                requestFocusInWindow();
            }

            public void mouseExited(MouseEvent e){
                drawingEdge = false;
                super.mouseExited(e);
                setFocusable(false);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if(drawingEdge && curEdge != null) {
                    curEdge = new VisualEdge(curEdge.getX1(), curEdge.getY1(), e.getX(), e.getY(), curEdge.getId1(), e.getY());
                }
                else{
                    curEdge = null;
                }
                getParent().repaint();
            }
        });
    }

    public void clear(){
        this.circles.clear();
        this.edges.clear();
        getParent().repaint();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        for(VisualEdge e : edges){
            g.drawLine(e.getX1(), e.getY1(), e.getX2(), e.getY2());
        }
        for(VisualVertex p: circles) {
            g.setColor(p.getColor());
            g.fillOval(p.getX() - RADIUS, p.getY() - RADIUS, RADIUS*2, RADIUS*2);
            g.setColor(Color.BLACK);
            g.drawOval(p.getX() - RADIUS, p.getY() - RADIUS, RADIUS*2, RADIUS*2);
            String s = Integer.toString(circles.indexOf(p) + 1);
            g.drawString(s, p.getX() - 3*(s.length()), p.getY() + 4);
        }
        if(curEdge != null){
            g.drawLine(curEdge.getX1(), curEdge.getY1(), curEdge.getX2(), curEdge.getY2());
        }

        for(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> p: edges){
            g.setColor(Color.BLACK);
            g.drawLine(p.first().first(), p.first().second(), p.second().first(), p.second().second());
        }

        if(mainCircle.first() != -1){
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(mainCircle.first() - RADIUS, mainCircle.second() - RADIUS, RADIUS*2, RADIUS*2);
        }

        if(curCircle.first() != -1){
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(curCircle.first() - RADIUS, curCircle.second() - RADIUS, RADIUS*2, RADIUS*2);
        }



    }

    public Pair<Integer, Integer> getMainVertex(){
        return mainCircle;
    };

    public void setMainVertex(){
        mainCircle = curCircle;
        //mainCircle.setF(curCircle.first());
        //mainCircle.setS(curCircle.second());
    }

}
