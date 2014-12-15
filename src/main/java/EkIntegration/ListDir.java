package EkIntegration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by manshu on 10/8/14.
 */
public class ListDir {
    ArrayList<String> pom_paths; // Stores paths of all the poms in the directory structure.

    // Main function just to check the functionality of individual module
    public static void main(String args[]) {
        String current_path = System.getProperty("user.dir");
        System.out.println("Current Path = " + current_path);
        String path = args[0];
        int max_depth = -1;
        if (args.length > 0) path = args[0];
        if (args.length > 1) max_depth = Integer.parseInt(args[1]);
        ListDir ld = new ListDir();
        ld.ListDir(path, max_depth);
        for (String p : ld.pom_paths) {
            System.out.println(p);
        }
    }

    // Calls ListDirRecursively with path and initialize curren_depth to 1
    public ArrayList<String> ListDir(String path, int maxdepth){
        pom_paths = new ArrayList<String>(5);
        ListDirRecursively(path, 1, maxdepth);
        return pom_paths;
    }

    // Looks for file named pom.xml or pom-main.xml. If finds a folder calls it recursively with curren_depth + 1,
    // constrained by max depth. If maxdepth = -1 then, looks in all files and folders.
    private void ListDirRecursively(String path, int curr_depth, int maxdepth){
        if (curr_depth > maxdepth && maxdepth != -1)
            return;
        File folder = new File(path);
        File[] dir_files = folder.listFiles();
        int pom_found = -1;
        for (File file : dir_files){
            if (file.isDirectory())
                ListDirRecursively(file.getPath(), curr_depth + 1, maxdepth);
            else
                if (file.getName().matches("pom([-]\\w+)?.xml")) {
                    if (file.getName().equalsIgnoreCase("pom.xml")){
                        if (pom_found == 1) {
                            pom_found = 0;
                            continue;
                        } else
                            pom_found = 0;
                    }
                    else if (file.getName().equalsIgnoreCase("pom-main.xml")){
                        if (pom_found == 0){
                            pom_paths.remove(pom_paths.size() - 1);
                        }
                        pom_found = 1;
                    }
                    else{
                        continue;
                    }
                    pom_paths.add(file.getAbsolutePath());
                }
        }
    }
}
