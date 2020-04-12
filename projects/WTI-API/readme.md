# WTI Documentation

## Building WTI

Make sure you have the following dependencies installed and accessible in the PATH of your command terminal:
* Apache Ant
* javac (Java 8 compiler or greater)
* NodeJS (v10.8 or greater)
* NPM (LTS)

In this directory (projects/WTI-API), run the following command:

`ant buildWTI`

This will generate the WTI binaries which are located in WTI-API/build/lib.

Run WTI with the following command:

`java -Djdk.crypto.KeyAgreement.legacyKDF=true -jar <name_of_generated_wti_jar>.jar`

This will run the WTI API and UI projects on port 8080 (default)
