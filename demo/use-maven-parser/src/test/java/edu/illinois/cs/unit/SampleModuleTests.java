package edu.illinois.cs.unit;

import edu.illinois.cs.utils.ParserUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class SampleModuleTests {

    private static String moduleRoot = System.getProperty("user.dir");

    @Test
    public void testModelReader() {
        Model model = ParserUtils.getPomObject(moduleRoot + File.separator + "pom.xml");
        Assert.assertNotNull("Model is null", model);
    }

    @Test
    public void testProjectInformation() {
        Model model = ParserUtils.getPomObject(moduleRoot + "/../pom.xml");
        Assert.assertNotNull("Model is null", model);

        Assert.assertEquals("Group Id is incorrect", "edu.illinois.cs", model.getGroupId());
        Assert.assertEquals("Artifact Id is incorrect", "cs527", model.getArtifactId());
        Assert.assertEquals("Version is incorrect", "1.0-SNAPSHOT", model.getVersion());
    }

    @Test
    public void testModuleDependencies() {
        Model model = ParserUtils.getPomObject(moduleRoot + "/../pom.xml");
        Assert.assertNotNull("Model is null", model);

        Assert.assertEquals("Number of modules is incorrect", 4, model.getModules().size());
        Assert.assertEquals("Name of module is incorrect", "use-maven-parser", model.getModules().get(3));

        List<Dependency> dependencyList = ParserUtils.getSameProjectDependencies(moduleRoot + File.separator + "pom.xml");
        Assert.assertEquals("Number of same project dependencies is incorrect", 1, dependencyList.size());
        Dependency dependency = dependencyList.get(0);
        Assert.assertEquals("Group Id is incorrect", "edu.illinois.cs", dependency.getGroupId());
        Assert.assertEquals("Artifact Id is incorrect", "maven-parser", dependency.getArtifactId());
        Assert.assertEquals("Version is incorrect", "1.0-SNAPSHOT", dependency.getVersion());
    }
}
