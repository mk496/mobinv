package com.cg.mobinv.mobinventory.service.impl.config.odata;

import org.apache.olingo.odata2.annotation.processor.core.datasource.DataSource;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;

public interface CustomDataSource extends DataSource {

	Object customCreateData(EdmEntitySet entitySet, Object data)
			throws ODataNotImplementedException, EdmException, ODataApplicationException;

	Object customUpdateData(EdmEntitySet entitySet, Object data)
			throws ODataNotImplementedException, EdmException, ODataApplicationException;
}
