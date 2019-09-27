package com.I_am_here.Database.Entity;


import javax.persistence.*;

@Entity
public class QR_key_word {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "qr_key_word_id")
    private Integer qr_key_word_id;

    @Column(name = "key_word", nullable = false)
    private String key_word;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public QR_key_word() {
    }

    public QR_key_word(String key_word, Host host, Subject subject) {
        this.key_word = key_word;
        this.host = host;
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QR_key_word that = (QR_key_word) o;

        if (!qr_key_word_id.equals(that.qr_key_word_id)) return false;
        if (!key_word.equals(that.key_word)) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        return subject != null ? subject.equals(that.subject) : that.subject == null;

    }

    @Override
    public int hashCode() {
        int result = qr_key_word_id.hashCode();
        result = 31 * result + key_word.hashCode();
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        return result;
    }

    public Integer getQr_key_word_id() {
        return qr_key_word_id;
    }

    public void setQr_key_word_id(Integer qr_key_word_id) {
        this.qr_key_word_id = qr_key_word_id;
    }

    public String getKey_word() {
        return key_word;
    }

    public void setKey_word(String key_word) {
        this.key_word = key_word;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
