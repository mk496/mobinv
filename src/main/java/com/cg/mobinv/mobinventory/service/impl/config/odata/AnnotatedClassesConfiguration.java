package com.cg.mobinv.mobinventory.service.impl.config.odata;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import org.apache.olingo.odata2.annotation.processor.core.util.ClassHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.cg.mobinv.mobinventory.service.impl.config.odata.util.AnnotationHelper;
import com.cg.mobinv.mobinventory.service.impl.config.odata.util.ImportFunctionHolder;

/**
 * dirty stuff with reflection here
 */
@Service
public class AnnotatedClassesConfiguration {

	private final Logger logger = LoggerFactory.getLogger(AnnotatedClassesConfiguration.class);

	public final String MODEL_PACKAGE = "com.cg.mobinv.mobinventory.common.api.to";
	public final String FUNCTION_IMPORT_PACKAGE = "com.cg.mobinv.mobinventory.service.api";

	// TODO remove/refactor it ...
	final AnnotationHelper annotationHelper = new AnnotationHelper();

	private List<Class<?>> allClassesToLoad;
	private List<Class<?>> classesAnnotatedEntity;
	private List<Class<?>> classesAnnotatedFunctionImport;

	private Map<String, ImportFunctionHolder> functionImportHolders;
	@Autowired
	private ApplicationContext applicationContext;

	//do heavy reflection loading only once when springboot is starting
	@PostConstruct
	private void extractAllClasses() {
		allClassesToLoad = new ArrayList<Class<?>>();
		classesAnnotatedEntity = getAnnotatedClassesFromPackage(MODEL_PACKAGE);
		logger.info("All loaded entities :" + classesAnnotatedEntity);
		classesAnnotatedFunctionImport = getAnnotatedClassesFromPackage(FUNCTION_IMPORT_PACKAGE);
		logger.info("All loaded FunctionImports :" + classesAnnotatedFunctionImport);
		allClassesToLoad.addAll(classesAnnotatedEntity);
		allClassesToLoad.addAll(classesAnnotatedFunctionImport);

		initfunctionImportHolders();
	}

	private void initfunctionImportHolders() {

		functionImportHolders = new LinkedHashMap<String, ImportFunctionHolder>();

		for (Class<?> annotatedClass : classesAnnotatedFunctionImport) {
			List<Method> methods = this.annotationHelper.getAnnotatedMethods(annotatedClass,
					org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.class, false);
					Object functionImport = applicationContext.getBean(annotatedClass);
			for (Method method : methods) {
				ImportFunctionHolder funcHolder = ImportFunctionHolder.initFunctionHolder(functionImport, method);
				org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport efi = method
						.getAnnotation(org.apache.olingo.odata2.api.annotation.edm.EdmFunctionImport.class);
				String name = efi.name();
				this.functionImportHolders.put(name, funcHolder);
			}
		}
	}

	public List<Class<?>> getAllAnnotatedClasses() {
		return allClassesToLoad;
	}

	public List<Class<?>> getAnnotatedEntites() {
		return classesAnnotatedEntity;
	}

	public List<Class<?>> getAnnotatedFunctionImport() {
		return classesAnnotatedFunctionImport;
	}

	public Map<String, ImportFunctionHolder> getFunctionImportHolders() {
		return functionImportHolders;
	}

	private List<Class<?>> getAnnotatedClassesFromPackage(String p) {

		return ClassHelper.loadClasses(p, new ClassHelper.ClassValidator() {
			@Override
			public boolean isClassValid(final Class<?> c) {
				return annotationHelper.isEdmAnnotated(c);
			}
		});
	}
}
