package com.sevenrtc.jpa2employee;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Anthony Accioly <a.accioly at 7rtc.com>
 */
@Entity
public class PrintJob implements Serializable {
    private static final long serialVersionUID = 1L;

    public PrintJob() {
    }

    public PrintJob(int id) {
        this.id = id;
    }

    @Id
    @Column(name="JOB_ID")
    private int id;
    
    @ManyToOne
    private PrintQueue queue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PrintQueue getQueue() {
        return queue;
    }

    public void setQueue(PrintQueue queue) {
        this.queue = queue;
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
        if (!(object instanceof PrintJob)) {
            return false;
        }
        PrintJob other = (PrintJob) object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.sevenrtc.jpa2employee.PrintJob[ id=" + id + " ]";
    }
    
}
