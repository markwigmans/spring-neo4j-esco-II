package com.btb.sne.model.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface J_EntityRepository extends JpaRepository<J_Entity, String> {
}
