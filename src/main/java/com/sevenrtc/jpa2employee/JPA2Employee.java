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
import javax.persistence.*;
import javax.persistence.criteria.*;

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
        
        Employee employee3 = new Employee();
        employee3.setEmployeeName(new EmployeeName("Flavio", "Antunes"));
        employee3.setPhoneNum("12345");
        em.persist(employee3);
        
        employee1.setManager(employee3);
        em.merge(employee1);

        Company company = new Company();
        company.setName("7RTC");
        company.setAddress(address);
        em.persist(company);
        
        Project p1 = new DesignProject();
        p1.setName("Telefonica");
        List<Employee> projectEmployees = new ArrayList<>();
        projectEmployees.add(employee1);
        projectEmployees.add(employee3);
        p1.setEmployees(projectEmployees);
        em.persist(p1);
        
        Project p2 = new QualityProject();
        p2.setName("Pernambucanas");
        em.persist(p2);
        
        List<Project> empProjects = Collections.singletonList(p1);
        employee1.setProjects(empProjects);
        employee3.setProjects(empProjects);
        em.merge(p1);
        
        Map<EmployeeName, Employee> itEmployees = new HashMap<>();
        itEmployees.put(employee1.getEmployeeName(), employee1);
        itEmployees.put(employee2.getEmployeeName(), employee2);
        itEmployees.put(employee3.getEmployeeName(), employee3);
        it.setEmployees(itEmployees);
        em.merge(it);
        
        em.getTransaction().commit();
        
        // Consultas
        
        camposOrdenados(em);
        clausulaNew(em);

        System.out.println("\nEmpregados ordenados por nome: ");
        List<Employee> employees = em.createQuery("select e from Employee e order by e.employeeName.firstName DESC", Employee.class).getResultList();
        print(employees, "\t", "\n");
        
        criterias(em);
        
        for (Employee employee : employees) {
            if (employee.getEmployeeName().equals(new EmployeeName("Anthony", "Accioly"))) {
                final Path toWrite = Paths.get(System.getProperty("user.home"),
                        "Desktop", "teste.jpg");
                Files.write(toWrite, employee.getPicture());
            }
        }

    }
    
    private static void camposOrdenados(EntityManager em) {
        
        System.out.println("\nFila de impressão ordenada: \n");
                
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
        
        em.getTransaction().begin();
        em.persist(pq);
        em.getTransaction().commit();
        
        System.out.printf("\tFila Inical: %s\n", pq);
        
        em.getTransaction().begin();  
        // swap job 2 with job 1
        job2 = pq.getJobs().remove(1);
        System.out.printf("\tSegundo job: %s\n", job2);
        pq.getJobs().add(0, job2);
        
        em.persist(pq);
        em.getTransaction().commit();

        pq = em.find(PrintQueue.class, "Default");
        System.out.printf("\tFila após o swap: %s\n", pq);
    }
    
    public static void clausulaNew(EntityManager em) {
        System.out.println("\nEmpregados e departamentos:");
        List<EmpDept> result = em.createQuery(
                " SELECT"
                + " NEW com.sevenrtc.jpa2employee.dto.EmpDept("
                + " e.employeeName.lastName, d.name) "
                + " FROM Employee e JOIN e.departments d"
                + " GROUP BY e.id"
                + " ORDER by e.employeeName.lastName",
                EmpDept.class).getResultList();

        int count = 0;
        for (EmpDept empDept : result) {
            System.out.printf("\t%d: %s, %s\n", 
                    ++count, 
                    empDept.getEmployeeName(),
                    empDept.getDepartmentName());
        }

    }
    
    public static void criterias(EntityManager em) {
        
        int criteriaCounter = 0;
        
        System.out.println("\n-------------\n *Criterias*\n-------------\n");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> c = cb.createQuery(Employee.class);
        Root<Employee> emp = c.from(Employee.class);
        
        c.select(emp).distinct(true)
                .where(cb.equal(
                emp.get(Employee_.employeeName).get(EmployeeName_.lastName), 
                "Accioly"));

        
        
        System.out.printf("Criteria %d:\n", ++criteriaCounter);
        Employee employee = em.createQuery(c).getSingleResult();
        System.out.printf("\t%s\n", employee);
        
        List<Employee> employees;
        
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        employees = findEmployees(em, "Anthony", null, null, null);
        print(employees, "\t", "\n");
        
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        employees = findEmployees(em, null , "IT", null, null);
        print(employees, "\t", "\n");
        
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        employees = findEmployees(em, null , null, "Telefonica", null);
        print(employees, "\t", "\n");
        
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        employees = findEmployees(em, null , null, null, "São Paulo");
        print(employees, "\t", "\n");
        
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        employees = findEmployees(em, "Anthony" , "IT", "Telefonica", "São Paulo");
        print(employees, "\t", "\n");
        
        // Multiplos selects
        List<Tuple> empTuples;
        
        // Tupla com ID e nome
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        CriteriaQuery<Tuple> t = cb.createTupleQuery();
        emp = t.from(Employee.class);
        t.distinct(true).select(
                cb.tuple(
                emp.get(Employee_.id).alias("id"),
                emp.get(Employee_.employeeName)
                    .get(EmployeeName_.firstName).alias("name")
                )
        ).groupBy(emp.get(Employee_.id));
        empTuples = em.createQuery(t).getResultList();
        printCollectionOfTuples(empTuples, "\t", "\n");
        
         // Mesma query com multiselect
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        t = cb.createTupleQuery();
        emp = t.from(Employee.class);
        t.distinct(true).multiselect(
                emp.get(Employee_.id).alias("id"),
                emp.get(Employee_.employeeName)
                    .get(EmployeeName_.firstName).alias("name")
        ).groupBy(emp.get(Employee_.id));
        empTuples = em.createQuery(t).getResultList();
        printCollectionOfTuples(empTuples, "\t", "\n");
        
        List<Object[]> empObjects;
        
        // Mesma query com multiselect para Object[]
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        CriteriaQuery<Object[]> o = cb.createQuery(Object[].class);
        emp = o.from(Employee.class);
        o.distinct(true).multiselect(
                emp.get(Employee_.id).alias("id"),
                emp.get(Employee_.employeeName)
                    .get(EmployeeName_.firstName).alias("name")
        ).groupBy(emp.get(Employee_.id));
        empObjects = em.createQuery(o).getResultList();
        printCollectionOfObjects(empObjects, "\t", "\n");
        
        List<EmpDept> empDepts;
        
        // Query para expressao por construtor
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        CriteriaQuery<EmpDept> e = cb.createQuery(EmpDept.class);
        emp = e.from(Employee.class);
        Join<Employee, Department> dep = emp.join(Employee_.departments);
        e.select(cb.construct(EmpDept.class, 
                emp.get(Employee_.employeeName).get(EmployeeName_.lastName), 
                dep.get(Department_.name)))
        .groupBy(emp.get(Employee_.id))
        .orderBy(cb.asc(
                emp.get(Employee_.employeeName).get(EmployeeName_.lastName)));
        empDepts = em.createQuery(e).getResultList();
        print(empDepts, "\t", "\n");
        
        // Mesma query com multiselect
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        e = cb.createQuery(EmpDept.class);
        emp = e.from(Employee.class);
        dep = emp.join(Employee_.departments);
        e.multiselect( 
                emp.get(Employee_.employeeName).get(EmployeeName_.lastName), 
                dep.get(Department_.name))
        .groupBy(emp.get(Employee_.id))
        .orderBy(cb.asc(
                emp.get(Employee_.employeeName).get(EmployeeName_.lastName)));
        empDepts = em.createQuery(e).getResultList();
        print(empDepts, "\t", "\n");
        
        // Query para mapas
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        t = cb.createTupleQuery();
        emp = t.from(Employee.class);
        MapJoin<Employee, PhoneType, String> phones = 
                emp.join(Employee_.phoneNumber);
                //emp.joinMap("phoneNumber"); // API não tipada -> joinMap
        t.multiselect(
                emp.get(Employee_.id).alias("id"),
                emp.get(Employee_.employeeName).get(EmployeeName_.firstName)
                    .alias("nome"),
                emp.get(Employee_.employeeName).get(EmployeeName_.lastName)
                    .alias("sobrenome"),
                phones.key().alias("tipo"),
                phones.value().alias("numero")
        ).groupBy(emp.get(Employee_.id), phones.key());        
        empTuples = em.createQuery(t).getResultList();
        printCollectionOfTuples(empTuples, "\t", "\n");
        
        // Join Fetch
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        c = cb.createQuery(Employee.class);
        emp = c.from(Employee.class);
        Fetch<Employee, String> fetchPhones = 
                emp.fetch(Employee_.phoneNumber, JoinType.LEFT);
        c.select(emp)
                .distinct(true)
                .orderBy(cb.asc(emp.get(Employee_.id)));       
        employees = em.createQuery(c).getResultList();
        print(employees, "\t", "\n");
          
        System.out.println("\n-------------\n");
    }
    
    public static List<Employee> findEmployees(EntityManager em, String name, String deptName,
            String projectName, String city) {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Employee> c = cb.createQuery(Employee.class);
        Root<Employee> emp = c.from(Employee.class);

        c.select(emp);
        c.distinct(true);
        
        Join<Employee, Project> project = emp.join(
                Employee_.projects, JoinType.LEFT);
        Join<Employee, Department> department = emp.join(
                Employee_.departments, JoinType.LEFT);
        
        List<Predicate> criteria = new ArrayList<>();
        
        if (name != null) {
            ParameterExpression<String> p = cb.parameter(String.class, "name");
            criteria.add(cb.equal(emp.get(Employee_.employeeName)
                    .get(EmployeeName_.firstName), p));
        }     
        if (deptName != null) {
            ParameterExpression<String> p = cb.parameter(String.class, "dept");
            criteria.add(cb.equal(department.get(Department_.name), p));
        }
        if (projectName != null) {
            ParameterExpression<String> p = cb.parameter(String.class, 
                    "project");
            criteria.add(cb.equal(project.get(Project_.name), p));
        }     
        if (city != null) {
            ParameterExpression<String> p = cb.parameter(String.class, "city");
            criteria.add(cb.equal(emp.get(Employee_.address).get(Address_.city), 
                    p));
        }
        
        switch(criteria.size()) {
            case 0: throw new RuntimeException("no criteria"); 
            case 1: c.where(criteria.get(0)); break;
            default: c.where(criteria.toArray(new Predicate[0]));    
        }
        
        TypedQuery<Employee> q = em.createQuery(c);
        if (name != null) { q.setParameter("name", name);}
        if (deptName != null) { q.setParameter("dept", deptName);}
        if (projectName != null) { q.setParameter("project", projectName);}
        if (city != null) { q.setParameter("city", city);}

        return q.getResultList();
    }
    
    public static void print(Collection<?> col, String prefix, String suffix) {
        for (Object elem : col) {
            System.out.printf("%s%s%s", prefix, elem.toString(), suffix);
        }
    }
    
    public static void printCollectionOfObjects(Collection<Object[]> col, 
            String prefix, String suffix) {
        for (Object[] elem : col) {
            System.out.printf("%s%s%s", prefix, Arrays.toString(elem), suffix);
        }
    }
    
    public static void printCollectionOfTuples(Collection<Tuple> col, 
            String prefix, String suffix) {
        for (Tuple tuple : col) {
            System.out.print(prefix + "<");
            final Iterator<TupleElement<?>> it = tuple.getElements().iterator();
            while (it.hasNext()) {
                TupleElement<? extends Object> tupleElement = it.next();
                System.out.printf("%s = %s", tupleElement.getAlias(), 
                        tuple.get(tupleElement));
                if (it.hasNext()) {
                    System.out.print(", ");
                }
            }
            System.out.print(">" + suffix);          
        }
    }

}
