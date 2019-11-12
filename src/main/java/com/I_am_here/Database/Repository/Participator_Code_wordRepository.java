package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Code_word_participator;
import com.I_am_here.Database.Entity.Participator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface Participator_Code_wordRepository extends JpaRepository<Code_word_participator, Integer> {


    @Query
    @Transactional
    int deleteAllByCodeWordInAndParticipator(Set<String> code_words, Participator participator);
}
