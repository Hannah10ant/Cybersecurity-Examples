README for SSRF CODE

code written by Amro Atta - 22485210 & Tadiwa Tambiri - 23151004
edited by Hannah Tennant - 22202998
Last edited 8/10/2026


Files:

Exploitation/exploitDessert.py

Security Enhanced Version/AdminServer.java
Security Enhanced Version/SecureDessertFinder.java
Security Enhanced Version/SecureShopServer.java

Vulnerable Version/AdminServer.java
Vulnerable Version/DessertFinder.java
Vulnerable Version/ShopServer.java


How To Run:

1. Compile first in terminal from inside Vulnerable Version folder:
javac DessertFinder.java ShopServer.java AdminServer.java

2. Open 3 terminals and in each run one of these commands from inside Vulnerable Version folder;
java ShopServer
java AdminServer
java DessertFinder

Running The Exploit Manually:

1. Search DessertFinder URL displayed in terminal
2. Enter an invlaid input
3. Read the overly descriptive edit messsage 
4. Edit the url parameter in the URL to match the AdminServer address
5. Search and see the confidential data

Running The Exploit with Script:

1. Make sure the vulnerable application and the servers are running
2. Ensure you are in the Exploitation folder in the terminal
2. In terminal run python3 exploitDessert.py

How to Run secure application 

1. Compile first in terminal:
javac SecureDessertFinder.java SecureShopServer.java AdminServer.java

2. Open 3 terminals and in each run one of these commands;
java SecureShopServer
java AdminServer
java SecureDessertFinder

