/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sevenrtc.jpa2employee.dto;

import java.util.Objects;


/**
 *
 * @author anthony
 */
public class EmpDept {
    
    private String employeeName;
    private String departmentName;

    public EmpDept(String employeeName, String departmentName) {
        this.employeeName = employeeName;
        this.departmentName = departmentName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EmpDept other = (EmpDept) obj;
        if (!Objects.equals(this.employeeName, other.employeeName)) {
            return false;
        }
        if (!Objects.equals(this.departmentName, other.departmentName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.employeeName);
        hash = 89 * hash + Objects.hashCode(this.departmentName);
        return hash;
    }

    @Override
    public String toString() {
        return "EmpDept{" + "employeeName=" + employeeName + ", departmentName=" + departmentName + '}';
    }

}
