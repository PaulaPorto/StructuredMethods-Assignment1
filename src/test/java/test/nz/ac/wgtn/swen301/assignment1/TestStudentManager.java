package test.nz.ac.wgtn.swen301.assignment1;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.Degree;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;
import nz.ac.wgtn.swen301.studentdb.StudentDB;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotNull;


public class TestStudentManager {

    // DO NOT REMOVE THE FOLLOWING -- THIS WILL ENSURE THAT THE DATABASE IS AVAILABLE
    // AND IN ITS INITIAL STATE BEFORE EACH TEST RUNS
    @Before
    public  void init () {
        StudentDB.init();
    }
    // DO NOT REMOVE BLOCK ENDS HERE

    @Test
    public void dummyTest() throws Exception {
        Student student = new StudentManager().readStudent("id42");
        // THIS WILL INITIALLY FAIL !!
        assertNotNull(student);
    }

    @Test
    public void testPerformance() throws Exception {
        Random r = new Random();
        long startTime = System.currentTimeMillis();
        for (int i = 0; i <= 1000; i ++){
            new StudentManager().readStudent("id" + r.nextInt(1000));
        }
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }

    @Test
    public void test_readStudent_1() throws Exception {
        Student s = new StudentManager().readStudent("id0");
        assert s.getId().equals("id0");
        assert s.getName().equals("James");
        assert s.getFirstName().equals("Smith");
        assert s.getDegree().getId().equals("deg0");
    }

    @Test
    public void test_readStudent_2() throws Exception {
        Student s = new StudentManager().readStudent("id3");
        assert s.getId().equals("id3");
        assert s.getName().equals("Max");
        assert s.getFirstName().equals("Muller");
        assert s.getDegree().getId().equals("deg3");
    }

    @Test
    public void test_readDegree_1() throws Exception {
        Degree d = new StudentManager().readDegree("deg0");
        assert d.getId().equals("deg0");
        assert d.getName().equals("BSc Computer Science");
    }

    @Test
    public void test_readDegree_2() throws Exception {
        Degree d = new StudentManager().readDegree("deg6");
        assert d.getId().equals("deg6");
        assert d.getName().equals("BA Art");
    }

    @Test
    public void test_delete() throws Exception {
        boolean bool = false;
        Student s = new StudentManager().readStudent("id0");
        try {
            new StudentManager().delete(s);
        } catch (NoSuchRecordException e){
            bool = true;
        }
        assert(bool == true);
    }

    @Test
    public void test_update() throws Exception {
        Degree degree = new Degree("deg6", "BA Art");
        Student student = new Student("id0", "Julia", "Mintt", degree );
        new StudentManager().update(student);

        Student s = new StudentManager().readStudent("id0");
        assert(s.getId().equals("id0"));
        assert(s.getName().equals("Mintt"));
        assert(s.getFirstName().equals("Julia"));
        assert(s.getDegree().getId().equals(degree.getId()));
    }

    @Test
    public void test_createStudent_1() throws Exception {
        Degree degree = new Degree("deg7", "BA Philosophy");
        new StudentManager().createStudent("Lola", "Smith", degree);
        Student s = new StudentManager().readStudent("id10000");
        assert(s.getId().equals("id10000"));
        assert(s.getFirstName().equals("Lola"));
        assert(s.getName().equals("Smith"));
        assert(s.getDegree().getId().equals(degree.getId()));

        Degree deg = new Degree("deg5", "BSc Chemistry");
        new StudentManager().createStudent("Jakob", "Simka", deg);
        Student stu = new StudentManager().readStudent("id10001");
        assert(stu.getId().equals("id10001"));
        assert(stu.getFirstName().equals("Jakob"));
        assert(stu.getName().equals("Simka"));
        assert(stu.getDegree().getId().equals(deg.getId()));
    }

    @Test
    public void test_createStudent_2() throws Exception {
        Degree degree = new Degree("deg4", "BSc Mathematics");
        new StudentManager().createStudent("Bob", "Connor", degree);
        Student s = new StudentManager().readStudent("id10002");
        assert(s.getId().equals("id10002"));
        assert(s.getFirstName().equals("Bob"));
        assert(s.getName().equals("Connor"));
        assert(s.getDegree().getId().equals(degree.getId()));
    }

    @Test
    public void test_getAllStudentIds_1() throws Exception {
        Collection<String> allIds = new StudentManager().getAllStudentIds();
        assertNotNull(allIds);
    }

    @Test
    public void test_getAllStudentIds_2() throws Exception {
        Collection<String> allIds = new StudentManager().getAllStudentIds();
        List<String> list = new ArrayList<>();
        String url = "jdbc:derby:memory:studentdb";
        Connection conn = DriverManager.getConnection(url);

        Statement statement = conn.createStatement();
        String sql = "SELECT id FROM STUDENTS";
        ResultSet results = statement.executeQuery(sql);
        while(results.next()) {
            String id = results.getString("id");
            list.add(id);
        }
        assert allIds.equals(list);
    }

    @Test
    public void test_getAllDegreeIds_1() throws Exception {
        Iterable<String> allDegreeIds = new StudentManager().getAllDegreeIds();
        assertNotNull(allDegreeIds);
    }

    @Test
    public void test_getAllDegreeIds_2() throws Exception {
        Iterable<String> allDegreeIds = new StudentManager().getAllDegreeIds();
        List<String> list = new ArrayList<>();
        String url = "jdbc:derby:memory:studentdb";
        Connection conn = DriverManager.getConnection(url);

        Statement statement = conn.createStatement();
        String sql = "SELECT id FROM DEGREES";
        ResultSet results = statement.executeQuery(sql);
        while(results.next()) {
            String id = results.getString("id");
            list.add(id);
        }
        assert allDegreeIds.equals(list);
    }
}
