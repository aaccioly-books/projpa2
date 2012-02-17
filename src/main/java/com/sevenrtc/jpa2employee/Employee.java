package com.sevenrtc.jpa2employee;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import static com.sevenrtc.jpa2employee.util.FormattingUtils.formatarData;
import java.util.*;

/**
 *
 * @author Anthony
 */
@Entity
@Access(AccessType.FIELD)
//@SequenceGenerator(name = "EMP_SEQ", sequenceName = "EMP_SEQ")
@Table(name = "EMP", schema = "HR")
public class Employee implements Serializable {

    public static final String LOCAL_AREA_CODE = "613";
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
    @Transient
    private String phoneNum;
    
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

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="state", column=@Column(name="PROVINCE")),
        @AttributeOverride(name="zip", column=@Column(name="POSTAL_CODE"))
    })
    private Address address;

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
    
    @ElementCollection
    @CollectionTable(name="EMP_PHONE")
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name="PHONE_TYPE")
    @Column(name="PHONE_NUM")
    private Map<PhoneType, String> phoneNumber;
    
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

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Access(AccessType.PROPERTY)
    @Column(name = "PHONE")
    protected String getPhoneNumberForDb() {
        if (phoneNum.length() == 10) {
            return phoneNum;
        } else {
            return LOCAL_AREA_CODE + phoneNum;
        }
    }

    protected void setPhoneNumberForDb(String num) {
        if (num.startsWith(LOCAL_AREA_CODE)) {
            phoneNum = num.substring(3);
        } else {
            phoneNum = num;
        }
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public Map<PhoneType, String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Map<PhoneType, String> phoneNumber) {
        this.phoneNumber = phoneNumber;
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
        return "Employee{" + "id=" + id + ", employeeName=" + employeeName + ", salary=" + salary + ", comments=" + comments + ", employeeType=" + employeeType + ", dob=" + formatarData(dob) + ", startDate=" + formatarData(startDate) + ", phoneNum=" + phoneNum + ", departments=" + departments + ", parkingSpace=" + parkingSpace + ", projects=" + projects + ", address=" + address + ", vacationBookings=" + vacationBookings + ", nickNames=" + nickNames + ", phoneNumber=" + phoneNumber + '}';
    }

}
