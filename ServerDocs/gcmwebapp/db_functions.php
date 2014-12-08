<?php

class DB_Functions {

    private $db;

    
    function __construct() {
        include_once './db_connect.php';
        // Connect to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }
    // destructor
    function __destruct() {
        
    }
    /**
     * Insert new user
     * 
     */
    public function insertUser($email, $gcmRegId) {
        // Insert user into database
        $result = mysql_query("INSERT INTO gcm_users (email,gcm_reg_id) VALUES('$email','$gcmRegId')");		
        if ($result) {
			return true;
        } else {			 
			return false;			          
        }
    }
	/**
     * Select all user
     * 
     */
	 public function getAllUsers() {
        $result = mysql_query("select * FROM gcm_users");
        return $result;
    }
	/**
     * Get GCMRegId
     * 
     */
	public function getGCMRegID($email){
		 $result = mysql_query("SELECT gcm_reg_id FROM gcm_users WHERE email = "."'$email'");
		 return $result;
	}
}
?>