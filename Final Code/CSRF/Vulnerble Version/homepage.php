<?php
session_start();

if(!isset($_SESSION["logged_in"])){
    header("Location: login.html");
    exit;
}
?>

<form method="post" action="change_email.php">
<label for="email>"Change Email:</label>
<input type="email" id="email" name="email">
<input type="submit" value="Submit">
</form>


