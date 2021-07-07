import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GPanel extends JPanel {

    private final int RADIUS = 20;
    private final ArrayList<Pair<Integer, Integer>> circles;
    private final ArrayList<Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>> edges;
    int x1, y1, x2, y2;
    int i1, i2;
    //private final ArrayList<Pair<Integer, Integer>> smescheniePoXY = new ArrayList<>();
    //private ArrayList<Integer> edgesForDraw = new ArrayList<>();

    public GPanel(){
        super();

        circles = new ArrayList<Pair<Integer, Integer>>(2);
        edges = new ArrayList<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {//Добавление вершин
                if((e.getClickCount() == 2) && (e.getButton() == 1)){
                    if (isVertex(e.getX(), e.getY())) {
                        circles.add(new Pair(e.getX(), e.getY()));
                        System.out.println("plus ver");
                    }
                    else
                        System.out.println("There are already ver");
                }
                getParent().repaint();
                super.mouseClicked(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {//Рисование прямой
                i2 = isVertexForEdge(e.getX(), e.getY());
                if (i1 != -1 && i2 != -1 && i1 != i2 && e.getX() != x1 && e.getY() != y1) {
                    x2 = e.getX();
                    y2 = e.getY();
                    System.out.println(x1 + " " + y1 + " ; " + x2 + " " + y2);
                    getParent().repaint();
                    super.mouseReleased(e);
                    int c1x, c1y, c2x, c2y;
                    c1x = circles.get(i1).first();
                    c1y = circles.get(i1).second();
                    c2x = circles.get(i2).first();
                    c2y = circles.get(i2).second();
                    double koeff = Math.sqrt(Math.pow(c1x - c2x, 2) + Math.pow(c1y - c2y, 2)) / RADIUS;
                    int smescheniePoX = (int) ((c1x - c2x) / koeff);
                    int smescheniePoY = (int) ((c1y - c2y) / koeff);
                    Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> edge1 = new Pair(new Pair(c1x - smescheniePoX,c1y - smescheniePoY),new Pair(c2x + smescheniePoX,c2y + smescheniePoY));
                    Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> edge2 = new Pair(new Pair(c2x + smescheniePoX,c2y + smescheniePoY),new Pair(c1x - smescheniePoX,c1y - smescheniePoY));
                    System.out.println((c1x - smescheniePoX) + " " + (c1y - smescheniePoY) + " " + (c2x + smescheniePoX) + " " + (c2y + smescheniePoY));
                    System.out.println("Edge is added");
                    System.out.println(edge1.first().first()+edge1.first().second());
                    edges.add(edge1);
                } else {        //...
                    System.out.println("EDGE NOT IN VERT2  x1 = " + x1 + " y1 = " + y1 + " " + e.getX() + " " + e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {//Рисование прямой
                super.mouseReleased(e);
                i1 = isVertexForEdge(e.getX(), e.getY());
                if (i1 != -1) {
                    System.out.println(e.getX() + " " + e.getY());
                    x1 = e.getX();
                    y1 = e.getY();
                } else {            //...
                    System.out.println("edge not in vert " + e.getX() + " " + e.getY());
                    x1 = -1;
                    y1 = -1;
                }

            }
        });
    }

    private boolean isVertex(int x, int y) //........
    {
        for (int i = 0; i < circles.size(); i++) {
            if (Math.sqrt(Math.pow((x - circles.get(i).first()), 2) + Math.pow((y - circles.get(i).second()), 2)) < RADIUS*2+2) {
                return false;
            }
        }
        return true; //если не задевает другую вершину
    }

    private int isVertexForEdge(int x, int y) //........
    {
        for (int i = 0; i < circles.size(); i++) {
            //System.out.println("x = " + x + " vertX = " + verticesX.get(i) + " y = " + y + " vertY = " + verticesY.get(i));
            if (Math.sqrt(Math.pow((x - circles.get(i).first()), 2) + Math.pow((y - circles.get(i).second()), 2)) < RADIUS+1) {
                //System.out.println("i = " + i);
                return i;    //если в вершине #i
            }
        }
        return -1;
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
        for(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> p: edges){
            g.setColor(Color.BLACK);
            g.drawLine(p.first().first(), p.first().second(), p.second().first(), p.second().second());

        }
    }
}
