<?php

define('HOST','MYSQL5011.Smarterasp.net');
define('USER','9ff70c_central');
define('PASS','asdf1234');
define('DB','db_9ff70c_central');

$con=mysqli_connect(HOST,USER,PASS,DB);

$sql="SELECT DISTINCT med.Name
from med";

$res=mysqli_query($con,$sql);
$result= array();

while($row = mysqli_fetch_array($res))
{
	array_push($result,
	array('medicine_name'=>$row[0])
              );
}	
echo json_encode(array("result"=>$result));
mysqli_close($con);
?>