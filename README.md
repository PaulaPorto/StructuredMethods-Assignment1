## SWEN301 Assignment 1 Template

Please refer to the assignment brief for details. 

a.  When reading the jdepend reports I looked at the cycle in order to check that there are no cyclic dependencies. When running the command (mvn jdepend:generate) to generate the report, no cycle dependencies were found. 

b. Initially to call on the main (CLI) I had to use the entire class path in the terminal.
(java -cp "classpath"). I ran the command mvn package and then altered the pom.xml file by adding a plugin in order to create a jar file. This allowed me to use maven so that when I run the main I wouldn’t have to use the classpath. Instead I can call on, java -jar "executableFileName" and the main inside FindStudentDetails will run. 

c. A garbage collector can remove objects that are unreferenced in the program. Referred objects on the other hand would not be collected even when the application no longer uses them. This would lead to memory leaks in my program since it contains static fields. In order to fix this, I would avoid using static as much as possible unless necessary.