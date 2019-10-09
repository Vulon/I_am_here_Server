package com.I_am_here.TransportableData;


import com.I_am_here.Database.Entity.Party;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PartyData implements Serializable {
    private static final long serialVersionUID = -8987181325853382436L;

    private Integer party_id;

    private String party_name;

    private String manager_name;

    private String description;

    private Integer participators_count;

    public PartyData(Party party) {
        this.party_id = party.getParty();
        this.party_name = party.getName();
        this.manager_name = party.getManager().getName();
        this.description = party.getDescription();
        this.participators_count = party.getParticipators().size();
    }

    public static Set<PartyData> createPartyData(Set<Party> partySet){
        HashSet<PartyData> partyData = new HashSet<>();
        partySet.forEach(party -> partyData.add(new PartyData(party)));
        return partyData;
    }

    public PartyData() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartyData partyData = (PartyData) o;

        if (!party_id.equals(partyData.party_id)) return false;
        if (!party_name.equals(partyData.party_name)) return false;
        if (manager_name != null ? !manager_name.equals(partyData.manager_name) : partyData.manager_name != null)
            return false;
        if (description != null ? !description.equals(partyData.description) : partyData.description != null)
            return false;
        return participators_count != null ? participators_count.equals(partyData.participators_count) : partyData.participators_count == null;

    }

    @Override
    public int hashCode() {
        int result = party_id.hashCode();
        result = 31 * result + party_name.hashCode();
        result = 31 * result + (manager_name != null ? manager_name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (participators_count != null ? participators_count.hashCode() : 0);
        return result;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getParty_id() {
        return party_id;
    }

    public void setParty_id(Integer party_id) {
        this.party_id = party_id;
    }

    public String getParty_name() {
        return party_name;
    }

    public void setParty_name(String party_name) {
        this.party_name = party_name;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParticipators_count() {
        return participators_count;
    }

    public void setParticipators_count(Integer participators_count) {
        this.participators_count = participators_count;
    }

    @Override
    public String toString() {
        return "PartyData{" +
                "party_id=" + party_id +
                ", party_name='" + party_name + '\'' +
                ", manager_name='" + manager_name + '\'' +
                ", description='" + description + '\'' +
                ", participators_count=" + participators_count +
                '}';
    }
}
