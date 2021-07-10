import java.util.ArrayList;

public class CustomLogger {
    private final ArrayList<String> messages;
    private int nextMessageIndex;
    private boolean endReached;

    public CustomLogger(int initCapacity){
        this.messages = new ArrayList<String>(initCapacity);
        this.nextMessageIndex = 0;
        this.endReached = true;
    }

    public void addMessage(String message){
        if(this.isEndReached()){
            this.endReached = false;
        }
        this.messages.add(message);
    }

    public String getNextMessage(){
        if(this.isEndReached()){
            return "";
        }
        if(this.nextMessageIndex == (this.messages.size() - 1)){
            this.endReached = true;
            return this.messages.get(this.nextMessageIndex++);
        }
        return this.messages.get(this.nextMessageIndex++);
    }

    public boolean isEndReached(){
        return this.endReached;
    }
}
