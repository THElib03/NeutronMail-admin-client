package org.martincorp.Database;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.sql.rowset.serial.SerialBlob;
import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import org.martincorp.Codec.Encrypt;
import org.martincorp.Interface.*;
import org.martincorp.Model.Certificate;
import org.martincorp.Model.Employee;
import org.martincorp.Model.Group;
import org.martincorp.Model.Online;

public class DBActions {
    //Variables:
    private MainBridge mBridge;
    private LoginBridge lBridge;
    private Encrypt enc;

      //Query variables:
    private final String COMPANY = "SELECT * FROM company WHERE com_name LIKE ?";
    private final String EMPLOYEE = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp)";
    private final String EMP_BY_ID = "SELECT * FROM employee WHERE emp_id = ?";
    private final String EMP_COUNT = "SELECT COUNT(*) FROM employee";
    private final String EMP_ADD = "INSERT INTO employee VALUES(NULL, ?, ?, ?, ?, ?, ?)";
    private final String EMP_ADD_PASS;
    private final String EMP_DROP = "UPDATE employee SET emp_pass = '' WHERE emp_id = ?; ";
    private final String LAST_EMP = "SELECT MAX(emp_id) FROM employee";
    private final String EMP_NUMBER = "SELECT emp_alias FROM employee WHERE emp_alias LIKE ?";
    private final String EMP_SEARCH_FNAME = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE emp_fname LIKE ?";
    private final String EMP_SEARCH_LNAME = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE emp_lname LIKE ?";
    private final String EMP_SEARCH_MAIL = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE emp_email LIKE ?";
    private final String EMP_SEARCH_USER = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE emp_alias LIKE ?";
    private final String EMP_SEARCH_SDATE_FULL = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE emp_sDate LIKE ?";
    private final String EMP_SEARCH_SDATE_YEAR = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE YEAR(emp_sDate) = ?";
    private final String EMP_SEARCH_SDATE_MONTH = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE MONTH(emp_sDate) = ?";
    private final String EMP_SEARCH_SDATE_DDMM = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE DAY(emp_sDate) = ? AND MONTH(emp_sDate) = ? ";
    private final String EMP_SEARCH_SDATE_MMYY = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE MONTH(emp_sDate) = ? AND YEAR(emp_sDate) = ?";
    private final String EMP_SEARCH_EDATE_FULL = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE emp_eDate LIKE ?";
    private final String EMP_SEARCH_EDATE_YEAR = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE YEAR(emp_eDate) = ?";
    private final String EMP_SEARCH_EDATE_MONTH = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE MONTH(emp_eDate) = ?";
    private final String EMP_SEARCH_EDATE_DDMM = "SELECT * FROM  employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE DAY(emp_eDate) = ? AND MONTH(emp_eDate) = ?";
    private final String EMP_SEARCH_EDATE_MMYY = "SELECT * FROM employee JOIN `active` ON (employee.emp_id=`active`.act_emp) WHERE MONTH(emp_sDate) = ? AND YEAR(emp_eDate) = ?";
    private final String ACTI_EMP = "SELECT COUNT(*) FROM `active` JOIN employee ON (`active`.act_emp=employee.emp_id) WHERE act_logged = 1";
    private final String ACTI_ADD = "INSERT INTO `active` VALUES(?, 0)";
    private final String ACTI_DROP = "DELETE FROM `active` WHERE act_emp = ?; ";
    private final String CERTIFICATE = "SELECT * FROM certificate";
    private final String CERT_ADD_EMP = "INSERT INTO certificate VALUES(NULL, ?, NULL, 'empty', 'empty')";
    private final String CERT_ADD_GROUP = "INSERT INTO certificate VALUES(NULL, NULL, ?, ?, ?)";
    private final String CERT_DROP = "DELETE FROM certificate WHERE cert_emp = ?; ";
    private final String GEN_CERT_COUNT = "SELECT COUNT(*) FROM certificate WHERE cert_public_key NOT IN ('empty')";
    private final String GEN_CERT_CHK_EMP = "SELECT COUNT(*) FROM certificate WHERE cert_emp=? AND cert_public_key NOT IN ('empty')";
    private final String GEN_CERT_CHK_GRP = "SELECT COUNT(*) FROM certificate WHERE cert_group=? AND cert_public_key NOT IN ('empty')";
    private final String GROUP = "SELECT * FROM publicgroup";
    private final String GROUP_BY_ID = "SELECT * FROM publicgroup WHERE grp_id = ?";
    private final String GROUP_COUNT = "SELECT COUNT(*) FROM publicgroup";
    private final String GROUP_EMPS = "SELECT COUNT(*) FROM groupuser";

    //Builder:
    public DBActions(){
        mBridge = new MainBridge();
        lBridge = new LoginBridge();
        enc = new Encrypt(2);

        //Querys declaration:
        EMP_ADD_PASS = "INSERT INTO " + lBridge.getDB() + ".`user` VALUES(NULL, ?, ?, ?)";
        try{
            Thread.sleep(750);
        }
        catch(InterruptedException ie){
            ie.printStackTrace();
            GUI.launchMessage(2, "Error interno", "Ha ocurrido un error durante el manejo de procesos:\n\n" + ie.getMessage());
        }

        //Query declaration:
        
    }

    //Methods:
    public void loginClose(){
        lBridge.closeConnection();
    }

    public void loginOpen(){
        lBridge.startConnection();
    }

    public void mainClose(){
        mBridge.closeConnection();
    }

    public void resetMainQuerier(){
        try{
            mBridge.querier = mBridge.conn.createStatement();
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error inesperado durante la\ncomunicación con la base de datos:\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
        }
    }

    public boolean checkUser(String name, String pass){
        boolean done = false;
        PreparedStatement loginSta;
        byte[] salt = null;
        byte[] hash = null;

        if(lBridge.connected){ 
            if(!name.equals("") && !pass.equals("")){
                try{
                    loginSta = lBridge.conn.prepareStatement(COMPANY);
                    loginSta.setString(1, name);
                    ResultSet res = loginSta.executeQuery();
                    
                    if(res.next()){
                        byte[] wall = res.getBytes(3);
                        salt = Arrays.copyOfRange(wall, 0, 32);
                        hash = Arrays.copyOfRange(wall, 32, 64);
                        if(enc.checkPassword(pass.toCharArray(), salt, hash)){
                            loginClose();

                            if(!lBridge.connected){
                                done = true;

                                Thread t = new Thread( () -> {                                    
                                    MainBridge loggedUser = new MainBridge(name, new String(wall, StandardCharsets.UTF_8));
                                });

                                t.run();
                            }
                            else{
                                done = true;
                                GUI.launchMessage(2, "Error de base de datos.", "La conexión de inicio de sesión no se ha cerrado correctamente.\nSe recomienda cerrar la aplicación lo antes posible para evitar una brecha de seguridad.");
                            }
                        }
                        else{
                            done = false;
                            GUI.launchMessage(5, "", "La contraseña introducida no es correcta,\npor favor inténtelo de nuevo.");
                        }
                    }
                    else{
                        done = false;
                        GUI.launchMessage(5, "", "No se ha encontrado ninguna compañía con las credenciales indicadas.");
                    }
                }
                catch(SQLException sqle){
                    sqle.printStackTrace();
                    done = false;
                    GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error inesperado durante la comunicación\ncon la base de datos. Inténtelo de nuevo en unos instantes:\n\n" + sqle.getMessage());
                    lBridge.checkConnection(true);
                    mBridge.checkConnection(false);
                }
            }
            else{
                done = false;
                GUI.launchMessage(5, "Datos incorrectos", "Uno de los campos obligatorios está vacío, por favor inténtelo de nuevo.");
            }
        }
        else{
            lBridge.startConnection();
            GUI.retryDatabase("", true);
        }

        return done;
    }

    public int getCompanyId(String comp){
        PreparedStatement loginSta;

        try{
            lBridge.startConnection();

            loginSta = lBridge.conn.prepareStatement(COMPANY);
            loginSta.setString(1, comp);
            ResultSet res = loginSta.executeQuery();
                    
            if(res.next()){
                return res.getInt(1);
            }
            else{
                return 1;
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return 1;
        }
        finally{
            lBridge.closeConnection();
        }
    }
    
    public List<Employee> getEmployees(){
        List<Employee> emps = new ArrayList<Employee>();

        try{
            ResultSet res = mBridge.querier.executeQuery(EMPLOYEE);

            while(res.next()){
                emps.add(
                    new Employee(
                        res.getInt(1),
                        res.getBoolean(9) ? Online.ONLINE : Online.OFFLINE,
                        res.getString(2) + " " + res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        res.getString(6),
                        res.getString(7)
                    )
                );
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
        }

        return emps;
    }

    public Employee getEmployeeById(int id){
        Employee emp = new Employee(0, Online.OFFLINE, "", "", "", "", "");
        PreparedStatement empSta;

        try{
            empSta = mBridge.conn.prepareStatement(EMP_BY_ID);
            empSta.setInt(1, id);
            ResultSet res = empSta.executeQuery();
            
            if(res.next()){
                emp.setID(res.getInt(1));
                emp.setName(res.getString(2) + " " + res.getString(3));
                emp.setStartDate(res.getString(4));
                emp.setEndDate(res.getString(5));
                emp.setAlias(res.getString(6));
                emp.setMail(res.getString(7));
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
        }

        return emp;
    }

    public boolean newEmp(String fName, String lName, LocalDate sDate, LocalDate eDate, byte[] pass, String alias, String mail){
        //DONE: Add an active row asigned to this new employee.
        PreparedStatement empSta, passSta;

        try{
            empSta = mBridge.conn.prepareStatement(EMP_ADD);
            empSta.setString(1, fName);
            empSta.setString(2, lName);
            empSta.setDate(3, Date.valueOf(sDate));
            
            if(eDate == null){
                empSta.setNull(4, Types.DATE);
            }
            else{
                empSta.setDate(4, Date.valueOf(eDate));
            }
            //DONE: add random number to alias.
            String newAlias = getEmpNumber(alias);
            empSta.setString(5, newAlias);
            empSta.setString(6, mail);

            passSta = mBridge.conn.prepareStatement(EMP_ADD_PASS);
            passSta.setString(1, newAlias);
            passSta.setInt(2, getCompanyId(mBridge.getDB()));
            passSta.setBlob(3, new SerialBlob(pass));


            if(empSta.executeUpdate() == 1 && passSta.executeUpdate() == 1){
                System.out.println("New employee row added.");
                return true;
            }
            else{
                System.out.println("New employee row creation failed.");
                GUI.launchMessage(2, "Error de base de datos", "No se ha creado un nuevo empleado,\ninténtelo de nuevo en unos instantes.");
                return false;
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar introducir datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return false;
        }
    }

    public boolean dropEmp(int id){
        PreparedStatement dropSta;
        PreparedStatement dropSta2;
        PreparedStatement dropSta3;

        try{
            dropSta = mBridge.conn.prepareStatement(EMP_DROP);
            dropSta2 = mBridge.conn.prepareStatement(CERT_DROP);
            dropSta3 = mBridge.conn.prepareStatement(ACTI_DROP);
            //DONE: ALSO DELETE IN THIS METHOD THE ASSOCIATED CERTIFICATE AND ACTIVE ROW. NO NEED FOR EXTRA METHODS FOR 3 THINGS SO LINKED
              //Not needed in the end, mySQL deletes active automatically and cert shouldn't be deleted
            dropSta.setInt(1, id);
            dropSta2.setInt(1, id);
            dropSta3.setInt(1, id);

            dropSta.execute();
            dropSta2.execute();
            dropSta3.execute();

            return true;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar introducir datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);

            return false;
        }
    }

    public int getEmpCount(){
        int count = 0;

        try{
            ResultSet res = mBridge.querier.executeQuery(EMP_COUNT);
            res.next();
            count = res.getInt(1);
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
        }

        return count;
    }

    public int getLastEmp(){
        try{
            ResultSet res = mBridge.querier.executeQuery(LAST_EMP);
            res.next();
            return res.getInt(1) + 1;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return 0;
        }
    }

    public String getEmpNumber(String alias){
        PreparedStatement numSta;
        List<String> nums = new ArrayList<String>();
        Random r = new Random();

        try{
            numSta = mBridge.conn.prepareStatement(EMP_NUMBER);
            numSta.setString(1, alias + "%");
            ResultSet res = numSta.executeQuery();

            while(res.next()){
                String emp = res.getString(1);
                nums.add(emp.substring(emp.length() - 5, emp.length()));
            }

            String ran = String.valueOf(r.nextInt(99999));
            for(int i = ran.length(); i < 5; i++){
                ran = "0" + ran;
            }

            for(int i = 0; i < nums.size(); i++){
                if(i == 90000){
                    GUI.launchMessage(2, "Nombre no disponible", "El nombre de usuario indicado no se\nencuentra disponible, inténtelo con uno distinto.");
                }
                if(ran.equals(nums.get(i))){
                    ran = String.valueOf(r.nextInt(99999));
                    i = 0;
                }
            }

            return alias + ran;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar introducir datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return "";
        }
    }

    public List<Employee> searchEmpTerm(int term, String text){
        PreparedStatement searchSta;
        List<Employee> emps = new ArrayList<Employee>();

        try{
            switch (term) {
                case 1:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_FNAME);
                    break;
                case 2:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_LNAME);
                    break;
                case 3:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_USER);
                    break;
                case 4:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_MAIL);
                    break;
                default:
                    emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                    return emps;
            }

            searchSta.setString(1, "%" + text + "%");
            ResultSet res = searchSta.executeQuery();
                    
            while (res.next()) {
                emps.add(new Employee(res.getInt(1), res.getBoolean(10) ? Online.ONLINE : Online.OFFLINE, res.getString(2) + " " + res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
            return emps;
        }
        
        if(emps.size() == 0){
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
        }
        
        return emps;
    }

    public List<Employee> searchEmpDateFull(int range, LocalDate date){
        PreparedStatement searchSta;
        List<Employee> emps = new ArrayList<Employee>();

        try{
            switch (range) {
                case 1:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_SDATE_FULL);
                    break;
                case 6:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_EDATE_FULL);
                    break;
            
                default:
                    emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                    return emps;
            }

            searchSta.setString(1, date.toString() + "%");
            ResultSet res = searchSta.executeQuery();
            
            while (res.next()) {
                emps.add(new Employee(res.getInt(1), res.getBoolean(10) ? Online.ONLINE : Online.OFFLINE, res.getString(2) + " " + res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
            return emps;
        }

        if(emps.size() == 0){
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
        }

        return emps;
    }

    public List<Employee> searchEmpDateSingle(int range, int date){
        PreparedStatement searchSta;
        List<Employee> emps = new ArrayList<Employee>();

        try{
            switch (range) {
                case 2:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_SDATE_YEAR);
                    break;
                case 3:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_SDATE_MONTH);
                    break;
                case 7:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_EDATE_YEAR);
                    break;
                case 8:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_EDATE_MONTH);
                    break;
            
                default:
                    emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                    return emps;
            }

            searchSta.setInt(1, date);
            ResultSet res = searchSta.executeQuery();
            
            while (res.next()) {
                emps.add(new Employee(res.getInt(1), res.getBoolean(10) ? Online.ONLINE : Online.OFFLINE, res.getString(2) + " " + res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
            return emps;
        }

        if(emps.size() == 0){
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
        }

        return emps;
    }

    public List<Employee> searchEmpDateDouble(int range, int date1, int date2){
        PreparedStatement searchSta;
        List<Employee> emps = new ArrayList<Employee>();

        try{
            switch (range) {
                case 4:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_SDATE_DDMM);
                    break;
                case 5:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_SDATE_MMYY);
                    break;
                case 9:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_EDATE_DDMM);
                    break;
                case 10:
                    searchSta = mBridge.conn.prepareStatement(EMP_SEARCH_EDATE_MMYY);
                    break;
            
                default:
                    emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                    return emps;
            }

            searchSta.setInt(1, date1);
            searchSta.setInt(2, date2);
            ResultSet res = searchSta.executeQuery();
            
            while (res.next()) {
                emps.add(new Employee(res.getInt(1), res.getBoolean(10) ? Online.ONLINE : Online.OFFLINE, res.getString(2) + " " + res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
            return emps;
        }

        if(emps.size() == 0){
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
        }

        return emps;
    }

    public int getActiveEmp(){
        int count = 0;

        try{
            ResultSet res = mBridge.querier.executeQuery(ACTI_EMP);
            res.next();
            count = res.getInt(1);
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
        }

        return count;
    }

    public boolean newEmpAct(int id){
        PreparedStatement actSta;

        try{
            actSta = mBridge.conn.prepareStatement(ACTI_ADD);
            actSta.setInt(1, id);

            if(actSta.executeUpdate() == 1){
                System.out.println("New active row added.");
                return true;
            }
            else{
                System.out.println("New active row creation failed.");
                GUI.launchMessage(2, "Error de base de datos", "No se ha creado un nuevo empleado,\ninténtelo de nuevo en unos instantes.");
                return false;
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return false;
        }
    }

    public List<Certificate> getCertificates(){
        List<Certificate> certs = new ArrayList<Certificate>();

        try{
            ResultSet res = mBridge.querier.executeQuery(CERTIFICATE);

            while(res.next()){
                String name = "";
                boolean status = false;
                
                res.getInt(2);
                if(!res.wasNull()){
                    name = getEmployeeById(res.getInt(2)).getAlias();
                    status = isGenerated(res.getInt(2), true);
                }
                else{
                    name = getGroupById(res.getInt(3)).getName();
                    status = isGenerated(res.getInt(3), true);
                }

                certs.add(new Certificate(name, status));
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
        }

        return certs;
    }

    public boolean newEmpCert(int emp){
        PreparedStatement certSta;

        try{
            certSta = mBridge.conn.prepareStatement(CERT_ADD_EMP);
            certSta.setInt(1, emp);

            if(certSta.executeUpdate() == 1){
                System.out.println("New empty certificate row added.");
                return true;
            }
            else{
                System.out.println("New empty certificate row creation failed.");
                GUI.launchMessage(2, "Error de base de datos", "No se ha creado un nuevo empleado,\ninténtelo de nuevo en unos instantes.");
                return false;
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return false;
        }
    }

    public boolean isGenerated(int id, boolean table){
        PreparedStatement certSta;

        try{
            if(table){
                certSta = mBridge.conn.prepareStatement(GEN_CERT_CHK_EMP);
                certSta.setInt(1, id);
            }
            else{
                certSta = mBridge.conn.prepareStatement(GEN_CERT_CHK_GRP);
                certSta.setInt(1, id);
            }

            ResultSet res = certSta.executeQuery();
            res.next();
            if(res.getInt(1) == 1){
                return true;
            }
            else{
                return false;
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return false;
        }
    }
    
    public int getGeneratedCert(){
        try{
            ResultSet res = mBridge.querier.executeQuery(GEN_CERT_COUNT);
            res.next();
            return res.getInt(1);
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return 0;
        }
    }

    public List<Group> getGroups() {
        List<Group> groups = new ArrayList<Group>();

        try{
            ResultSet res = mBridge.querier.executeQuery(GROUP);

            while(res.next()){
                groups.add(new Group(res.getInt(1), res.getString(2), getEmployeeById(res.getInt(4)).getAlias()));   
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
        }

        return groups;
    }

    public Group getGroupById(int id){
        Group grp = new Group(0, "", "");
        PreparedStatement grpSta;

        try{
            grpSta = mBridge.conn.prepareStatement(GROUP_BY_ID);
            grpSta.setInt(1, id);

            ResultSet res = grpSta.executeQuery();
            if(res.next()){
                grp.setId(res.getInt(1));
                grp.setName(res.getString(2));
                grp.setOwner(getEmployeeById(res.getInt(4)).getAlias());
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
        }

        return grp;
    }

    public int getGroupCount(){
        try{
            ResultSet res = mBridge.querier.executeQuery(GROUP_COUNT);
            res.next();
            return res.getInt(1);
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return 0;
        }
    }

    public int getEmpInGroup(){
        try{
            ResultSet res = mBridge.querier.executeQuery(GROUP_EMPS);
            res.next();
            return res.getInt(1);
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            return 0;
        }
    }
}