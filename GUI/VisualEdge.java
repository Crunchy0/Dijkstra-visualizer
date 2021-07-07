public class VisualEdge {
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int id1;
    private int id2;

    public VisualEdge(int x1, int y1, int x2, int y2, int id1, int id2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.id1 = id1;
        this.id2 = id2;
    }

    public boolean equals(VisualEdge other){
        if (this.id1 == other.id1 && this.id2 == other.id2 || this.id1 == other.id2 && this.id2 == other.id1){
            return true;
        }
        return false;
    }

    public int getX1(){
        return this.x1;
    }

    public int getY1(){
        return this.y1;
    }

    public int getX2(){
        return this.x2;
    }

    public int getY2(){
        return this.y2;
    }

    public int getId1(){
        return this.id1;
    }

    public int getId2(){
        return this.id2;
    }

    public void setFirstCoord(int x1, int y1){
        this.x1 = x1;
        this.y1 = y1;
    }

    public  void setSecondCoord(int x2, int y2){
        this.x2 = x2;
        this.y2 = y2;
    }

    public void setId1(int id1){
        this.id1 = id1;
    }

    public void setId2(int id2){
        this.id2 = id2;
    }
}
