import EkIntegration.ListDir;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by manshu on 12/14/14.
 */
public class TestListDir {

    public void createDirectoryStructure(int num_files, int num_files_2) throws IOException {
        File test_lstdir = new File("test_lstdir");
        System.out.println("Num of folders and files = " + num_files + " " + num_files_2);

        if (!test_lstdir.exists()){
            test_lstdir.mkdir();
        } else {
            FileUtils.deleteDirectory(test_lstdir);
            test_lstdir.mkdir();
        }

        File tl[] = new File[num_files];
        File tl2[] = new File[num_files_2];
        File pom_file = null;

        for (int i = 0; i < num_files; i++){
            String rel_path = "test_lstdir/" + "test_lstdir_" + String.valueOf(i + 1);

            tl[i] = new File(rel_path);
            tl[i].mkdir();
            pom_file = new File(rel_path + "/pom.xml");
            pom_file.createNewFile();
            for (int j = 0; j < num_files_2; j++){
                String rel_path_2 = rel_path + "/test_lstdir_" + String.valueOf(i + 1) + String.valueOf(j + 1);
                tl2[j] = new File(rel_path_2);
                tl2[j].mkdir();
                pom_file = new File(rel_path_2 + "/pom.xml");
                pom_file.createNewFile();
            }
        }
    }

    @Test
    public void testPomFileCount() throws IOException {
        int max_files = 5;
        int num_files = (int) Math.random() * max_files + 1;
        int num_files_2 = (int) Math.random() * max_files + 1;
        createDirectoryStructure(num_files, num_files_2);

        ListDir ld = new ListDir();
        ArrayList<String> poms = ld.ListDir("test_lstdir", -1);
        System.out.println("Number of files = " + poms.size());

        // check if number of actual pom files are actually same
        assertEquals(poms.size(), num_files * (num_files_2 + 1));

    }

    @Test
    public void testPomFilePresence() throws IOException {
        int max_files = 5;
        int num_files = (int) Math.random() * max_files + 1;
        int num_files_2 = (int) Math.random() * max_files + 1;

        createDirectoryStructure(num_files, num_files_2);

        ListDir ld = new ListDir();
        ArrayList<String> poms = ld.ListDir("test_lstdir", -1);

        File check_file = null;

        // check if all those files are actually present
        for (int i = 0; i < poms.size(); i++){
            check_file = new File(poms.get(i));
            assertTrue(check_file.exists());
        }

        poms.add("random_file");
        check_file = new File(poms.get(poms.size() - 1));
        // check if random file doesn't exist
        assertFalse(check_file.exists());
    }

}
