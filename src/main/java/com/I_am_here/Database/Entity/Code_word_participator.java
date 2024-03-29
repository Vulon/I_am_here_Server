package com.I_am_here.Database.Entity;


import javax.persistence.*;

@Entity
@Table(name = "code_word_participator")
public class Code_word_participator {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "participator_code_word_id")
    private Integer codeWordId;


    @Column(name = "code_word", nullable = false)
    private String codeWord;

    @ManyToOne
    @JoinColumn(name = "participator_id")
    private Participator participator;

    public Code_word_participator() {
    }

    public Code_word_participator(String codeWord, Participator participator) {
        if(codeWord == null){
            this.codeWord = "";
        }
        if(participator == null){
            System.out.println("EMPTY PARTICIPATOR WHEN TRIED TO CREATE " + codeWord);
        }
        this.codeWord = codeWord;
        this.participator = participator;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Code_word_participator code_wordParticipator1 = (Code_word_participator) o;
        if(this.getCodeWord().hashCode() != code_wordParticipator1.getCodeWord().hashCode()){
            return false;
        }
        return this.getCodeWord().equals(code_wordParticipator1.getCodeWord());

    }


    @Override
    public int hashCode() {
        if(codeWord == null){
            codeWord = "";
        }
        System.out.println("Trying to get hash code for string: " + codeWord);
        int result = 31 + codeWord.hashCode();
        result = 31 * result + (participator == null ? 31 :participator.hashCode());
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

    public Participator getParticipator() {
        return participator;
    }

    public void setParticipator(Participator participator) {
        this.participator = participator;
    }

}
