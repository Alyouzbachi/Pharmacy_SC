<?php

define('HOST','MYSQL5011.Smarterasp.net');
define('USER','9ff70c_central');
define('PASS','asdf1234');
define('DB','db_9ff70c_central');

$con=mysqli_connect(HOST,USER,PASS,DB);


$sql=" select pharmacy.Name,pharmacy.Open,pharmacy.latitude,pharmacy.langtitude
from pharmacy ";

$res=mysqli_query($con,$sql);
$result= array();
while($row = mysqli_fetch_array($res))
{
	array_push($result,
	array('name'=>$row[0],
              'open'=>$row[1],
              'latitude'=>$row[2],
              'langtitude'=>$row[3]
));
}	
echo json_encode(array("result"=>$result));
mysqli_close($con);
?>	