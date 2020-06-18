# Zeebe Test

A repo to test various functionalities of the Zeebe tool.

## JAVA Versions 
  Use JDK 8 for zeebe client side Spring Boot code.
  
  Use OpenJDK 11 for running the Zeebe Broker and Zeebe Simple Monitor.
  
## Local Set Up
  Install both OpenJDK 11 and Oracle JDK 1.8.
  
  Add an environment variable - `JAVA_HOME` with value `C:\Program Files\Java\jdk-11.0.2\` (Point it to the JDK 11 path).
  
  In the pre existing environment `Path` add one more value `C:\Program Files\Java\jdk-11.0.2\bin\`.
  
  Make sure that the JAVA version should be 11 by typing out the command `java -version` in a PowerShell window.
  
  Download the latest Zeebe broker distribution for the Zeebe Repo.
  
  Also download the Zeebe Simple Monitor JAR from their repo and place it inside the Zeebe distribution folder.
  
  In a PowerShell window type the command - `./bin/broker` to run the Zeebe broker.
  
  In a new PowerShell window type ` java -jar zeebe-simple-monitor-${VERSION}.jar` to start the Zeebe Simple Monitor.
  
  Once the Zeebe Simple Monitor server starts then open `localhost:8082` to access it.
