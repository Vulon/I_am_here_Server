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

    @Column(name = "broadcast_word")
    private String broadcast_word;

    @Column(name = "broadcast_start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date broadcast_start;


    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @ManyToMany
    @JoinTable(name = "host_subject", joinColumns = @JoinColumn(name = "subject_id"),
    inverseJoinColumns = @JoinColumn(name = "host_id"))
    private Set<Host> hosts;

    @ManyToMany
    @JoinTable(name = "party_subject", joinColumns = @JoinColumn(name = "subject_id"),
    inverseJoinColumns = @JoinColumn(name = "party_id"))
    private Set<Party> parties;

    public Subject() {
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        if (!subject_id.equals(subject.subject_id)) return false;
        if (!name.equals(subject.name)) return false;
        if (plan != null ? !plan.equals(subject.plan) : subject.plan != null) return false;
        if (description != null ? !description.equals(subject.description) : subject.description != null) return false;
        if (start_date != null ? !start_date.equals(subject.start_date) : subject.start_date != null) return false;
        if (finish_date != null ? !finish_date.equals(subject.finish_date) : subject.finish_date != null) return false;
        if (!broadcast_word.equals(subject.broadcast_word)) return false;
        if (broadcast_start != null ? !broadcast_start.equals(subject.broadcast_start) : subject.broadcast_start != null)
            return false;
        if (manager != null ? !manager.equals(subject.manager) : subject.manager != null) return false;
        if (hosts != null ? !hosts.equals(subject.hosts) : subject.hosts != null) return false;
        return parties != null ? parties.equals(subject.parties) : subject.parties == null;

    }

    @Override
    public int hashCode() {
        int result = subject_id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (plan != null ? plan.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (start_date != null ? start_date.hashCode() : 0);
        result = 31 * result + (finish_date != null ? finish_date.hashCode() : 0);
        result = 31 * result + broadcast_word.hashCode();
        result = 31 * result + (broadcast_start != null ? broadcast_start.hashCode() : 0);
        return result;
    }

    public String getBroadcast_word() {
        return broadcast_word;
    }

    public void setBroadcast_word(String broadcast_word) {
        this.broadcast_word = broadcast_word;
    }

    public Date getBroadcast_start() {
        return broadcast_start;
    }

    public void setBroadcast_start(Date broadcast_start) {
        this.broadcast_start = broadcast_start;
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

    public Set<Party> getParties() {
        return parties;
    }

    public void setParties(Set<Party> parties) {
        this.parties = parties;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subject_id=" + subject_id +
                ", name='" + name + '\'' +
                ", plan=" + plan +
                ", description='" + description + '\'' +
                ", start_date=" + start_date +
                ", finish_date=" + finish_date +
                ", broadcast_word='" + broadcast_word + '\'' +
                ", broadcast_start=" + broadcast_start +
                '}';
    }
}
