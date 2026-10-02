<?php
session_start();

if(!isset($_SESSION["logged_in"])){
    header("Location: login.html");
    exit;
}
?>

<!-- This form performs a state-changing operation through a POST request.
    However there is no CSRF token included in the form. -->

<form method="post" action="change_email.php">
<label for="email>"Change Email:</label>
<input type="email" id="email" name="email">
<input type="submit" value="Submit">
</form>


