package com.I_am_here.Database.Entity;


import com.I_am_here.Database.Account;
import com.I_am_here.TransportableData.TokenData;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "participator")
public class Participator implements Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "participator_id")
    private Integer participatorId;

    @Column(name = "participator_uuid", nullable = false)
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;


    @OneToMany(mappedBy = "participator")
    private Set<Code_word_participator> codeWords;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "participator")
    private Set<Visit> visits;

    @ManyToMany
    @JoinTable(name = "party_participator", joinColumns = @JoinColumn(name = "participator_id"),
    inverseJoinColumns = @JoinColumn(name = "party_id"))
    private Set<Party> parties;

    public Participator() {
    }


    public Participator(String uuid, String name, String email, String password, TokenData tokenData) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.codeWords = new HashSet<>();
        this.accessToken = tokenData.getAccess_token();
        this.refreshToken = tokenData.getRefresh_token();
        this.visits = new HashSet<>();
        this.parties = new HashSet<>();
    }

    public void addParty(Party party){
        this.parties.add(party);
    }

    public void addCodeWords(List<String> code_words){
        System.out.println("PARTICIPATOR ADD CODE WORDS: " + code_words.toString());
        code_words.forEach(s -> this.codeWords.add(new Code_word_participator(s, this)));
    }
    public void removeCodeWords(Set<String> code_words){
        this.codeWords = this.codeWords.stream().filter(code_word_participator -> !code_words.contains(code_word_participator.getCodeWord()))
                .collect(Collectors.toSet());
    }

    public Set<String> getCodeWordsStrings(){
        HashSet<String> code_word_strings = new HashSet<>();
        this.codeWords.forEach(code_word_participator -> code_word_strings.add(code_word_participator.getCodeWord()));
        return code_word_strings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participator that = (Participator) o;

        if (!participatorId.equals(that.participatorId)) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (codeWords != null ? !codeWords.equals(that.codeWords) : that.codeWords != null) return false;
        if (accessToken != null ? !accessToken.equals(that.accessToken) : that.accessToken != null) return false;
        if (refreshToken != null ? !refreshToken.equals(that.refreshToken) : that.refreshToken != null)
            return false;
        if (visits != null ? !visits.equals(that.visits) : that.visits != null) return false;
        return parties != null ? parties.equals(that.parties) : that.parties == null;

    }

    @Override
    public int hashCode() {
        int result = participatorId.hashCode();
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (accessToken != null ? accessToken.hashCode() : 0);
        result = 31 * result + (refreshToken != null ? refreshToken.hashCode() : 0);
        return result;
    }



    public Integer getParticipatorId() {
        return participatorId;
    }

    public void setParticipatorId(Integer participatorId) {
        this.participatorId = participatorId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Set<Code_word_participator> getCodeWords() {
        return codeWords;
    }

    public void setCodeWords(Set<Code_word_participator> codeWords) {
        this.codeWords = codeWords;
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

    public Set<Visit> getVisits() {
        return visits;
    }

    public void setVisits(Set<Visit> visits) {
        this.visits = visits;
    }

    public Set<Party> getParties() {
        return parties;
    }

    public void setParties(Set<Party> parties) {
        this.parties = parties;
    }

    @Override
    public String toString() {
        return "Participator{" +
                "participatorId=" + participatorId +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
