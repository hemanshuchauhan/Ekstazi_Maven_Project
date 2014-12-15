CODE :
--------------------------------------------------------------------------------------------------------------------------------------------------------

All our code has been uploaded on Github at the following public repositories:

https://github.com/sharma55-umehrot2/EkstaziPomParser : Contains the scripts/code that can be directly used to run the tool on any single project

https://github.com/sharma55-umehrot2/Ekstazi_Maven_Project : Contains the complete Project code which we have integrated with Maven.

NOTES:
-> We have done pair programming and therefore all the commits made have been through a common account.


TECHNICAL / CODE DOCUMENTATION :
--------------------------------------------------------------------------------------------------------------------------------------------------------

We have created a Code/Technical documentation as well, which we have uploaded in our SVN project folder:

https://subversion.ews.illinois.edu/svn/fa14-cs527/_projects/sharma55_umehrot2/Project_technical_documentation.pdf

as well as at our github repositories.


STEPS To RUN :
--------------------------------------------------------------------------------------------------------------------------------------------------------

1. To run all the projects supported by our tool : We have uploaded the script run_all.sh on our project svn page.

STEPS:
  -> svn co https://subversion.ews.illinois.edu/svn/fa14-cs527/_projects/sharma55_umehrot2/
  -> cd sharma55_umehrot2/
  -> chmod +x runAll.sh
  -> ./runAll.sh


2. To run any single project using the tool:

STEPS:
  -> git clone https://github.com/sharma55-umehrot2/EkstaziPomParser
  -> cd EkstaziPomParser
  -> ./run_ekstazi.sh -u "Project Url" -p "Project Folder Name" -v "Ekstazi Version" - s "Surefire Version" -r "rev1,rev2,rev3.." -d "{1|-1}" -m "module1,module2"

All of the arguments except project url and project folder name are OPTIONAL.
• Project Url : SVN/GIT project url
• Project Folder Name to be created locally
• Ekstazi version : Currently we support 3.4.2 and 4.2.0. (OPTIONAL)
• Modules to run : Specify comma separated list of modules to run on. (OPTIONAL)
• Revisions to run : Specify comma separated list of revisions to run on. (OPTIONAL)
• Surefire version : Specify surefire version to be used. (OPTIONAL)
• Depth parameter : Specify depth 1 for modifying only root pom.xml, else -1 for all. (OPTIONAL)

Example: 
./run_ekstazi.sh -u "http://svn.apache.org/repos/asf/commons/proper/configuration/trunk" -p "commons-config" -r "r1629051,r1629231,r1636033,r1636041,r1636042"

3. To download and use our project code integrated with Maven: 

STEPS:
 -> git clone https://github.com/sharma55-umehrot2/Ekstazi_Maven_Project
 -> cd Ekstazi_Maven_Project
 -> mvn test // to run project test
 -> mvn package
 -> cp target/ek_pom_parser.one-jar.jar {destination_folder}/pom_parser.jar //{destination_folder} can be the folder where run_ekstazi.sh is present and one wants to use as a part of that script


