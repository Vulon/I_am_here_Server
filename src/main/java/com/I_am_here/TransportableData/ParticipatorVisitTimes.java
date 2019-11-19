package com.I_am_here.TransportableData;

import com.I_am_here.Database.Entity.Participator;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ParticipatorVisitTimes implements Serializable {
    private static final long serialVersionUID = 4879215539218193477L;

    public Integer id;

    public String name;

    public Set<Long> visits;


    public void addVisit(Date date){
        this.visits.add(date.getTime());
    }

    public ParticipatorVisitTimes() {
    }

    public ParticipatorVisitTimes(Participator participator) {
        this.id = participator.getParticipatorId();
        this.name = participator.getName();
        visits = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipatorVisitTimes that = (ParticipatorVisitTimes) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return visits != null ? visits.equals(that.visits) : that.visits == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (visits != null ? visits.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ParticipatorVisitTimes{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", visits=" + visits +
                '}';
    }
}
