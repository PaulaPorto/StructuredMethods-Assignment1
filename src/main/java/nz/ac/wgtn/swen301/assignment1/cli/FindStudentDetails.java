package nz.ac.wgtn.swen301.assignment1.cli;

import nz.ac.wgtn.swen301.assignment1.StudentManager;
import nz.ac.wgtn.swen301.studentdb.NoSuchRecordException;
import nz.ac.wgtn.swen301.studentdb.Student;

public class FindStudentDetails {

    // THE FOLLOWING METHOD MUST IMPLEMENTED
    /**
     * Executable: the user will provide a student id as single argument, and if the details are found,
     * the respective details are printed to the console.
     * E.g. a user could invoke this by running "java -cp <someclasspath> nz.ac.wgtn.swen301.assignment1.cli.FindStudentDetails id42"
     * @param arg
     */
    public static void main (String[] arg) {
        try {
            Student s = new StudentManager().readStudent(arg[0]);
            if (arg != null) {
                System.out.println("ID: " + s.getId());
                System.out.println("Name: " + s.getFirstName());
                System.out.println("First Name: " + s.getName());
                System.out.println("Degree ID: " + s.getDegree().getId());
                System.out.println("Degree Name: " + s.getDegree().getName());
            }
        }catch (Exception e) {
        }
    }

}
