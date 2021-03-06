package com.example.zzpj.ranking;

import com.example.zzpj.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByFkUserIdAndFkSquadId(final long fkUserId, final long fkSquadId);
    List<Optional<Rate>> findAllByFkSquadId(final long fkSquadId);
    List<Optional<Rate>> findAllByFkUserId(final long fkUserId);
    Optional<Rate> findByFkUserId(final long fkUserId);
}
