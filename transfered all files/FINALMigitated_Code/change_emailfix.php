<?php
session_start();

if (!isset($_SESSION["logged_in"])) {
    header("Location: login.html");
    exit;
}



/* APPLICATION LOG FOR DETECTION: 

Get the origin of the request */
$origin = $_SERVER["HTTP_ORIGIN"];

/* Log the origin */
error_log("Email change request from: " . $origin);

/* Check if it came from the banking website */
if ($origin != "http://localhost:8000") {
    error_log("WARNING: Possible CSRF request detected");
}




/*CSRF TOKEN MECHANISM */

/* Checking that both CSRF tokens first exist and printing out an error if not */
if (!isset($_POST["csrf_token"]) ||
    !isset($_SESSION["csrf_token"])) {

    http_response_code(403);
    exit("CSRF token missing - request rejected");
}

/* Checking that the submitted token matches the session token and exiting out with an error */
if (!hash_equals($_SESSION["csrf_token"], $_POST["csrf_token"])) {

    http_response_code(403);
    exit("Invalid CSRF token - request rejected");
}
/* If everything is satisfied and we don't exit, process the email */
$email = $_POST["email"];

/* Log that the request was processed */
error_log("Email change request processed");

echo "New Email: $email<br>";
?>