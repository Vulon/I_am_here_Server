package com.I_am_here.TransportableData;

import com.I_am_here.Database.Entity.Party;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ExtendedPartyData implements Serializable {
    private static final long serialVersionUID = -8333840907270694885L;

    private Integer id;

    private Integer manager_id;

    private String name;

    private String description;

    private String code;

    private ArrayList<HashMap<String, String>> subjects;

    private ArrayList<HashMap<String, String>> participators;

    private Integer participator_count;

    public ExtendedPartyData() {
    }

    public ExtendedPartyData(Party party){
        id = party.getParty();
        manager_id = party.getManager().getManagerId();
        name = party.getName();
        description = party.getDescription();
        code = party.getBroadcastWord();
        subjects = new ArrayList<>();
        {
            party.getSubjects().forEach(subject -> {
                HashMap<String, String> map = new HashMap<>(2);
                map.put("id", subject.getSubjectId().toString());
                map.put("name", subject.getName());
                subjects.add(map);
            });
        }
        {
            participators = new ArrayList<>();
            party.getParticipators().forEach(participator -> {
                HashMap<String, String> map = new HashMap<>(2);
                map.put("id", participator.getParticipatorId().toString());
                map.put("name", participator.getName());
                participators.add(map);
            });
        }
        participator_count = participators.size();

    }

    public static Set<ExtendedPartyData> createExtendedPartyData(Set<Party> partySet){
        HashSet<ExtendedPartyData> partyData = new HashSet<>();
        if(partySet == null){
            return partyData;
        }
        partySet.forEach(party -> partyData.add(new ExtendedPartyData(party)));
        return partyData;
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

    public ArrayList<HashMap<String, String>> getSubjects() {
        return subjects;
    }

    public void setSubjects(ArrayList<HashMap<String, String>> subjects) {
        this.subjects = subjects;
    }

    public ArrayList<HashMap<String, String>> getParticipators() {
        return participators;
    }

    public void setParticipators(ArrayList<HashMap<String, String>> participators) {
        this.participators = participators;
    }

    public Integer getParticipator_count() {
        return participator_count;
    }

    public void setParticipator_count(Integer participator_count) {
        this.participator_count = participator_count;
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
                ", participator_count=" + participator_count +
                '}';
    }
}
