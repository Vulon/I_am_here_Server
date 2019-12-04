package com.I_am_here.TransportableData;

import com.I_am_here.Database.Entity.Host;
import com.I_am_here.Database.Entity.Participator;
import com.I_am_here.Database.Entity.Subject;
import com.I_am_here.Database.Entity.Visit;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

public class VisitData implements Serializable {

    private static final long serialVersionUID = 1475970845273090432L;
    private Integer visit_id;

    private Long timestamp;

    private Integer participator_id;

    private String participator_name;

    private Integer host_id;

    private String host_name;

    private Integer subject_id;


    private String subject_name;




    public VisitData() {

    }

    public VisitData(Visit visit) {
        visit_id = visit.getVisitId();
        timestamp = visit.getDate().getTime();
        participator_id = visit.getParticipator().getParticipatorId();
        participator_name = visit.getParticipator().getName();
        host_id = visit.getHost().getHostId();
        host_name = visit.getHost().getName();
        subject_id = visit.getSubject().getSubjectId();
        subject_name = visit.getSubject().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VisitData visitData = (VisitData) o;

        if (visit_id != null ? !visit_id.equals(visitData.visit_id) : visitData.visit_id != null) return false;
        if (timestamp != null ? !timestamp.equals(visitData.timestamp) : visitData.timestamp != null) return false;
        if (participator_id != null ? !participator_id.equals(visitData.participator_id) : visitData.participator_id != null)
            return false;
        if (participator_name != null ? !participator_name.equals(visitData.participator_name) : visitData.participator_name != null)
            return false;
        if (host_id != null ? !host_id.equals(visitData.host_id) : visitData.host_id != null) return false;
        if (host_name != null ? !host_name.equals(visitData.host_name) : visitData.host_name != null) return false;
        if (subject_id != null ? !subject_id.equals(visitData.subject_id) : visitData.subject_id != null) return false;
        return subject_name != null ? subject_name.equals(visitData.subject_name) : visitData.subject_name == null;

    }

    @Override
    public int hashCode() {
        int result = visit_id != null ? visit_id.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (participator_id != null ? participator_id.hashCode() : 0);
        result = 31 * result + (participator_name != null ? participator_name.hashCode() : 0);
        result = 31 * result + (host_id != null ? host_id.hashCode() : 0);
        result = 31 * result + (host_name != null ? host_name.hashCode() : 0);
        result = 31 * result + (subject_id != null ? subject_id.hashCode() : 0);
        result = 31 * result + (subject_name != null ? subject_name.hashCode() : 0);
        return result;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(Integer visit_id) {
        this.visit_id = visit_id;
    }



    public Integer getParticipator_id() {
        return participator_id;
    }

    public void setParticipator_id(Integer participator_id) {
        this.participator_id = participator_id;
    }

    public String getParticipator_name() {
        return participator_name;
    }

    public void setParticipator_name(String participator_name) {
        this.participator_name = participator_name;
    }

    public Integer getHost_id() {
        return host_id;
    }

    public void setHost_id(Integer host_id) {
        this.host_id = host_id;
    }

    public String getHost_name() {
        return host_name;
    }

    public void setHost_name(String host_name) {
        this.host_name = host_name;
    }

    public Integer getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Integer subject_id) {
        this.subject_id = subject_id;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    @Override
    public String toString() {
        return "VisitData{" +
                "visit_id=" + visit_id +
                ", timestamp=" + timestamp +
                ", participator_id=" + participator_id +
                ", participator_name='" + participator_name + '\'' +
                ", host_id=" + host_id +
                ", host_name='" + host_name + '\'' +
                ", subject_id=" + subject_id +
                ", subject_name='" + subject_name + '\'' +
                '}';
    }
}
