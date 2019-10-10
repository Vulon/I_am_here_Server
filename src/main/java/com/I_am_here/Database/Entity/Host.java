package com.I_am_here.Database.Entity;


import com.I_am_here.Database.Account;
import com.I_am_here.TransportableData.TokenData;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "host")
public class Host implements Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "host_id")
    private Integer host_id;

    @Column(name = "host_uuid", nullable = false)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "access_token")
    private String access_token;

    @Column(name = "refresh_token")
    private String refresh_token;

    @Column(name = "qr_token")
    private String qr_token;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host")
    private Set<QR_key_word> qr_key_words;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host")
    private Set<Code_word_host> code_words;

    @ManyToMany
    @JoinTable(name = "manager_host", joinColumns =
    @JoinColumn(name = "host_id"), inverseJoinColumns = @JoinColumn(name = "manager_id"))
    private Set<Manager> managers;


    @ManyToMany
    @JoinTable(name = "host_subject", joinColumns = @JoinColumn(name = "host_id"),
    inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects;

    public Host() {
    }

    public Host(String uuid, String name, String email, String phoneNumber, String password, TokenData tokenData){
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.access_token = tokenData.getAccess_token();
        this.refresh_token = tokenData.getRefresh_token();
        this.qr_token = "";
        this.qr_key_words = new HashSet<>();
        this.code_words = new HashSet<>();
        this.managers = new HashSet<>();
        this.subjects = new HashSet<>();
    }

    public Host(String uuid, String name, String email, String phoneNumber, String password, String access_token, String refresh_token, String qr_token, Set<QR_key_word> qr_key_words, Set<Code_word_host> code_words, Set<Manager> managers, Set<Subject> subjects) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.qr_token = qr_token;
        this.qr_key_words = qr_key_words;
        this.code_words = code_words;
        this.managers = managers;
        this.subjects = subjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        if (!host_id.equals(host.host_id)) return false;
        if (!uuid.equals(host.uuid)) return false;
        if (name != null ? !name.equals(host.name) : host.name != null) return false;
        if (email != null ? !email.equals(host.email) : host.email != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(host.phoneNumber) : host.phoneNumber != null) return false;
        if (!password.equals(host.password)) return false;
        if (access_token != null ? !access_token.equals(host.access_token) : host.access_token != null) return false;
        if (refresh_token != null ? !refresh_token.equals(host.refresh_token) : host.refresh_token != null)
            return false;
        if (qr_token != null ? !qr_token.equals(host.qr_token) : host.qr_token != null) return false;
        if (qr_key_words != null ? !qr_key_words.equals(host.qr_key_words) : host.qr_key_words != null) return false;
        if (code_words != null ? !code_words.equals(host.code_words) : host.code_words != null) return false;
        if (managers != null ? !managers.equals(host.managers) : host.managers != null) return false;
        return subjects != null ? subjects.equals(host.subjects) : host.subjects == null;

    }

    @Override
    public int hashCode() {
        int result = host_id.hashCode();
        result = 31 * result + uuid.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + password.hashCode();
        result = 31 * result + (access_token != null ? access_token.hashCode() : 0);
        result = 31 * result + (refresh_token != null ? refresh_token.hashCode() : 0);
        result = 31 * result + (qr_token != null ? qr_token.hashCode() : 0);
        return result;
    }

    public Integer getHost_id() {
        return host_id;
    }

    public void setHost_id(Integer host_id) {
        this.host_id = host_id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @Override
    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getQr_token() {
        return qr_token;
    }

    public void setQr_token(String qr_token) {
        this.qr_token = qr_token;
    }

    public Set<QR_key_word> getQr_key_words() {
        return qr_key_words;
    }

    public void setQr_key_words(Set<QR_key_word> qr_key_words) {
        this.qr_key_words = qr_key_words;
    }

    public Set<Manager> getManagers() {
        return managers;
    }

    public void setManagers(Set<Manager> managers) {
        this.managers = managers;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Code_word_host> getCode_words() {
        return code_words;
    }

    public void setCode_words(Set<Code_word_host> code_words) {
        this.code_words = code_words;
    }

    @Override
    public String toString() {
        return "Host{" +
                "host_id=" + host_id +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", qr_token='" + qr_token + '\'' +
                ", qr_key_words=" + qr_key_words +
                '}';
    }
}
