package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

    @Query
    Subject getBySubject_id(Integer id);
}
