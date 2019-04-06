package com.cg.mobinv.mobinventory.common.mappingbean.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.annotation.processor.core.util.AnnotationHelper;
import org.apache.olingo.odata2.annotation.processor.core.util.AnnotationRuntimeException;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @param <EdmType> The type of the associated EDM object (i.e. a class annotated with
 *        {@link org.apache.olingo.odata2.api.annotation.edm.EdmEntityType}}
 * @param <EntityType> The type of the corresponding JPA entity (i.e. a class annotated with
 *        {@link javax.persistence.Entity}
 * @param <EntityKeyType> The type of the primary key of the JPA entity
 */
public abstract class AbstractDataStoreBean<EdmType, EntityType, EntityKeyType extends Serializable> {

  protected static final AnnotationHelper ANNOTATION_HELPER = new AnnotationHelper();

  public abstract Class<?> getDataTypeClass();

  public abstract Class<?> getEntityClass();

  public String getEntityTypeName() {

    return ANNOTATION_HELPER.extractEntityTypeName(getDataTypeClass());
  }

  @SuppressWarnings("unchecked")
  public EdmType createInstance() {

    try {
      return (EdmType) getDataTypeClass().newInstance();
    } catch (InstantiationException e) {
      throw new AnnotationRuntimeException("Unable to create instance of class '" + getDataTypeClass() + "'.", e);
    } catch (IllegalAccessException e) {
      throw new AnnotationRuntimeException("Unable to create instance of class '" + getDataTypeClass() + "'.", e);
    }
  }

  protected abstract CrudRepository<EntityType, EntityKeyType> getDao();
  // protected abstract GenericDao<EntityType, EntityKeyType> getDao();

  public abstract List<Class<?>> getRelatedEdmEntityTypeClasses();

  protected abstract EntityKeyType mapKeyMapToJpaEntityKey(Map<String, Object> keyMap);

  /**
   * Maps an EDM object to its corresponding JPA entity.
   *
   * @param The EDM object
   * @return The corresponding JPA entity object
   */
  public abstract EntityType mapEdmObjectToJpaEntity(EdmType object);

  public List<EntityType> mapEdmObjectsToJpaEntities(Collection<EdmType> objects) {

    if (objects == null || objects.isEmpty()) {
      return Collections.<EntityType> emptyList();
    }

    List<EntityType> result =
        objects.stream().map(object -> mapEdmObjectToJpaEntity(object)).collect(Collectors.toList());

    return result;
  }

  public List<EdmType> mapJpaEntitiesToEdmObjects(Collection<EntityType> entities) {

    if (entities == null || entities.isEmpty()) {
      return Collections.<EdmType> emptyList();
    }

    List<EdmType> result =
        entities.stream().map(entity -> mapJpaEntityToEdmObject(entity)).collect(Collectors.toList());

    return result;
  }

  /**
   * Maps a JPA entity to its corresponding EDM object.
   *
   * @param entity The JPA entity object
   * @return The corresponding EDM object.
   */
  public abstract EdmType mapJpaEntityToEdmObject(EntityType entity);

  private void assertKeyMapContainsKey(Map<String, Object> keyMap, String key) {

    if (!keyMap.containsKey(key)) {
      throw new IllegalArgumentException("Key map for EDM object must contain key named \"" + key + "\"");
    }
  }

  /**
   * Ensures that <code>keyMap</code> contains entries for all keys in <code>keys</code>. If a key in <code>keys</code>
   * does not exist in the map, an {@link IllegalArgumentException} is raised.
   *
   * @param keyMap The map to check for keys
   * @param keys The list of keys that must be in <code>keyMap</code>
   */
  protected void assertKeyMapContainsKeys(Map<String, Object> keyMap, List<String> keys) {

    keys.forEach((key) -> assertKeyMapContainsKey(keyMap, key));
  }

  protected void validateRelatedTypeClasses(Class<?> relatedTypeClass, Class<?> targetTypeClass) {

    validateTargetTypeClass(targetTypeClass);
    validateRelatedTypeClass(relatedTypeClass);
  }

  protected void validateTargetTypeClass(Class<?> targetTypeClass) {

    if (targetTypeClass != getDataTypeClass()) {
      throw new IllegalArgumentException("This DataStore can only return EDM entities of type "
          + getDataTypeClass().getName()
          + "! Please set the targetTypeClass parameter accordingly, or call the readRelatedData method on the correct DataStore.");
    }
  }

  protected void validateRelatedTypeClass(Class<?> relatedTypeClass) {

    if (!(getRelatedEdmEntityTypeClasses().contains(relatedTypeClass))) {
      throw new IllegalArgumentException("The EDM entity " + getDataTypeClass().getName()
          + " of this DataStore is not related to the EDM entity " + relatedTypeClass.getName() + "!");
    }
  }

  public abstract Object readRelatedData(Class<?> relatedTypeClass, Object relatedObject);

  public Object readRelatedData(Class<?> relatedTypeClass, Object relatedObject, Class<?> targetTypeClass,
      Map<String, Object> targetKeys) {

    validateRelatedTypeClasses(relatedTypeClass, targetTypeClass);

    // If the target object is determined by a (composite) key, we simply
    // load it by this primary key
    if (!targetKeys.isEmpty()) {
      return read(targetKeys);
    }

    // Otherwise we need to load it by its association to the relatedObject
    Object result = readRelatedData(relatedTypeClass, relatedObject);

    if (result == null) {
      throw new IllegalStateException("Ensure that the method getRelatedDataTypeClasses of this DataStore is correct!");
    }
    return result;
  }

  public List<EdmType> read() {

    List<EntityType> jpaEntities = (List<EntityType>) getDao().findAll();
    List<EdmType> mappedEdmObjects = mapJpaEntitiesToEdmObjects(jpaEntities);
    return mappedEdmObjects;
  }

  public EdmType read(EntityKeyType primaryKey) {

    Optional<EntityType> optionalEntity = Optional.ofNullable(getDao().findOne(primaryKey));

    return (optionalEntity.isPresent() ? mapJpaEntityToEdmObject(optionalEntity.get()) : null);
  }

  /**
   * Load an Olingo EDM object by a primary key that consists of all fields annotated with
   * {@link org.apache.olingo.odata2.api.annotation.edm.EdmKey}.
   *
   * Example: Suppose an EDM object has two fields <code>key1</code> and <code>key2</code> annotated with
   * {@link org.apache.olingo.odata2.api.annotation.edm.EdmKey}. Then, the <code>keys</code> map should contain two keys
   * "Key1" and "Key2" (note the capitalization). This method converts the map of keys to a primary key of the
   * corresponding JPA entity and loads the entity with this primary key from the database.
   *
   * @param keys Map with keyvalues for each field annotated with
   *        {@link org.apache.olingo.odata2.api.annotation.edm.EdmKey}.
   * @return The EDM object with the (composite) key represented by the <code>keys</code> map, iff such an entity with
   *         this key exists in the database.
   */
  public EdmType read(Map<String, Object> keys) {

    EntityKeyType key = mapKeyMapToJpaEntityKey(keys);
    return read(key);
  }

  public EdmType create(Object obj) {
    EntityType mappedEntity = mapEdmObjectToJpaEntity((EdmType) obj);
    EntityType persistentEntity = getDao().save(mappedEntity);
    EdmType mappedEdmObject = mapJpaEntityToEdmObject(persistentEntity);

    return mappedEdmObject;
  }

  public EdmType update(Object obj) {
    EntityType mappedEntity = mapEdmObjectToJpaEntity((EdmType) obj);
    EntityType persistentEntity = getDao().save(mappedEntity);
    EdmType mappedEdmObject = mapJpaEntityToEdmObject(persistentEntity);

	return mappedEdmObject;
  }

  public void delete(Map<String, Object> keys) {

    EntityKeyType key = mapKeyMapToJpaEntityKey(keys);
    Optional<EntityType> optionalEntity = Optional.ofNullable(getDao().findOne(key));
    optionalEntity.ifPresent(entity -> getDao().delete(entity));
  }

  public void delete(EdmType obj) {

    EntityType mappedEntity = mapEdmObjectToJpaEntity(obj);
    getDao().delete(mappedEntity);
  }
}
