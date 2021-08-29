package nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.studentdb.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * A student managers providing basic CRUD operations for instances of Student, and a read operation for instances of Degree.
 * @author jens dietrich
 */
public class StudentManager {

    private static Connection conn;
    private static String url = "jdbc:derby:memory:studentdb";
    private static String lastID = "";
    private static boolean bool = true;
    private static PreparedStatement statement;

    private static String sql1 = "SELECT * FROM STUDENTS WHERE id LIKE ?";
    private static String sql2 = "SELECT * FROM DEGREES WHERE id LIKE ?";
    private static String identity;
    private static String firstName;
    private static String name;
    private static String degree;

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND THE APPLICATION CAN CONNECT TO IT WITH JDBC
    static {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE


    // THE FOLLOWING METHODS MUST IMPLEMENTED :

    /**
     * Return a student instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id of the student.
     * @return a student object according to the input id.
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_readStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student readStudent(String id) throws NoSuchRecordException{
        try {
        conn = DriverManager.getConnection(url);
        HashMap<String, Student> studentsMap = new HashMap<>();
        if(studentsMap.containsKey(id)){
            return studentsMap.get(id);
        } else {
            statement = conn.prepareStatement(sql1);
            statement.setString(1, id);
            ResultSet results = statement.executeQuery();
            while(results.next()) {
                identity = results.getString(1);
                firstName = results.getString(2);
                name = results.getString(3);
                degree = results.getString(4);
                Student s = new Student(identity, firstName, name, readDegree(degree));
                studentsMap.put(id,s);
                conn.close();
                statement.close();
                results.close();
                return s;
            }
        }
        } catch (SQLException e){
        }
        throw new NoSuchRecordException("The input id does not exist in the STUDENT file");
    }

    /**
     * Return a degree instance with values from the row with the respective id in the database.
     * If an instance with this id already exists, return the existing instance and do not create a second one.
     * @param id of the degree.
     * @return a degree object according to the input id.
     * @throws NoSuchRecordException if no record with such an id exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_readDegree (followed by optional numbers if multiple tests are used)
     */
    public static Degree readDegree(String id) throws NoSuchRecordException{
        try {
        conn = DriverManager.getConnection(url);
        statement = conn.prepareStatement(sql2);
        statement.setString(1, id);
        ResultSet results = statement.executeQuery();
        HashMap<String, Degree> degreeMap = new HashMap<>();
        if(degreeMap.containsKey(id)){
            return degreeMap.get(id);
        } else {
            while(results.next()) {
                Degree d = new Degree(id,results.getString("name"));
                degreeMap.put(id, d);
                statement.close();
                results.close();
                conn.close();
                return d;
            }
        }
        } catch (SQLException e){
        }
        throw new NoSuchRecordException("The input degree Id does not exists in the database");
    }

    /**
     * Delete a student instance from the database.
     * I.e., after this, trying to read a student with this id will result in a NoSuchRecordException.
     * @param student object that will be deleted.
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_delete
     */
    public static void delete(Student student) throws NoSuchRecordException{
        try {
        conn = DriverManager.getConnection(url);
        String id = student.getId();
        Statement statement = conn.createStatement();
        String sql = "DELETE FROM STUDENTS WHERE id LIKE '" + id + "'";
        int rowsAffected = statement.executeUpdate(sql);
        if(rowsAffected > 0) {
            conn.close();
            statement.close();
            throw new NoSuchRecordException("Student has been deleted");
        }
        conn.close();
        statement.close();
        } catch (SQLException e){
        }
        throw new NoSuchRecordException("Input Student does not exist");
    }

    /**
     * Update (synchronize) a student instance with the database.
     * The id will not be changed, but the values for first names or degree in the database might be changed by this operation.
     * After executing this command, the attribute values of the object and the respective database value are consistent.
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param student object with the new values to be updated.
     * @throws NoSuchRecordException if no record corresponding to this student instance exists in the database
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_update (followed by optional numbers if multiple tests are used)
     */
    public static void update(Student student) throws NoSuchRecordException{
        try {
        String id = student.getId();
        String firstN = student.getFirstName();
        String name = student.getName();
        String deg = student.getDegree().getId();
        if(name.length() > 10){
            String newN = name.substring(0,10);
            name = newN;
        }
        if(firstN.length() > 10){
           String newFN = firstN.substring(0,10);
           firstN = newFN;
        }
        conn = DriverManager.getConnection(url);
        Statement statement = conn.createStatement();
        String sql = "UPDATE STUDENTS SET first_name = '" + firstN + "', name = '" +
                name + "', degree = '" + deg + "' WHERE id = '" + id + "'";
        int rowsAffected = statement.executeUpdate(sql);
        if(rowsAffected < 1){
            conn.close();
            statement.close();
            throw new NoSuchRecordException("This id does not exist in the databse");
        }
        conn.close();
        statement.close();
        } catch (SQLException e){
        }
    }


    /**
     * Create a new student with the values provided, and save it to the database.
     * The student must have a new id that is not been used by any other Student instance or STUDENTS record (row).
     * Note that names and first names can only be max 1o characters long.
     * There is no special handling required to enforce this, just ensure that tests only use values with < 10 characters.
     * @param name of the student being created.
     * @param firstName of the student being created.
     * @param degree of the student being created.
     * @return a freshly created student instance
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_createStudent (followed by optional numbers if multiple tests are used)
     */
    public static Student createStudent(String name,String firstName,Degree degree){
        Student student = new Student();
        try {
        conn = DriverManager.getConnection(url);
        List<String> list = new ArrayList<>();
        Collection<String> collection = getAllStudentIds();
        for(String ids : collection){
            list.add(ids);
        }
        if(bool == true){
            lastID = list.get(list.size()-1);
            bool = false;
        }
        String newId = "";
        System.out.println(lastID);
        list.add(lastID);
        if(list.contains(lastID)){
            int length = lastID.length();
            String number = lastID.substring(2, length);
            int num = Integer.parseInt(number);
            num++;
            System.out.println(num);
            newId = "id" + num;
            lastID = newId;
        }
        Statement statement = conn.createStatement();
        String sql = "INSERT INTO STUDENTS (id, first_name, name, degree) VALUES " +
                "('" + newId + "', '" + firstName + "', '" + name + "', '" + degree.getId() + "')";
        int rowsAffected = statement.executeUpdate(sql);
        if(rowsAffected > 0){
            Student s = new Student(newId, name, firstName, degree);
            student = s;
        }
        statement.close();
        conn.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return student;
    }

    /**
     * Get all student ids currently being used in the database.
     * @return the collection of all student ids.
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_getAllStudentIds (followed by optional numbers if multiple tests are used)
     */
    public static Collection<String> getAllStudentIds(){
        List<String> collection = new ArrayList<>();
        try {
        conn = DriverManager.getConnection(url);
        Statement statement = conn.createStatement();
        String sql = "SELECT id FROM STUDENTS";
        ResultSet results = statement.executeQuery(sql);
        while(results.next()) {
            String id = results.getString("id");
            collection.add(id);
        }
        } catch (SQLException e){
        }
        return collection;
    }

    /**
     * Get all degree ids currently being used in the database.
     * @return collection of all degree ids.
     * This functionality is to be tested in test.nz.ac.wgtn.swen301.assignment1.TestStudentManager::test_getAllDegreeIds (followed by optional numbers if multiple tests are used)
     */
    public static Iterable<String> getAllDegreeIds(){
        List<String> collection = new ArrayList<>();
        try {
        conn = DriverManager.getConnection(url);
        Statement statement = conn.createStatement();
        String sql = "SELECT id FROM DEGREES";
        ResultSet results = statement.executeQuery(sql);
        while(results.next()) {
            String id = results.getString("id");
            collection.add(id);
        }
        } catch (SQLException e){
        }
        return collection;
    }


}
