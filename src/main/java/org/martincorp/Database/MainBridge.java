/**
 *  NeutronMail client for admintration purposes.
 *  Copyright (C) 2024 by Martín Marín.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.martincorp.Database;

import java.sql.*;

import org.martincorp.Interface.GUI;

import javafx.application.Platform;

public class MainBridge {
    //Variables:
    private static String DBName;
    public static String user;
    private static String pass;

    public static boolean connected;
    public static Connection conn;
    public static Statement querier;
    
    //Builders:
    public MainBridge(String db, String p){
        DBName = db;
        user = db;
        pass = p;

        startConnection();
    }

    public MainBridge(){}
    
    //Methods:
    public static void startConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DBName, user, pass);
            
            querier = conn.createStatement();
            querier.execute("USE " + DBName);
            ResultSet result = querier.executeQuery("SELECT * FROM " + DBName + ".employee");
            
            connected = true;
            result.close();
        }
        catch(ClassNotFoundException cnfe){
            connected = false;
            cnfe.printStackTrace();
            Platform.runLater( () -> GUI.launchMessage(2, "Error de base de datos", "No es posible establecer un conexión con la base de datos,\nno se encuentra el módulo de conexión.\n\n" + cnfe.getMessage()));
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            connected = false;
            if(querier != null){
                Platform.runLater( () -> GUI.launchMessage(2, "Error de base de datos", "La base de datos asociada a su empresa\nse encuentra dañada.\nPor favor contacte con soporte técnico para\nresolver su problema."));
            }
            Platform.runLater( () -> GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar conectar con la\nbase de datos. Inténtelo de nuevo en unos instantes.\n\n" + sqle.getMessage()));
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
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cerrar la conexión.\nSe recomienda cerrar la conexión para evitar brechas de seguridad.\n\n" + sqle.getMessage());
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
