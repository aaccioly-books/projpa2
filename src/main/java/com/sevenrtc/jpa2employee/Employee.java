package com.sevenrtc.jpa2employee;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import static com.sevenrtc.jpa2employee.util.FormattingUtils.formatarData;
import java.util.*;
import javax.persistence.AttributeOverrides;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToMany;

/**
 *
 * @author Anthony
 */
@Entity
@Access(AccessType.FIELD)
//@SequenceGenerator(name = "EMP_SEQ", sequenceName = "EMP_SEQ")
@Table(name = "EMP", schema = "HR")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @GeneratedValue(/*generator = "EMP_SEQ",*/ strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "EMP_ID")
    private int id;
    
    @Embedded
    private EmployeeName employeeName;
    
    @Column(name = "SAL")
    private long salary;
    @Column(name = "COMM")
    private String comments;
    
    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(name = "PIC")
    private byte[] picture;  
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private EmployeeType employeeType;
    @Temporal(TemporalType.DATE)
    private Calendar dob;
    @Temporal(TemporalType.DATE)
    @Column(name = "S_DATE")
    private Date startDate;
    
    @ManyToOne
    @JoinColumn(name = "MANAGER_ID")
    private Employee manager;
    
    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private Collection<Employee> directs; 
    
    @ManyToMany(mappedBy="employees")
    private List<Department> departments;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PSPACE_ID")
    private ParkingSpace parkingSpace;
    
    @ManyToMany
    @JoinTable(name = "EMP_PROJ",
            joinColumns = @JoinColumn(name = "EMP_ID"),
            inverseJoinColumns = @JoinColumn(name = "PROJ_ID"))
    private Collection<Project> projects;

    @ElementCollection
    @CollectionTable(
            name="VACATION", 
            joinColumns=@JoinColumn(name="EMP_ID"))
    @AttributeOverride(name="daysTaken",
            column=@Column(name="DAYS_ABS"))
    private Collection<VacationEntry> vacationBookings;
    
    @ElementCollection
    @CollectionTable(
            name="NICKNAME",
            joinColumns=@JoinColumn(name="EMP_ID"))
    @Column(name="NICKNAME")
    private Collection<String> nickNames;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="state", column=@Column(name="PROVINCE")),
        @AttributeOverride(name="zip", column=@Column(name="POSTAL_CODE"))
    })
    private Address residence;
    
    @Embedded
    @AttributeOverride(name="phoneNumberForDb", column=@Column(name="MAIN_PHONE"))
    private Phone primaryPhone;
    
    @ElementCollection
    @CollectionTable(name="EMP_PHONES", 
            joinColumns=@JoinColumn(name="EMP_ID"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name="PHONE_TYPE")
    private Map<PhoneType, Phone> phones; 
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public EmployeeName getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(EmployeeName employeeName) {
        this.employeeName = employeeName;
    }

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public Calendar getDob() {
        return dob;
    }

    public void setDob(Calendar dob) {
        this.dob = dob;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Collection<Employee> getDirects() {
        return directs;
    }

    public void setDirects(Collection<Employee> directs) {
        this.directs = directs;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public ParkingSpace getParkingSpace() {
        return parkingSpace;
    }

    public void setParkingSpace(ParkingSpace parkingSpace) {
        this.parkingSpace = parkingSpace;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    public Collection<String> getNickNames() {
        return nickNames;
    }

    public void setNickNames(Collection<String> nickNames) {
        this.nickNames = nickNames;
    }

    public Collection<VacationEntry> getVacationBookings() {
        return vacationBookings;
    }

    public void setVacationBookings(Collection<VacationEntry> vacationBookings) {
        this.vacationBookings = vacationBookings;
    }

    public Address getResidence() {
        return residence;
    }

    public void setResidence(Address residence) {
        this.residence = residence;
    }

    public Phone getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(Phone primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public Map<PhoneType, Phone> getPhones() {
        return phones;
    }

    public void setPhones(Map<PhoneType, Phone> phones) {
        this.phones = phones;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += Integer.valueOf(id).hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if (!(this.id == other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Employee{" + "id=" + id + ", employeeName=" + employeeName + ", salary=" + salary + ", comments=" + comments + ", employeeType=" + employeeType + ", dob=" + formatarData(dob) + ", startDate=" + formatarData(startDate) +  ", departments=" + departments + ", parkingSpace=" + parkingSpace + ", projects=" + projects + ", vacationBookings=" + vacationBookings + ", nickNames=" + nickNames + "residence=" + residence + ", primaryPhone=" + primaryPhone + ", phones=" + phones + '}';
    }

}
