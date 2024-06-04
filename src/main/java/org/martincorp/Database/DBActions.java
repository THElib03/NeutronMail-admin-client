package org.martincorp.Database;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.sql.rowset.serial.SerialBlob;

import org.martincorp.Codec.Encrypt;
import org.martincorp.Interface.GUI;
import org.martincorp.Model.Certificate;
import org.martincorp.Model.Employee;
import org.martincorp.Model.Group;
import org.martincorp.Model.Online;

/**
 * @author <a href="https://github.com/THElib03">Martín Marín</a>
 * @version 1.0, 06/21/23
 */
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
    private final String EMP_DROP;
    private final String EMP_EDIT = "UPDATE employee SET emp_fname = ?, emp_lname = ?, emp_sDate = ?, emp_eDate = ?, emp_alias = ?, emp_email = ? WHERE emp_id = ?";
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
    private final String ACTI_DROP = "DELETE FROM `active` WHERE act_emp = ?";
    private final String CERTIFICATE = "SELECT * FROM certificate";
    private final String CERT_ADD_EMP = "INSERT INTO certificate VALUES(NULL, ?, NULL, NULL)";
    private final String CERT_ADD_GROUP = "INSERT INTO certificate VALUES(NULL, NULL, ?, ?, ?)";
    private final String EMP_CERT_DROP = "DELETE FROM certificate WHERE cert_emp = ?";
    private final String GEN_CERT_COUNT = "SELECT COUNT(*) FROM certificate WHERE cert_public_key NOT IN ('empty')";
    private final String GEN_CERT_CHK_EMP = "SELECT COUNT(*) FROM certificate WHERE cert_emp=? AND cert_public_key NOT IN ('empty')";
    private final String GEN_CERT_CHK_GRP = "SELECT COUNT(*) FROM certificate WHERE cert_group=? AND cert_public_key NOT IN ('empty')";
    private final String GROUP_RAW = "SELECT * FROM publicgroup";
    private final String GROUP_SECURE = "SELECT grp_id, grp_name, grp_owner, CONCAT(emp_fname, ' ', emp_lname) AS 'owner_name', grp_creationDate FROM publicgroup JOIN employee ON (publicgroup.grp_owner=employee.emp_id)";
    private final String GROUP_BY_ID = "SELECT grp_id, grp_name, grp_owner, CONCAT(emp_fname, ' ', emp_lname) AS 'owner_name', grp_creationDate FROM publicgroup JOIN employee ON (publicgroup.grp_owner=employee.emp_id) WHERE grp_id = ?";
    private final String GROUP_ADD = "INSERT INTO publicgroup VALUES(NULL, ?, ?, ?, NULL)";
    private final String GROUP_COUNT = "SELECT COUNT(*) FROM publicgroup";
    private final String GROUP_EMPS = "SELECT COUNT(*) FROM groupuser";
    private final String GROUP_SEARCH_NAME = "SELECT grp_id, grp_name, grp_owner, CONCAT(emp_fname, ' ', emp_lname) AS 'owner_name', grp_creationDate FROM publicgroup JOIN employee ON (publicgroup.grp_owner=employee.emp_id) WHERE grp_name LIKE ?";
    private final String GROUP_SEARCH_OWNER = "SELECT grp_id, grp_name, grp_owner, CONCAT(emp_fname, ' ', emp_lname), grp_creationDate AS owner_name FROM publicgroup JOIN employee ON (publicgroup.grp_owner=employee.emp_id) WHERE CONCAT(emp_fname, ' ', emp_lname) LIKE ?";
    private final String GROUP_SEARCH_DATE_FULL = "SELECT grp_id, grp_name, grp_owner, CONCAT(emp_fname, ' ', emp_lname) AS 'owner_name', grp_creationDate FROM publicgroup JOIN employee ON (publicgroup.grp_owner=employee.emp_id) WHERE grp_creationDate LIKE ?";
    private final String GROUP_SEARCH_DATE_YEAR = "SELECT grp_id, grp_name, grp_owner, CONCAT(emp_fname, ' ', emp_lname) AS 'owner_name', grp_creationDate FROM publicgroup JOIN employee ON (publicgroup.grp_owner=employee.emp_id) WHERE YEAR(grp_creationDate) = ?";
    private final String GROUP_SEARCH_DATE_MONTH = "SELECT grp_id, grp_name, grp_owner, CONCAT(emp_fname, ' ', emp_lname) AS 'owner_name', grp_creationDate FROM publicgroup JOIN employee ON (publicgroup.grp_owner=employee.emp_id) WHERE MONTH(grp_creationDate) = ?";
    private final String GROUP_SEARCH_DATE_DDMM = "SELECT grp_id, grp_name, grp_owner, CONCAT(emp_fname, ' ', emp_lname) AS 'owner_name', grp_creationDate FROM publicgroup JOIN employee ON (publicgroup.grp_owner=employee.emp_id) WHERE DAY(grp_creationDate) = ? AND MONTH(grp_creationDate) = ?";
    private final String GROUP_SEARCH_DATE_MMYY = "SELECT grp_id, grp_name, grp_owner, CONCAT(emp_fname, ' ', emp_lname) AS 'owner_name', grp_creationDate FROM publicgroup JOIN employee ON (publicgroup.grp_owner=employee.emp_id) WHERE MONTH(grp_creationDate) = ? AND YEAR(grp_creationDate) = ?";

    //Builder:
    public DBActions(){
        mBridge = new MainBridge();
        lBridge = new LoginBridge();
        enc = new Encrypt(2);

        //Querys declaration:
        EMP_ADD_PASS = "INSERT INTO " + lBridge.getDB() + ".`user` VALUES(NULL, ?, ?, ?)";
        EMP_DROP = "DELETE FROM " + lBridge.getDB() + ".`user` WHERE user_alias LIKE ? AND user_comp = ?";
        try{
            Thread.sleep(750);
        }
        catch(InterruptedException ie){
            ie.printStackTrace();
            GUI.launchMessage(2, "Error interno", "Ha ocurrido un error durante el manejo de procesos:\n\n" + ie.getMessage());
        }
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
            res.next();

            while(res.next()){
                if(res.getInt(1) != 1){
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

    public boolean dropEmp(int empId, String empAlias){
        PreparedStatement dropSta;
        PreparedStatement dropSta2;
        PreparedStatement dropSta3;

        try{
            dropSta = mBridge.conn.prepareStatement(EMP_DROP);
            dropSta2 = mBridge.conn.prepareStatement(EMP_CERT_DROP);
            dropSta3 = mBridge.conn.prepareStatement(ACTI_DROP);
            //DONE: ALSO DELETE IN THIS METHOD THE ASSOCIATED CERTIFICATE AND ACTIVE ROW. NO NEED FOR EXTRA METHODS FOR 3 THINGS SO LINKED
            dropSta.setString(1, empAlias);
            dropSta.setInt(2, getCompanyId(mBridge.getDB()));
            dropSta2.setInt(1, empId);
            dropSta3.setInt(1, empId);

            if(dropSta.executeUpdate() == 1 && dropSta2.executeUpdate() == 1 && dropSta3.executeUpdate() == 1){
                GUI.launchMessage(3, "Operación completada", "Se ha eliminado con éxito al empleado seleccionado.");
                return true;
            }
            else{
                GUI.launchMessage(2, "Operación fallida", "No se ha podido completar la operación solicitada,\nes posible que no se haya encontrado el empleado seleccionado.");
                return false;
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar eliminar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);

            return false;
        }
    }

    /**
     * Modifies the indicated employee row.
     * @since 1.0
     * @param empId id of the employee to be modified
     * @param newEmp updated employee object to push into the database
     * @return true if the edit is successful, false if any problem arises
     */
    public boolean editEmp(int empId, Employee newEmp){
        PreparedStatement editSta;

        try{
            editSta = mBridge.conn.prepareStatement(EMP_EDIT);

            editSta.setString(1, newEmp.getName().split(" ", 2)[0]);
            editSta.setString(2, newEmp.getName().split(" ", 2)[1]);
            editSta.setDate(3, Date.valueOf(LocalDate.parse(newEmp.getStartDate())));
            editSta.setDate(4, Date.valueOf(LocalDate.parse(newEmp.getStartDate())));
            editSta.setString(5, newEmp.getAlias());
            editSta.setString(6, newEmp.getMail());
            editSta.setInt(7, empId);

            if(editSta.executeUpdate() == 1){
                GUI.launchMessage(3, "Operación completada", "Se ha modifiado con éxito al empleado seleccionado.");
                return true;
            }
            else{
                GUI.launchMessage(2, "Operación fallida", "No se ha podido completar la operación solicitada,\nes posible que no se haya encontrado el empleado seleccionado.");
                return false;
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar modificar datos.\n\n" + sqle.getMessage());
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
                    GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                    emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                    return emps;
            }

            searchSta.setString(1, "%" + text + "%");
            ResultSet res = searchSta.executeQuery();
                    
            while (res.next()){
                if(res.getInt(1) != 1){
                    emps.add(new Employee(res.getInt(1), res.getBoolean(9) ? Online.ONLINE : Online.OFFLINE, res.getString(2) + " " + res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
                }
            }

            return emps;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
            return emps;
        }
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
                    GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                    emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                    return emps;
            }

            searchSta.setString(1, date.toString() + "%");
            ResultSet res = searchSta.executeQuery();
            
            while (res.next()){
                if(res.getInt(1) != 1){
                    emps.add(new Employee(res.getInt(1), res.getBoolean(9) ? Online.ONLINE : Online.OFFLINE, res.getString(2) + " " + res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
                }
            }

            return emps;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
            return emps;
        }
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
                    GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                    emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                    return emps;
            }

            searchSta.setInt(1, date);
            ResultSet res = searchSta.executeQuery();
            
            while (res.next()){
                if(res.getInt(1) != 1){
                    emps.add(new Employee(res.getInt(1), res.getBoolean(9) ? Online.ONLINE : Online.OFFLINE, res.getString(2) + " " + res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
                }
            }

            return emps;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
            return emps;
        }
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
                    GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                    emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                    return emps;
            }

            searchSta.setInt(1, date1);
            searchSta.setInt(2, date2);
            ResultSet res = searchSta.executeQuery();
            
            while (res.next()){
                if(res.getInt(1) != 1){
                    emps.add(new Employee(res.getInt(1), res.getBoolean(9) ? Online.ONLINE : Online.OFFLINE, res.getString(2) + " " + res.getString(3), res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
                }
            }

            return emps;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
            return emps;
        }
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
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar crear datos.\n\n" + sqle.getMessage());
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

    public boolean newGrpCert(int grp){
        return false;
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
            ResultSet res = mBridge.querier.executeQuery(GROUP_SECURE);
            res.next();

            while(res.next()){
                groups.add(new Group(res.getInt(1), res.getInt(3), res.getString(2), res.getString(4) + " (" + res.getInt(3) + ")", res.getDate(5).toString()));   
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
        Group grp = new Group(0, 0, "", "", LocalDate.now().toString());
        PreparedStatement grpSta;

        try{
            grpSta = mBridge.conn.prepareStatement(GROUP_BY_ID);
            grpSta.setInt(1, id);

            ResultSet res = grpSta.executeQuery();
            if(res.next()){
                grp.setId(res.getInt(1));
                grp.setName(res.getString(2));
                grp.setOwner(res.getString(4) + " (" + res.getInt(3) + ")");
                grp.setCreationDate(res.getDate(5).toString());
            }
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar cargar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
        }

        return grp;
    }

    public boolean newGroup(Group newGrp){
        PreparedStatement newSta;
        // INSERT INTO publicgroup VALUES(NULL, ?, ?, ?, NULL)

        try{
            newSta = mBridge.conn.prepareStatement(GROUP_ADD);
            newSta.setString(1, newGrp.getName());
            
        }
        catch(SQLException sqle){

        }

        return false;
    }

    public boolean dropGrp(int grpId){
        return false;
    }

    public boolean editGrp(int grpId, Group newGrp){
        return false;
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

    /**
     * Searches for groups that match the text input with a '%(text)%' pattern.
     * @since 1.0
     * @param term indicates which column to search, group  name (1) or the group creator (2)
     * @param text the string which MySQL will attempt to match
     * @return a list of groups that match the given text
     */
    public List<Group> searchGrpTerm(int term, String text){
        PreparedStatement searchSta;
        List<Group> grps = new ArrayList<Group>();

        try{
            switch(term){
                case 1:
                    searchSta = mBridge.conn.prepareStatement(GROUP_SEARCH_NAME);
                    break;
                case 2:
                    searchSta = mBridge.conn.prepareStatement(GROUP_SEARCH_OWNER);
                    break;
            
                default:
                    GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                    grps.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
                    return grps;
            }

            searchSta.setString(1, "%" + text + "%");
            ResultSet res = searchSta.executeQuery();

            while (res.next()){
                if(res.getInt(1) != 1){
                    grps.add(new Group(res.getInt(1), res.getInt(3), res.getString(2), res.getString(4) + " (" + res.getInt(3) + ")", res.getDate(5).toString()));
                }
            }

            return grps;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);
            grps.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
            return grps;
        }
    }

    /**
     * Searches for groups whose date of creation matches the given date.
     * @since 1.0
     * @param date the date which MySQL will attempt to match
     * @return a list of groups that match the given date
     */
    public List<Group> searchGrpDateFull(LocalDate date){
        PreparedStatement searchSta;
        List<Group> grps = new ArrayList<Group>();

        try{
            searchSta = mBridge.conn.prepareStatement(GROUP_SEARCH_DATE_FULL);
            searchSta.setString(1, date.toString() + "%");
            ResultSet res = searchSta.executeQuery();

            while(res.next()){
                if(res.getInt(1) != 1){
                    grps.add(new Group(res.getInt(1), res.getInt(3), res.getString(2), res.getString(4) + " (" + res.getInt(3) + ")", res.getDate(5).toString()));
                }
            }

            return grps;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);

            grps.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
            return grps;
        }
    }

    /**
     * Searches for groups whose date of creation matches the given year or month.
     * @since 1.0
     * @param range whether it will compare year (2) or month (3)
     * @param date the given year or month that MySQL will attempt to match
     * @return a list of groups that match the given year or month
     */
    public List<Group> searchGrpDateSingle(int range, int date){
        PreparedStatement searchSta;
        List<Group> grps = new ArrayList<Group>();

        try{
            switch(range){
                case 2:
                    searchSta = mBridge.conn.prepareStatement(GROUP_SEARCH_DATE_YEAR);
                    break;
                case 3:
                    searchSta = mBridge.conn.prepareStatement(GROUP_SEARCH_DATE_MONTH);
                    break;
            
                default:
                    GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                    grps.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
                    return grps;
            }

            searchSta.setString(1, String.valueOf(date));
            ResultSet res = searchSta.executeQuery();

            while(res.next()){
                if(res.getInt(1) != 1){
                    grps.add(new Group(res.getInt(1), res.getInt(3), res.getString(2), res.getString(4) + " (" + res.getInt(3) + ")", res.getDate(5).toString()));
                }
            }

            return grps;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);

            grps.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
            return grps;
        }
    }

    /**
     * Searches for groups whose date of creation matches the given period of time, a day/month (dd/MM) of every year or a exact month of a exact year (MM/yyyy).
     * @since 1.0
     * @param range whether it will compare dd/MM or MM/yyyy
     * @param date1 the first unit of the date, either dd or MM
     * @param date2 the second unit of the date, either MM or yyyy
     * @return
     */
    public List<Group> searchGrpDateDouble(int range, int date1, int date2){
        PreparedStatement searchSta;
        List<Group> grps = new ArrayList<Group>();

        try{
            switch(range){
                case 4:
                    searchSta = mBridge.conn.prepareStatement(GROUP_SEARCH_DATE_DDMM);
                    break;
                case 5:
                    searchSta = mBridge.conn.prepareStatement(GROUP_SEARCH_DATE_MMYY);
                    break;
            
                default:
                    GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                    grps.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
                    return grps;
            }
            
            searchSta.setInt(1, date1);
            searchSta.setInt(2, date2);
            ResultSet res = searchSta.executeQuery();

            while(res.next()){
                if(res.getInt(1) != 1){
                    grps.add(new Group(res.getInt(1), res.getInt(3), res.getString(2), res.getString(4) + " (" + res.getInt(3) + ")", res.getDate(5).toString()));
                }
            }

            return grps;
        }
        catch(SQLException sqle){
            sqle.printStackTrace();
            GUI.launchMessage(2, "Error de base de datos", "Ha ocurrido un error al intentar insertar datos.\n\n" + sqle.getMessage());
            mBridge.checkConnection(false);

            grps.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
            return grps;
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