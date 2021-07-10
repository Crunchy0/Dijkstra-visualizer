import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {
    private final File file;

<<<<<<< Updated upstream
    public FileHandler(String path){
        file = new File(path);
=======
    public FileHandler(){
        file = null;
>>>>>>> Stashed changes
    }

    public void save(ArrayList<VisualVertex> vertices, ArrayList<VisualEdge> edges) {
        JFileChooser chooser = new JFileChooser();
        chooser.showDialog(null, "Сохранить");
        file = chooser.getSelectedFile();
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(vertices.size() + "\n");
            for(VisualVertex v : vertices){
                fw.write(v.getX() + " " + v.getY() + "\n");
            }
            int vId = 1;
            for(VisualEdge e : edges){
                while(e.getV1().getId() != vId){
                    vId++;
                    fw.write("/\n");
                }
                fw.write(e.getV2().getId() + " " + e.getWeight() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }

    public ArrayList<Integer> load(){
<<<<<<< Updated upstream
=======
        JFileChooser chooser = new JFileChooser();
        chooser.showDialog(null, "Загрузить");
        file = chooser.getSelectedFile();
>>>>>>> Stashed changes
        ArrayList<Integer> input = new ArrayList<Integer>();
        try{
            Scanner scanner = new Scanner(file);
            String next = scanner.next();
            while(scanner.hasNext()){
                if(next.equals("/")){
                   input.add(-1);
                }
                else{
                    input.add(Integer.parseInt(next));
                }
                next = scanner.next();
            }
            if(next.equals("/")){
                input.add(-1);
            }
            else{
                input.add(Integer.parseInt(next));
            }
        }
        catch(FileNotFoundException e){
            System.out.println("File not found");
        }
        catch (IOException e){
            System.out.println("IO Exception");
        }
        return input;
    }
}
