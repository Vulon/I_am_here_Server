package com.I_am_here.Database.Entity;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "party")
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "party_id")
    private Integer party_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

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

    public Party(String name, String description, Set<Subject> subjects, Set<Participator> participators, Manager manager) {
        this.name = name;
        this.description = description;
        this.subjects = subjects;
        this.participators = participators;
        this.manager = manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Party party = (Party) o;

        if (!party_id.equals(party.party_id)) return false;
        if (!name.equals(party.name)) return false;
        if (description != null ? !description.equals(party.description) : party.description != null) return false;
        if (subjects != null ? !subjects.equals(party.subjects) : party.subjects != null) return false;
        if (participators != null ? !participators.equals(party.participators) : party.participators != null)
            return false;
        return manager != null ? manager.equals(party.manager) : party.manager == null;

    }

    @Override
    public int hashCode() {
        int result = party_id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (subjects != null ? subjects.hashCode() : 0);
        result = 31 * result + (participators != null ? participators.hashCode() : 0);
        result = 31 * result + (manager != null ? manager.hashCode() : 0);
        return result;
    }

    public Integer getParty_id() {
        return party_id;
    }

    public void setParty_id(Integer party_id) {
        this.party_id = party_id;
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
}
