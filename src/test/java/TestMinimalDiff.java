import EkIntegration.ListDir;
import EkIntegration.MinimizeDiff;
import EkIntegration.PomParser;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Created by manshu on 12/14/14.
 */
public class TestMinimalDiff {

    @Test
    public void testMinimal() throws IOException {
        ListDir ld = new ListDir();
        String test_folder_path = "demo_test";
        String test_folder_source = "demo";

        File test_folder = new File(test_folder_path);
        if (test_folder.exists())
            FileUtils.deleteDirectory(test_folder);

        FileUtils.copyDirectory(new File(test_folder_source), test_folder);

        ArrayList<String> poms = ld.ListDir(test_folder_path, -1);
        for (String s : poms){
            System.out.println(s);
        }

        PomParser pp = new PomParser();

        try {
            for (String pom_path : poms) {
                System.out.print("File : " + pom_path + ", ");
                pp.queryPom(pom_path, "4.2.0", "2.13", false, test_folder_path);
                System.out.println();
                List<String> original = MinimizeDiff.fileToLines(pom_path.replace(test_folder_path, test_folder_source));
                List<String> revised  = MinimizeDiff.fileToLines(pom_path);

                // Compute diff. Get the Patch object. Patch is the container for computed deltas.
                Patch patch = DiffUtils.diff(original, revised);
                for (Delta delta: patch.getDeltas()) {
                    String delta_type = delta.getType().toString();

                    if (delta_type.equalsIgnoreCase("INSERT"))
                        continue;
                    else if (delta_type.equalsIgnoreCase("CHANGE")){
                        String changed_string = delta.getRevised().toString();
                        if (changed_string.matches(".*?<version>\\s*[0-9.]*\\s*<[/]version>.*"))
                            continue;
                        else if (changed_string.matches(".*?<argLine>\\s*[$]\\{argLine\\}.*"))
                            continue;
                        else if (changed_string.matches(".*?<excludesFile>.*"))
                            continue;
                        else if (changed_string.matches(".*?ekstazi.*"))
                            continue;
                        else
                            fail();
                    } else {
                        fail();
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Some configuration is not valid. Check path or other parameters");
            fail();
        } catch (SAXException e) {
            e.printStackTrace();
            fail();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail();
        } catch (XPathException e) {
            e.printStackTrace();
            fail();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }


    }
}
