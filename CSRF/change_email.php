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


$email = $_POST["email"];
echo "New Email: $email<br>";
?>

