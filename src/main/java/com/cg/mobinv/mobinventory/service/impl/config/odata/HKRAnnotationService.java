package com.cg.mobinv.mobinventory.service.impl.config.odata;

import javax.annotation.PostConstruct;

import org.apache.olingo.odata2.annotation.processor.core.ListsProcessor;
import org.apache.olingo.odata2.annotation.processor.core.datasource.AnnotationValueAccess;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.rt.RuntimeDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

@Service
public class HKRAnnotationService {

	@Autowired
	private AnnotatedClassesConfiguration annotatedClassesConfiguration;

	@Autowired
	private PersistentDataSource dataSource;
	
	private final Logger logger = LoggerFactory.getLogger(HKRAnnotationService.class);

	private ODataService annotationOdataService;

	@PostConstruct
	public void initializeODataService() {
		try {
			
			AnnotationEdmProviderExtension edmProvider = new AnnotationEdmProviderExtension(
					annotatedClassesConfiguration.getAllAnnotatedClasses());

			// Edm via Annotations and ListProcessor via AnnotationDS with
			// AnnotationsValueAccess
			annotationOdataService = RuntimeDelegate.createODataSingleProcessorService(edmProvider,
					new CustomListsProcessor(dataSource, new AnnotationValueAccess()));

			logger.info("ODataService initilialized with instance " + annotationOdataService);
		} catch (ODataApplicationException ex) {
			throw new RuntimeException("Exception during sample data generation.", ex);
		} catch (ODataException ex) {
			throw new RuntimeException("Exception during data source initialization generation.", ex);
		}
	}

	public ODataService getOdataService() {
		Preconditions.checkNotNull(annotationOdataService, "ODataService is not correctly initilialized");
		return annotationOdataService;
	}
}
