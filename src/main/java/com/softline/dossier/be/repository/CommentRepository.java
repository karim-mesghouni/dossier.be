package com.softline.dossier.be.repository;

import com.softline.dossier.be.domain.Agent;
import com.softline.dossier.be.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findAllByFileActivity_File_Id(Long fileId);
}
