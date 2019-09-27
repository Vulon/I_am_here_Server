package com.I_am_here.Database.Entity;


import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @Column(name = "subject_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer subject_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "plan")
    private Integer plan;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "start_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date start_date;

    @Column(name = "finish_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date finish_date;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @ManyToMany
    @JoinTable(name = "host_subject", joinColumns = @JoinColumn(name = "subject_id"),
    inverseJoinColumns = @JoinColumn(name = "host_id"))
    private Set<Host> hosts;

    @ManyToMany
    @JoinTable(name = "group_subject", joinColumns = @JoinColumn(name = "subject_id"),
    inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups;

    public Subject() {
    }

    public Subject(String name, Integer plan, String description, Date start_date, Date finish_date, Manager manager, Set<Host> hosts, Set<Group> groups) {
        this.name = name;
        this.plan = plan;
        this.description = description;
        this.start_date = start_date;
        this.finish_date = finish_date;
        this.manager = manager;
        this.hosts = hosts;
        this.groups = groups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        if (!subject_id.equals(subject.subject_id)) return false;
        if (name != null ? !name.equals(subject.name) : subject.name != null) return false;
        if (plan != null ? !plan.equals(subject.plan) : subject.plan != null) return false;
        if (description != null ? !description.equals(subject.description) : subject.description != null) return false;
        if (start_date != null ? !start_date.equals(subject.start_date) : subject.start_date != null) return false;
        if (finish_date != null ? !finish_date.equals(subject.finish_date) : subject.finish_date != null) return false;
        if (manager != null ? !manager.equals(subject.manager) : subject.manager != null) return false;
        if (hosts != null ? !hosts.equals(subject.hosts) : subject.hosts != null) return false;
        return groups != null ? groups.equals(subject.groups) : subject.groups == null;

    }

    @Override
    public int hashCode() {
        int result = subject_id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (plan != null ? plan.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (start_date != null ? start_date.hashCode() : 0);
        result = 31 * result + (finish_date != null ? finish_date.hashCode() : 0);
        result = 31 * result + (manager != null ? manager.hashCode() : 0);
        result = 31 * result + (hosts != null ? hosts.hashCode() : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        return result;
    }

    public Integer getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Integer subject_id) {
        this.subject_id = subject_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPlan() {
        return plan;
    }

    public void setPlan(Integer plan) {
        this.plan = plan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(Date finish_date) {
        this.finish_date = finish_date;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Set<Host> getHosts() {
        return hosts;
    }

    public void setHosts(Set<Host> hosts) {
        this.hosts = hosts;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}
