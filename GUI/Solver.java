import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

public class Solver {
    private Vertex init;
    private Vertex prev;
    private final ArrayList<Vertex> vertSet;
    private PriorityQueue<Vertex> front;

    public Solver() {
        this.init = null;
        this.prev = null;
        this.vertSet = new ArrayList<Vertex>(0);
        this.front = new PriorityQueue<Vertex>(1, distComp);
    }

    public static Comparator<Vertex> distComp = new Comparator<Vertex>() {
        @Override
        public int compare(Vertex o1, Vertex o2) {
            return (int)(o1.getPathLen() - o2.getPathLen());
        }
    };

    public int addVertex() {
        this.vertSet.add(new Vertex(vertSet.size() + 1));
        return this.vertSet.size();
    }

    public void deleteVertex(int id) {
        Vertex toDel = this.vertSet.get(id - 1);
        for (Map.Entry<Vertex, Integer> pair : toDel.getAdjList().entrySet()) {
            pair.getKey().getAdjList().remove(toDel);
        }
        this.vertSet.remove(toDel);
        for (int i = id; i < this.vertSet.size() + 1; i++) {
            this.vertSet.get(i - 1).setId(i);
        }
    }

    public Vertex getVertex(int id) {
        return this.vertSet.get(id - 1);
    }

    public void addEdge(int from, int to, int dist){
        Vertex fromVer = this.vertSet.get(from - 1);
        Vertex toVer = this.vertSet.get(to - 1);
        if(!fromVer.getAdjList().containsKey(toVer)) {
            fromVer.addToAdjList(toVer, dist);
            toVer.addToAdjList(fromVer, dist);
        }
    }

    public void setEdgeWeight(int from, int to, int weight){
        Vertex s = getVertex(from);
        Vertex d = getVertex(to);
        if(d != null && s != null && s.getAdjList().containsKey(d)) {
            s.getAdjList().replace(d, weight);
            d.getAdjList().replace(s, weight);
        }
    }

    public void deleteEdge(int from, int to){
        Vertex fromVer = this.vertSet.get(from - 1);
        Vertex toVer = this.vertSet.get(to - 1);
        fromVer.getAdjList().remove(toVer);
        toVer.getAdjList().remove(fromVer);
    }

    public void setInit(int init){
        this.init = this.vertSet.get(init - 1);
        this.front.add(this.init);
    }

    boolean step(CustomLogger logger){
        if(prev != null){
            prev.setColor(Colors.COLOR4);
        }
        if(!(front.isEmpty())){
            Vertex current = front.poll();
            current.setColor(Colors.COLOR2);
            prev = current;
            if(current == init){
                current.setPathLen(0);
            }
            logger.addMessage(formInfo(current));

            for(Map.Entry<Vertex, Integer> next : current.getAdjList().entrySet()){
                if((next.getKey().getColor() != Colors.COLOR4) &&
                        (current.getPathLen() + next.getValue() < next.getKey().getPathLen())){
                    next.getKey().setParent(current);
                    next.getKey().setPathLen(current.getPathLen() + next.getValue());
                    if(!(front.contains(next.getKey()))){
                        next.getKey().setColor(Colors.COLOR3);
                        front.add(next.getKey());
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void clear(){
        this.init = null;
        this.prev = null;
        front.clear();
        vertSet.clear();
    }

    public void reset(){
        this.init = null;
        this.prev = null;
        for(Vertex v : vertSet){
            v.setPathLen(Integer.MAX_VALUE);
            v.setParent(null);
            v.setColor(Colors.COLOR1);
        }
        front.clear();
    }

    private String formInfo(Vertex v){
        String info = v.getId() + ". ";
        String path = "";
        Vertex par = v.getParent();
        if(par == null && !(v == init)){
            path = "Путь: не существует\n";
            info = info + path + "Длина пути: " + "\u221E";
        }
        else {
            while (!(par == null)) {
                path = par.getId() + " " + path;
                par = par.getParent();
            }
            path = "Путь: " + path + v.getId() + "\n";
            info = info + path + "Длина пути: " + Integer.toString(v.getPathLen());
        }
        return info;
    }

    ArrayList<String> results(){
        ArrayList<String> res = new ArrayList<String>(0);
        for(Vertex v : this.vertSet){
            res.add(formInfo(v));
        }
        return res;
    }
}
