import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class GPanel extends JPanel {

    private final Font FONT = new Font("TimesRoman", Font.BOLD, 14);
    private final int RADIUS = 20;
    private final Solver solver;
    private final ArrayList<VisualVertex> circles;
    private final ArrayList<VisualEdge> edges;
    private VisualEdge edgeDrawn;
    private VisualEdge chosenEdge;
    private VisualVertex chosenCircle;
    private VisualVertex consideredCircle;
    boolean drawingEdge = false;
    private VisualVertex draggingCircle;
    boolean holdingCircle = false;
    boolean dragged = false;
    private boolean isEditable = true;
    private boolean choosingInit = false;

    public GPanel(Solver solver){
        super();

        this.solver = solver;

        circles = new ArrayList<VisualVertex>(2);
        edges = new ArrayList<VisualEdge>(2);

        draggingCircle = null;
        chosenCircle = null;
        consideredCircle = null;
        draggingCircle = null;

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
                super.mouseClicked(e);
                if(isEditable) {
                    Window window = (Window) getTopLevelAncestor();
                    if ((e.getClickCount() == 2) && (e.getButton() == 1)) {
                        boolean intersects = false;
                        for (VisualVertex v : circles) {
                            if ((Math.pow((double) (e.getX() - v.getX()), 2) + Math.pow((double) (e.getY() - v.getY()), 2)) <
                                    Math.pow(2 * (double) RADIUS, 2) + 10) {
                                intersects = true;
                                break;
                            }
                        }
                        if (!intersects && !(e.getX() < RADIUS + 10 || e.getY() < RADIUS + 10
                                || e.getX() > size().width - RADIUS - 10 || e.getY() > size().height - RADIUS - 10)) {
                            circles.add(new VisualVertex(e.getX(), e.getY(), solver.addVertex(), Color.GREEN));
                            if (circles.size() == 1) {
                                window.onGraphNotEmpty();
                            }
                            getParent().repaint();
                        }
                    } else if ((e.getClickCount() == 1) && (e.getButton() == 1)) {
                        chosenCircle = chooseCircle(e.getX(), e.getY());
                        if (chosenCircle == null) {
                            ArrayList<VisualEdge> toChose = new ArrayList<VisualEdge>(5);
                            for (VisualEdge edge : edges) {
                                if (edge.getLine().contains(e.getX(), e.getY())) {
                                    toChose.add(edge);
                                }
                            }
                            double closeEnough = 0.0;
                            if (toChose.isEmpty()) {
                                chosenEdge = null;
                            }
                            for (VisualEdge edge : toChose) {
                                double curDistToPoint = Math.sqrt(Math.pow(e.getX() - edge.getV1().getX(), 2) + Math.pow(e.getY() - edge.getV1().getY(), 2))
                                        + Math.sqrt(Math.pow(e.getX() - edge.getV2().getX(), 2) + Math.pow(e.getY() - edge.getV2().getY(), 2));
                                double diff = curDistToPoint - Math.sqrt(Math.pow(edge.getV2().getX() - edge.getV1().getX(), 2) + Math.pow(edge.getV2().getY() - edge.getV1().getY(), 2));
                                if (closeEnough == 0.0) {
                                    closeEnough = diff;
                                    chosenEdge = edge;
                                } else if (diff < closeEnough) {
                                    closeEnough = diff;
                                    chosenEdge = edge;
                                }
                            }
                        } else {
                            chosenEdge = null;
                            window.onEdgeUnchoice();
                            window.onVertexChoice(chosenCircle.getId());
                        }
                        if (chosenEdge != null) {
                            window.onEdgeChoice(chosenEdge.getWeight());
                        }
                        if (chosenCircle == null && chosenEdge == null) {
                            window.onEdgeUnchoice();
                        }
                        getParent().repaint();
                    }
                }
                else if(choosingInit){
                    consideredCircle = chooseCircle(e.getX(), e.getY());
                    getParent().repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(isEditable) {
                    if (drawingEdge) {
                        VisualVertex vertex = new VisualVertex(e.getX(), e.getY(), 0, Color.BLACK);
                        edgeDrawn = new VisualEdge(vertex, vertex);
                        getParent().repaint();
                    }
                    draggingCircle = chooseCircle(e.getX(), e.getY());
                    if (draggingCircle != null)
                        holdingCircle = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if(isEditable) {
                    holdingCircle = false;
                    if (drawingEdge && edgeDrawn != null && (e.getX() != edgeDrawn.getV1().getX() || e.getY() != edgeDrawn.getV1().getY())) {
                        boolean isInFirst = false;
                        boolean isInSecond = false;
                        VisualVertex first = null;
                        VisualVertex second = null;
                        for (VisualVertex v : circles) {
                            if ((Math.pow((double) (edgeDrawn.getV1().getX() - v.getX()), 2) + Math.pow((double) (edgeDrawn.getV1().getY() - v.getY()), 2)) < RADIUS * RADIUS) {
                                first = v;
                                isInFirst = true;
                            }
                            if ((Math.pow((double) (edgeDrawn.getV2().getX() - v.getX()), 2) + Math.pow((double) (edgeDrawn.getV2().getY() - v.getY()), 2)) < RADIUS * RADIUS) {
                                second = v;
                                isInSecond = true;
                            }
                        }
                        if (isInFirst && isInSecond && first != second) {
                            if (first.getId() < second.getId()) {
                                edgeDrawn.setV1(first);
                                edgeDrawn.setV2(second);
                            } else {
                                edgeDrawn.setV2(first);
                                edgeDrawn.setV1(second);
                            }
                            boolean exists = false;
                            for (VisualEdge edge : edges) {
                                if (edgeDrawn.equals(edge)) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                setShape(edgeDrawn);
                                edges.add(edgeDrawn);
                                solver.addEdge(edgeDrawn.getV1().getId(), edgeDrawn.getV2().getId(), edgeDrawn.getWeight());
                            }
                        }
                    }
                    edgeDrawn = null;
                    getParent().repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e){
                super.mouseEntered(e);
                if(isEditable) {
                    setFocusable(true);
                    requestFocusInWindow();
                }
            }

            public void mouseExited(MouseEvent e){
                super.mouseExited(e);
                if(isEditable) {
                    drawingEdge = false;
                    setFocusable(false);
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if(isEditable) {
                    if (drawingEdge && edgeDrawn != null) {
                        edgeDrawn.setV2(new VisualVertex(e.getX(), e.getY(), 0, Color.BLACK));
                    } else
                        edgeDrawn = null;

                    if (holdingCircle && !drawingEdge) {
                        dragged = false;
                        for (VisualEdge edge : edges) {
                            if (edge.getV1().getX() == draggingCircle.getX() && edge.getV1().getY() == draggingCircle.getY()) {
                                draggingCircle.setX(e.getX());
                                draggingCircle.setY(e.getY());
                                edge.setV1(draggingCircle);
                                setShape(edge);
                                dragged = true;
                            }
                            if (edge.getV2().getX() == draggingCircle.getX() && edge.getV2().getY() == draggingCircle.getY()) {
                                draggingCircle.setX(e.getX());
                                draggingCircle.setY(e.getY());
                                edge.setV2(draggingCircle);
                                setShape(edge);
                                dragged = true;
                            }
                        }
                        if (!dragged) {
                            draggingCircle.setX(e.getX());
                            draggingCircle.setY(e.getY());
                        }
                    }
                    getParent().repaint();
                }
            }
        });
    }

    public void clear(){
        this.circles.clear();
        this.edges.clear();
        this.chosenCircle = null;
        this.chosenEdge = null;
        this.consideredCircle = null;
        this.solver.clear();
        getParent().repaint();
    }

    public void unchoose(){
        this.chosenCircle = null;
        this.chosenEdge = null;
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

    public void setChoosingInit(boolean flag){
        choosingInit = flag;
    }

    public void setEditable(boolean flag){
        isEditable = flag;
    }

    public boolean start(){
        if(consideredCircle == null){
            return false;
        }
        solver.setInit(consideredCircle.getId());
        getParent().repaint();
        return true;
    }

    public void finish(){
        consideredCircle = null;
        isEditable = true;
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
            solver.deleteVertex(chosenCircle.getId());
            chosenCircle = null;
            if(circles.isEmpty()){
                Window window = (Window)getTopLevelAncestor();
                window.onGraphEmpty();
            }
            getParent().repaint();
        }
    }

    public void deleteEdge(){
        if(chosenEdge != null){
            edges.remove(chosenEdge);
            solver.deleteEdge(chosenEdge.getV1().getId(), chosenEdge.getV2().getId());
            chosenEdge = null;
            getParent().repaint();
        }
    }

    public void setShape(VisualEdge edgeDrawn){
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
    }

    public void load(){
        FileHandler fh = new FileHandler();
        ArrayList<Integer> loaded =  fh.load();
        if(!loaded.isEmpty()) {
            clear();
            int number = loaded.get(0);
            int vId = 1;
            for (int i = 0; i < number; i++) {
                circles.add(new VisualVertex(loaded.get(vId + i*2), loaded.get(vId + i*2 + 1), solver.addVertex(), Color.GREEN));
            }
            for (int j = number*2 + 1; j < loaded.size(); j++) {
                if (loaded.get(j) == -1) {
                    vId++;
                } else {
                    int destVer = loaded.get(j++);
                    int weight = loaded.get(j);
                    solver.addEdge(vId, destVer, weight);
                    VisualEdge addedEdge = new VisualEdge(circles.get(vId - 1), circles.get(destVer - 1));
                    addedEdge.setWeight(weight);
                    setShape(addedEdge);
                    edges.add(addedEdge);
                }
            }
            Window window = (Window)getTopLevelAncestor();
            window.onGraphNotEmpty();
        }
    }

    public void save(){
        edges.sort(edgeComparator);
        FileHandler fh = new FileHandler();
        fh.save(circles, edges);
    }

    public static Comparator<VisualEdge> edgeComparator = new Comparator<VisualEdge>() {
        @Override
        public int compare(VisualEdge o1, VisualEdge o2) {
            return (int)(o1.getV1().getId() - o2.getV1().getId());
        }
    };

    public void otladka(){
        System.out.println("Текущее состояние графа:");
        for(VisualVertex vertex: circles) {
            System.out.println("Вершина " + solver.getVertex(vertex.getId()).getId());
        }
        for(VisualEdge edge : edges){
            System.out.println("Ребро " + edge.getV1().getId() + " " + edge.getV2().getId());
        }
    }

    public void setEdgeWeight(int w){
        chosenEdge.setWeight(w);
        solver.setEdgeWeight(chosenEdge.getV1().getId(), chosenEdge.getV2().getId(), w);
        getParent().repaint();
    }

    public void paintComponent(Graphics g2){
        Graphics2D g = (Graphics2D)g2;
        super.paintComponent(g);
        for(VisualEdge e : edges){
            g.setColor(Color.BLACK);
            g.draw(e.getLine());
            g.fill(e.getLine());
            String weight = Integer.toString(e.getWeight());
            int xOffset = 0;
            int yOffset = 0;
            double tan = (double)(e.getV2().getY() - e.getV1().getY())/(e.getV2().getX() - e.getV1().getX());
            if(tan < 0){
                xOffset = (e.getV1().getX() + e.getV2().getX())/2 - weight.length()*6 - 11;
                yOffset = (e.getV1().getY() + e.getV2().getY())/2 - 6;
            }
            else{
                xOffset = (e.getV1().getX() + e.getV2().getX())/2 - weight.length()*6 - 11;
                yOffset = (e.getV1().getY() + e.getV2().getY())/2 + 14;// + weight.length()*10;
            }

            g.drawString(weight, xOffset, yOffset);
        }
        if(chosenEdge != null){
            g.setColor(Color.ORANGE);
            g.draw(chosenEdge.getLine());
            g.fill(chosenEdge.getLine());
        }
        for(VisualVertex p: circles) {
            Color col = Color.BLACK;
            Colors color = solver.getVertex(p.getId()).getColor();
            int pathLen = solver.getVertex(p.getId()).getPathLen();
            String pLen = "";
            if(pathLen == Integer.MAX_VALUE){
                pLen = "\u221E";
            }
            else{
                pLen = Integer.toString(pathLen);
            }
            switch (color){
                case COLOR1 -> col = Color.GREEN;
                case COLOR2 -> col = Color.PINK;
                case COLOR3 -> col = Color.ORANGE;
<<<<<<< Updated upstream
                case COLOR4 -> col = Color.BLUE;
=======
                case COLOR4 -> col = Color.lightGray;//Было BLUE
>>>>>>> Stashed changes
            }
            g.setColor(col);
            //g.setColor(p.getColor());
            g.fillOval(p.getX() - RADIUS, p.getY() - RADIUS, RADIUS*2, RADIUS*2);
            g.setColor(Color.BLACK);
            g.drawOval(p.getX() - RADIUS, p.getY() - RADIUS, RADIUS*2, RADIUS*2);
            String s = Integer.toString(p.getId());
            g.setFont(FONT);
            g.drawString(s, p.getX() - 3 - 4*(s.length() - 1), p.getY() + 6);
            g.setColor(Color.WHITE);
            g.fillRect(p.getX() - 37 - 7*(pLen.length() - 1), p.getY() + 6, 8*(pLen.length() + 1), 14);
            g.setColor(Color.BLACK);
            g.drawRect(p.getX() - 37 - 7*(pLen.length() - 1), p.getY() + 6, 8*(pLen.length() + 1), 14);
            g.drawString(pLen, p.getX() - 35 - 7*(pLen.length() - 1), p.getY() + 18);
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