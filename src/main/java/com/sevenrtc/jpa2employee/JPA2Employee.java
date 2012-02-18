package com.sevenrtc.jpa2employee;

import com.sevenrtc.jpa2employee.dto.EmpDept;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Anthony
 */
public class JPA2Employee {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException se houver qualquer falha
     */
    public static void main(String[] args) throws IOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA2EmployeePU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Department it = new Department();
        it.setName("IT");
        em.persist(it);
        
        final List<Department> departments = Collections.singletonList(it);
        
        Employee employee1 = new Employee();
        employee1.setPhoneNum("1234567");
        
        employee1.setEmployeeName(new EmployeeName("Anthony", "Accioly"));
        employee1.setSalary(1l);
        employee1.setComments("He is ok!");

        try {
            final Path p = Paths.get(JPA2Employee.class.getResource(
                    "/images/Manager-Cropped.jpg").toURI());
            employee1.setPicture(Files.readAllBytes(p));
        } catch (URISyntaxException ex) {
            Logger.getLogger(JPA2Employee.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        employee1.setEmployeeType(EmployeeType.CONTRACT_EMPLOYEE);
        Calendar dob = Calendar.getInstance();
        dob.set(1987, 6, 2);
        employee1.setDob(dob);
        Calendar startDate = Calendar.getInstance();
        startDate.set(2010, 8, 1);
        employee1.setStartDate(startDate.getTime());
        EnumMap<PhoneType,String> phones = new EnumMap<>(PhoneType.class);
        phones.put(PhoneType.HOME, "1234567");
        phones.put(PhoneType.MOBILE, "7654321");
        employee1.setPhoneNumber(phones);
        employee1.setDepartments(departments);
        
        em.persist(employee1);

        Employee employee2 = new Employee();
        employee2.setEmployeeName(new EmployeeName("Someone", "Else"));
        employee2.setPhoneNum("1231234567");
        employee2.setDepartments(departments);

        em.persist(employee2);

        Address address = new Address();
        address.setStreet("Rua Angélica");
        address.setCity("São Paulo");
        address.setState("São Paulo");
        address.setZip("01222-010");

        employee1.setAddress(address);
        em.merge(employee1);

        Company company = new Company();
        company.setName("7RTC");
        company.setAddress(address);
        em.persist(company);
        
        Map<EmployeeName, Employee> itEmployees = new HashMap<>();
        itEmployees.put(employee1.getEmployeeName(), employee1);
        itEmployees.put(employee2.getEmployeeName(), employee2);
        it.setEmployees(itEmployees);
        em.merge(it);
        
        camposOrdenados(em);
        clausulaNew(em);

        em.getTransaction().commit();

        List<Employee> employees = em.createQuery("select e from Employee e order by e.employeeName.firstName DESC", Employee.class).
                getResultList();

        for (Employee employee : employees) {
            System.out.println(employee);
            if (employee.getEmployeeName().equals(new EmployeeName("Anthony", "Accioly"))) {
               final Path toWrite = Paths.get(System.getProperty("user.home"), 
                       "Desktop", "teste.jpg");
               Files.write(toWrite, employee.getPicture());
            }
        }

    }
    
    private static void camposOrdenados(EntityManager em) {
        
        PrintQueue pq = new PrintQueue();
        pq.setName("Default");
        
        List<PrintJob> jobs = new ArrayList<>();
        PrintJob job1 = new PrintJob(1);
        PrintJob job2 = new PrintJob(2);
        PrintJob job3 = new PrintJob(3);
        
        jobs.add(job1);
        jobs.add(job2);
        jobs.add(job3);

        job1.setQueue(pq);
        job2.setQueue(pq);
        job3.setQueue(pq);
        pq.setJobs(jobs);
        
        em.persist(pq);
        System.out.println(pq);
        
        // swap job 2 with job 1
        job2 = pq.getJobs().remove(1);
        System.out.println(job2);
        pq.getJobs().add(0, job2);
        
        em.persist(pq);

        pq = em.find(PrintQueue.class, "Default");
        System.out.println(pq);     
    }
    
    public static void clausulaNew(EntityManager em) {
        List<EmpDept> result = em.createQuery(
                " SELECT"
                + " NEW com.sevenrtc.jpa2employee.dto.EmpDept("
                + " e.employeeName.lastName, d.name) "
                + " FROM Employee e JOIN e.departments d"
                + " GROUP BY e.id"
                + " ORDER by e.employeeName.lastName",
                EmpDept.class).getResultList();

        int count = 0;
        for (EmpDept menu : result) {
            System.out.println(++count + ": "
                    + menu.getEmployeeName() + ", "
                    + menu.getDepartmentName());
        }

    }
}
