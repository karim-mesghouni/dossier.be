package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.DescriptionComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface DescriptionCommentRepository extends JpaRepository<DescriptionComment, Long> {
}
