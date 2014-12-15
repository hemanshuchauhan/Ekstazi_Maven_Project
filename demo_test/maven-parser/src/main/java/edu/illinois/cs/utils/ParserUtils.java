package edu.illinois.cs.utils;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ParserUtils {

    /**
     * Reads/parses the pom at the given path and constructs a Model object containing the information within the pom file
     *
     * @param pomPath
     * @return Model object containing all maven information in the pom
     */
    public static Model getPomObject(String pomPath) {
        Model model = null;
        try {
            Reader reader = new FileReader(pomPath);
            MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
            model = xpp3Reader.read(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }

    /**
     * Reads/parses the pom at the given path, constructs a Model object containing the information within the pom file,
     * and returns the list of dependencies within that pom
     *
     * @param pomPath
     * @return List of dependency object within the given pom
     */
    public static List<Dependency> getSameProjectDependencies(String pomPath) {
        List<Dependency> dependencies = new ArrayList<Dependency>();

        Model model = getPomObject(pomPath);
        if (model != null) {
            for (Dependency dependency : model.getDependencies()) {
                if (dependency.getGroupId().equals(model.getGroupId())) {
                    dependencies.add(dependency);
                }
            }
        }

        return dependencies;
    }

    /**
     * Reads/parses the pom at the given path, constructs a Model object containing the information within the pom file,
     * and returns a list of the names of modules within the pom file
     *
     * @param pomPath
     * @return List of names of the modules within the pom file
     */
    public static List<String> getSameParentModules(String pomPath) {
        List<String> modules = new ArrayList<String>();

        Model model = getPomObject(pomPath);
        if (model != null) {
            modules.addAll(model.getModules());
        }

        return modules;
    }
    
    public static void main(String[] args) {
    	if(args.length != 1) {
    		System.err.println("USAGE: java -cp <MAVEN_PARSER_JAR> edu.illinois.cs.utils.ParserUtils <POM_LOCATION>");
    		System.exit(-1);
    	}
    	
    	List<String> modules = getSameParentModules(args[0]);
    	if(modules.size() > 0) {
    		System.out.println("\nIdentified the following modules for the pom file specified:");
	    	for(String module : modules) {
	    		System.out.println("\t"+module);
	    	}
    	} else {
    		System.out.println("\nFailed to identify any modules - perhaps the pom file location was specified incorrectly.");
    	}
    }
}
