package com.I_am_here.TransportableData;

import com.I_am_here.Database.Entity.Party;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class ExtendedPartyData implements Serializable {
    private static final long serialVersionUID = -8333840907270694885L;

    private Integer id;

    private Integer manager_id;

    private String name;

    private String description;

    private String code;

    private ArrayList<Pair<Integer, String>> subjects;

    private ArrayList<Pair<Integer, String>> participators;

    public ExtendedPartyData() {
    }

    public ExtendedPartyData(Party party){
        id = party.getParty();
        manager_id = party.getManager().getManagerId();
        name = party.getName();
        description = party.getDescription();
        code = party.getBroadcastWord();
        subjects = new ArrayList<>();
        party.getSubjects().forEach(subject -> {
            subjects.add(new Pair(subject.getSubjectId(), subject.getName()));
        });
        participators = new ArrayList<>();
        party.getParticipators().forEach(participator -> {
            participators.add(new Pair(participator.getParticipatorId(), participator.getName()));
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExtendedPartyData that = (ExtendedPartyData) o;

        if (!id.equals(that.id)) return false;
        if (!manager_id.equals(that.manager_id)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (subjects != null ? !subjects.equals(that.subjects) : that.subjects != null) return false;
        return participators != null ? participators.equals(that.participators) : that.participators == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + manager_id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (subjects != null ? subjects.hashCode() : 0);
        result = 31 * result + (participators != null ? participators.hashCode() : 0);
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getManager_id() {
        return manager_id;
    }

    public void setManager_id(Integer manager_id) {
        this.manager_id = manager_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<Pair<Integer, String>> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<Pair<Integer, String>> subjects) {
        this.subjects = subjects;
    }

    public ArrayList<Pair<Integer, String>> getParticipators() {
        return participators;
    }

    public void setParticipators(ArrayList<Pair<Integer, String>> participators) {
        this.participators = participators;
    }

    @Override
    public String toString() {
        return "ExtendedPartyData{" +
                "id=" + id +
                ", manager_id=" + manager_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", code='" + code + '\'' +
                ", subjects=" + subjects +
                ", participators=" + participators +
                '}';
    }
}
