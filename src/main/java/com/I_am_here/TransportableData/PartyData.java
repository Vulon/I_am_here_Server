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

    private Integer participator_count;

    private String broadcast_code;

    public PartyData(Party party) {
        this.party_id = party.getParty();
        this.party_name = party.getName();
        this.manager_name = party.getManager().getName();
        this.description = party.getDescription();
        this.participator_count = party.getParticipators().size();
        this.broadcast_code = party.getBroadcastWord();
    }

    public static Set<PartyData> createPartyData(Set<Party> partySet){
        HashSet<PartyData> partyData = new HashSet<>();
        if(partySet == null){
            return partyData;
        }
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

        if (party_id != null ? !party_id.equals(partyData.party_id) : partyData.party_id != null) return false;
        if (party_name != null ? !party_name.equals(partyData.party_name) : partyData.party_name != null) return false;
        if (manager_name != null ? !manager_name.equals(partyData.manager_name) : partyData.manager_name != null)
            return false;
        if (description != null ? !description.equals(partyData.description) : partyData.description != null)
            return false;
        if (participator_count != null ? !participator_count.equals(partyData.participator_count) : partyData.participator_count != null)
            return false;
        return broadcast_code != null ? broadcast_code.equals(partyData.broadcast_code) : partyData.broadcast_code == null;
    }

    @Override
    public int hashCode() {
        int result = party_id != null ? party_id.hashCode() : 0;
        result = 31 * result + (party_name != null ? party_name.hashCode() : 0);
        result = 31 * result + (manager_name != null ? manager_name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (participator_count != null ? participator_count.hashCode() : 0);
        result = 31 * result + (broadcast_code != null ? broadcast_code.hashCode() : 0);
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

    public Integer getParticipator_count() {
        return participator_count;
    }

    public void setParticipator_count(Integer participator_count) {
        this.participator_count = participator_count;
    }

    public String getBroadcast_code() {
        return broadcast_code;
    }

    public void setBroadcast_code(String broadcast_code) {
        this.broadcast_code = broadcast_code;
    }

    @Override
    public String toString() {
        return "PartyData{" +
                "party_id=" + party_id +
                ", party_name='" + party_name + '\'' +
                ", manager_name='" + manager_name + '\'' +
                ", description='" + description + '\'' +
                ", participator_count=" + participator_count +
                ", broadcast_code='" + broadcast_code + '\'' +
                '}';
    }
}
