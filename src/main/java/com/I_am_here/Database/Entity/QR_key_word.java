package com.I_am_here.Database.Entity;


import javax.persistence.*;

@Entity
public class QR_key_word {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "qr_key_word_id")
    private Integer qrKeyWordId;

    @Column(name = "key_word", nullable = false)
    private String keyWord;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public QR_key_word() {
    }

    public QR_key_word(String keyWord, Host host, Subject subject) {
        this.keyWord = keyWord;
        this.host = host;
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QR_key_word that = (QR_key_word) o;

        if (!qrKeyWordId.equals(that.qrKeyWordId)) return false;
        if (!keyWord.equals(that.keyWord)) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        return subject != null ? subject.equals(that.subject) : that.subject == null;

    }

    @Override
    public int hashCode() {
        int result = qrKeyWordId.hashCode();
        result = 31 * result + keyWord.hashCode();
        result = 31 * result + (host != null ? host.getUuid().hashCode() : 0);
        result = 31 * result + (subject != null ? subject.getName().hashCode() : 0);
        return result;
    }

    public Integer getQrKeyWordId() {
        return qrKeyWordId;
    }

    public void setQrKeyWordId(Integer qrKeyWordId) {
        this.qrKeyWordId = qrKeyWordId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
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
