#!/bin/bash

#project=commons-math
#project=joda-time
project=commons-col
# Ekstazi version
version="3.4.2"

#deps="<dependency> <groupId>org.ekstazi</groupId> <artifactId>ekstazi-maven-plugin</artifactId> <version>${version}</version> </dependency>"
#
#plugin="<plugin> <dependencies> <dependency> <groupId>org.ekstazi</groupId> <artifactId>org.ekstazi.core</artifactId> <version>${version}</version> </dependency> </dependencies> <groupId>org.ekstazi</groupId> <artifactId>ekstazi-maven-plugin</artifactId> <version>${version}</version> <executions> <execution> <id>selection</id> <goals> <goal>selection</goal> <goal>restore</goal> </goals> </execution> </executions> </plugin>"
#
#excludes="<excludesFile>myExcludes</excludesFile>"
#
#function run() {
#        rev="$1"
#        excludes_line="$2"
#        plugin_line="$3"
#        deps_line="$4"
#
#		#rm -rf pom.xml
#		#svn up pom.xml 
#		svn up -r"${rev}"
#
#        sed -i "${excludes_line}i\\${excludes}" pom.xml
#        sed -i "${plugin_line}i\\${plugin}" pom.xml
#        sed -i "${deps_line}i\\${deps}" pom.xml
#        
#        # Compile separate not to measure time
#        mvn test-compile
#        # Run tests
#        mvn test
#}
#
#function step1() {
#        ( cd ${project};
#                run "1573523" "391" "379" "333"
#                run "1573506" "391" "379" "333"
#                run "1573351" "388" "376" "330"
#                run "1573316" "388" "376" "330"
#                run "1573308" "388" "376" "330"
#        )
#}

function run(){
	rev="$1"
	rm -rf pom.xml
	svn up -r"${rev}"
	#git checkout pom.xml
	#git checkout ${rev}
	java -jar ../pom_parser.jar . "${version}"	
	mvn test-compile
	mvn test
	
}
function step1(){
	(
		cd ${project};
        run "1567759"
	)
}

# Clone repository
if [ ! -d "${project}" ]; then
		#svn co https://svn.apache.org/repos/asf/commons/proper/math/trunk/ ${project}
		#git clone https://github.com/JodaOrg/joda-time.git ${project} 
		svn co http://svn.apache.org/repos/asf/commons/proper/collections/trunk/ ${project}
else
        rm -rf ${project}/.ekstazi
fi

# Check if clone was good
if [ ! -d "${project}" ]; then
        echo "Nothing was cloned"
        exit 1
fi

# Download Ekstazi
url="mir.cs.illinois.edu/gliga/projects/ekstazi/release/"
if [ ! -e org.ekstazi.core-${version}.jar ]; then wget "${url}"org.ekstazi.core-${version}.jar; fi
if [ ! -e org.ekstazi.ant-${version}.jar ]; then wget "${url}"org.ekstazi.ant-${version}.jar; fi
if [ ! -e ekstazi-maven-plugin-${version}.jar ]; then wget "${url}"ekstazi-maven-plugin-${version}.jar; fi

# Install Ekstazi
mvn install:install-file -Dfile=org.ekstazi.core-${version}.jar -DgroupId=org.ekstazi -DartifactId=org.ekstazi.core -Dversion=${version} -Dpackaging=jar -DlocalRepositoryPath=$HOME/.m2/repository/
mvn install:install-file -Dfile=ekstazi-maven-plugin-${version}.jar -DgroupId=org.ekstazi -DartifactId=ekstazi-maven-plugin -Dversion=${version} -Dpackaging=jar -DlocalRepositoryPath=$HOME/.m2/repository/

# Run tests with Ekstazi over five revisions
cwd=`pwd`
step1 | tee step11.txt
sed -i 's/.*'"${cwd//\//\\/}"'/USER/g' step11.txt
grep 'sec -' step11.txt | cut -f1,5 -d',' | cut -f1 -d'-' > table11.txt
