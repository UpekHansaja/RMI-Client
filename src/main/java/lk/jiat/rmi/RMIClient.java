package lk.jiat.rmi;

import lk.jiat.rmi.client.Msg;
import lk.jiat.rmi.client.StudentService;
import lk.jiat.rmi.model.Student;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Properties;

public class RMIClient {

    public static void main(String[] args) {
        try {

            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            String[] list = registry.list();

//            List all the registered objects
            for (String s : list) {
                System.out.println(s);
            }

//            Remote remote = registry.lookup("msg_service");
//            System.out.println("Remote object found: " + remote);
//            ---- Down cast to Message from Remote ----
            Msg msg = (Msg) registry.lookup("msg_service");
//            System.out.println("Remote object found: " + msg);

//            ---- Call the remote method ----
            String response = msg.hello("Upek");
            System.out.println("Response from server: " + response);

//            StudentService studentService = (StudentService) registry.lookup("student_service");
//            Instead of using registry.lookup, we can use Naming.lookup
            StudentService studentService = (StudentService) Naming.lookup("rmi://localhost:1099/student_service");
            List<Student> students = studentService.getStudents();
            students.forEach(student -> {
                System.out.println(student.getId() + " " + student.getName() + " " + student.getAddress() + " " + student.getContact());
            });

            System.out.println(((Msg) Naming.lookup("rmi://localhost:1099/msg_service")).hello("Upek"));

//            Also we can use InitialContext

            Properties prop = new Properties();
            prop.put(Context.PROVIDER_URL, "rmi://localhost:1099");
            prop.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");

            InitialContext ctx = new InitialContext(prop);
            StudentService studentServiceCTX = (StudentService) ctx.lookup("student_service");

            List<Student> studentsCTX = studentServiceCTX.getStudents();

            System.out.println(" \n Using InitialContext:");

            studentsCTX.forEach(student -> {
                System.out.println(student.getId() + " " + student.getName() + " " + student.getAddress() + " " + student.getContact());
            });

        } catch (Exception e) {
            System.out.println("RMI Client failed to connect: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
