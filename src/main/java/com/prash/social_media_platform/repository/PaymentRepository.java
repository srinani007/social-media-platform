package com.prash.social_media_platform.repository;

import com.prash.social_media_platform.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
     //List<Payment> findByUserId(Long userId);
}
