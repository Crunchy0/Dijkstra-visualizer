import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;

public class GPanel extends JPanel {

    private final int RADIUS = 20;
    private final ArrayList<VisualVertex> circles;
    private final ArrayList<VisualEdge> edges;

    private VisualEdge edgeDrawn;
    private VisualEdge chosenEdge;
    private VisualVertex chosenCircle;
    private VisualVertex consideredCircle;

    boolean drawingEdge = false;
    private Boolean ec;

    public GPanel(Solver solver, Boolean edgeChosen){
        super();

        circles = new ArrayList<VisualVertex>(2);
        edges = new ArrayList<VisualEdge>(2);

        ec = edgeChosen;
        chosenCircle = null;
        consideredCircle = null;

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
            public void mouseClicked(MouseEvent e) {
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
                else if((e.getClickCount() == 1) && (e.getButton() == 1)){
                    chosenCircle = chooseCircle(e.getX(), e.getY());
                    if(chosenCircle == null){
                        ArrayList<VisualEdge> toChose = new ArrayList<VisualEdge>(5);
                        for(VisualEdge edge : edges){
                            if(edge.getLine().contains(e.getX(), e.getY())){
                                toChose.add(edge);
                            }
                        }
                        double closeEnough = 0.0;
                        if(toChose.isEmpty()){
                            chosenEdge = null;
                        }
                        for(VisualEdge edge : toChose){
                            double curDistToPoint = Math.sqrt(Math.pow(e.getX() - edge.getV1().getX(),2) + Math.pow(e.getY() - edge.getV1().getY(),2))
                                    + Math.sqrt(Math.pow(e.getX() - edge.getV2().getX(),2) + Math.pow(e.getY() - edge.getV2().getY(),2));
                            double diff = curDistToPoint - Math.sqrt(Math.pow(edge.getV2().getX() - edge.getV1().getX(),2) + Math.pow(edge.getV2().getY() - edge.getV1().getY(),2));
                            if(closeEnough == 0.0){
                                closeEnough = diff;
                                chosenEdge = edge;
                            }
                            else if(diff < closeEnough){
                                closeEnough = diff;
                                chosenEdge = edge;
                            }
                        }
                    }
                    else{
                        chosenEdge = null;
                    }
                    ec = !(chosenEdge == null);
                    getParent().repaint();
                }

                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(drawingEdge) {
                    VisualVertex vertex = new VisualVertex(e.getX(), e.getY(), 0, Color.BLACK);
                    edgeDrawn = new VisualEdge (vertex, vertex);
                    getParent().repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(drawingEdge && edgeDrawn != null && (e.getX() != edgeDrawn.getV1().getX() || e.getY() != edgeDrawn.getV1().getY())) {
                    boolean isInFirst = false;
                    boolean isInSecond = false;
                    VisualVertex first = null;
                    VisualVertex second = null;
                    for(VisualVertex v : circles){
                        if((Math.pow((double)(edgeDrawn.getV1().getX() - v.getX()), 2) + Math.pow((double)(edgeDrawn.getV1().getY() - v.getY()), 2)) < RADIUS*RADIUS){
                            first = v;
                            isInFirst = true;
                        }
                        if((Math.pow((double)(edgeDrawn.getV2().getX() - v.getX()), 2) + Math.pow((double)(edgeDrawn.getV2().getY() - v.getY()), 2)) < RADIUS*RADIUS){
                            second = v;
                            isInSecond = true;
                        }
                    }
                    if(isInFirst && isInSecond && first != second){
                        if(first.getId() < second.getId()){
                            edgeDrawn.setV1(first);
                            edgeDrawn.setV2(second);
                        }
                        else{
                            edgeDrawn.setV2(first);
                            edgeDrawn.setV1(second);
                        }
                        boolean exists = false;
                        for(VisualEdge edge: edges){
                            if(edgeDrawn.equals(edge)){
                                exists = true;
                                break;
                            }
                        }
                        if(!exists){
                            int newWidth = (int)Math.sqrt((Math.pow(edgeDrawn.getV2().getX() - edgeDrawn.getV1().getX(),2))
                                    + (Math.pow(edgeDrawn.getV2().getY() - edgeDrawn.getV1().getY(),2)));
                            int newX = (edgeDrawn.getV2().getX() + edgeDrawn.getV1().getX())/2 - newWidth/2;
                            int newY = (edgeDrawn.getV2().getY() + edgeDrawn.getV1().getY())/2 - 1;
                            int dX = edgeDrawn.getV2().getX() - edgeDrawn.getV1().getX();
                            int dY = edgeDrawn.getV2().getY() - edgeDrawn.getV1().getY();
                            Rectangle rect = new Rectangle(newX, newY, newWidth, 3);
                            AffineTransform at = new AffineTransform();
                            at.rotate(Math.atan((double)dY/dX), newX + (float)newWidth/2, newY + 1.5f);
                            Shape shape = at.createTransformedShape(rect);

                            edgeDrawn.setLine(shape);
                            edges.add(edgeDrawn);
                        }
                        else {
                            System.out.println("exists");
                        }
                    }
                }
                edgeDrawn = null;
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
                if(drawingEdge && edgeDrawn != null) {
                    edgeDrawn.setV2(new VisualVertex(e.getX(), e.getY(), 0, Color.BLACK));
                }
                else{
                    edgeDrawn = null;
                }
                getParent().repaint();
            }
        });
    }

    public void clear(){
        this.circles.clear();
        this.edges.clear();
        this.chosenCircle = null;
        this.chosenEdge = null;
        this.consideredCircle = null;
        getParent().repaint();
    }

    private VisualVertex chooseCircle(int x, int y) //........
    {
        for (VisualVertex vertex : circles) {
            if ((Math.pow((x - vertex.getX()), 2) + Math.pow((y - vertex.getY()), 2)) < RADIUS*RADIUS + 1) {
                return vertex;
            }
        }
        return null;
    }

    public void setMainVertex(){
        consideredCircle = chosenCircle;
        chosenCircle = null;
        getParent().repaint();
    }

    public void deleteVertex(){
        if(chosenCircle != null) {
            int index = circles.indexOf(chosenCircle);
            circles.remove(chosenCircle);
            edges.removeIf(edge -> chosenCircle.getId() == edge.getV1().getId() || chosenCircle.getId() == edge.getV2().getId());
            for(int i = index; i < circles.size(); i++){
                circles.get(i).setId(i + 1);
            }
            chosenCircle = null;
            getParent().repaint();
        }
    }

    public void deleteEdge(){
        if(chosenEdge != null){
            edges.remove(chosenEdge);
            chosenEdge = null;
            getParent().repaint();
        }
    }

    public void otladka(){
        System.out.println("Текущее состояние графа:");
        for(VisualVertex vertex: circles) {
            System.out.println("Вершина " + vertex.getId());
        }
        for(VisualEdge edge : edges){
            System.out.println("Ребро " + edge.getV1().getId() + " " + edge.getV2().getId());
        }
    }

  public void paintComponent(Graphics g2){
        Graphics2D g = (Graphics2D)g2;
        super.paintComponent(g);
        for(VisualEdge e : edges){
            g.setColor(Color.BLACK);
            g.draw(e.getLine());
            g.fill(e.getLine());
        }
        if(chosenEdge != null){
            g.setColor(Color.ORANGE);
            g.draw(chosenEdge.getLine());
            g.fill(chosenEdge.getLine());
        }
        for(VisualVertex p: circles) {
            g.setColor(p.getColor());
            g.fillOval(p.getX() - RADIUS, p.getY() - RADIUS, RADIUS*2, RADIUS*2);
            g.setColor(Color.BLACK);
            g.drawOval(p.getX() - RADIUS, p.getY() - RADIUS, RADIUS*2, RADIUS*2);
            String s = Integer.toString(p.getId());
            g.drawString(s, p.getX() - 3*(s.length()), p.getY() + 4);
        }
        if(edgeDrawn != null){
            g.drawLine(edgeDrawn.getV1().getX(), edgeDrawn.getV1().getY(), edgeDrawn.getV2().getX(), edgeDrawn.getV2().getY());
        }
        if(consideredCircle != null){
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(3));
            g.drawOval(consideredCircle.getX() - RADIUS, consideredCircle.getY() - RADIUS, RADIUS*2, RADIUS*2);
        }

        if(chosenCircle != null){
            g.setColor(Color.YELLOW);
            g.setStroke(new BasicStroke(3));
            g.drawOval(chosenCircle.getX() - RADIUS, chosenCircle.getY() - RADIUS, RADIUS*2, RADIUS*2);
        }
    }
}
