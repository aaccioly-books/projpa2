package com.sevenrtc.jpa2employee;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
/**
 *
 * @author Anthony
 */
@Embeddable
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String street;
    private String city;
    private String state;
    @Column(name="ZIP_CODE")
    private String zip;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public String toString() {
        return "Address{" + "street=" + street + ", city=" + city + ", state=" + state + ", zip=" + zip + '}';
    }

}
