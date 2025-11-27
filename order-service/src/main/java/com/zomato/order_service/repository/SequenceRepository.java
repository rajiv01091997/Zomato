package com.zomato.order_service.repository;

import com.zomato.order_service.entity.Sequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SequenceRepository extends JpaRepository<Sequence, Long> {
}
