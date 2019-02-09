<?php
define('HOST','MYSQL5011.Smarterasp.net');
define('USER','9ff70c_central');
define('PASS','asdf1234');
define('DB','db_9ff70c_central');

$con=mysqli_connect(HOST,USER,PASS,DB);

$username=$_POST['username'];
$password=$_POST['password'];

$sql="select * from users where username='$username' and password='$password'";

$res=mysqli_query($con,$sql);

$check = mysqli_fetch_array($res);
 
if(isset($check)){
echo 'success';
}else{
echo 'failure';
}
 
mysqli_close($con);
?>
