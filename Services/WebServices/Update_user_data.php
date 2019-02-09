<?php

define('HOST','MYSQL5011.Smarterasp.net');
define('USER','9ff70c_central');
define('PASS','asdf1234');
define('DB','db_9ff70c_central');

$con=mysqli_connect(HOST,USER,PASS,DB);

$oldusername=$_POST['oldusername'];
$username=$_POST['username'];
$password=$_POST['password'];
$firstname=$_POST['$firstname'];
$birthdate=$_POST['birthdate'];

$sql="update users
set username ='$username' , password = '$password', firstName = '$firstname' , birthDate = '$birthdate'
where username ='$oldusername';";	

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