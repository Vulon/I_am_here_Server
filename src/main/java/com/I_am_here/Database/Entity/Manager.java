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
    private Integer manager_id;

    @Column(name = "manager_uuid", nullable = false)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phone_number;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "access_token")
    private String access_token;

    @Column(name = "refresh_token")
    private String refresh_token;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "manager")
    private Set<Subject> subjects;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "manager")
    private Set<Host> hosts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "manager")
    private Set<Party> parties;

    public Manager() {
    }

    public Manager(String uuid, String name, String email, String phone_number, String password, String access_token, String refresh_token, Set<Subject> subjects, Set<Host> hosts, Set<Party> parties) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.password = password;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.subjects = subjects;
        this.hosts = hosts;
        this.parties = parties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Manager manager = (Manager) o;

        if (!manager_id.equals(manager.manager_id)) return false;
        if (name != null ? !name.equals(manager.name) : manager.name != null) return false;
        if (email != null ? !email.equals(manager.email) : manager.email != null) return false;
        if (!phone_number.equals(manager.phone_number)) return false;
        if (password != null ? !password.equals(manager.password) : manager.password != null) return false;
        if (access_token != null ? !access_token.equals(manager.access_token) : manager.access_token != null)
            return false;
        if (refresh_token != null ? !refresh_token.equals(manager.refresh_token) : manager.refresh_token != null)
            return false;
        if (subjects != null ? !subjects.equals(manager.subjects) : manager.subjects != null) return false;
        if (hosts != null ? !hosts.equals(manager.hosts) : manager.hosts != null) return false;
        return parties != null ? parties.equals(manager.parties) : manager.parties == null;

    }

    @Override
    public int hashCode() {
        int result = manager_id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + phone_number.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (access_token != null ? access_token.hashCode() : 0);
        result = 31 * result + (refresh_token != null ? refresh_token.hashCode() : 0);
        result = 31 * result + (subjects != null ? subjects.hashCode() : 0);
        result = 31 * result + (hosts != null ? hosts.hashCode() : 0);
        result = 31 * result + (parties != null ? parties.hashCode() : 0);
        return result;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
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
                "manager_id=" + manager_id +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", password='" + password + '\'' +
                ", access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", subjects=" + subjects +
                ", hosts=" + hosts +
                ", parties=" + parties +
                '}';
    }
}
