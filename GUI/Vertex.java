import java.util.HashMap;
import javax.swing.*;

enum Colors{COLOR1, COLOR2, COLOR3, COLOR4}

public class Vertex {
    private int id;
    private int pathLen;
    private Vertex parent;
    private HashMap<Vertex, Integer> adjList;
    private Colors color;

    public Vertex(int id){
        this.id = id;
        this.pathLen = Integer.MAX_VALUE;
        this.parent = null;
        this.adjList = new HashMap<Vertex, Integer>(0);
        this.color = Colors.COLOR1;
    }

    public void addToAdjList(Vertex v, Integer dist){
        this.adjList.put(v, dist);
    }

    public void setPathLen(int pathLen){
        this.pathLen = pathLen;
    }

    public void setParent(Vertex v){
        this.parent = v;
    }

    public void setColor(Colors color){
        this.color = color;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public int getPathLen(){
        return this.pathLen;
    }

    public Vertex getParent(){
        return this.parent;
    }

    public Colors getColor(){
        return this.color;
    }

    public HashMap<Vertex, Integer> getAdjList(){
        return this.adjList;
    }
}
