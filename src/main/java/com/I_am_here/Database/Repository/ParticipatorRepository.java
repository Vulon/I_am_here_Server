package com.I_am_here.Database.Repository;

import com.I_am_here.Database.Entity.Participator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipatorRepository extends JpaRepository<Participator, Integer> {
    @Query
    Participator findByUuidAndPassword(String UUID, String password);



    @Query
    Participator getByUuid(String UUID);

    @Query
    Participator getByUuidAndPassword(String UUID, String password);

    @Query
    Participator getByParticipatorId(Integer participator);


}
