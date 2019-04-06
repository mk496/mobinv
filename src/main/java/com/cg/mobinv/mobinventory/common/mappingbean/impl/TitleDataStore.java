package com.cg.mobinv.mobinventory.common.mappingbean.impl;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.cg.mobinv.mobinventory.common.api.to.TitleTo;
import com.cg.mobinv.mobinventory.common.mappingbean.api.AbstractDataStore;
import com.cg.mobinv.mobinventory.common.mappingbean.api.AbstractDataStoreBean;
import com.cg.mobinv.mobinventory.dataaccess.api.TitleEntity;
import com.cg.mobinv.mobinventory.dataaccess.api.dao.TitleEntityRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author manjinsi
 */
@Service
public class TitleDataStore extends AbstractDataStoreBean<TitleTo, TitleEntity, Long>
        implements AbstractDataStore<TitleTo, TitleEntity> {

    public static final String KEY = "Id";

    @Autowired
    private Mapper mapper;

    @Autowired
    private TitleEntityRepository titleEntityRepository;

    @Override
    public Class<?> getDataTypeClass() {

        return TitleTo.class;
    }

    @Override
    public Class<?> getEntityClass() {

        return TitleEntity.class;
    }

    @Override
    protected CrudRepository<TitleEntity, Long> getDao() {

        return this.titleEntityRepository;
    }

    @Override
    public List<Class<?>> getRelatedEdmEntityTypeClasses() {

        return Arrays.asList(null);

    }

    @Override
    protected Long mapKeyMapToJpaEntityKey(Map<String, Object> keyMap) {

        assertKeyMapContainsKeys(keyMap, Arrays.asList(KEY));
        Long result = (Long) keyMap.get(KEY);
        return result;

    }

    @Override
    public TitleEntity mapEdmObjectToJpaEntity(TitleTo object) {

        return mapper.map(object, TitleEntity.class);
    }

    @Override
    public TitleTo mapJpaEntityToEdmObject(TitleEntity object) {
//        object.setActivityGroup(null);
//        object.setAmplificationMemoTitles(null);
//        object.setAppropriationMemoTitles(null);
//        object.setAttributeMatchings(null);
//        object.setBudgetMemoTitles(null);
//        object.setCommitmentAuthorizations(null);
//        object.setFileMatchings(null);
//        object.setFinancings(null);
    	TitleTo tos = new TitleTo();
    	tos.setId(object.getId());
    	tos.setKeyFigure(tos.getKeyFigure());
    	tos.setLongIdentifier(object.getLongIdentifier());
    	tos.setReasonDisputable(object.getReasonDisputable());
    	tos.setShortIdentifier(object.getShortIdentifier());
//        return mapper.map(object, TitleTo.class);
    	return tos;
    }

    @Override
    public Object readRelatedData(Class<?> relatedTypeClass, Object relatedObject) {
        Object result = null;

//        if (ChapterTo.class == relatedTypeClass) {
//            List<TitleEntity> rounds = this.titleEntityRepository.findByChapter_Id(((ChapterTo) relatedObject).getId());
//            result = rounds.stream().map(x -> mapJpaEntityToEdmObject(x)).collect(Collectors.toList());
//        }
        return result;
    }

}
