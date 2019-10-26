package com.I_am_here.Database.Entity;


import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @Column(name = "subject_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer subjectId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "plan")
    private Integer plan;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "start_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "finish_date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date finishDate;

    @Column(name = "broadcast_word")
    private String broadcastWord;

    @Column(name = "broadcast_start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date broadcastStart;


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

    public Subject(String name, Integer plan, String description, Date start_date, Date finish_date, String broadcast_word, Manager manager) {
        this.name = name;
        this.plan = plan;
        this.description = description;
        this.startDate = start_date;
        this.finishDate = finish_date;
        this.broadcastWord = broadcast_word;
        this.manager = manager;
        this.broadcastStart = Date.from(Instant.now());
        this.hosts = new HashSet<>();
        this.parties = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subject subject = (Subject) o;

        if (!subjectId.equals(subject.subjectId)) return false;
        if (!name.equals(subject.name)) return false;
        if (plan != null ? !plan.equals(subject.plan) : subject.plan != null) return false;
        if (description != null ? !description.equals(subject.description) : subject.description != null) return false;
        if (startDate != null ? !startDate.equals(subject.startDate) : subject.startDate != null) return false;
        if (finishDate != null ? !finishDate.equals(subject.finishDate) : subject.finishDate != null) return false;
        if (!broadcastWord.equals(subject.broadcastWord)) return false;
        if (broadcastStart != null ? !broadcastStart.equals(subject.broadcastStart) : subject.broadcastStart != null)
            return false;
        if (manager != null ? !manager.equals(subject.manager) : subject.manager != null) return false;
        if (hosts != null ? !hosts.equals(subject.hosts) : subject.hosts != null) return false;
        return parties != null ? parties.equals(subject.parties) : subject.parties == null;

    }

    @Override
    public int hashCode() {
        int result = subjectId.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (plan != null ? plan.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (finishDate != null ? finishDate.hashCode() : 0);
        result = 31 * result + broadcastWord.hashCode();
        result = 31 * result + (broadcastStart != null ? broadcastStart.hashCode() : 0);
        return result;
    }

    public String getBroadcastWord() {
        return broadcastWord;
    }

    public void setBroadcastWord(String broadcastWord) {
        this.broadcastWord = broadcastWord;
    }

    public Date getBroadcastStart() {
        return broadcastStart;
    }

    public void setBroadcastStart(Date broadcastStart) {
        this.broadcastStart = broadcastStart;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
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
                "subjectId=" + subjectId +
                ", name='" + name + '\'' +
                ", plan=" + plan +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", finishDate=" + finishDate +
                ", broadcastWord='" + broadcastWord + '\'' +
                ", broadcastStart=" + broadcastStart +
                '}';
    }
}
