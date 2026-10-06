<?php
session_start();

if (!isset($_SESSION["logged_in"])) {
    header("Location: login.html");
    exit;
}
/* The application checks whether the user is authenticated, but it does
    not check whether the request was intentionally made from the
    legitimate banking website.

    No CSRF token is generated or validated here. */




/* APPLICATION LOG FOR DETECTION: 

Get the origin of the request */
$origin = $_SERVER["HTTP_ORIGIN"];

/* Log the origin */
error_log("Email change request from: " . $origin);

/* Check if it came from the banking website */
if ($origin != "http://localhost:8000") {
    error_log("WARNING: Possible CSRF request detected");
}

$email = $_POST["email"];
* Log that the request was processed */
error_log("Email change request processed");
echo "New Email: $email<br>";
?>

