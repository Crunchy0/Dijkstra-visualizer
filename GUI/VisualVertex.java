import java.awt.*;

public class VisualVertex {
    private int x;
    private int y;
    private int id;
    private Color color;

    public VisualVertex(int x, int y, int id, Color color){
        this.x = x;
        this.y = y;
        this.id = id;
        this.color = color;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int getId(){
        return this.id;
    }

    public Color getColor(){
        return this.color;
    }
}
