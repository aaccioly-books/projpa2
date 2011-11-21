package com.sevenrtc.jpa2employee;

import java.io.Serializable;
import java.util.Map;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyJoinColumn;

/**
 *
 * @author Anthony
 */
@Entity
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String name;
    
    @ManyToMany
    @JoinTable(name="DEPT_EMP",
            joinColumns=@JoinColumn(name="DEPT_ID"),
            inverseJoinColumns=@JoinColumn(name="EMP_ID"))
    @AttributeOverrides({
        @AttributeOverride(name = "firstName", column =
        @Column(name = "EMP_FNAME")),
        @AttributeOverride(name = "lastName", column =
        @Column(name = "EMP_LNAME"))
    })
    private Map<EmployeeName, Employee> employees;
    
    @ElementCollection
    @CollectionTable(name="EMP_SENIORITY", 
            joinColumns=@JoinColumn(name="DEPT_ID"))
    @MapKeyJoinColumn(name="EMP_ID")
    @Column(name="SENIORITY")
    private Map<Employee, Integer> seniorities;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<EmployeeName, Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<EmployeeName, Employee> employees) {
        this.employees = employees;
    }

    public Map<Employee, Integer> getSeniorities() {
        return seniorities;
    }

    public void setSeniorities(Map<Employee, Integer> seniorities) {
        this.seniorities = seniorities;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Department)) {
            return false;
        }
        Department other = (Department) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jpa2employee.Department[ id=" + id + " ]";
    }
}
