package EkIntegration; /**
 * Created by manshu on 11/26/14.
 */

import difflib.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MinimizeDiff {
    // converts each of the file content in List Structure
    public static List<String> fileToLines(String filename) throws IOException {
        List<String> lines = new LinkedList<String>();
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        return lines;
    }

    // writes each of the file structure into the given file
    private static void linesToFile(List result, String filename) throws FileNotFoundException {
        //List<String>
        PrintWriter writer = new PrintWriter(filename);

        List<String> out = (List<String>) result;
        Iterator<String> iterator = out.iterator();
        String s = "";
        while(iterator.hasNext()){
            s = iterator.next();
            writer.println(s);
        }
        writer.close();
    }

    // Given an original file and a modified files, find their diff and use only the required diff
    public static boolean MinimizeDiff(String original_file, String revised_file) throws IOException {
        List<String> original = fileToLines(original_file);
        List<String> revised  = fileToLines(revised_file);

        // Compute diff. Get the Patch object. Patch is the container for computed deltas.
        Patch patch = DiffUtils.diff(original, revised);
        Patch p2 = new Patch();

        // for each delta component get its type and check whether this is an ekstazi related change
        for (Delta delta: patch.getDeltas()) {
            //System.out.println(delta.getType());
            //Delta delta = (Delta) delta_obj;
            String delta_type = delta.getType().toString();

            if (delta_type.equalsIgnoreCase("INSERT"))
                p2.addDelta(delta);
            else if (delta_type.equalsIgnoreCase("CHANGE")){
                String changed_string = delta.getRevised().toString();
                if (changed_string.matches(".*?<version>\\s*[0-9.]*\\s*<[/]version>.*"))
                    p2.addDelta(delta);//System.out.println("Keep this version statement");
                else if (changed_string.matches(".*?<argLine>\\s*[$]\\{argLine\\}.*"))
                    p2.addDelta(delta);//System.out.println("Keep this argline statement");
                else if (changed_string.matches(".*?<excludesFile>.*"))
                    p2.addDelta(delta);
                else if (changed_string.matches(".*?ekstazi.*"))
                    p2.addDelta(delta);
                else
                    continue;
            }else {
                continue;
            }
        }

        // Path the original file with only the necessary changes and write it into output.
        List result = null;
        try {
            result = DiffUtils.patch(original, p2);
            //List out = p2.applyTo(original);
            System.out.println();
            System.out.println("Successfully patched");
            linesToFile(result, original_file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("UnSuccessfull patch\nExiting....");
            return false;
        }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            System.out.println("File writing failed\nExiting....");
//            return false;
//        }
    }



    public static void main(String args[]){
        try{
            MinimizeDiff(args[0], args[1]);
        }catch (IOException ie){
            System.out.println("FIle not found");
        }
    }

}
