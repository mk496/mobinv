package com.cg.mobinv.mobinventory.logic.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cg.mobinv.mobinventory.common.api.to.MaterialTo;
import com.cg.mobinv.mobinventory.dataaccess.api.MaterialEntity;
import com.cg.mobinv.mobinventory.dataaccess.api.dao.MaterialEntityRepository;
import com.cg.mobinv.mobinventory.logic.api.MaterialLogic;
import com.google.common.collect.Lists;

/**
 * @author manjinsi
 */
@Component
public class MaterialLogicImpl implements MaterialLogic {

    @Inject
    private MaterialEntityRepository MaterialRepository;

   

  
  
  

    @Inject
    private Mapper mapper;

    private final Logger logger = LoggerFactory.getLogger(MaterialLogicImpl.class);

    @Override
    public List<MaterialTo> readAll() {

        List<MaterialEntity> queryResult = Lists.newArrayList(this.MaterialRepository.findAll());
        List<MaterialTo> resultMapped = queryResult.stream().map(x -> mapToTransferObject(x)).collect(Collectors.toList());

        return resultMapped;
    }

    @Override
    public MaterialTo readById(Long id) {

        return mapToTransferObject(this.MaterialRepository.findOne(id));
    }

    @Override
    public MaterialTo create(MaterialTo to) {

        MaterialEntity entity = mapToEntity(to);
        this.MaterialRepository.save(entity);
        return mapToTransferObject(entity);
    }

    @Override
    public MaterialTo createEmpty() {

        return new MaterialTo();
    }

    @Override
    public MaterialTo update(MaterialTo to) {

        MaterialEntity entity = mapToEntity(to);
        this.MaterialRepository.save(entity);
        return mapToTransferObject(entity);
    }

    @Transactional
    @Override
    public <S> List<S> readRelatedEntities(MaterialTo source, Class<S> targetClass) {

        List<S> result = null;
  
        return result;
    }

    @Override
    public <S> S readRelatedEntity(MaterialTo source, Class<S> targetClass) {

        S result = null;
          return result;
    }

    @Override
    public void deleteById(Long id) {

        this.MaterialRepository.delete(id);

    }

    private MaterialTo mapToTransferObject(MaterialEntity entity) {

        MaterialTo MaterialTo = null;
        if (entity != null) {
            MaterialTo = this.mapper.map(entity, MaterialTo.class);
        }
        return MaterialTo;
    }

    private MaterialEntity mapToEntity(MaterialTo to) {

        return this.mapper.map(to, MaterialEntity.class);
    }

    @Transactional
    @Override
    public MaterialTo setRelation(MaterialTo source, Object targetObject) {

        MaterialTo result = null;
      

        return result;
    }

}
