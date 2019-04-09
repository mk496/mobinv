package com.cg.mobinv.mobinventory.dataaccess.api.dao;

import org.springframework.data.repository.CrudRepository;

import com.cg.mobinv.mobinventory.dataaccess.api.MaterialEntity;

import java.util.List;

/**
 * @author manjinsi
 *
 */
public interface MaterialEntityRepository extends CrudRepository<MaterialEntity, Long> {

}
