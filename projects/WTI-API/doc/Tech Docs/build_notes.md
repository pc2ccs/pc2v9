# Building WTI
cd into /projects/EWWebTeam/WTI-API/
To build both the UI and API together, you will first need to have Apache Ant installed and accessible from your current working directory.

`ant build`

From the current directory, cd into build/lib. A directory will be present 

To run the compiled WTI Jar, use the following command:

`java -jar <name_of_jar>.jar`

The app should now be running on http://localhost:8080