<?php

define('HOST','MYSQL5011.Smarterasp.net');
define('USER','9ff70c_central');
define('PASS','asdf1234');
define('DB','db_9ff70c_central');

$con=mysqli_connect(HOST,USER,PASS,DB);

$SearchMedicine=$_POST['SearchMedicine'];

$sql="SELECT pharmacy.Name,pharmacy.address,pharmacy.Tel,pharmacy.Open , pharmacy.latitude,pharmacy.langtitude
FROM pharmacy JOIN med
ON pharmacy.id=med.Pharmacy_id
where med.Name='$SearchMedicine' ";

$res=mysqli_query($con,$sql);
$result= array();
while($row = mysqli_fetch_array($res))
{
	array_push($result,
	array('Pharmacy Name'=>$row[0],
              'address'=>$row[1],
              'Tel'=>$row[2],
              'Open'=>$row[3],
              'latitude'=>$row[4],
              'langtitude'=>$row[5])
);
}	
echo json_encode(array("result"=>$result));
mysqli_close($con);
?>		