<?php
session_start();

if(!isset($_SESSION["logged_in"])){
    header("Location: login.html");
    exit;
}
/* creating a CSRF token for the authenticated session to be used later */
if (empty($_SESSION["csrf_token"])) {
    $_SESSION["csrf_token"] = bin2hex(random_bytes(32));
}

?>


<form method="post" action="change_emailfix.php">
<label for="email>"Change Email:</label>
<input type="email" id="email" name="email">

<!-- Sending the token with the request hidden -->
    <input type="hidden"
           name="csrf_token"
           value="<?php echo $_SESSION["csrf_token"]; ?>">

<input type="submit" value="Submit">
</form>


