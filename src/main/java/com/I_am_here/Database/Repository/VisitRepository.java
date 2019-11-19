package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Host;
import com.I_am_here.Database.Entity.Subject;
import com.I_am_here.Database.Entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface VisitRepository extends JpaRepository<Visit, Integer> {

    @Query
    Set<Visit> getAllByHostAndAndSubject(Host host, Subject subject);
}
