package com.restuarant.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restuarant.app.entity.TableEntity;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long>{

}
