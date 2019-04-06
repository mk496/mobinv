package com.cg.mobinv.mobinventory.logic.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cg.mobinv.mobinventory.common.api.to.TitleTo;
import com.cg.mobinv.mobinventory.dataaccess.api.TitleEntity;
import com.cg.mobinv.mobinventory.dataaccess.api.dao.TitleEntityRepository;
import com.cg.mobinv.mobinventory.logic.api.TitleLogic;
import com.google.common.collect.Lists;

/**
 * @author manjinsi
 */
@Component
public class TitleLogicImpl implements TitleLogic {

    @Inject
    private TitleEntityRepository titleRepository;

   

  
  
  

    @Inject
    private Mapper mapper;

    private final Logger logger = LoggerFactory.getLogger(TitleLogicImpl.class);

    @Override
    public List<TitleTo> readAll() {

        List<TitleEntity> queryResult = Lists.newArrayList(this.titleRepository.findAll());
        List<TitleTo> resultMapped = queryResult.stream().map(x -> mapToTransferObject(x)).collect(Collectors.toList());

        return resultMapped;
    }

    @Override
    public TitleTo readById(Long id) {

        return mapToTransferObject(this.titleRepository.findOne(id));
    }

    @Override
    public TitleTo create(TitleTo to) {

        TitleEntity entity = mapToEntity(to);
        this.titleRepository.save(entity);
        return mapToTransferObject(entity);
    }

    @Override
    public TitleTo createEmpty() {

        return new TitleTo();
    }

    @Override
    public TitleTo update(TitleTo to) {

        TitleEntity entity = mapToEntity(to);
        this.titleRepository.save(entity);
        return mapToTransferObject(entity);
    }

    @Transactional
    @Override
    public <S> List<S> readRelatedEntities(TitleTo source, Class<S> targetClass) {

        List<S> result = null;
  
        return result;
    }

    @Override
    public <S> S readRelatedEntity(TitleTo source, Class<S> targetClass) {

        S result = null;
          return result;
    }

    @Override
    public void deleteById(Long id) {

        this.titleRepository.delete(id);

    }

    private TitleTo mapToTransferObject(TitleEntity entity) {

        TitleTo titleTo = null;
        if (entity != null) {
            titleTo = this.mapper.map(entity, TitleTo.class);
        }
        return titleTo;
    }

    private TitleEntity mapToEntity(TitleTo to) {

        return this.mapper.map(to, TitleEntity.class);
    }

    @Transactional
    @Override
    public TitleTo setRelation(TitleTo source, Object targetObject) {

        TitleTo result = null;
      

        return result;
    }

}
