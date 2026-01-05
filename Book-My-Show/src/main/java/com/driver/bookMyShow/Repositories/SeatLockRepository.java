package com.driver.bookMyShow.Repositories;

import com.driver.bookMyShow.Enums.SeatLockStatus;
import com.driver.bookMyShow.Models.SeatLock;
import com.driver.bookMyShow.Models.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SeatLockRepository - Data access for seat lock operations
 */
@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Integer> {

    /**
     * Find active lock for a specific seat in a show
     */
    @Query("SELECT sl FROM SeatLock sl WHERE sl.show.showId = :showId " +
           "AND sl.seatNumber = :seatNumber " +
           "AND sl.status = 'LOCKED' " +
           "AND sl.expiryTime > :currentTime")
    Optional<SeatLock> findActiveLock(@Param("showId") Integer showId,
                                       @Param("seatNumber") String seatNumber,
                                       @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find all active locks for a show
     */
    @Query("SELECT sl FROM SeatLock sl WHERE sl.show.showId = :showId " +
           "AND sl.status = 'LOCKED' " +
           "AND sl.expiryTime > :currentTime")
    List<SeatLock> findActiveLocksForShow(@Param("showId") Integer showId,
                                           @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find all locks for a user in a specific show
     */
    List<SeatLock> findByShowAndUser(Show show, com.driver.bookMyShow.Models.User user);

    /**
     * Find locks by session ID
     */
    List<SeatLock> findBySessionId(String sessionId);

    /**
     * Find expired locks
     */
    @Query("SELECT sl FROM SeatLock sl WHERE sl.status = 'LOCKED' " +
           "AND sl.expiryTime <= :currentTime")
    List<SeatLock> findExpiredLocks(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Release expired locks (bulk update)
     */
    @Modifying
    @Query("UPDATE SeatLock sl SET sl.status = 'RELEASED' " +
           "WHERE sl.status = 'LOCKED' " +
           "AND sl.expiryTime <= :currentTime")
    int releaseExpiredLocks(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Count active locks for a user
     */
    @Query("SELECT COUNT(sl) FROM SeatLock sl WHERE sl.user.id = :userId " +
           "AND sl.status = 'LOCKED' " +
           "AND sl.expiryTime > :currentTime")
    long countActiveLocksForUser(@Param("userId") Integer userId,
                                  @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find lock by session and seat
     */
    Optional<SeatLock> findBySessionIdAndSeatNumber(String sessionId, String seatNumber);
}
