package com.cg.mobinv.mobinventory.common.mappingbean.impl;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.cg.mobinv.mobinventory.common.api.to.MaterialTo;
import com.cg.mobinv.mobinventory.common.mappingbean.api.AbstractDataStore;
import com.cg.mobinv.mobinventory.common.mappingbean.api.AbstractDataStoreBean;
import com.cg.mobinv.mobinventory.dataaccess.api.MaterialEntity;
import com.cg.mobinv.mobinventory.dataaccess.api.dao.MaterialEntityRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author manjinsi
 */
@Service
public class MaterialDataStore extends AbstractDataStoreBean<MaterialTo, MaterialEntity, Long>
        implements AbstractDataStore<MaterialTo, MaterialEntity> {

    public static final String KEY = "Id";

    @Autowired
    private Mapper mapper;

    @Autowired
    private MaterialEntityRepository materialEntityRepository;

    @Override
    public Class<?> getDataTypeClass() {

        return MaterialTo.class;
    }

    @Override
    public Class<?> getEntityClass() {

        return MaterialEntity.class;
    }

    @Override
    protected CrudRepository<MaterialEntity, Long> getDao() {

        return this.materialEntityRepository;
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
    public MaterialEntity mapEdmObjectToJpaEntity(MaterialTo object) {

        return mapper.map(object, MaterialEntity.class);
    }

    @Override
    public MaterialTo mapJpaEntityToEdmObject(MaterialEntity object) {
//        object.setActivityGroup(null);
//        object.setAmplificationMemoMaterials(null);
//        object.setAppropriationMemoMaterials(null);
//        object.setAttributeMatchings(null);
//        object.setBudgetMemoMaterials(null);
//        object.setCommitmentAuthorizations(null);
//        object.setFileMatchings(null);
//        object.setFinancings(null);
    	MaterialTo tos = new MaterialTo();
    	tos.setId(object.getId());
    	tos.setKeyFigure(tos.getKeyFigure());
    	tos.setLongIdentifier(object.getLongIdentifier());
    	tos.setReasonDisputable(object.getReasonDisputable());
    	tos.setShortIdentifier(object.getShortIdentifier());
//        return mapper.map(object, MaterialTo.class);
    	return tos;
    }

    @Override
    public Object readRelatedData(Class<?> relatedTypeClass, Object relatedObject) {
        Object result = null;

//        if (ChapterTo.class == relatedTypeClass) {
//            List<MaterialEntity> rounds = this.MaterialEntityRepository.findByChapter_Id(((ChapterTo) relatedObject).getId());
//            result = rounds.stream().map(x -> mapJpaEntityToEdmObject(x)).collect(Collectors.toList());
//        }
        return result;
    }

}
