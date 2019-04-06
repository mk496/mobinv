package com.cg.mobinv.mobinventory.service.impl.config.odata;

import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.mobinv.mobinventory.common.mappingbean.MappingDataStoreService;
import com.cg.mobinv.mobinventory.common.mappingbean.api.AbstractDataStore;
import com.cg.mobinv.mobinventory.service.impl.config.odata.util.ImportFunctionHolder;

/**
 *
 *
 */
@Service
public class PersistentDataSource implements CustomDataSource {

	@Autowired
	private AnnotatedClassesConfiguration annotatedClasses;

	@Autowired
	private MappingDataStoreService mappingDataStoreService;

	private final Logger logger = LoggerFactory.getLogger(PersistentDataSource.class);

	@Override
	public List<?> readData(EdmEntitySet entitySet)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

		this.logger.info("received read data request with param entitySet = " + entitySet.getName());

		AbstractDataStore<?, ?> ds = this.mappingDataStoreService.getDataStore(entitySet.getName());
		return ds.read();
	}

	@Override
	public Object readData(EdmEntitySet entitySet, Map<String, Object> keys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

		this.logger.info(
				"received read data request with param entitySet = " + entitySet.getName() + " and keys = " + keys);
		AbstractDataStore<?, ?> ds = this.mappingDataStoreService.getDataStore(entitySet.getName());
		Object result = ds.read(keys);
		return result;
	}

	@Override
	public Object readData(EdmFunctionImport function, Map<String, Object> parameters, Map<String, Object> keys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

		this.logger.info("received read data request with param function = " + function + " and parameters = "
				+ parameters + " and keys = " + keys);

		String functionImportName = function.getName();

		ImportFunctionHolder funcHolder = this.annotatedClasses.getFunctionImportHolders().get(functionImportName);
		return funcHolder.execute(parameters);
	}

	@Override
	public Object readRelatedData(EdmEntitySet sourceEntitySet, Object sourceData, EdmEntitySet targetEntitySet,
			Map<String, Object> targetKeys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

		AbstractDataStore<?, ?> sourceDs = this.mappingDataStoreService.getDataStore(sourceEntitySet.getName());
		AbstractDataStore<?, ?> targetDs = this.mappingDataStoreService.getDataStore(targetEntitySet.getName());
		Object result = targetDs.readRelatedData(sourceDs.getDataTypeClass(), sourceData, targetDs.getDataTypeClass(),
				targetKeys);

		return result;
	}

	@Override
	public BinaryData readBinaryData(EdmEntitySet entitySet, Object mediaLinkEntryData)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

		throw new ODataNotImplementedException(ODataNotImplementedException.COMMON);
	}

	@Override
	public Object newDataObject(EdmEntitySet entitySet)
			throws ODataNotImplementedException, EdmException, ODataApplicationException {

		this.logger.info("Received newDataObject request with paramater entitySet = " + entitySet);

		AbstractDataStore<?, ?> ds = this.mappingDataStoreService.getDataStore(entitySet.getName());
		return ds.createInstance();
	}

	@Override
	public void writeBinaryData(EdmEntitySet entitySet, Object mediaLinkEntryData, BinaryData binaryData)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

		throw new ODataNotImplementedException(ODataNotImplementedException.COMMON);
	}

	@Override
	public void deleteData(EdmEntitySet entitySet, Map<String, Object> keys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

		AbstractDataStore<?, ?> ds = this.mappingDataStoreService.getDataStore(entitySet.getName());
		ds.delete(keys);
	}

	@Override
	public void createData(EdmEntitySet entitySet, Object data)
			throws ODataNotImplementedException, EdmException, ODataApplicationException {

		this.logger.info("Received create data request with parameters entitySet = " + entitySet.getName()
				+ " and Object " + data);

		AbstractDataStore<?, ?> ds = this.mappingDataStoreService.getDataStore(entitySet.getName());
		ds.create(data);
	}

	@Override
	public void deleteRelation(EdmEntitySet sourceEntitySet, Object sourceData, EdmEntitySet targetEntitySet,
			Map<String, Object> targetKeys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

		throw new ODataNotImplementedException(ODataNotImplementedException.COMMON);
	}

	@Override
	public void writeRelation(EdmEntitySet sourceEntitySet, Object sourceData, EdmEntitySet targetEntitySet,
			Map<String, Object> targetKeys)
			throws ODataNotImplementedException, ODataNotFoundException, EdmException, ODataApplicationException {

		throw new ODataNotImplementedException(ODataNotImplementedException.COMMON);
	}

	@Override
	public Object customCreateData(EdmEntitySet entitySet, Object data)
			throws ODataNotImplementedException, EdmException, ODataApplicationException {

		this.logger.info("Received create data request with parameters entitySet: " + entitySet.getName()
				+ " and Object: " + data);

		AbstractDataStore<?, ?> ds = this.mappingDataStoreService.getDataStore(entitySet.getName());
		return ds.create(data);
	}

	@Override
	public Object customUpdateData(EdmEntitySet entitySet, Object data)
			throws ODataNotImplementedException, EdmException, ODataApplicationException {

		this.logger.info("Received update data request with parameters entitySet: " + entitySet.getName()
				+ " and Object: " + data);

		AbstractDataStore<?, ?> ds = this.mappingDataStoreService.getDataStore(entitySet.getName());
		return ds.update(data);
	}
}
