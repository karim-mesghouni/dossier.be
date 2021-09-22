package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.ReturnedComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ReturnedCommentRepository extends JpaRepository<ReturnedComment, Long> {
}
