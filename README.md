# simpleCalculatorProject
My version of a simple calculator with separable backend and frontend.
It is based on Java 11. 

# How to use?

The software meant to be started via the launchPackage/Launcher/main method. 
Start the .jar file via command line. 

# Optional Command line arguments: 
-s Starts a backend server. 

-s [n] Starts a backend server and tries to establish a server socket at port [n] 

-l [n] Starts a calculator frontend application and tries to connect to a server instance on the localhost at port [n]

-l [ipAdress] [n] Same as above, but uses [ipAdress] instead of localhost

# Please Note:
The conncetion between frontend and backend is neither authenticated nor encrypted
