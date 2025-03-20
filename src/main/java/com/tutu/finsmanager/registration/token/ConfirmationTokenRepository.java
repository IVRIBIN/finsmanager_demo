package com.tutu.finsmanager.registration.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepository
        extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    List<ConfirmationToken> findByAppUserId(Integer AppUserId);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " +
            "SET c.confirmedAt = ?2 " +
            "WHERE c.token = ?1")
    int updateConfirmedAt(String token,
                          LocalDateTime confirmedAt);
    @Query(value = "select * from confirmation_token c where c.app_user_id = ?1 and c.token_type = 'recovery' and c.expires_at >= current_date", nativeQuery = true)
    List<ConfirmationToken> getActiveRecovery(Integer AppUserId);

    @Query(value = "select * from confirmation_token c where c.app_user_id = ?1 and c.token_type = 'recovery'", nativeQuery = true)
    List<ConfirmationToken> getRecovery(Integer AppUserId);

    /*
    @Transactional
    @Modifying
    @Query("DELETE from ConfirmationToken where app_user_id = ?1 and token_type = 'recovery' and expires_at < current_timestamp")
    int deleteOldRecovery(Integer AppUserId);

     */
}

