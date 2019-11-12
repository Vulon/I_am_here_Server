package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Code_word_host;
import com.I_am_here.Database.Entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface Host_Code_wordRepository extends JpaRepository<Code_word_host, Integer> {

    @Query
    @Transactional
    int deleteAllByCodeWordInAndHost(Set<String> code_words, Host host);

}
