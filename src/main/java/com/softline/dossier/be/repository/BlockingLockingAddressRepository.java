package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.BlockingLockingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface BlockingLockingAddressRepository extends JpaRepository<BlockingLockingAddress, Long> {
}
