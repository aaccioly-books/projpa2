package com.sevenrtc.jpa2employee;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
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
     */
    public static void main(String[] args) throws IOException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA2EmployeePU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        Employee employee1 = new Employee();
        employee1.setPhoneNum("1234567");
        employee1.setName("Anthony");
        employee1.setSalary(1l);
        employee1.setComments("He is ok!");
        employee1.setPicture(Resources.toByteArray(JPA2Employee.class.getResource("/images/Manager-Cropped.jpg")));
        employee1.setEmployeeType(EmployeeType.CONTRACT_EMPLOYEE);
        Calendar dob = Calendar.getInstance();
        dob.set(1987, 6, 2);
        employee1.setDob(dob);
        Calendar startDate = Calendar.getInstance();
        startDate.set(2010, 8, 1);
        employee1.setStartDate(startDate.getTime());
        em.persist(employee1);

        Employee employee2 = new Employee();
        employee2.setPhoneNum("1231234567");

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

        em.getTransaction().commit();

        List<Employee> employees = em.createQuery("select e from Employee e order by e.name DESC", Employee.class).
                getResultList();

        for (Employee employee : employees) {
            System.out.println(employee);
        }

        File toWrite = new File(System.getProperty("user.home") + "/Desktop/teste.jpg");
        Files.write(employees.get(0).getPicture(), toWrite);

    }
}
