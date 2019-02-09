<?php

define('HOST','MYSQL5011.Smarterasp.net');
define('USER','9ff70c_central');
define('PASS','asdf1234');
define('DB','db_9ff70c_central');

$con=mysqli_connect(HOST,USER,PASS,DB);

$username=$_POST['username'];
$password=$_POST['password'];
$firstname=$_POST['firstname'];
$lastname=$_POST['lastname'];
$email=$_POST['email'];
$date=$_POST['date'];
$gender=$_POST['gender'];

$sql="insert into users(username,password,email,firstName,lastName,gender,birthDate) values('$username','$password','$email','$firstname','$lastname','$gender','$date')";	
if(mysqli_query($con,$sql))
{
	echo 'success';
}
   else
   {
	echo 'failure';
   }
  mysqli_close($con);
  ?>
  