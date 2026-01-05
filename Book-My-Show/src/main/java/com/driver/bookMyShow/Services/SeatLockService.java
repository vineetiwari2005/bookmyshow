package com.driver.bookMyShow.Services;

import com.driver.bookMyShow.Enums.SeatLockStatus;
import com.driver.bookMyShow.Models.SeatLock;
import com.driver.bookMyShow.Models.Show;
import com.driver.bookMyShow.Models.User;
import com.driver.bookMyShow.Repositories.SeatLockRepository;
import com.driver.bookMyShow.Repositories.ShowRepository;
import com.driver.bookMyShow.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SeatLockService - Manages seat locking for race-condition-safe booking
 * 
 * Features:
 * - Temporary seat locking (10-minute expiry)
 * - Automatic lock release on timeout
 * - Prevention of double-booking
 * - Session-based lock management
 */
@Service
public class SeatLockService {

    @Autowired
    private SeatLockRepository seatLockRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    // Lock duration: 10 minutes
    private static final int LOCK_DURATION_MINUTES = 10;

    // Max seats per transaction
    private static final int MAX_SEATS_PER_USER = 10;

    /**
     * Lock seats for a user
     * Returns session ID if successful, throws exception if seats unavailable
     */
    @Transactional
    public String lockSeats(Integer showId, Integer userId, List<String> seatNumbers) 
            throws Exception {
        
        // Validate show exists
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new Exception("Show not found"));

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Validate seat count
        if (seatNumbers.size() > MAX_SEATS_PER_USER) {
            throw new Exception("Cannot lock more than " + MAX_SEATS_PER_USER + " seats at once");
        }

        // Release expired locks first
        releaseExpiredLocks();

        // Check if seats are available
        LocalDateTime now = LocalDateTime.now();
        for (String seatNumber : seatNumbers) {
            Optional<SeatLock> existingLock = seatLockRepository.findActiveLock(
                    showId, seatNumber, now);
            
            if (existingLock.isPresent()) {
                throw new Exception("Seat " + seatNumber + " is already locked by another user");
            }
        }

        // Create session ID for this booking attempt
        String sessionId = UUID.randomUUID().toString();

        // Lock all seats
        LocalDateTime expiryTime = now.plusMinutes(LOCK_DURATION_MINUTES);
        List<SeatLock> locks = new ArrayList<>();

        for (String seatNumber : seatNumbers) {
            SeatLock lock = SeatLock.builder()
                    .show(show)
                    .seatNumber(seatNumber)
                    .user(user)
                    .lockTime(now)
                    .expiryTime(expiryTime)
                    .status(SeatLockStatus.LOCKED)
                    .sessionId(sessionId)
                    .build();
            locks.add(lock);
        }

        seatLockRepository.saveAll(locks);
        return sessionId;
    }

    /**
     * Release locks for a session (on payment failure or user cancellation)
     */
    @Transactional
    public void releaseLocks(String sessionId) {
        List<SeatLock> locks = seatLockRepository.findBySessionId(sessionId);
        locks.forEach(lock -> {
            if (lock.getStatus() == SeatLockStatus.LOCKED) {
                lock.setStatus(SeatLockStatus.RELEASED);
            }
        });
        seatLockRepository.saveAll(locks);
    }

    /**
     * Confirm locks for a session (after successful payment)
     */
    @Transactional
    public void confirmLocks(String sessionId) {
        List<SeatLock> locks = seatLockRepository.findBySessionId(sessionId);
        locks.forEach(lock -> {
            if (lock.getStatus() == SeatLockStatus.LOCKED) {
                lock.setStatus(SeatLockStatus.CONFIRMED);
            }
        });
        seatLockRepository.saveAll(locks);
    }

    /**
     * Get remaining time for locks in a session (in seconds)
     */
    public Long getRemainingTime(String sessionId) {
        List<SeatLock> locks = seatLockRepository.findBySessionId(sessionId);
        if (locks.isEmpty()) {
            return 0L;
        }

        SeatLock firstLock = locks.get(0);
        if (firstLock.getStatus() != SeatLockStatus.LOCKED) {
            return 0L;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = firstLock.getExpiryTime();
        
        if (now.isAfter(expiryTime)) {
            return 0L;
        }

        return java.time.Duration.between(now, expiryTime).getSeconds();
    }

    /**
     * Get list of locked seats for a show
     */
    public List<String> getLockedSeatsForShow(Integer showId) {
        LocalDateTime now = LocalDateTime.now();
        List<SeatLock> activeLocks = seatLockRepository.findActiveLocksForShow(showId, now);
        return activeLocks.stream()
                .map(SeatLock::getSeatNumber)
                .collect(Collectors.toList());
    }

    /**
     * Check if specific seats are available
     */
    public Map<String, Boolean> checkSeatsAvailability(Integer showId, List<String> seatNumbers) {
        releaseExpiredLocks();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Boolean> availability = new HashMap<>();

        for (String seatNumber : seatNumbers) {
            Optional<SeatLock> lock = seatLockRepository.findActiveLock(showId, seatNumber, now);
            availability.put(seatNumber, lock.isEmpty());
        }

        return availability;
    }

    /**
     * Scheduled task to release expired locks
     * Runs every 2 minutes
     */
    @Scheduled(fixedRate = 120000) // 2 minutes
    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        int releasedCount = seatLockRepository.releaseExpiredLocks(now);
        if (releasedCount > 0) {
            System.out.println("Released " + releasedCount + " expired seat locks");
        }
    }

    /**
     * Extend lock time for a session (if user needs more time)
     */
    @Transactional
    public void extendLockTime(String sessionId, int additionalMinutes) throws Exception {
        List<SeatLock> locks = seatLockRepository.findBySessionId(sessionId);
        
        if (locks.isEmpty()) {
            throw new Exception("No locks found for session");
        }

        LocalDateTime now = LocalDateTime.now();
        for (SeatLock lock : locks) {
            if (lock.getStatus() == SeatLockStatus.LOCKED && lock.getExpiryTime().isAfter(now)) {
                lock.setExpiryTime(lock.getExpiryTime().plusMinutes(additionalMinutes));
            }
        }

        seatLockRepository.saveAll(locks);
    }
}
