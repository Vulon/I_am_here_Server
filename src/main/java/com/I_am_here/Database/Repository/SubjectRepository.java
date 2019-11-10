package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Set;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    @Query
    Subject getBySubjectId(Integer id);

    @Query
    Set<Subject> getAllByStartDateBeforeAndFinishDateAfter(Date date, Date date_again);

    @Query
    Set<Subject> getAllByBroadcastWord(String code);

    @Query
    Set<Subject> getAllByBroadcastWordIn(Set<String> code_words);
}
