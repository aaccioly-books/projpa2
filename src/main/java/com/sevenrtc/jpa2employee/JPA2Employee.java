package com.sevenrtc.jpa2employee;

import com.google.common.io.ByteStreams;
import com.sevenrtc.jpa2employee.dto.EmpDept;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

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
        employee1.setEmployeeName(new EmployeeName("Anthony", "Accioly"));
        employee1.setSalary(1l);
        employee1.setComments("He is ok!");

       employee1.setPicture(
               ByteStreams.toByteArray(JPA2Employee.class.getResourceAsStream("/images/Manager-Cropped.jpg")));

        employee1.setEmployeeType(EmployeeType.CONTRACT_EMPLOYEE);
        Calendar dob = Calendar.getInstance();
        dob.set(1987, Calendar.JULY, 2);
        employee1.setDob(dob);
        Calendar startDate = Calendar.getInstance();
        startDate.set(2010, Calendar.SEPTEMBER, 1);
        employee1.setStartDate(startDate.getTime());
        
        employee1.setDepartments(departments);
        
        em.persist(employee1);

        Employee employee2 = new Employee();
        employee2.setEmployeeName(new EmployeeName("Someone", "Else"));
        employee2.setDepartments(departments);

        em.persist(employee2);
        
        Address address = new Address();
        address.setStreet("Rua Angélica");
        address.setCity("São Paulo");
        address.setState("São Paulo");
        address.setZip("01222-010");
        
        {
            Phone primaryPhoneCfEmp1 = new Phone();
            primaryPhoneCfEmp1.setPhoneNum("99991234");
            employee1.setPrimaryPhone(primaryPhoneCfEmp1);

            Phone secondaryPhoneCfEmp1 = new Phone();
            secondaryPhoneCfEmp1.setPhoneNum("021322233333");

            EnumMap<PhoneType, Phone> phones = new EnumMap<>(PhoneType.class);
            phones.put(PhoneType.MOBILE, primaryPhoneCfEmp1);
            phones.put(PhoneType.HOME, secondaryPhoneCfEmp1);
            employee1.setPhones(phones);

            employee1.setResidence(address);

            em.merge(employee1);
        }
        
        Employee employee3 = new Employee();
        employee3.setEmployeeName(new EmployeeName("Flavio", "Antunes"));
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
        criteriaSubqueries(em);
        
        for (Employee employee : employees) {
            if (employee.getEmployeeName().equals(new EmployeeName("Anthony", "Accioly"))) {
                final Path toWrite = Paths.get(System.getProperty("user.home"),
                        "Desktop", "teste.jpg");
                Files.write(toWrite, employee.getPicture());
            }
        }
        
        // Metamodel
        
        printMetadata(Employee.class, em);
        

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
        List<Tuple> tuples;
        
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
        tuples = em.createQuery(t).getResultList();
        printCollectionOfTuples(tuples, "\t", "\n");
        
         // Mesma query com multiselect
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        t = cb.createTupleQuery();
        emp = t.from(Employee.class);
        t.distinct(true).multiselect(
                emp.get(Employee_.id).alias("id"),
                emp.get(Employee_.employeeName)
                    .get(EmployeeName_.firstName).alias("name")
        ).groupBy(emp.get(Employee_.id));
        tuples = em.createQuery(t).getResultList();
        printCollectionOfTuples(tuples, "\t", "\n");
        
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
        // Ordenação
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
        MapJoin<Employee, PhoneType, Phone> phones = emp
                .join(Employee_.phones);
        t.multiselect(
                emp.get(Employee_.id).alias("id"),
                emp.get(Employee_.employeeName).get(EmployeeName_.firstName)
                    .alias("nome"),
                emp.get(Employee_.employeeName).get(EmployeeName_.lastName)
                    .alias("sobrenome"),
                phones.key().alias("tipo"),
                phones.value().get(Phone_.phoneNumberForDb).alias("numero")
        ).groupBy(emp.get(Employee_.id), phones.key());        
        tuples = em.createQuery(t).getResultList();
        printCollectionOfTuples(tuples, "\t", "\n");
        
        // Query para mapas com EntityType
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        t = cb.createTupleQuery();
        emp = t.from(Employee.class);
        EntityType<Employee> emp_ = emp.getModel();
        EmbeddableType<EmployeeName> empName_ = em.getMetamodel()
                .embeddable(EmployeeName.class);
        phones = emp.join(emp_.getMap("phones", PhoneType.class, Phone.class));
        EmbeddableType<Phone> phone_ = em.getMetamodel().embeddable(Phone.class);
                //emp.join("contactInfo").joinMap("phoneNumber"); // API não tipada -> joinMap
        t.multiselect(
                emp.get(emp_.getSingularAttribute("id", Integer.class)).alias("id"),
                emp.get(emp_.getSingularAttribute("employeeName", EmployeeName.class))
                    .get(empName_.getSingularAttribute("firstName", String.class))
                    .alias("nome"),
                emp.get(emp_.getSingularAttribute("employeeName", EmployeeName.class))
                    .get(empName_.getSingularAttribute("lastName", String.class))
                    .alias("sobrenome"),
                phones.key().alias("tipo"),
                phones.value().get(phone_
                .getSingularAttribute("phoneNumberForDb", String.class)).alias("numero")
        ).groupBy(emp.get(emp_.getSingularAttribute("id", Integer.class)), phones.key());        
        tuples = em.createQuery(t).getResultList();
        printCollectionOfTuples(tuples, "\t", "\n");
        
        // Join Fetch
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        c = cb.createQuery(Employee.class);
        emp = c.from(Employee.class);
        Fetch<Employee, Phone> fetchPhones = 
                emp.fetch(Employee_.phones, JoinType.LEFT);
        c.select(emp)
                .distinct(true)
                .orderBy(cb.asc(emp.get(Employee_.id)));       
        employees = em.createQuery(c).getResultList();
        print(employees, "\t", "\n");
        
        // Case Expressions
        
        /*
         * Completa. Equivalente a consulta: 
         * 
         *     SELECT p.name,
         *       CASE 
         *            WHEN TYPE(p) = DesignProject THEN 'Development'
         *            WHEN TYPE(p) = QualityProject THEN 'QA'
         *            ELSE 'Non-Development'
         *        END
         *       FROM Project p
         *      WHERE p.employees IS NOT EMPTY
         */
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        t = cb.createTupleQuery();
        Root<Project> p = t.from(Project.class);
        t.multiselect(
                p.get(Project_.name).alias("project"),
                cb.selectCase()
                    .when(cb.equal(p.type(), DesignProject.class), "Development")
                    .when(cb.equal(p.type(), QualityProject.class), "QA")
                    .otherwise("Non-Development")
                    .alias("type")
        ).where(cb.isNotEmpty(p.get(Project_.employees)));
        tuples = em.createQuery(t).getResultList();
        printCollectionOfTuples(tuples, "\t", "\n");
        
        /*
         * Simples. Equivalente a consulta: 
         * 
         *     SELECT p.name,
         *       CASE TYPE(p) 
         *            WHEN DesignProject THEN 'Development'
         *            WHEN QualityProject THEN 'QA'
         *            ELSE 'Non-Development'
         *        END
         *       FROM Project p
         *      WHERE p.employees IS NOT EMPTY
         */
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        t = cb.createTupleQuery();
        p = t.from(Project.class);
        t.multiselect(
                p.get(Project_.name).alias("project"),
                cb.selectCase(p.type().as(String.class))
                    .when("DesignProject", "Development")
                    .when("QualityProject", "QA")
                    .otherwise("Non-Development")
                    .alias("type")
        ).where(cb.isNotEmpty(p.get(Project_.employees)));
        tuples = em.createQuery(t).getResultList();
        printCollectionOfTuples(tuples, "\t", "\n");
        
        // Functions

        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        t = cb.createTupleQuery();
        emp = t.from(Employee.class);
        t.multiselect(
                emp.get(Employee_.id).alias("id"),
                // Funcao sem argumentos
                cb.function("DATABASE_PATH", String.class).alias(
                    "DATABASE_PATH"),
                // Argumento para parametro externo
                cb.function("SOUNDEX", String.class, cb.parameter(String.class, 
                    "soundex")).alias("SOUNDEX"),
                // Argumento a partir da query
                cb.function("TAN", Double.class, emp.get(Employee_.id)).alias(
                    "TAN"),
                // Funcao matematica entre duas funcoes
                cb.quot(
                    cb.function("SIN", Double.class, emp.get(Employee_.id)), 
                    cb.function("COS", Double.class, emp.get(Employee_.id))
                ).alias("SIN/COS"),
                // Funcao chamando funcoes
                cb.function("POWER", Double.class, 
                    cb.function("SIN", Double.class, emp.get(Employee_.id)), 
                    cb.function("COS", Double.class, emp.get(Employee_.id))).
                    alias("POWER(SIN/COS)")
        );
        TypedQuery<Tuple> tupleQ = em.createQuery(t);
        tupleQ.setParameter("soundex", "abacate");
        tuples = tupleQ.getResultList();
        printCollectionOfTuples(tuples, "\t", "\n");
        
        // GROUP BY, HAVING e ORDER BY
        
        /*
         * Equivalente a consulta:
         * 
         *    SELECT proj.id, proj.name, COUNT(emp) 
         *      FROM Project proj JOIN proj.employees emp 
         *  GROUP BY proj 
         *    HAVING COUNT(emp) >= 2 
         *  ORDER BY proj.name
         */
        System.out.printf("Criteria %d: \n", ++criteriaCounter);
        t = cb.createTupleQuery();
        p = t.from(Project.class);
        Join<Project, Employee> projEmps = p.join(Project_.employees);
        t.multiselect(
                p.get(Project_.id).alias("id"),
                p.get(Project_.name).alias("name"),
                cb.count(projEmps).alias("employees")
        ).groupBy(p)
         .having(cb.ge(
                cb.count(projEmps),
                cb.parameter(Long.class, "nEmployees")))
         .orderBy(cb.asc(p.get(Project_.name))); 
        tupleQ = em.createQuery(t);
        tupleQ.setParameter("nEmployees", 2);
        tuples = tupleQ.getResultList();
        printCollectionOfTuples(tuples, "\t", "\n");

        System.out.println("\n-------------\n");
    }
    
    public static void criteriaSubqueries(EntityManager em) {
        
        int subqueryCounter  = 0;
        
        System.out.println("\n-------------\n*Criteria API Subqueries*\n-------------\n");
        
        // Nao correlacionada:
        
        /*
         * Equivalente a consulta: 
         * 
         *    SELECT e 
         *    FROM Employee e 
         *    WHERE e.id IN (SELECT emp.id 
         *                     FROM Project p JOIN p.employees emp 
         *                    WHERE p.name = :projectName)
         */
        {
            System.out.printf("Subquery %d: \n", ++subqueryCounter);

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> c = cb.createQuery(Employee.class);
            Root<Employee> emp = c.from(Employee.class);

            // Cria subquery nao relacionada
            Subquery<Integer> sq = c.subquery(Integer.class);
            // Root da subquery
            Root<Project> project = sq.from(Project.class);
            // Join na subquery
            Join<Project, Employee> sqEmp =
                    project.join(Project_.employees);
            // Where da subquery
            sq.select(sqEmp.get(Employee_.id)).where(
                    cb.equal(project.get(Project_.name),
                    cb.parameter(String.class, "projectName")));
            // Where com in na query 
            c.select(emp).where(
                    cb.in(emp.get(Employee_.id)).value(sq));

            TypedQuery<Employee> q = em.createQuery(c);
            q.setParameter("projectName", "Telefonica");
            List<Employee> employeess = q.getResultList();
            print(employeess, "\t", "\n");
        }
        
        // Correlacionada:
        
        /*
         * Equivalente a consulta:
         *
         *    SELECT e 
         *    FROM Employee e 
         *    WHERE EXISTS (SELECT p 
         *                    FROM Project p JOIN p.employees emp 
         *                   WHERE emp = e AND
         *                      p.name = :name)
         */
        {
            System.out.printf("Subquery %d: \n", ++subqueryCounter);

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> c = cb.createQuery(Employee.class);
            Root<Employee> emp = c.from(Employee.class);


            Subquery<Project> sq = c.subquery(Project.class);
            Root<Project> project = sq.from(Project.class);
            Join<Project, Employee> sqEmp =
                    project.join(Project_.employees);

            sq.select(project).where(
                    // correlaciona project.employees com a root
                    cb.equal(sqEmp, emp), 
                    cb.equal(project.get(Project_.name),
                    cb.parameter(String.class, "projectName")));
            // Where com exists na query 
            c.select(emp).where(cb.exists(sq));

            TypedQuery<Employee> q = em.createQuery(c);
            q.setParameter("projectName", "Telefonica");
            List<Employee> employeess = q.getResultList();
            print(employeess, "\t", "\n");
        }
        
        /*
         * Correlacionada + referencia para a entidade raiz da outer query na
         * subquery
         */
        
        /*
         * Equivalente a consulta:
         *
         *    SELECT e 
         *    FROM Employee e 
         *    WHERE EXISTS (SELECT p 
         *                    FROM e.projects p 
         *                   WHERE p.name = :name)
         */
        {
            System.out.printf("Subquery %d: \n", ++subqueryCounter);

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> c = cb.createQuery(Employee.class);
            Root<Employee> emp = c.from(Employee.class);


            Subquery<Project> sq = c.subquery(Project.class);
            // Equivalente a (SELECT p FROM e.projects p)
            Root<Employee> sqEmp = sq.correlate(emp);
            Join<Employee, Project> project =
                    sqEmp.join(Employee_.projects);

            sq.select(project).where(
                    cb.equal(project.get(Project_.name),
                    cb.parameter(String.class, "projectName")));
            // Where com exists na query 
            c.select(emp).where(cb.exists(sq));

            TypedQuery<Employee> q = em.createQuery(c);
            q.setParameter("projectName", "Telefonica");
            List<Employee> employeess = q.getResultList();
            print(employeess, "\t", "\n");
        }
        
        // Correlacionada + join da outer query referenciado na subquery
        
        /*
         * Equivalente a consulta:
         *
         *     SELECT p FROM 
         *     Project p JOIN p.employees e 
         *     WHERE TYPE(p) = DesignProject AND 
         *           e.directs IS NOT EMPTY AND
         *           (SELECT AVG(d.salary)
         *            FROM e.directs d) >= :value   
         */
        {
            System.out.printf("Subquery %d: \n", ++subqueryCounter);

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Project> c = cb.createQuery(Project.class);
            Root<Project> project = c.from(Project.class);
            Join<Project, Employee> emp = project.join(Project_.employees);

            Subquery<Double> sq = c.subquery(Double.class);
            // Equivalente a (FROM e.directs)
            Join<Project, Employee> sqEmp = sq.correlate(emp);
            Join<Employee, Employee> directs  = sqEmp.join(Employee_.directs);
            // SELECT AVG(d.salary)
            sq.select(cb.avg(directs.get(Employee_.salary)));

            c.select(project).where(
                    // TYPE(p) = DesignProject
                    cb.equal(project.type(), DesignProject.class),
                    // e.directs IS NOT EMPTY AND
                    cb.isNotEmpty(emp.get(Employee_.directs)),
                    // AVG(d.salary) >= :value
                    cb.ge(sq, cb.parameter(Double.class, "value")));

            TypedQuery<Project> q = em.createQuery(c);
            q.setParameter("value", 1.0);
            List<Project> projects = q.getResultList();
            print(projects, "\t", "\n");
        }
        
        // Não correlacionada + like + upper
        
        /*
         * Equivalente a consulta:
         *
         *     SELECT e 
         *     FROM Employee e JOIN e.departments ed
         *     WHERE ed.id IN
         *     (
         *       SELECT DISTINCT d.id
         *       FROM Department d JOIN d.employees de
         *       JOIN de.projects p
         *       WHERE UPPER(p.name) LIKE UPPER(:projectName)
         *     )        
         */
        {
            System.out.printf("Subquery %d: \n", ++subqueryCounter);

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Employee> c = cb.createQuery(Employee.class);
            Root<Employee> emp = c.from(Employee.class);
            Join<Employee, Department> empDet = emp.join(Employee_.departments);

            Subquery<Integer> sq = c.subquery(Integer.class);
            Root<Department> dept = sq.from(Department.class);
            // Department d JOIN d.employees de JOIN de.projects p
            Join<Employee, Project> project = dept
                    .join(Department_.employees)
                    .join(Employee_.projects);
            
            sq.select(dept.get(Department_.id))
                    .distinct(true)
                    .where(cb.like(
                    cb.upper(project.get(Project_.name)), 
                    cb.upper(cb.parameter(String.class, "projectName"))));

            c.select(emp).where(
                    cb.in(empDet.get(Department_.id)).value(sq));
            
            TypedQuery<Employee> q = em.createQuery(c);
            q.setParameter("projectName", "tel%");
            List<Employee> projects = q.getResultList();
            print(projects, "\t", "\n");
        }
        
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
        
        Predicate criteria = cb.conjunction();
        int criteriaCount = 0;
        
        if (name != null) {
            criteria = cb.and(criteria, cb.equal(emp.get(Employee_.employeeName)
                    .get(EmployeeName_.firstName),
                    cb.parameter(String.class, "name")));
            criteriaCount++;
        }     
        if (deptName != null) {
            criteria = cb.and(criteria, 
                    cb.equal(department.get(Department_.name), 
                    cb.parameter(String.class, "dept")));
            criteriaCount++;
        }
        if (projectName != null) {
            criteria = cb.and(criteria, 
                    cb.equal(project.get(Project_.name), 
                    cb.parameter(String.class, "project")));
            criteriaCount++;
        }     
        if (city != null) {
            criteria = cb.and(criteria,
                    cb.equal(emp.get(Employee_.residence).get(Address_.city),
                    cb.parameter(String.class, "city")));
            criteriaCount++;
        }
        
        if (criteriaCount == 0) {
            throw new RuntimeException("no criteria");
        }
        
        c.where(criteria);
        
        TypedQuery<Employee> q = em.createQuery(c);
        if (name != null) { q.setParameter("name", name);}
        if (deptName != null) { q.setParameter("dept", deptName);}
        if (projectName != null) { q.setParameter("project", projectName);}
        if (city != null) { q.setParameter("city", city);}

        return q.getResultList();
    }
    
    public static <T> void printMetadata(Class<T> entityClass, EntityManager em) {
        final Metamodel mm = em.getMetamodel();
        EntityType<T> entityType = mm.entity(entityClass);
        System.out.println(entityType.getName());
        System.out.println("-------------");
        for (Attribute<? super T, ?> attribute : entityType.getAttributes()) {
            System.out.printf("\t%s %s %s\n", attribute.getName(),
                    getJavaType(attribute),
                    attribute.getPersistentAttributeType());
        }
        System.out.println("-------------");
    }
    
    public static String getJavaType(Attribute<?, ?> attribute) {
        StringBuilder sb = new StringBuilder();
        if (attribute.getJavaType().isArray()) {
            sb.append(attribute.getJavaType().getComponentType().getName());
            sb.append("[]");
        } else {
           sb.append(attribute.getJavaType().getName()); 
        }
        // Print Generic arguments
        if (attribute.getJavaType().getTypeParameters().length > 0) {
            Type type = ((Field) attribute.getJavaMember()).getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType pm = (ParameterizedType) type;
                sb.append('<');
                Type[] actualTypeArguments = pm.getActualTypeArguments();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    Type argumentType = actualTypeArguments[i];
                    sb.append(argumentType.toString().substring(6));
                    if (i < actualTypeArguments.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append('>');
            }
        }
        
        return sb.toString();
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
                TupleElement<?> tupleElement = it.next();
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
