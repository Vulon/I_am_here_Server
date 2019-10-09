package com.I_am_here.Database.Entity;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "visit")
public class Visit {
    @Id
    @Column(name = "visit_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer visit_id;

    @Column(name = "date")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "participator_id")
    private Participator participator;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;


    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public Visit() {
    }

    public Visit(Date date, Participator participator, Host host, Subject subject) {
        this.date = date;
        this.participator = participator;
        this.host = host;
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visit visit = (Visit) o;

        if (!visit_id.equals(visit.visit_id)) return false;
        if (date != null ? !date.equals(visit.date) : visit.date != null) return false;
        if (participator != null ? !participator.equals(visit.participator) : visit.participator != null) return false;
        if (host != null ? !host.equals(visit.host) : visit.host != null) return false;
        return subject != null ? subject.equals(visit.subject) : visit.subject == null;

    }

    @Override
    public int hashCode() {
        int result = visit_id.hashCode();
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (participator != null ? participator.getUuid().hashCode() : 0);
        result = 31 * result + (host != null ? host.getUuid().hashCode() : 0);
        result = 31 * result + (subject != null ? subject.getName().hashCode() : 0);
        return result;
    }

    public Integer getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(Integer visit_id) {
        this.visit_id = visit_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Participator getParticipator() {
        return participator;
    }

    public void setParticipator(Participator participator) {
        this.participator = participator;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
