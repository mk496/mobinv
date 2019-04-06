package com.cg.mobinv.mobinventory.service.impl.config.odata;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.olingo.odata2.annotation.processor.core.ListsProcessor;
import org.apache.olingo.odata2.annotation.processor.core.datasource.DataSource.BinaryData;
import org.apache.olingo.odata2.annotation.processor.core.datasource.ValueAccess;
import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.batch.BatchRequestPart;
import org.apache.olingo.odata2.api.batch.BatchResponsePart;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmConcurrencyMode;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmFunctionImport;
import org.apache.olingo.odata2.api.edm.EdmLiteral;
import org.apache.olingo.odata2.api.edm.EdmLiteralKind;
import org.apache.olingo.odata2.api.edm.EdmMapping;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmSimpleType;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeException;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;
import org.apache.olingo.odata2.api.edm.EdmStructuralType;
import org.apache.olingo.odata2.api.edm.EdmTypeKind;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderBatchProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.callback.OnWriteEntryContent;
import org.apache.olingo.odata2.api.ep.callback.OnWriteFeedContent;
import org.apache.olingo.odata2.api.ep.callback.WriteCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteEntryCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteEntryCallbackResult;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackResult;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataBadRequestException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.ExpandSelectTreeNode;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.CommonExpression;
import org.apache.olingo.odata2.api.uri.expression.ExpressionKind;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.LiteralExpression;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodExpression;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;
import org.apache.olingo.odata2.api.uri.expression.UnaryExpression;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;

public class CustomListsProcessor extends ListsProcessor {

	private CustomDataSource customDataSource;

	public CustomListsProcessor(final CustomDataSource dataSource, final ValueAccess valueAccess) {
		super(dataSource, valueAccess);
		customDataSource = dataSource;
	}

	@Override
	public ODataResponse createEntity(final PostUriInfo uriInfo, final InputStream content,
			final String requestContentType, final String contentType) throws ODataException {
		final EdmEntitySet entitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType entityType = entitySet.getEntityType();

		Object data = customDataSource.newDataObject(entitySet);
		ExpandSelectTreeNode expandSelectTree = null;

		if (entityType.hasStream()) {
			customDataSource.customCreateData(entitySet, data);
			customDataSource.writeBinaryData(entitySet, data,
					new BinaryData(EntityProvider.readBinary(content), requestContentType));

		} else {
			final EntityProviderReadProperties properties = EntityProviderReadProperties.init().mergeSemantic(false)
					.addTypeMappings(getStructuralTypeTypeMap(data, entityType)).build();
			final ODataEntry entryValues = parseEntry(entitySet, content, requestContentType, properties);

			setStructuralTypeValuesFromMap(data, entityType, entryValues.getProperties(), false);
			data = customDataSource.customCreateData(entitySet, data);
			createInlinedEntities(entitySet, data, entryValues);
			expandSelectTree = entryValues.getExpandSelectTree();
		}

		// Link back to the entity the target entity set is related to, if any.
		final List<NavigationSegment> navigationSegments = uriInfo.getNavigationSegments();
		if (!navigationSegments.isEmpty()) {
			final List<NavigationSegment> previousSegments = navigationSegments.subList(0,
					navigationSegments.size() - 1);
			final Object sourceData = retrieveData(uriInfo.getStartEntitySet(), uriInfo.getKeyPredicates(),
					uriInfo.getFunctionImport(), mapFunctionParameters(uriInfo.getFunctionImportParameters()),
					previousSegments);
			final EdmEntitySet previousEntitySet = previousSegments.isEmpty() ? uriInfo.getStartEntitySet()
					: previousSegments.get(previousSegments.size() - 1).getEntitySet();
			customDataSource.writeRelation(previousEntitySet, sourceData, entitySet,
					getStructuralTypeValueMap(data, entityType));
		}

		return ODataResponse.fromResponse(writeEntry(uriInfo.getTargetEntitySet(), expandSelectTree, data, contentType))
				.eTag(constructETag(entitySet, data)).build();
	}

	@Override
	public ODataResponse updateEntity(final PutMergePatchUriInfo uriInfo, final InputStream content,
			final String requestContentType, final boolean merge, final String contentType) throws ODataException {
		Object data = retrieveData(uriInfo.getStartEntitySet(), uriInfo.getKeyPredicates(), uriInfo.getFunctionImport(),
				mapFunctionParameters(uriInfo.getFunctionImportParameters()), uriInfo.getNavigationSegments());

		if (!appliesFilter(data, uriInfo.getFilter())) {
			throw new ODataNotFoundException(ODataNotFoundException.ENTITY);
		}

		ExpandSelectTreeNode expandSelectTree = null;
		final EdmEntitySet entitySet = uriInfo.getTargetEntitySet();
		final EdmEntityType entityType = entitySet.getEntityType();
		final EntityProviderReadProperties properties = EntityProviderReadProperties.init().mergeSemantic(merge)
				.addTypeMappings(getStructuralTypeTypeMap(data, entityType)).build();
		final ODataEntry entryValues = parseEntry(entitySet, content, requestContentType, properties);

		setStructuralTypeValuesFromMap(data, entityType, entryValues.getProperties(), merge);
		data = customDataSource.customUpdateData(entitySet, data);
		createInlinedEntities(entitySet, data, entryValues);
		expandSelectTree = entryValues.getExpandSelectTree();

		// TODO: Investigate why the empty response is returned
		return ODataResponse.fromResponse(writeEntry(uriInfo.getTargetEntitySet(), expandSelectTree, data, contentType))
				.eTag(constructETag(entitySet, data)).build();
	}

	private static Map<String, Object> mapKey(final List<KeyPredicate> keys) throws EdmException {
		Map<String, Object> keyMap = new HashMap<String, Object>();
		for (final KeyPredicate key : keys) {
			final EdmProperty property = key.getProperty();
			final EdmSimpleType type = (EdmSimpleType) property.getType();
			keyMap.put(property.getName(), type.valueOfString(key.getLiteral(), EdmLiteralKind.DEFAULT,
					property.getFacets(), type.getDefaultType()));
		}
		return keyMap;
	}

	private static Map<String, Object> mapFunctionParameters(final Map<String, EdmLiteral> functionImportParameters)
			throws EdmSimpleTypeException {
		if (functionImportParameters == null) {
			return Collections.emptyMap();
		} else {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			for (final Entry<String, EdmLiteral> parameter : functionImportParameters.entrySet()) {
				final EdmLiteral literal = parameter.getValue();
				final EdmSimpleType type = literal.getType();
				parameterMap.put(parameter.getKey(),
						type.valueOfString(literal.getLiteral(), EdmLiteralKind.DEFAULT, null, type.getDefaultType()));
			}
			return parameterMap;
		}
	}

	private Object retrieveData(final EdmEntitySet startEntitySet, final List<KeyPredicate> keyPredicates,
			final EdmFunctionImport functionImport, final Map<String, Object> functionImportParameters,
			final List<NavigationSegment> navigationSegments) throws ODataException {
		Object data;
		final Map<String, Object> keys = mapKey(keyPredicates);

		ODataContext context = getContext();
		final int timingHandle = context.startRuntimeMeasurement(getClass().getSimpleName(), "retrieveData");

		try {
			data = functionImport == null
					? keys.isEmpty() ? dataSource.readData(startEntitySet) : dataSource.readData(startEntitySet, keys)
					: dataSource.readData(functionImport, functionImportParameters, keys);

			EdmEntitySet currentEntitySet = functionImport == null ? startEntitySet : functionImport.getEntitySet();
			for (NavigationSegment navigationSegment : navigationSegments) {
				data = dataSource.readRelatedData(currentEntitySet, data, navigationSegment.getEntitySet(),
						mapKey(navigationSegment.getKeyPredicates()));
				currentEntitySet = navigationSegment.getEntitySet();
			}
		} finally {
			context.stopRuntimeMeasurement(timingHandle);
		}
		return data;
	}

	private <T> String constructETag(final EdmEntitySet entitySet, final T data) throws ODataException {
		final EdmEntityType entityType = entitySet.getEntityType();
		String eTag = null;
		for (final String propertyName : entityType.getPropertyNames()) {
			final EdmProperty property = (EdmProperty) entityType.getProperty(propertyName);
			if (property.getFacets() != null && property.getFacets().getConcurrencyMode() == EdmConcurrencyMode.Fixed) {
				final EdmSimpleType type = (EdmSimpleType) property.getType();
				final String component = type.valueToString(valueAccess.getPropertyValue(data, property),
						EdmLiteralKind.DEFAULT, property.getFacets());
				eTag = eTag == null ? component : eTag + Edm.DELIMITER + component;
			}
		}
		return eTag == null ? null : "W/\"" + eTag + "\"";
	}

	private <T> Map<String, ODataCallback> getCallbacks(final T data, final EdmEntityType entityType)
			throws EdmException {
		final List<String> navigationPropertyNames = entityType.getNavigationPropertyNames();
		if (navigationPropertyNames.isEmpty()) {
			return null;
		} else {
			final WriteCallback callback = new WriteCallback(data);
			Map<String, ODataCallback> callbacks = new HashMap<String, ODataCallback>();
			for (final String name : navigationPropertyNames) {
				callbacks.put(name, callback);
			}
			return callbacks;
		}
	}

	private class WriteCallback implements OnWriteEntryContent, OnWriteFeedContent {
		private final Object data;

		private <T> WriteCallback(final T data) {
			this.data = data;
		}

		@Override
		public WriteFeedCallbackResult retrieveFeedResult(final WriteFeedCallbackContext context)
				throws ODataApplicationException {
			try {
				final EdmEntityType entityType = context.getSourceEntitySet()
						.getRelatedEntitySet(context.getNavigationProperty()).getEntityType();
				List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();
				Object relatedData = null;
				try {
					relatedData = readRelatedData(context);
					for (final Object entryData : (List<?>) relatedData) {
						values.add(getStructuralTypeValueMap(entryData, entityType));
					}
				} catch (final ODataNotFoundException e) {
					values.clear();
				}
				WriteFeedCallbackResult result = new WriteFeedCallbackResult();
				result.setFeedData(values);
				EntityProviderWriteProperties inlineProperties = EntityProviderWriteProperties
						.serviceRoot(getContext().getPathInfo().getServiceRoot())
						.callbacks(getCallbacks(relatedData, entityType))
						.expandSelectTree(context.getCurrentExpandSelectTreeNode()).selfLink(context.getSelfLink())
						.build();
				result.setInlineProperties(inlineProperties);
				return result;
			} catch (final ODataException e) {
				throw new ODataApplicationException(e.getLocalizedMessage(), Locale.ROOT, e);
			}
		}

		@Override
		public WriteEntryCallbackResult retrieveEntryResult(final WriteEntryCallbackContext context)
				throws ODataApplicationException {
			try {
				final EdmEntityType entityType = context.getSourceEntitySet()
						.getRelatedEntitySet(context.getNavigationProperty()).getEntityType();
				WriteEntryCallbackResult result = new WriteEntryCallbackResult();
				Object relatedData;
				try {
					relatedData = readRelatedData(context);
				} catch (final ODataNotFoundException e) {
					relatedData = null;
				}

				if (relatedData == null) {
					result.setEntryData(Collections.<String, Object>emptyMap());
				} else {
					result.setEntryData(getStructuralTypeValueMap(relatedData, entityType));

					EntityProviderWriteProperties inlineProperties = EntityProviderWriteProperties
							.serviceRoot(getContext().getPathInfo().getServiceRoot())
							.callbacks(getCallbacks(relatedData, entityType))
							.expandSelectTree(context.getCurrentExpandSelectTreeNode()).build();
					result.setInlineProperties(inlineProperties);
				}
				return result;
			} catch (final ODataException e) {
				throw new ODataApplicationException(e.getLocalizedMessage(), Locale.ROOT, e);
			}
		}

		private Object readRelatedData(final WriteCallbackContext context) throws ODataException {
			final EdmEntitySet entitySet = context.getSourceEntitySet();
			return dataSource.readRelatedData(entitySet,
					data instanceof List
							? readEntryData((List<?>) data, entitySet.getEntityType(),
									context.extractKeyFromEntryData())
							: data,
					entitySet.getRelatedEntitySet(context.getNavigationProperty()),
					Collections.<String, Object>emptyMap());
		}

		private <T> T readEntryData(final List<T> data, final EdmEntityType entityType, final Map<String, Object> key)
				throws ODataException {
			for (final T entryData : data) {
				boolean found = true;
				for (final EdmProperty keyProperty : entityType.getKeyProperties()) {
					if (!valueAccess.getPropertyValue(entryData, keyProperty).equals(key.get(keyProperty.getName()))) {
						found = false;
						break;
					}
				}
				if (found) {
					return entryData;
				}
			}
			return null;
		}
	}

	private <T> ODataResponse writeEntry(final EdmEntitySet entitySet, final ExpandSelectTreeNode expandSelectTree,
			final T data, final String contentType) throws ODataException, EntityProviderException {
		final EdmEntityType entityType = entitySet.getEntityType();
		final Map<String, Object> values = getStructuralTypeValueMap(data, entityType);

		ODataContext context = getContext();
		EntityProviderWriteProperties writeProperties = EntityProviderWriteProperties
				.serviceRoot(context.getPathInfo().getServiceRoot()).expandSelectTree(expandSelectTree)
				.callbacks(getCallbacks(data, entityType)).build();

		final int timingHandle = context.startRuntimeMeasurement("EntityProvider", "writeEntry");

		final ODataResponse response = EntityProvider.writeEntry(contentType, entitySet, values, writeProperties);

		context.stopRuntimeMeasurement(timingHandle);

		return response;
	}

	private ODataEntry parseEntry(final EdmEntitySet entitySet, final InputStream content,
			final String requestContentType, final EntityProviderReadProperties properties)
			throws ODataBadRequestException {
		ODataContext context = getContext();
		final int timingHandle = context.startRuntimeMeasurement("EntityConsumer", "readEntry");

		ODataEntry entryValues;
		try {
			entryValues = EntityProvider.readEntry(requestContentType, entitySet, content, properties);
		} catch (final EntityProviderException e) {
			throw new ODataBadRequestException(ODataBadRequestException.BODY, e);
		}

		context.stopRuntimeMeasurement(timingHandle);

		return entryValues;
	}

	private Map<String, Object> parseLinkUri(final EdmEntitySet targetEntitySet, final String uriString)
			throws EdmException {
		ODataContext context = getContext();
		final int timingHandle = context.startRuntimeMeasurement("UriParser", "getKeyPredicatesFromEntityLink");

		List<KeyPredicate> key = null;
		try {
			key = UriParser.getKeyPredicatesFromEntityLink(targetEntitySet, uriString,
					context.getPathInfo().getServiceRoot());
		} catch (ODataException e) {
			// We don't understand the link target. This could also be seen as an error.
		}

		context.stopRuntimeMeasurement(timingHandle);

		return key == null ? null : mapKey(key);
	}

	private <T> void createInlinedEntities(final EdmEntitySet entitySet, final T data, final ODataEntry entryValues)
			throws ODataException {
		final EdmEntityType entityType = entitySet.getEntityType();
		for (final String navigationPropertyName : entityType.getNavigationPropertyNames()) {

			final EdmNavigationProperty navigationProperty = (EdmNavigationProperty) entityType
					.getProperty(navigationPropertyName);
			final EdmEntitySet relatedEntitySet = entitySet.getRelatedEntitySet(navigationProperty);
			final EdmEntityType relatedEntityType = relatedEntitySet.getEntityType();

			final Object relatedValue = entryValues.getProperties().get(navigationPropertyName);
			if (relatedValue == null) {
				for (final String uriString : entryValues.getMetadata().getAssociationUris(navigationPropertyName)) {
					final Map<String, Object> key = parseLinkUri(relatedEntitySet, uriString);
					if (key != null) {
						dataSource.writeRelation(entitySet, data, relatedEntitySet, key);
					}
				}

			} else {
				if (relatedValue instanceof ODataFeed) {
					ODataFeed feed = (ODataFeed) relatedValue;
					final List<ODataEntry> relatedValueList = feed.getEntries();
					for (final ODataEntry relatedValues : relatedValueList) {
						Object relatedData = dataSource.newDataObject(relatedEntitySet);
						setStructuralTypeValuesFromMap(relatedData, relatedEntityType, relatedValues.getProperties(),
								false);
						dataSource.createData(relatedEntitySet, relatedData);
						dataSource.writeRelation(entitySet, data, relatedEntitySet,
								getStructuralTypeValueMap(relatedData, relatedEntityType));
						createInlinedEntities(relatedEntitySet, relatedData, relatedValues);
					}
				} else if (relatedValue instanceof ODataEntry) {
					final ODataEntry relatedValueEntry = (ODataEntry) relatedValue;
					final Map<String, Object> relatedProperties = relatedValueEntry.getProperties();
					if (relatedProperties.isEmpty()) {
						final Map<String, Object> key = parseLinkUri(relatedEntitySet,
								relatedValueEntry.getMetadata().getUri());
						if (key != null) {
							dataSource.writeRelation(entitySet, data, relatedEntitySet, key);
						}
					} else {
						Object relatedData = dataSource.newDataObject(relatedEntitySet);
						setStructuralTypeValuesFromMap(relatedData, relatedEntityType, relatedProperties, false);
						dataSource.createData(relatedEntitySet, relatedData);
						dataSource.writeRelation(entitySet, data, relatedEntitySet,
								getStructuralTypeValueMap(relatedData, relatedEntityType));
						createInlinedEntities(relatedEntitySet, relatedData, relatedValueEntry);
					}
				} else {
					throw new ODataException(
							"Unexpected class for a related value: " + relatedValue.getClass().getSimpleName());
				}

			}
		}
	}

	private <T> boolean appliesFilter(final T data, final FilterExpression filter) throws ODataException {
		ODataContext context = getContext();
		final int timingHandle = context.startRuntimeMeasurement(getClass().getSimpleName(), "appliesFilter");

		try {
			return data != null && (filter == null || "true".equals(evaluateExpression(data, filter.getExpression())));
		} catch (final RuntimeException e) {
			return false;
		} finally {
			context.stopRuntimeMeasurement(timingHandle);
		}
	}

	private <T> String evaluateExpression(final T data, final CommonExpression expression) throws ODataException {
		switch (expression.getKind()) {
		case UNARY:
			final UnaryExpression unaryExpression = (UnaryExpression) expression;
			final String operand = evaluateExpression(data, unaryExpression.getOperand());

			switch (unaryExpression.getOperator()) {
			case NOT:
				return Boolean.toString(!Boolean.parseBoolean(operand));
			case MINUS:
				return operand.startsWith("-") ? operand.substring(1) : "-" + operand;
			default:
				throw new ODataNotImplementedException();
			}

		case BINARY:
			final BinaryExpression binaryExpression = (BinaryExpression) expression;
			final EdmSimpleType type = (EdmSimpleType) binaryExpression.getLeftOperand().getEdmType();
			final String left = evaluateExpression(data, binaryExpression.getLeftOperand());
			final String right = evaluateExpression(data, binaryExpression.getRightOperand());

			switch (binaryExpression.getOperator()) {
			case ADD:
				if (binaryExpression.getEdmType() == EdmSimpleTypeKind.Decimal.getEdmSimpleTypeInstance()
						|| binaryExpression.getEdmType() == EdmSimpleTypeKind.Double.getEdmSimpleTypeInstance()
						|| binaryExpression.getEdmType() == EdmSimpleTypeKind.Single.getEdmSimpleTypeInstance()) {
					return Double.toString(Double.valueOf(left) + Double.valueOf(right));
				} else {
					return Long.toString(Long.valueOf(left) + Long.valueOf(right));
				}
			case SUB:
				if (binaryExpression.getEdmType() == EdmSimpleTypeKind.Decimal.getEdmSimpleTypeInstance()
						|| binaryExpression.getEdmType() == EdmSimpleTypeKind.Double.getEdmSimpleTypeInstance()
						|| binaryExpression.getEdmType() == EdmSimpleTypeKind.Single.getEdmSimpleTypeInstance()) {
					return Double.toString(Double.valueOf(left) - Double.valueOf(right));
				} else {
					return Long.toString(Long.valueOf(left) - Long.valueOf(right));
				}
			case MUL:
				if (binaryExpression.getEdmType() == EdmSimpleTypeKind.Decimal.getEdmSimpleTypeInstance()
						|| binaryExpression.getEdmType() == EdmSimpleTypeKind.Double.getEdmSimpleTypeInstance()
						|| binaryExpression.getEdmType() == EdmSimpleTypeKind.Single.getEdmSimpleTypeInstance()) {
					return Double.toString(Double.valueOf(left) * Double.valueOf(right));
				} else {
					return Long.toString(Long.valueOf(left) * Long.valueOf(right));
				}
			case DIV:
				final String number = Double.toString(Double.valueOf(left) / Double.valueOf(right));
				return number.endsWith(".0") ? number.replace(".0", "") : number;
			case MODULO:
				if (binaryExpression.getEdmType() == EdmSimpleTypeKind.Decimal.getEdmSimpleTypeInstance()
						|| binaryExpression.getEdmType() == EdmSimpleTypeKind.Double.getEdmSimpleTypeInstance()
						|| binaryExpression.getEdmType() == EdmSimpleTypeKind.Single.getEdmSimpleTypeInstance()) {
					return Double.toString(Double.valueOf(left) % Double.valueOf(right));
				} else {
					return Long.toString(Long.valueOf(left) % Long.valueOf(right));
				}
			case AND:
				return Boolean.toString("true".equals(left) && "true".equals(right));
			case OR:
				return Boolean.toString("true".equals(left) || "true".equals(right));
			case EQ:
				return Boolean.toString(left.equals(right));
			case NE:
				return Boolean.toString(!left.equals(right));
			case LT:
				if (type == EdmSimpleTypeKind.String.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.DateTime.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.DateTimeOffset.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.Guid.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.Time.getEdmSimpleTypeInstance()) {
					return Boolean.toString(left.compareTo(right) < 0);
				} else {
					return Boolean.toString(Double.valueOf(left) < Double.valueOf(right));
				}
			case LE:
				if (type == EdmSimpleTypeKind.String.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.DateTime.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.DateTimeOffset.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.Guid.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.Time.getEdmSimpleTypeInstance()) {
					return Boolean.toString(left.compareTo(right) <= 0);
				} else {
					return Boolean.toString(Double.valueOf(left) <= Double.valueOf(right));
				}
			case GT:
				if (type == EdmSimpleTypeKind.String.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.DateTime.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.DateTimeOffset.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.Guid.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.Time.getEdmSimpleTypeInstance()) {
					return Boolean.toString(left.compareTo(right) > 0);
				} else {
					return Boolean.toString(Double.valueOf(left) > Double.valueOf(right));
				}
			case GE:
				if (type == EdmSimpleTypeKind.String.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.DateTime.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.DateTimeOffset.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.Guid.getEdmSimpleTypeInstance()
						|| type == EdmSimpleTypeKind.Time.getEdmSimpleTypeInstance()) {
					return Boolean.toString(left.compareTo(right) >= 0);
				} else {
					return Boolean.toString(Double.valueOf(left) >= Double.valueOf(right));
				}
			case PROPERTY_ACCESS:
				throw new ODataNotImplementedException();
			default:
				throw new ODataNotImplementedException();
			}

		case PROPERTY:
			final EdmProperty property = (EdmProperty) ((PropertyExpression) expression).getEdmProperty();
			final EdmSimpleType propertyType = (EdmSimpleType) property.getType();
			return propertyType.valueToString(valueAccess.getPropertyValue(data, property), EdmLiteralKind.DEFAULT,
					property.getFacets());

		case MEMBER:
			final MemberExpression memberExpression = (MemberExpression) expression;
			final PropertyExpression propertyExpression = (PropertyExpression) memberExpression.getProperty();
			final EdmProperty memberProperty = (EdmProperty) propertyExpression.getEdmProperty();
			final EdmSimpleType memberType = (EdmSimpleType) memberExpression.getEdmType();
			List<EdmProperty> propertyPath = new ArrayList<EdmProperty>();
			CommonExpression currentExpression = memberExpression;
			while (currentExpression != null) {
				final PropertyExpression currentPropertyExpression = (PropertyExpression) (currentExpression
						.getKind() == ExpressionKind.MEMBER ? ((MemberExpression) currentExpression).getProperty()
								: currentExpression);
				final EdmTyped currentProperty = currentPropertyExpression.getEdmProperty();
				final EdmTypeKind kind = currentProperty.getType().getKind();
				if (kind == EdmTypeKind.SIMPLE || kind == EdmTypeKind.COMPLEX) {
					propertyPath.add(0, (EdmProperty) currentProperty);
				} else {
					throw new ODataNotImplementedException();
				}
				currentExpression = currentExpression.getKind() == ExpressionKind.MEMBER
						? ((MemberExpression) currentExpression).getPath()
						: null;
			}
			return memberType.valueToString(getPropertyValue(data, propertyPath), EdmLiteralKind.DEFAULT,
					memberProperty.getFacets());

		case LITERAL:
			final LiteralExpression literal = (LiteralExpression) expression;
			final EdmSimpleType literalType = (EdmSimpleType) literal.getEdmType();
			return literalType.valueToString(literalType.valueOfString(literal.getUriLiteral(), EdmLiteralKind.URI,
					null, literalType.getDefaultType()), EdmLiteralKind.DEFAULT, null);

		case METHOD:
			final MethodExpression methodExpression = (MethodExpression) expression;
			final String first = evaluateExpression(data, methodExpression.getParameters().get(0));
			final String second = methodExpression.getParameterCount() > 1
					? evaluateExpression(data, methodExpression.getParameters().get(1))
					: "";
			final String third = methodExpression.getParameterCount() > 2
					? evaluateExpression(data, methodExpression.getParameters().get(2))
					: "";

			switch (methodExpression.getMethod()) {
			case ENDSWITH:
				return Boolean.toString(first.endsWith(second));
			case INDEXOF:
				return Integer.toString(first.indexOf(second));
			case STARTSWITH:
				return Boolean.toString(first.startsWith(second));
			case TOLOWER:
				return first.toLowerCase(Locale.ROOT);
			case TOUPPER:
				return first.toUpperCase(Locale.ROOT);
			case TRIM:
				return first.trim();
			case SUBSTRING:
				final int offset = second.length() == 0 ? 0 : Integer.parseInt(second);
				final int length = third.length() == 0 ? 0 : Integer.parseInt(second);
				return first.substring(offset, offset + length);
			case SUBSTRINGOF:
				return Boolean.toString(second.contains(first));
			case CONCAT:
				return first + second;
			case LENGTH:
				return Integer.toString(first.length());
			case YEAR:
				return String.valueOf(Integer.parseInt(first.substring(0, 4)));
			case MONTH:
				return String.valueOf(Integer.parseInt(first.substring(5, 7)));
			case DAY:
				return String.valueOf(Integer.parseInt(first.substring(8, 10)));
			case HOUR:
				return String.valueOf(Integer.parseInt(first.substring(11, 13)));
			case MINUTE:
				return String.valueOf(Integer.parseInt(first.substring(14, 16)));
			case SECOND:
				return String.valueOf(Integer.parseInt(first.substring(17, 19)));
			case ROUND:
				return Long.toString(Math.round(Double.valueOf(first)));
			case FLOOR:
				return Long.toString(Math.round(Math.floor(Double.valueOf(first))));
			case CEILING:
				return Long.toString(Math.round(Math.ceil(Double.valueOf(first))));
			default:
				throw new ODataNotImplementedException();
			}

		default:
			throw new ODataNotImplementedException();
		}
	}

	private <T> Object getPropertyValue(final T data, final List<EdmProperty> propertyPath) throws ODataException {
		Object dataObject = data;
		for (final EdmProperty property : propertyPath) {
			if (dataObject != null) {
				dataObject = valueAccess.getPropertyValue(dataObject, property);
			}
		}
		return dataObject;
	}

	private void handleMimeType(final Object data, final EdmMapping mapping, final Map<String, Object> valueMap)
			throws ODataException {
		final String mimeTypeName = mapping.getMediaResourceMimeTypeKey();
		if (mimeTypeName != null) {
			Object value = valueAccess.getMappingValue(data, mapping);
			valueMap.put(mimeTypeName, value);
		}
	}

	private <T> Map<String, Object> getSimpleTypeValueMap(final T data, final List<EdmProperty> propertyPath)
			throws ODataException {
		final EdmProperty property = propertyPath.get(propertyPath.size() - 1);
		Map<String, Object> valueWithMimeType = new HashMap<String, Object>();
		valueWithMimeType.put(property.getName(), getPropertyValue(data, propertyPath));

		handleMimeType(data, property.getMapping(), valueWithMimeType);
		return valueWithMimeType;
	}

	private <T> Map<String, Object> getStructuralTypeValueMap(final T data, final EdmStructuralType type)
			throws ODataException {
		ODataContext context = getContext();
		final int timingHandle = context.startRuntimeMeasurement(getClass().getSimpleName(),
				"getStructuralTypeValueMap");

		Map<String, Object> valueMap = new HashMap<String, Object>();

		EdmMapping mapping = type.getMapping();
		if (mapping != null) {
			handleMimeType(data, mapping, valueMap);
		}

		for (final String propertyName : type.getPropertyNames()) {
			final EdmProperty property = (EdmProperty) type.getProperty(propertyName);
			final Object value = valueAccess.getPropertyValue(data, property);

			if (property.isSimple()) {
				if (property.getMapping() == null || property.getMapping().getMediaResourceMimeTypeKey() == null) {
					valueMap.put(propertyName, value);
				} else {
					// TODO: enable MIME type mapping outside the current subtree
					valueMap.put(propertyName, getSimpleTypeValueMap(data, Arrays.asList(property)));
				}
			} else {
				valueMap.put(propertyName, getStructuralTypeValueMap(value, (EdmStructuralType) property.getType()));
			}
		}

		context.stopRuntimeMeasurement(timingHandle);

		return valueMap;
	}

	private <T> Map<String, Object> getStructuralTypeTypeMap(final T data, final EdmStructuralType type)
			throws ODataException {
		ODataContext context = getContext();
		final int timingHandle = context.startRuntimeMeasurement(getClass().getSimpleName(),
				"getStructuralTypeTypeMap");

		Map<String, Object> typeMap = new HashMap<String, Object>();
		for (final String propertyName : type.getPropertyNames()) {
			final EdmProperty property = (EdmProperty) type.getProperty(propertyName);
			if (property.isSimple()) {
				Object value = valueAccess.getPropertyType(data, property);
				if (value != null) {
					typeMap.put(propertyName, value);
				}
			} else {
				Object value = valueAccess.getPropertyValue(data, property);
				if (value == null) {
					Class<?> complexClass = valueAccess.getPropertyType(data, property);
					value = createInstance(complexClass);
				}
				typeMap.put(propertyName, getStructuralTypeTypeMap(value, (EdmStructuralType) property.getType()));
			}
		}

		context.stopRuntimeMeasurement(timingHandle);

		return typeMap;
	}

	private <T> void setStructuralTypeValuesFromMap(final T data, final EdmStructuralType type,
			final Map<String, Object> valueMap, final boolean merge) throws ODataException {
		if (data == null) {
			throw new ODataException("Unable to set structural type values to NULL data.");
		}
		ODataContext context = getContext();
		final int timingHandle = context.startRuntimeMeasurement(getClass().getSimpleName(),
				"setStructuralTypeValuesFromMap");

		for (final String propertyName : type.getPropertyNames()) {
			final EdmProperty property = (EdmProperty) type.getProperty(propertyName);
			if (type instanceof EdmEntityType && ((EdmEntityType) type).getKeyProperties().contains(property)) {
				Object v = valueAccess.getPropertyValue(data, property);
				if (v != null) {
					continue;
				}
			}

			if (!merge || valueMap != null && valueMap.containsKey(propertyName)) {
				final Object value = valueMap == null ? null : valueMap.get(propertyName);
				if (property.isSimple()) {
					valueAccess.setPropertyValue(data, property, value);
				} else {
					@SuppressWarnings("unchecked")
					final Map<String, Object> values = (Map<String, Object>) value;
					Object complexData = valueAccess.getPropertyValue(data, property);
					if (complexData == null) {
						Class<?> complexClass = valueAccess.getPropertyType(data, property);
						complexData = createInstance(complexClass);
						valueAccess.setPropertyValue(data, property, complexData);
					}
					setStructuralTypeValuesFromMap(complexData, (EdmStructuralType) property.getType(), values, merge);
				}
			}
		}

		context.stopRuntimeMeasurement(timingHandle);
	}

	private Object createInstance(final Class<?> complexClass) throws ODataException {
		try {
			return complexClass.newInstance();
		} catch (InstantiationException e) {
			throw new ODataException("Unable to create instance for complex data class '" + complexClass + "'.", e);
		} catch (IllegalAccessException e) {
			throw new ODataException("Unable to create instance for complex data class '" + complexClass + "'.", e);
		}
	}

	@Override
	public ODataResponse executeBatch(final BatchHandler handler, final String contentType, final InputStream content)
			throws ODataException {
		ODataResponse batchResponse;
		List<BatchResponsePart> batchResponseParts = new ArrayList<BatchResponsePart>();
		PathInfo pathInfo = getContext().getPathInfo();
		EntityProviderBatchProperties batchProperties = EntityProviderBatchProperties.init().pathInfo(pathInfo).build();
		List<BatchRequestPart> batchParts = EntityProvider.parseBatchRequest(contentType, content, batchProperties);
		for (BatchRequestPart batchPart : batchParts) {
			batchResponseParts.add(handler.handleBatchPart(batchPart));
		}
		batchResponse = EntityProvider.writeBatchResponse(batchResponseParts);
		return batchResponse;
	}

	@Override
	public BatchResponsePart executeChangeSet(final BatchHandler handler, final List<ODataRequest> requests)
			throws ODataException {
		List<ODataResponse> responses = new ArrayList<ODataResponse>();
		for (ODataRequest request : requests) {
			ODataResponse response = handler.handleRequest(request);
			if (response.getStatus().getStatusCode() >= HttpStatusCodes.BAD_REQUEST.getStatusCode()) {
				// Rollback
				List<ODataResponse> errorResponses = new ArrayList<ODataResponse>(1);
				errorResponses.add(response);
				return BatchResponsePart.responses(errorResponses).changeSet(false).build();
			}
			responses.add(response);
		}
		return BatchResponsePart.responses(responses).changeSet(true).build();
	}
}
