package com.cg.mobinv.mobinventory.logic.api;

import java.util.List;
//import com.cg.mobinv.mobinventory.common.api.to.EntityCounterTo;

/**
 * @author Szymon Kuc
 */
public interface LogicComponent<T> {

    List<T> readAll();

    T readById(Long id);

    T create(T to);

    T createEmpty();

    T update(T to);

    T setRelation(T source, Object targetObject);

    <S> List<S> readRelatedEntities(T source, Class<S> targetClass);

    <S> S readRelatedEntity(T source, Class<S> targetClass);

 //   void deleteById(Long id, String token, boolean logical);

	void deleteById(Long id);

//    EntityCounterTo countEntities(Long id, EntityCounterTo entityCounter, boolean first);

}