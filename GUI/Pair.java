public class Pair<F, S> {
    private final F f;
    private final S s;

    public Pair(F f, S s){
        this.f = f;
        this.s = s;
    }

    public F first(){
        return this.f;
    }
    public S second(){
        return this.s;
    }

}
