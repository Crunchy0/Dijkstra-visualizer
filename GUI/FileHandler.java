import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHandler {
    private File file;

    public FileHandler(){
        file = null;
    }

    public void save(){
        try {
            boolean created = file.createNewFile();
        }
        catch (IOException e){
            // handler
        }
    }

    public ArrayList<Integer> load(){
        JFileChooser chooser = new JFileChooser();
        chooser.showDialog(null, "Загрузить");
        file = chooser.getSelectedFile();
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
