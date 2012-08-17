package com.sevenrtc.jpa2employee;

import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @author Anthony
 */
@Embeddable
@Access(AccessType.FIELD)
public class Phone implements Serializable {
    
    private static final long serialVersionUID = 1L;
    public static final String LOCAL_AREA_CODE = "011";
    
    @Transient
    private String phoneNum;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Access(AccessType.PROPERTY)
    @Column(name = "PHONE")
    protected String getPhoneNumberForDb() {
        if (phoneNum.length() > 10) {
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

    @Override
    public String toString() {
        return "Phone{phoneNum=" + phoneNum + '}';
    }
    
}
