package EkIntegration;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

/**
 * Created by manshu on 10/27/14.
 */
public class PomParser {

    private Document doc;
    private String ekstazi_version;
    private String xml_file;
    private XPathExpression expr;
    private XPathFactory xFactory;
    private XPath xpath;

    public static void main(String args[]) {

        String path = "";

        String ek_version = "4.2.0";
        String surefire_version = "2.13";
        boolean surefire_force = false;
        int max_depth = -1;

        if (args.length > 0)
            path = args[0]; // path of the project
        if (args.length > 1){
            try{
                max_depth = Integer.parseInt(args[1]); // depth till which pom is modified
            } catch (NumberFormatException nfe){
                System.out.println("Wrong parameter");
                System.exit(99);
            }
        }
        if (args.length > 2)
            ek_version = args[2]; // passed ekstazi version
        if (args.length > 3){
            surefire_version = args[3]; // passed surefire version
            surefire_force = true; // Set surefire force true
        }

        // create ListDir and PomParser objects
        ListDir ld = new ListDir();
        PomParser pp = new PomParser();

        try {
            // get all the pom files in the path
            ArrayList<String> poms = ld.ListDir(path, max_depth);
            // for each pom call the queryPom which modifies the pom.xml and writes it.
            for (String pom_path : poms) {
                System.out.print("File : " + pom_path + ", ");
                pp.queryPom(pom_path, ek_version, surefire_version, surefire_force, path);
                System.out.println();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Some configuration is not valid. Check path or other parameters");
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inserts the myExcludes tag in the configuration tag
    private void insertExcludesFile(Node configuration_node, String project_path)
    {
        //String path = xml_file_path.substring(0, xml_file_path.lastIndexOf("/"));
        Element excElement = doc.createElement("excludesFile");
        excElement.appendChild(doc.createTextNode("${java.io.tmpdir}/myExcludes"));
        configuration_node.appendChild(excElement);
    }

    // Adds Surefire Version, if passed forcefully use that otherwise 2.13 if nothing passed
    private void addSureFireVersion(Node surefire_node, boolean surefire_force, String surefire_new_version){
        Element versionNode = doc.createElement("version");
        if (!surefire_force)
            versionNode.appendChild(doc.createTextNode("2.13"));
        else
            versionNode.appendChild(doc.createTextNode(surefire_new_version));
        surefire_node.appendChild(versionNode);
    }

    // insert dependency tag block of ekstazi inside dependencies tag
    private void insertDependency(Node node){

        Element dInsert0 = doc.createElement("dependency");
        Element gInsert0 = doc.createElement("groupId");
        Element aInsert0 = doc.createElement("artifactId");
        Element vInsert0 = doc.createElement("version");

        dInsert0.appendChild(gInsert0);
        dInsert0.appendChild(aInsert0);
        dInsert0.appendChild(vInsert0);
        gInsert0.appendChild(doc.createTextNode("org.ekstazi"));
        aInsert0.appendChild(doc.createTextNode("ekstazi-maven-plugin"));
        vInsert0.appendChild(doc.createTextNode(ekstazi_version));

        node.insertBefore(dInsert0, node.getFirstChild());

    }

    // inserts plugin node to the build tag
    private void insertBuild(Node node){
        //Element build = doc.createElement("build");
        Element plugins = doc.createElement("plugins");
        insertSurefire(plugins);
        //build.appendChild(plugins);
        node.insertBefore(plugins, node.getFirstChild());
    }

    // inserts the surefire version if it is not present
    private void insertSurefire(Node plugins)
    {
        Element plugin = doc.createElement("plugin");
        Element groupId = doc.createElement("groupId");
        Element artifactId = doc.createElement("artifactId");
        Element configuration = doc.createElement("configuration");
        groupId.setTextContent("org.apache.maven.plugin");
        artifactId.setTextContent("maven-surefire-plugin");
        plugins.appendChild(plugin);
        plugin.appendChild(groupId);
        plugin.appendChild(artifactId);
        plugin.appendChild(configuration);
    }

    // insert ekstazi plugin above the surefire node mentioning its internal tags according to the passed version
    private void insertPlugin(Node surefire_node)
    {
        Element toInsert = doc.createElement("plugin");
        Element dsInsert = doc.createElement("dependencies");
        Element dInsert = doc.createElement("dependency");
        Element gInsert0 = doc.createElement("groupId");
        Element gInsert = doc.createElement("groupId");
        Element aInsert0 = doc.createElement("artifactId");
        Element aInsert = doc.createElement("artifactId");
        Element vInsert0 = doc.createElement("version");
        Element vInsert = doc.createElement("version");
        Element exsInsert = doc.createElement("executions");
        Element exInsert = doc.createElement("execution");
        Element idInsert = doc.createElement("id");
        Element goalsInsert = doc.createElement("goals");
        Element goalInsert = doc.createElement("goal");
        Element goalInsert2 = doc.createElement("goal");

        Text txt1 = doc.createTextNode("org.ekstazi");
        Text txt2 = doc.createTextNode("org.ekstazi.core");
        Text txt3 = doc.createTextNode(ekstazi_version);
        toInsert.appendChild(dsInsert);
        dsInsert.appendChild(dInsert);
        dInsert.appendChild(gInsert0);
        dInsert.appendChild(aInsert0);
        dInsert.appendChild(vInsert0);
        gInsert0.appendChild(txt1);
        aInsert0.appendChild(txt2);
        vInsert0.appendChild(txt3);


        toInsert.appendChild(gInsert);
        toInsert.appendChild(aInsert);
        toInsert.appendChild(vInsert);
        gInsert.appendChild(doc.createTextNode("org.ekstazi"));
        aInsert.appendChild(doc.createTextNode("ekstazi-maven-plugin"));
        vInsert.appendChild(doc.createTextNode(ekstazi_version));
        toInsert.appendChild(exsInsert);
        exsInsert.appendChild(exInsert);
        exInsert.appendChild(idInsert);
        if (ekstazi_version.startsWith("3"))
            idInsert.appendChild(doc.createTextNode("selection"));
        else
            idInsert.appendChild(doc.createTextNode("select"));
        exInsert.appendChild(goalsInsert);
        goalsInsert.appendChild(goalInsert);
        goalsInsert.appendChild(goalInsert2);
        if (ekstazi_version.startsWith("3"))
            goalInsert.appendChild(doc.createTextNode("selection"));
        else
            goalInsert.appendChild(doc.createTextNode("select"));
        goalInsert2.appendChild(doc.createTextNode("restore"));

        surefire_node.getParentNode().insertBefore(toInsert, surefire_node);
    }

    // Returns node from the xml according to the given search expression
    private Node getNode(String search_expression) throws XPathException {
        Node node = (Node) xpath.evaluate(search_expression, doc, XPathConstants.NODE);
        return node;
    }

    // Returns value of node according to the given search expression
    private String getNodeValue(String search_expression) throws XPathException {
        String node_val = (String) xpath.evaluate(search_expression, doc, XPathConstants.STRING);
        return node_val;
    }

    // Returns list of nodes whose path in the xml matches the given search expression
    private NodeList getNodeList(String search_expression) throws XPathException {
        NodeList nodelist = (NodeList) xpath.evaluate(search_expression, doc, XPathConstants.NODESET);
        return nodelist;
    }

    // write the modified document dom structure into xml
    private void writeXml(){
        //////////////////////////////////////////////////////////////////
        //Write out the final xml file again into the same pom.xml file//
        /////////////////////////////////////////////////////////////////
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        // set the output xml configurations like indentation
        try {
            transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            //transformer.setOutputProperty(OutputKeys.ENCODING, "US-ASCII");
            //transformer.setErrorListener(OutputKeys.);
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            doc.setXmlStandalone(true);

            // if wants to keep original file as backup
            //FileUtils.copyFile(new File(xml_file), new File(xml_file+"_orig"));

            // Write a temporary xml on the disk which may not be the minimal file
            DOMSource source = new DOMSource(doc);
            String modified_xml_file = xml_file.substring(0, xml_file.lastIndexOf("/") + 1) + "ekstazi_" + xml_file.substring(xml_file.lastIndexOf("/") + 1);
            StreamResult _result = new StreamResult(modified_xml_file);
            transformer.transform(source, _result);

            // Compare that modified file with the original file to get the minimal diff
            boolean minimized = MinimizeDiff.MinimizeDiff(xml_file, modified_xml_file);
            // if minimized then delete the temporary file
            if (!minimized){
                File foriginal = new File(xml_file);
                File fmodified = new File(modified_xml_file);
                FileUtils.moveFile(foriginal, fmodified);
            }else{
                File fmodified = new File(modified_xml_file);
                FileUtils.forceDelete(fmodified);
            }

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Queries nodes of the pom xml structure to find which components need to modified or where ekstazi specific
    // settings are to be inserted.
    public boolean queryPom(String xml_file, String ekstazi_version, String surefire_new_version, boolean surefire_force, String project_path) throws ParserConfigurationException, IOException, XPathException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.ekstazi_version = ekstazi_version;
        this.xml_file = xml_file;
        this.expr = null;
        this.xFactory = XPathFactory.newInstance();
        try{
            // parse the xml file and load into document dom structure.
            this.doc = builder.parse(xml_file);
        } catch (Exception e){
            // Error in parsing. Malformed POM
            e.printStackTrace();
            System.out.println("Craaaaaaaaash");
            return false;
        }
        this.doc.getDocumentElement().normalize();
        this.xpath = xFactory.newXPath();

        // check if build is present, if not present create one node for build
        Node build = getNode("/project/build");
        if(build == null) {
            System.out.println("Build Not Present !!");
            Node project_node = getNode("/project");
            Node project_artifact_node = getNode("/project/artifactId");
            Element build_node = doc.createElement("build");
            if (project_artifact_node != null && project_artifact_node.getNextSibling() != null){
                project_node.insertBefore(build_node, project_artifact_node.getNextSibling());
                insertBuild(build_node);
            }
        }
        else {
            // check if plugins node is present in whole pom, if not create one node for plugins inside build/plugins
            Node plugins = getNode("/project/build/plugins|/project/build/pluginManagement/plugins");
            if (plugins == null){
                insertBuild(build);
                System.out.println("Added plugins node Now !!");
            }
            else
                System.out.println("Plugins Present");
        }

        // get all the nodes of surefire plugin
        NodeList surefire_plugins_node = getNodeList("//plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]");
        ArrayList<String> surefire_plugin_paths = new ArrayList<String>();

        // For each surefire ocurrence, trace back the xslt path to the root node to find its exact location as search expression
        for (int n = 0; n < surefire_plugins_node.getLength(); n++){
            Node current_node = surefire_plugins_node.item(n);
            String surefire_node_path = "";
            while (current_node.getParentNode() != null){
                //System.out.println("Surefire address = " + current_node.getNodeName());
                current_node = current_node.getParentNode();
                if (current_node.getParentNode() != null)
                    surefire_node_path = "/" + current_node.getNodeName() + surefire_node_path;
            }
            if (surefire_plugin_paths.contains(surefire_node_path))
                continue;
            surefire_plugin_paths.add(surefire_node_path);
            System.out.println(surefire_node_path);
        }
        System.out.println("Num surefires = " + surefire_plugins_node.getLength());

        // if no surefire is there, then create an empty entry
        if (surefire_plugin_paths.size() == 0)
            surefire_plugin_paths.add("");

        // Loop the whole program for each surefire occurrence
        for (String plugin_path : surefire_plugin_paths) {
            System.out.println();
            System.out.println("\nUsing surefire path = " + plugin_path);
            NodeList nodes = getNodeList(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/artifactId/text()");
            NodeList surefire_plugin = nodes;
            System.out.print("Surefire : " + (nodes.getLength() != 0) + " ");

            NodeList ekstazi_plugin = getNodeList(plugin_path + "/plugin[artifactId[contains(text(), 'ekstazi-maven-plugin')]]/artifactId/text()");
            System.out.print(" Ekstazi Plugin Present : " + (ekstazi_plugin.getLength() != 0) + ", ");

            Node surefire_node = getNode(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]");

            // if the surefire plugin is not there then create a new one
            if (surefire_node == null) {
                Node plugins = getNode("/project/build/plugins");
                String plugin_new_path = "/project/build/plugins";
                if(plugins == null){
                    plugins = getNode("/project/build/pluginManagement/plugins");
                    plugin_new_path = "/project/build/pluginManagement/plugins";
                }
                if(plugins == null){
                    plugins = getNode("/project/build/pluginManagement");
                    plugin_new_path = "/project/build/pluginManagement";
                }

                if (plugins != null)
                    insertSurefire(plugins);

                plugin_path = plugin_new_path;
                surefire_node = getNode(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]");
            }


            // Check the version of surefire plugin, if its < 2.13 set it equal to 2.13
            // or if user has forcefully passed the version, then use that one
            if (surefire_node != null) {
                String surefire_version = getNodeValue(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/version");
                if (!surefire_version.equals("")) {
                    try {
                        double version = Double.parseDouble(surefire_version);
                        //if (version <= 2.10 || version > 2.2) {
                        if (!surefire_force) {
                            if (version < 2.13) {
                                System.out.println("\nVersion not supported = " + surefire_version);
                                Node version_node = getNode(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/version");
                                version_node.setTextContent(surefire_new_version);
                                System.out.println("Surfire version upgraded to " + surefire_new_version);
                            } else {
                                System.out.println("\nVersion Supported = " + surefire_version);
                            }
                        } else {
                            System.out.println("\nPrevious Version = " + surefire_version);
                            Node version_node = getNode(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/version");
                            version_node.setTextContent(surefire_new_version);
                            System.out.println("Surfire version forcefully upgraded to " + surefire_new_version);
                        }
                    } catch (NumberFormatException ex) {
                        if (surefire_force) {
                            System.out.println("\nPrevious Version = " + surefire_version);
                            Node version_node = getNode(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/version");
                            version_node.setTextContent(surefire_new_version);
                            System.out.println("Surefire version forcefully upgraded to " + surefire_new_version);
                        } else {
                            System.out.println("Creates variable as version of surefire");
                        }

                    }
                } else {
                    addSureFireVersion(surefire_node, surefire_force, surefire_new_version);
                }
            }

            // for all the argLine nodes that occur inside surefire plugin append its text by ${argLine}
            NodeList argline_nodes = getNodeList(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]//argLine");
            Node argline_node = null;
            for (int i = 0; i < argline_nodes.getLength(); i++) {
                argline_node = argline_nodes.item(i);
                if (argline_node != null) {
                    System.out.println("ArgLine present in this one");
                    String argText = argline_node.getTextContent();
                    if (!argText.contains("${argLine}")) {
                        argline_node.setTextContent("${argLine} " + argText);
                        System.out.println("Now argLine content = " + argline_node.getTextContent());
                    } else
                        System.out.println("No change required");

                } else {
                    System.out.println("ArgLine not present");
                }
            }

            // Adds Ekstazi Plugin above the surefire node if its not already there
            nodes = getNodeList(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/artifactId/text()");
            if (nodes.getLength() != 0 && ekstazi_plugin.getLength() == 0) {
                insertPlugin(surefire_node);
            }

            // Adds dependencies tag and ekstazi dependency in dependencies tag if its not already there
            NodeList dependencies = getNodeList("/project/dependencies|/project/dependencyManagement/dependencies");
            System.out.print("Dependencies : " + (dependencies.getLength() != 0) + " ");

            NodeList ekstazi_dependency = getNodeList("/project/dependencies/dependency[artifactId[contains(text(), 'ekstazi-maven-plugin')]]|/project/dependencyManagement/dependencies/dependency[artifactId[contains(text(), 'ekstazi-maven-plugin')]]");
            System.out.print("Ekstazi Dependency : " + (ekstazi_dependency.getLength() != 0) + " ");

            // if it was already there check whether version has changed or not
            if (ekstazi_dependency.getLength() != 0){
                String ekVersion = getNodeValue("/project/dependencies/dependency[artifactId[contains(text(), 'ekstazi-maven-plugin')]]/version|/project/dependencyManagement/dependencies/dependency[artifactId[contains(text(), 'ekstazi-maven-plugin')]]/version");
                System.out.println("Ekstazi Version = " + ekVersion);
                Node ek_version_node = getNode("/project/dependencies/dependency[artifactId[contains(text(), 'ekstazi-maven-plugin')]]/version|/project/dependencyManagement/dependencies/dependency[artifactId[contains(text(), 'ekstazi-maven-plugin')]]/version");
                if (!ekVersion.equals(ekstazi_version))
                    ek_version_node.setTextContent(ekstazi_version);
            }

            // if dependencies node is not present then create that also and then add ekstazi dependency
            if (dependencies.getLength() == 0) {
                Node project_node = getNode("/project");
                Node project_artifact_node = getNode("/project/artifactId");
                Element dependencies_node = doc.createElement("dependencies");
                if (project_artifact_node != null && project_artifact_node.getNextSibling() != null) {
                    project_node.insertBefore(dependencies_node, project_artifact_node.getNextSibling());
                    insertDependency(dependencies_node);
                }
            } else if (ekstazi_dependency.getLength() == 0) {
                Node dependencies_node = getNode("/project/dependencies|/project/dependencyManagement/dependencies");
                insertDependency(dependencies_node);
            }

            // Adds myExcludes tag inside of configuration
            NodeList excludes_configuration = getNodeList(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/configuration");
            System.out.print("Excludes : " + (excludes_configuration.getLength() != 0) + " ");

            NodeList excludesFile = getNodeList(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/configuration/excludesFile/text()");
            System.out.print("ExcludesFile Present : " + (excludesFile.getLength() > 0 && excludesFile.item(0).getNodeValue().equalsIgnoreCase("myExcludes")) + ", ");

            if (surefire_plugin.getLength() != 0 && excludes_configuration.getLength() == 0) {
                surefire_node = getNode(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]");
                Node artifactId = getNode(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/artifactId");
                Element configuration = doc.createElement("configuration");
                surefire_node.insertBefore(configuration, artifactId.getNextSibling());
                insertExcludesFile(configuration, project_path);
            } else if (excludesFile.getLength() == 0 && excludes_configuration.getLength() != 0) {
                Node configuration_node = getNode(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/configuration");
                insertExcludesFile(configuration_node, project_path);
            }

            //Check ArgsLine
            NodeList argsLine = getNodeList(plugin_path + "/plugin[artifactId[contains(text(), 'maven-surefire-plugin')]]/configuration/argLine");
            System.out.print("ArgLine : " + (argsLine.getLength() != 0) + "");
        }

        // Write the modifications to the final file
        writeXml();

        return true;
    }


}