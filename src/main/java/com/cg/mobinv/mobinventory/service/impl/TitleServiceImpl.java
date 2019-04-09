package com.cg.mobinv.mobinventory.service.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cg.mobinv.mobinventory.common.api.to.MaterialTo;
import com.cg.mobinv.mobinventory.logic.api.MaterialLogic;
import com.cg.mobinv.mobinventory.service.api.TitleService;

/**
 * @author manjinsi
 *
 */
@Service
public class TitleServiceImpl implements TitleService {

  private final String KEY = "Id";

  @Inject
  MaterialLogic titleLogic;

  @Override
  public MaterialTo createEntity(MaterialTo to) {

    return this.titleLogic.create(to);
  }

  @Override
  public MaterialTo createEmptyEntity() {

    return this.titleLogic.createEmpty();
  }

  @Override
  public MaterialTo updateEntity(MaterialTo to) {

    return this.titleLogic.update(to);
  }

  @Override
  public void deleteEntity(Map<String, Object> keys) {

    this.titleLogic.deleteById((Long) keys.get(this.KEY));

  }

  @Override
  public List<MaterialTo> readAllEntities() {

    return this.titleLogic.readAll();
  }

  @Override
  public MaterialTo readEntity(Map<String, Object> keys) {

    return this.titleLogic.readById((Long) keys.get(this.KEY));
  }

  @Override
    public <S> List<S> readRelatedEntities(MaterialTo source, Class<S> targetClass) {

        return this.titleLogic.readRelatedEntities(source, targetClass);
  }

  @Override
    public <S> S readRelatedEntity(MaterialTo source, Class<S> targetClass) {
  	
        return this.titleLogic.readRelatedEntity(source, targetClass);
  }
  
  @Override
  public Class<?> getToClassType() {

    return MaterialTo.class;
  }

@Override
public MaterialTo setRelation(MaterialTo source, Object nestedObject) {
	
	return this.titleLogic.setRelation(source, nestedObject);
}
}
