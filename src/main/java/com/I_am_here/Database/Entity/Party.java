package com.I_am_here.Database.Entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "party")
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "party_id")
    private Integer party;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;


    @Column(name = "broadcast_word")
    private String broadcastWord;

    @Column(name = "broadcast_start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date broadcastStart;

    @ManyToMany
    @JoinTable(name = "party_subject", joinColumns =
    @JoinColumn(name = "party_id"), inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects;

    @ManyToMany
    @JoinTable(name = "party_participator", joinColumns =
    @JoinColumn(name = "party_id"), inverseJoinColumns = @JoinColumn(name = "participator_id"))
    private Set<Participator> participators;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    public Party() {
    }



    public Party(String name, String description, String broadcastWord, Manager manager){
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.subjects = new HashSet<>();
        this.participators = new HashSet<>();
        this.broadcastStart = Date.from(Instant.now());
        this.broadcastWord = broadcastWord;
    }

    public void addParticipator(Participator p){
        this.participators.add(p);
    }

    public void addSubject(Subject s){
        this.subjects.add(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Party party = (Party) o;

        if (!this.party.equals(party.party)) return false;
        if (!name.equals(party.name)) return false;
        if (description != null ? !description.equals(party.description) : party.description != null) return false;
        if (!broadcastWord.equals(party.broadcastWord)) return false;
        if (broadcastStart != null ? !broadcastStart.equals(party.broadcastStart) : party.broadcastStart != null)
            return false;
        if (subjects != null ? !subjects.equals(party.subjects) : party.subjects != null) return false;
        if (participators != null ? !participators.equals(party.participators) : party.participators != null)
            return false;
        return manager != null ? manager.equals(party.manager) : party.manager == null;

    }

    @Override
    public int hashCode() {
        int result = party.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
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

    public Integer getParty() {
        return party;
    }

    public void setParty(Integer party) {
        this.party = party;
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

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Participator> getParticipators() {
        return participators;
    }

    public void setParticipators(Set<Participator> participators) {
        this.participators = participators;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return "Party{" +
                "party=" + party +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", broadcastWord='" + broadcastWord + '\'' +
                ", broadcastStart=" + broadcastStart +
                '}';
    }
}
