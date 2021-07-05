import javax.swing.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
        Solver sol = new Solver();

        FileHandler fh = new FileHandler("file.txt");
        ArrayList<Integer> input = fh.load();
        int i = 1;
        for(int j = 0; j < input.get(0); j++){
            sol.addVertex();
        }
        for(int j = 1; j < input.size(); j++){
            if(input.get(j) != -1) {
                sol.addEdge(i, input.get(j), input.get(j + 1));
                j++;
                continue;
            }
            i++;
        }
        /*sol.addEdge(1, 2, 2);
        sol.addEdge(1, 3, 4);
        sol.addEdge(2, 3, 1);*/
        sol.setInit(1);
        CustomLogger logger = new CustomLogger(1);
        boolean running = sol.step(logger);
        System.out.println(logger.getNextMessage());
        while(running){
            logger = new CustomLogger(1);
            running = sol.step(logger);
            System.out.println(logger.getNextMessage());
        }

        ArrayList<String> res = sol.results();
        for(String s : res){
            System.out.println(s);
        }
    }
}
