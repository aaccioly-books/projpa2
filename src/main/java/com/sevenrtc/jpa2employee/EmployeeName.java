package com.sevenrtc.jpa2employee;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Anthony Accioly <a.accioly at 7rtc.com>
 */
@Embeddable
public class EmployeeName implements Serializable {

    @Column(name = "F_NAME")
    private String firstName;
    @Column(name = "L_NAME")
    private String lastName;

    public EmployeeName() {
    }

    public EmployeeName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EmployeeName other = (EmployeeName) obj;

        return Objects.equals(this.firstName, other.firstName) && Objects.equals(this.lastName, other.lastName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 53 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "EmployeeName{" + "firstName=" + firstName + ", lastName=" + lastName + '}';
    }

}
