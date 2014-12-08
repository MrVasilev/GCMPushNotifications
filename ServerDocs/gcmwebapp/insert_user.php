<?php
include_once './db_functions.php';
//Create Object for DB_Functions class
$db = new DB_Functions(); 
$email = $_POST["email"];
$regId = $_POST["registration_id"];
$res = $db->insertUser($email, $regId);
echo "Email ".$email." RegId ".$regId ;
if ($res) {
	echo "GCM Reg Id bas been shared successfully with Server";
} else {			 
	echo "Error occured while sharing GCM Reg Id with Server web app";			          
}
?>