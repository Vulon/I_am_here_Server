package com.I_am_here.Database.Entity;


import javax.persistence.*;

@Entity
@Table(name = "code_word_host")
public class Code_word_host {
    @Id
    @GeneratedValue
    @Column(name = "h_code_word_id")
    private Integer codeWordId;

    @Column(name = "code_word")
    private String codeWord;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

    public Code_word_host() {
    }

    public Code_word_host(String code_word, Host host) {
        this.codeWord = code_word;
        this.host = host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Code_word_host that = (Code_word_host) o;
        if(codeWord.hashCode() != that.codeWord.hashCode()){
            return false;
        }

        return codeWord.equals(that.codeWord);

    }

    @Override
    public int hashCode() {
        int result = codeWord.hashCode();
        result = 31 * result + host.hashCode();
        return result;
    }

    public Integer getCodeWordId() {
        return codeWordId;
    }

    public void setCodeWordId(Integer codeWordId) {
        this.codeWordId = codeWordId;
    }

    public String getCodeWord() {
        return codeWord;
    }

    public void setCodeWord(String codeWord) {
        this.codeWord = codeWord;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return "Code_word_host{" +
                "codeWordId=" + codeWordId +
                ", codeWord='" + codeWord + '\'' +
                ", host=" + host +
                '}';
    }
}
