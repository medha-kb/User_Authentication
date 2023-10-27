package com.intuit.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.intuit.model.entity.PasswordResetToken;
import com.intuit.model.entity.User;

import java.util.Optional;

@Repository
public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Transactional
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.user.id = :userId")
    @Modifying
    void deleteByUserId(@Param("userId") Long userId);

}
