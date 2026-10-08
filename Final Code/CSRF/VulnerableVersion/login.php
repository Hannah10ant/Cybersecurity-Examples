<?php
session_start();

$bankno = $_POST['bsb'];
$pass =$_POST['password'];

$_SESSION['logged_in'] = true;
$_SESSION['bankno'] = $bankno;

header("Location: homepage.php");
exit;
?>



