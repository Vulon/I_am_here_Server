package com.I_am_here.Database.Entity;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "host")
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "host_id")
    private Integer host_id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phone_number;

    @Column(name = "password")
    private String password;

    @Column(name = "access_token")
    private String access_token;

    @Column(name = "refresh_token")
    private String refresh_token;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host")
    private Set<QR_key_word> qr_key_words;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;


    @ManyToMany
    @JoinTable(name = "host_subject", joinColumns = @JoinColumn(name = "host_id"),
    inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects;

    public Host() {
    }

    public Host(String name, String email, String phone_number, String password, String access_token, String refresh_token, Set<QR_key_word> qr_key_words, Manager manager, Set<Subject> subjects) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.password = password;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.qr_key_words = qr_key_words;
        this.manager = manager;
        this.subjects = subjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        if (!host_id.equals(host.host_id)) return false;
        if (name != null ? !name.equals(host.name) : host.name != null) return false;
        if (email != null ? !email.equals(host.email) : host.email != null) return false;
        if (!phone_number.equals(host.phone_number)) return false;
        if (password != null ? !password.equals(host.password) : host.password != null) return false;
        if (access_token != null ? !access_token.equals(host.access_token) : host.access_token != null) return false;
        if (refresh_token != null ? !refresh_token.equals(host.refresh_token) : host.refresh_token != null)
            return false;
        if (qr_key_words != null ? !qr_key_words.equals(host.qr_key_words) : host.qr_key_words != null) return false;
        if (manager != null ? !manager.equals(host.manager) : host.manager != null) return false;
        return subjects != null ? subjects.equals(host.subjects) : host.subjects == null;

    }

    @Override
    public int hashCode() {
        int result = host_id.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + phone_number.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (access_token != null ? access_token.hashCode() : 0);
        result = 31 * result + (refresh_token != null ? refresh_token.hashCode() : 0);
        result = 31 * result + (qr_key_words != null ? qr_key_words.hashCode() : 0);
        result = 31 * result + (manager != null ? manager.hashCode() : 0);
        result = 31 * result + (subjects != null ? subjects.hashCode() : 0);
        return result;
    }

    public Integer getHost_id() {
        return host_id;
    }

    public void setHost_id(Integer host_id) {
        this.host_id = host_id;
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

    public Set<QR_key_word> getQr_key_words() {
        return qr_key_words;
    }

    public void setQr_key_words(Set<QR_key_word> qr_key_words) {
        this.qr_key_words = qr_key_words;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }
}
