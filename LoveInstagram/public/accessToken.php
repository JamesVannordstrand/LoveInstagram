<?php
//set POST variables
$url = $_POST['url'];
unset($_POST['url']);

$fields_string = "";
//url-ify the data for the POST
foreach($_POST as $key=>$value) { 
  $fields_string .= $key.'='.$value.'&'; 
}
$fields_string = rtrim($fields_string,'&');

//open connection
$ch = curl_init();

//set the url, number of POST vars, POST data
curl_setopt($ch,CURLOPT_URL,$url);
curl_setopt($ch,CURLOPT_POST,count($_POST));
curl_setopt($ch,CURLOPT_POSTFIELDS,$fields_string);

//execute post
$result = curl_exec($ch);

echo $result;

//close connection
curl_close($ch);
?>