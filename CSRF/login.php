<?php
session_start();

$bankno = $_POST['bsb'];
$pass =$_POST['password'];

/* The login process establishes the authenticated session, but the
    application does not implement any CSRF protection for subsequent
    state-changing requests.

    The session therefore acts as the authentication mechanism, while
    change_email.php relies on that session without requiring an
    additional CSRF token.
*/

$_SESSION['logged_in'] = true;
$_SESSION['bankno'] = $bankno;

header("Location: homepage.php");
exit;
?>



