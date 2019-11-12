package com.I_am_here.Database.Entity;


import com.I_am_here.Database.Account;
import com.I_am_here.TransportableData.TokenData;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "host")
public class Host implements Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "host_id")
    private Integer hostId;

    @Column(name = "host_uuid", nullable = false)
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = true)
    private String email;



    @Column(name = "password")
    private String password;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "qr_token")
    private String qrToken;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "host")
    private Set<QR_key_word> qrKeyWords;

    @OneToMany(mappedBy = "host")
    private Set<Code_word_host> codeWords;




    @ManyToMany
    @JoinTable(name = "host_subject", joinColumns = @JoinColumn(name = "host_id"),
    inverseJoinColumns = @JoinColumn(name = "subject_id"))
    private Set<Subject> subjects;

    public Host() {
    }

    public Host(String uuid, String name, String email, String password, TokenData tokenData){
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.accessToken = tokenData.getAccess_token();
        this.refreshToken = tokenData.getRefresh_token();
        this.qrToken = "";
        this.qrKeyWords = new HashSet<>();
        this.codeWords = new HashSet<>();
        this.subjects = new HashSet<>();
    }

    public Host(String uuid, String name, String email, String password, String accessToken, String refreshToken, String qrToken, Set<QR_key_word> qrKeyWords, Set<Code_word_host> codeWords, Set<Manager> managers, Set<Subject> subjects) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.qrToken = qrToken;
        this.qrKeyWords = qrKeyWords;
        this.codeWords = codeWords;
        this.subjects = subjects;
    }

    public void addCodeWords(List<String> code_words){
        code_words.forEach(s -> this.codeWords.add(new Code_word_host(s, this)));
    }
    public void removeCodeWords(Set<String> code_words){
        HashSet<Code_word_host> newCodeWords = new HashSet<>();
        this.codeWords.forEach(code_word_host -> {
            if(!code_words.contains(code_word_host.getCodeWord())){
                newCodeWords.add(code_word_host);
            }
        });
        System.out.println("WAS: "+ this.codeWords.toString());
//        this.codeWords = this.codeWords.stream().filter(code_word_host -> !code_words.contains(code_word_host.getCodeWord()))
//                .collect(Collectors.toSet());
        this.codeWords = newCodeWords;
        System.out.println("BECAME: " + this.codeWords.toString());
    }

    public Set<String> getCodeWordsStrings(){
        HashSet<String> code_word_strings = new HashSet<>();
        this.codeWords.forEach(code_word_host -> code_word_strings.add(code_word_host.getCodeWord()));
        return code_word_strings;
    }

    public void addSubject(Subject s){
        subjects.add(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        if (!hostId.equals(host.hostId)) return false;
        if (!uuid.equals(host.uuid)) return false;
        if (name != null ? !name.equals(host.name) : host.name != null) return false;
        if (email != null ? !email.equals(host.email) : host.email != null) return false;
        if (!password.equals(host.password)) return false;
        if (accessToken != null ? !accessToken.equals(host.accessToken) : host.accessToken != null) return false;
        if (refreshToken != null ? !refreshToken.equals(host.refreshToken) : host.refreshToken != null)
            return false;
        if (qrToken != null ? !qrToken.equals(host.qrToken) : host.qrToken != null) return false;
        if (qrKeyWords != null ? !qrKeyWords.equals(host.qrKeyWords) : host.qrKeyWords != null) return false;
        if (codeWords != null ? !codeWords.equals(host.codeWords) : host.codeWords != null) return false;
        return subjects != null ? subjects.equals(host.subjects) : host.subjects == null;

    }

    @Override
    public int hashCode() {
        int result = hostId.hashCode();
        result = 31 * result + uuid.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + password.hashCode();
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
        result = 31 * result + (qrToken != null ? qrToken.hashCode() : 0);
        return result;
    }

    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
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
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public Set<QR_key_word> getQrKeyWords() {
        return qrKeyWords;
    }

    public void setQrKeyWords(Set<QR_key_word> qrKeyWords) {
        this.qrKeyWords = qrKeyWords;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Code_word_host> getCodeWords() {
        return codeWords;
    }

    public void setCodeWords(Set<Code_word_host> codeWords) {
        this.codeWords = codeWords;
    }

    @Override
    public String toString() {
        return "Host{" +
                "hostId=" + hostId +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", qrToken='" + qrToken + '\'' +
                ", qrKeyWords=" + qrKeyWords +
                '}';
    }
}
