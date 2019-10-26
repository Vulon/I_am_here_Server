package com.I_am_here.Database.Entity;


import com.I_am_here.Database.Account;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "manager")

public class Manager implements Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "manager_id")
    private Integer managerId;

    @Column(name = "manager_uuid", nullable = false)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "manager")
    private Set<Subject> subjects;


    @ManyToMany
    @JoinTable(name = "manager_host", joinColumns =
    @JoinColumn(name = "manager_id"), inverseJoinColumns = @JoinColumn(name = "host_id"))
    private Set<Host> hosts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "manager")
    private Set<Party> parties;

    public Manager() {
    }

    public Manager(String uuid, String name, String email, String password, String access_token, String refresh_token, Set<Subject> subjects, Set<Host> hosts, Set<Party> parties) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.accessToken = access_token;
        this.refreshToken = refresh_token;
        this.subjects = subjects;
        this.hosts = hosts;
        this.parties = parties;
    }

    public void addParty(Party party){
        this.parties.add(party);
    }








    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Manager manager = (Manager) o;

        if (!managerId.equals(manager.managerId)) return false;
        if (name != null ? !name.equals(manager.name) : manager.name != null) return false;
        if (email != null ? !email.equals(manager.email) : manager.email != null) return false;
        if (password != null ? !password.equals(manager.password) : manager.password != null) return false;
        if (accessToken != null ? !accessToken.equals(manager.accessToken) : manager.accessToken != null)
            return false;
        if (refreshToken != null ? !refreshToken.equals(manager.refreshToken) : manager.refreshToken != null)
            return false;
        if (subjects != null ? !subjects.equals(manager.subjects) : manager.subjects != null) return false;
        if (hosts != null ? !hosts.equals(manager.hosts) : manager.hosts != null) return false;
        return parties != null ? parties.equals(manager.parties) : manager.parties == null;

    }

    @Override
    public int hashCode() {
        int result = managerId.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
        return result;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Host> getHosts() {
        return hosts;
    }

    public void setHosts(Set<Host> hosts) {
        this.hosts = hosts;
    }

    public Set<Party> getParties() {
        return parties;
    }

    public void setParties(Set<Party> parties) {
        this.parties = parties;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "managerId=" + managerId +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
