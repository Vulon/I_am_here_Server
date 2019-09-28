package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Manager;
import com.I_am_here.Database.Entity.Participator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipatorRepository extends JpaRepository<Participator, Integer> {
    @Query
    Participator findByUUIDAndPassword(String UUID, String password);
    @Query
    Participator getByUUIDAndPassword(String UUID, String password);
}
