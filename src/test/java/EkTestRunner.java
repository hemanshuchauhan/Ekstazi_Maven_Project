import EkIntegration.MinimizeDiff;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Created by manshu on 12/14/14.
 */
public class EkTestRunner {

    public static void main(String args[]){
        Result result = JUnitCore.runClasses(TestListDir.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());

        result = JUnitCore.runClasses(TestMinimalDiff.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());

        result = JUnitCore.runClasses(TestPomParser.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println(result.wasSuccessful());
    }
}
