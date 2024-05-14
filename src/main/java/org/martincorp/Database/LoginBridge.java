package org.martincorp.Database;

import java.sql.*;

import org.martincorp.Interface.GUI;

public class LoginBridge {
    //Variables:
    private static String DBName;
    public static boolean connected;
    public static Connection conn;
    public static Statement querier;

    //Constructor:
    public LoginBridge(String db){
        DBName = db;
    }

    public LoginBridge(){
        
    }
    
    //Métodos:
    public void startConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection
                ("jdbc:mysql://localhost/" + DBName, "corp_pass", "");

            querier = conn.createStatement();
            querier.execute("USE " + DBName);
            ResultSet result = querier.executeQuery("SELECT * FROM " + DBName + ".company");

            if(result.next() == false){
                GUI.launchMessage(0, DBName, DBName);
            }
            else{
                System.out.println("'"+ DBName + "' database successfully connected.");
            }
                
            connected = true;
            result.close();
        }
        catch(ClassNotFoundException cnfe){
            connected = false;
            cnfe.printStackTrace();
            GUI.retryDatabase("No se encuentra el módulo de conexión.\n\n" + cnfe.getMessage(), true);
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            connected = false;
            GUI.retryDatabase(sqle.getMessage(), true);
        }
    }

    public void closeConnection(){
        try{
            querier.close();
            conn.close();
            connected = false;
            System.out.println("Connection to database '" + getDB() + "' closed.");
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cerrar la conexión.\nSe recomienda cerrar la aplicación para evitar brechas de seguridad.\n\n" + sqle.getMessage());
        }
    }

    public void checkConnection(boolean db){
        try{
            if(!conn.isValid(3)){
                GUI.retryDatabase("", db);
            }
        }
        catch(SQLException sqle){
            //This catch is here for literally nothing, is only thrown if timeout is less than 0.
            sqle.printStackTrace();
        }
    }

    public String getDB(){
        return DBName;
    }
}
