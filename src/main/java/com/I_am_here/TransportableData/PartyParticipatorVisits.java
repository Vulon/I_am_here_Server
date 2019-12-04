package com.I_am_here.TransportableData;


import com.I_am_here.Database.Entity.Party;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PartyParticipatorVisits implements Serializable {

    private static final long serialVersionUID = 61647418669464111L;
    private String id;

    private String name;

    private String participator_count;

    private Set<ParticipatorVisitTimes> participator_visit_times;

    public PartyParticipatorVisits(Party party) {
        this.id = String.valueOf(party.getParty());
        this.name = party.getName();
        this.participator_count = String.valueOf(party.getParticipators().size());
        this.participator_visit_times = new HashSet<>();
    }

    public void addParticipatorVisitTime(ParticipatorVisitTimes participatorVisitTime) {
        this.participator_visit_times.add(participatorVisitTime);
    }

    public ParticipatorVisitTimes findParticipator (int participator_id) {
        for (ParticipatorVisitTimes pvt : participator_visit_times) {
            if (pvt.getId().equals(participator_id)) {
                return pvt;
            }
        }
        return null;
    }

    public PartyParticipatorVisits() {
    }

    public Integer getId() {
        return Integer.valueOf(id);
    }

    public void setId(Integer id) {
        this.id = String.valueOf(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParticipator_count() {
        return Integer.valueOf(participator_count);
    }

    public void setParticipator_count(Integer participator_count) {
        this.participator_count = String.valueOf(participator_count);
    }

    public Set<ParticipatorVisitTimes> getParticipator_visit_times() {
        return participator_visit_times;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartyParticipatorVisits that = (PartyParticipatorVisits) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (participator_count != null ? !participator_count.equals(that.participator_count) : that.participator_count != null)
            return false;
        return participator_visit_times != null ? participator_visit_times.equals(that.participator_visit_times) : that.participator_visit_times == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (participator_count != null ? participator_count.hashCode() : 0);
        result = 31 * result + (participator_visit_times != null ? participator_visit_times.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PartyParticipatorVisits{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", participator_count=" + participator_count +
                ", participator_visit_times=" + participator_visit_times +
                '}';
    }
}
