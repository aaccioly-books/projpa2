package com.sevenrtc.jpa2employee;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Anthony Accioly <a.accioly at 7rtc.com>
 */
@Entity
public class PrintQueue implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String name;
    
    @OneToMany(mappedBy="queue", cascade= CascadeType.ALL)
    @OrderColumn(name="PRINT_ORDER")
    private List<PrintJob> jobs; 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PrintJob> getJobs() {
        return jobs;
    }

    public void setJobs(List<PrintJob> jobs) {
        this.jobs = jobs;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PrintQueue)) {
            return false;
        }
        PrintQueue other = (PrintQueue) object;
        return !((this.name == null && other.name != null) || (this.name != null && !this.name.equals(other.name)));
    }

    @Override
    public String toString() {
        return "PrintQueue{" + "name=" + name + ", jobs=" + jobs + '}';
    }
    
}
