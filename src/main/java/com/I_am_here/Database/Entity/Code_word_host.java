package com.I_am_here.Database.Entity;


import javax.persistence.*;

@Entity
@Table(name = "code_word_host")
public class Code_word_host {
    @Id
    @GeneratedValue
    @Column(name = "h_code_word_id")
    private Integer h_code_word_id;

    @Column(name = "code_word")
    private String code_word;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

    public Code_word_host() {
    }

    public Code_word_host(String code_word, Host host) {
        this.code_word = code_word;
        this.host = host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Code_word_host that = (Code_word_host) o;

        if (!h_code_word_id.equals(that.h_code_word_id)) return false;
        if (code_word != null ? !code_word.equals(that.code_word) : that.code_word != null) return false;
        return host.equals(that.host);

    }

    @Override
    public int hashCode() {
        int result = h_code_word_id.hashCode();
        result = 31 * result + (code_word != null ? code_word.hashCode() : 0);
        return result;
    }

    public Integer getH_code_word_id() {
        return h_code_word_id;
    }

    public void setH_code_word_id(Integer h_code_word_id) {
        this.h_code_word_id = h_code_word_id;
    }

    public String getCode_word() {
        return code_word;
    }

    public void setCode_word(String code_word) {
        this.code_word = code_word;
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
                "h_code_word_id=" + h_code_word_id +
                ", code_word='" + code_word + '\'' +
                ", host=" + host +
                '}';
    }
}
