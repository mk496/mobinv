package com.cg.mobinv.mobinventory.common.mappingbean;

import org.apache.olingo.odata2.api.annotation.edm.EdmEntitySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.mobinv.mobinventory.common.api.to.*;
import com.cg.mobinv.mobinventory.common.mappingbean.api.AbstractDataStore;
import com.cg.mobinv.mobinventory.common.mappingbean.impl.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class MappingDataStoreService {

    private final Logger logger = LoggerFactory.getLogger(MappingDataStoreService.class);

    @Autowired
    private MaterialDataStore titleDataStore;

    private Map<String, AbstractDataStore<?, ?>> edmEntityDataStoreLookup;

    @PostConstruct
    private void mapDataStores() {

        this.edmEntityDataStoreLookup = new HashMap<String, AbstractDataStore<?, ?>>();
        this.edmEntityDataStoreLookup.put(MaterialTo.class.getAnnotation(EdmEntitySet.class).name(), this.titleDataStore);
        this.logger.info("All loaded repos  :" + this.edmEntityDataStoreLookup);
    }

    public AbstractDataStore<?, ?> getDataStore(String entityToName) {

        this.logger.info(" quering for entityToName  :" + entityToName);
        return this.edmEntityDataStoreLookup.get(entityToName);
    }
}
