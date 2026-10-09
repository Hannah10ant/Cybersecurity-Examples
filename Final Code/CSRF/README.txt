README for CSRF CODE

code written by Tadiwa Tambiri - 23151004 & Yasmin Haji - 23152832
edited by Hannah Tennant - 22202998
Last edited 7/10/2026


Files:

Exploitation/csrf.html - HTML code containing the exploitation method

Security Enhanced Version/login.php - Processes login request, establishes authenticated session, redirects to homepage.php
Security Enhanced Version/login.html - Interface which allows user to input credentials for login
Security Enhanced Version/homepage.php - Homepage that contains legitimate email-change form with included random generated csrf token
Security Enhanced Version/CSRF vulnerableCode.html - HTML files as an interface to demonstrate email-change functionality. 
Security Enhanced Version/change_email.php - Processes email-change request, validates csrf token to reject forged request, includes application logging for extra information and detection against suspicious activity. 

Vulnerable Version/login.php - Processes login request, establishes authenticated session, redirects to homepage.php
Vulnerable Version/login.html - Interface which allows user to input credentials for login
Vulnerable Version/homepage.php - Homepage that contains legitimate email-change form without csrf token protection
Vulnerable Version/CSRF vulnerableCode.html - HTML files as an interface to demonstrate email-change functionality. 
Vulnerable Version/change_email.php - Processes email-change request without csrf token validation or logging



How To Run:

Running The Website

1. PHP must be installed on local computer
2. Open up "Vulnerable Version" or "Security Enhanced Version" File
3. Type in Command php -s localhost:8000 in terminal
4. Move to "Exploitation" File
5 .In separate terminal, write command php -s localhost:8001
6. Enter browser of choice and enter URL https://lcoalhost:8000/login.php
7. this should direct you to homepage.php, you may login and change your email in your account

Running The Exploit

1. While the php commands are running, search https://localhot:8001/csrf.html in browser
2. Run the test and depending on whether the security enhanced version is used or the vulnerable 3 3. version is used, it will direct you to change email page.
4. Application logs are shown via terminal, if errors or extra information is needed.


