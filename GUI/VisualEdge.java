import java.awt.*;

public class VisualEdge {
    private VisualVertex v1;
    private VisualVertex v2;
    private Shape line = null;

    public VisualEdge(VisualVertex v1, VisualVertex v2){
        this.v1 = v1;
        this.v2 = v2;
    }

    public boolean equals(VisualEdge other){
        return (v1 == other.getV1() && v2 == other.getV2());
    }

    public void setV1(VisualVertex v1){
        this.v1 = v1;
    }

    public void setV2(VisualVertex v2){
        this.v2 = v2;
    }

    public void setLine(Shape shape){
        this.line = shape;
    }

    public VisualVertex getV1(){
        return this.v1;
    }

    public  VisualVertex getV2(){
        return this.v2;
    }

    public Shape getLine(){
        return this.line;
    }
}
