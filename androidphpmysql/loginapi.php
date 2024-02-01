<?php   
  require_once 'connection.php';  
  $response = array();  
  if(isset($_GET['apicall'])){  
  switch($_GET['apicall']){    
case 'login':  
  if(isTheseParametersAvailable(array('username', 'password'))){  
    $username = $_POST['username'];  
    $password = md5($_POST['password']);   
   
    $stmt = $conn->prepare("SELECT id, username, email, gender FROM userdata WHERE username = ? AND password = ?");  
    $stmt->bind_param("ss",$username, $password);  
    $stmt->execute();  
    $stmt->store_result();  
    if($stmt->num_rows > 0){  
    $stmt->bind_result($id, $username, $email, $gender);  
    $stmt->fetch();  
    $user = array(  
    'id'=>$id,   
    'username'=>$username,   
    'email'=>$email,  
    'gender'=>$gender  
    );  
   
    $response['error'] = false;   
    $response['message'] = 'Login successfull';   
    $response['user'] = $user;   
 }  
 else{  
    $response['error'] = false;   
    $response['message'] = 'Invalid username or password';  
 }  
}  
break;  
default:   
 $response['error'] = true;   
 $response['message'] = 'Invalid Operation Called';  
}  
}  
else{  
 $response['error'] = true;   
 $response['message'] = 'Invalid API Call';  
}  
echo json_encode($response);  
function isTheseParametersAvailable($params){  
foreach($params as $param){  
 if(!isset($_POST[$param])){  
     return false;   
  }  
}  
return true;   
}  
?>  