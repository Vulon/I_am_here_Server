package com.I_am_here.Database.Entity;


import javax.persistence.*;

@Entity
@Table(name = "code_word_participator")
public class Code_word_participator {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "code_word_participator_id")
    private Integer code_word_participator_id;


    @Column(name = "code_word", nullable = false)
    private String code_word;

    @ManyToOne
    @JoinColumn(name = "participator_id")
    private Participator participator;

    public Code_word_participator() {
    }

    public Code_word_participator(String code_word, Participator participator) {
        this.code_word = code_word;
        this.participator = participator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Code_word_participator code_wordParticipator1 = (Code_word_participator) o;

        if (!code_word_participator_id.equals(code_wordParticipator1.code_word_participator_id)) return false;
        if (code_word != null ? !code_word.equals(code_wordParticipator1.code_word) : code_wordParticipator1.code_word != null) return false;
        return participator.equals(code_wordParticipator1.participator);

    }

    @Override
    public int hashCode() {
        int result = code_word_participator_id.hashCode();
        result = 31 * result + (code_word != null ? code_word.hashCode() : 0);
        return result;
    }

    public Integer getCode_word_participator_id() {
        return code_word_participator_id;
    }

    public void setCode_word_participator_id(Integer code_word_participator_id) {
        this.code_word_participator_id = code_word_participator_id;
    }

    public String getCode_word() {
        return code_word;
    }

    public void setCode_word(String code_word) {
        this.code_word = code_word;
    }

    public Participator getParticipator() {
        return participator;
    }

    public void setParticipator(Participator participator) {
        this.participator = participator;
    }

}
