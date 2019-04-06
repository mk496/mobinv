package com.cg.mobinv.mobinventory.service.impl.config.odata;

import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnnotationODataServiceFactory extends ODataServiceFactory {

	@Autowired
	HKRAnnotationService hkrAnnotationService;

	@Override
	public ODataService createService(final ODataContext context) throws ODataException {
		
		return hkrAnnotationService.getOdataService();
	}
}