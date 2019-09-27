package com.I_am_here.Database.Entity;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table(name = "group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "group_id")
    private Integer group_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @ManyToMany
    @JoinTable(name = "group_subject", joinColumns =
    @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects;

    @ManyToMany
    @JoinTable(name = "group_participator", joinColumns =
    @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "participator_id"))
    private Set<Participator> participators;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;

    public Group() {
    }

    public Group(String name, String description, Set<Subject> subjects, Set<Participator> participators, Manager manager) {
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

        Group group = (Group) o;

        if (!group_id.equals(group.group_id)) return false;
        if (!name.equals(group.name)) return false;
        if (description != null ? !description.equals(group.description) : group.description != null) return false;
        if (subjects != null ? !subjects.equals(group.subjects) : group.subjects != null) return false;
        if (participators != null ? !participators.equals(group.participators) : group.participators != null)
            return false;
        return manager != null ? manager.equals(group.manager) : group.manager == null;

    }

    @Override
    public int hashCode() {
        int result = group_id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (subjects != null ? subjects.hashCode() : 0);
        result = 31 * result + (participators != null ? participators.hashCode() : 0);
        result = 31 * result + (manager != null ? manager.hashCode() : 0);
        return result;
    }

    public Integer getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Integer group_id) {
        this.group_id = group_id;
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
