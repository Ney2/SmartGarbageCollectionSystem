<?php 

	$connection = mysqli_connect("localhost","root","","smart_garbage");
	
	$result = array();
	$result['data'] = array();
	$select= "SELECT * FROM garbage WHERE Level > 50";
	$responce = mysqli_query($connection,$select);
	
	while($row = mysqli_fetch_array($responce))
		{
			$index['id']      = $row['0'];
			$index['latitude']    = $row['1'];
			$index['longitude']   = $row['2'];
			$index['level'] = $row['3'];
			
			array_push($result['data'], $index);
		}
			
			$result["success"]="1";
			echo json_encode($result);
			mysqli_close($connection);

 ?>