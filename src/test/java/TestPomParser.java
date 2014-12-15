/**
 * Created by manshu on 12/14/14.
 */
import EkIntegration.ListDir;
import EkIntegration.MinimizeDiff;
import EkIntegration.PomParser;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

public class TestPomParser {


    @Test
    public void testMalformedException() throws IOException {
        File f = new File("a.xml");
        if (f.exists())
            f.delete();
        f.createNewFile();

        Throwable tr = null;
        PomParser pp = new PomParser();
        try {
            pp.queryPom("a.xml", "4.2.0", "2.13", false, ".");
        } catch (Exception e) {
            tr = e;
            assert true;
        }
    }

    @Test
    public void testOutputPOM() throws IOException {
        ListDir ld = new ListDir();
        String test_folder_path = "demo_test";
        String test_folder_source = "demo";

        File test_folder = new File(test_folder_path);
        if (test_folder.exists())
            FileUtils.deleteDirectory(test_folder);

        FileUtils.copyDirectory(new File(test_folder_source), test_folder);

        ArrayList<String> poms = ld.ListDir(test_folder_path, -1);
        for (String s : poms) {
            System.out.println(s);
        }

        PomParser pp = new PomParser();

        try {
            for (String pom_path : poms) {
                System.out.print("File : " + pom_path + ", ");
                pp.queryPom(pom_path, "4.2.0", "2.13", false, test_folder_path);
                System.out.println();

                List<String> original = MinimizeDiff.fileToLines(pom_path.replace(test_folder_path, test_folder_source) + "_expected");
                List<String> revised  = MinimizeDiff.fileToLines(pom_path);

                // Compute diff. If same as output then correct file produced.
                Patch patch = DiffUtils.diff(original, revised);
                for (Delta delta: patch.getDeltas()) {
                    String delta_type = delta.getType().toString();

                    if (delta_type.equalsIgnoreCase("INSERT")){
                        String changed_string = delta.getRevised().toString();
                        if (changed_string.matches("\\s*"))
                            continue;
                        else
                            fail();
                    }
                    else if (delta_type.equalsIgnoreCase("CHANGE")){
                        String changed_string = delta.getRevised().toString();
                        if (changed_string.matches("\\s*"))
                            continue;
                        else
                            fail();
                    } else {
                        fail();
                    }
                }
            }
        } catch (Exception e){
            fail();
        }
    }
}
