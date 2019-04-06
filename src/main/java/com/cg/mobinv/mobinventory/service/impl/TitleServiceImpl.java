package com.cg.mobinv.mobinventory.service.impl;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.cg.mobinv.mobinventory.common.api.to.TitleTo;
import com.cg.mobinv.mobinventory.logic.api.TitleLogic;
import com.cg.mobinv.mobinventory.service.api.TitleService;

/**
 * @author manjinsi
 *
 */
@Service
public class TitleServiceImpl implements TitleService {

  private final String KEY = "Id";

  @Inject
  TitleLogic titleLogic;

  @Override
  public TitleTo createEntity(TitleTo to) {

    return this.titleLogic.create(to);
  }

  @Override
  public TitleTo createEmptyEntity() {

    return this.titleLogic.createEmpty();
  }

  @Override
  public TitleTo updateEntity(TitleTo to) {

    return this.titleLogic.update(to);
  }

  @Override
  public void deleteEntity(Map<String, Object> keys) {

    this.titleLogic.deleteById((Long) keys.get(this.KEY));

  }

  @Override
  public List<TitleTo> readAllEntities() {

    return this.titleLogic.readAll();
  }

  @Override
  public TitleTo readEntity(Map<String, Object> keys) {

    return this.titleLogic.readById((Long) keys.get(this.KEY));
  }

  @Override
    public <S> List<S> readRelatedEntities(TitleTo source, Class<S> targetClass) {

        return this.titleLogic.readRelatedEntities(source, targetClass);
  }

  @Override
    public <S> S readRelatedEntity(TitleTo source, Class<S> targetClass) {
  	
        return this.titleLogic.readRelatedEntity(source, targetClass);
  }
  
  @Override
  public Class<?> getToClassType() {

    return TitleTo.class;
  }

@Override
public TitleTo setRelation(TitleTo source, Object nestedObject) {
	
	return this.titleLogic.setRelation(source, nestedObject);
}
}
