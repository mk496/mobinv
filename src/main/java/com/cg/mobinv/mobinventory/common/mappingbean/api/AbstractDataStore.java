package com.cg.mobinv.mobinventory.common.mappingbean.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public interface AbstractDataStore<T, K> {

  public Class<?> getDataTypeClass();

  public String getEntityTypeName();

  public T createInstance();

  public K mapEdmObjectToJpaEntity(T object);

  public T mapJpaEntityToEdmObject(K entity);

  public List<K> mapEdmObjectsToJpaEntities(Collection<T> objects);

  public List<T> mapJpaEntitiesToEdmObjects(Collection<K> entities);

  public T read(final Map<String, Object> keys);

  public List<T> read();

  public T create(final Object obj);

  public T update(final Object obj);

  public void delete(final Map<String, Object> keys);

  public void delete(final T obj);

  /**
   * Returns the field of type <code>targetTypeClass</code> that <code>relatedObject</code> of type
   * <code>relatedTypeClass</code> has. The contract for reading relations with this method is as follows: If for
   * example the relatedObject is of type <code>A</code>, that has a one-to-many relationship to <code>B</code> through
   * a List of the target type <code>B</code> and <code>B</code> is mapped bi-directional with a single object field of
   * type <code>A</code>, then to get
   *
   * (1) the related List of <code>B</code>, you call this method on the DataStore for <code>B</code> and pass
   * <code>A</code> as related class
   *
   * (2) the related parent object for <code>A</code>, you call this method on the DataStore for <code>A</code> and pass
   * <code>B</code> as related class.
   *
   * That is to say, that you always <b>call this method on the DataStore for the target class</b>, passing the "parent"
   * object as the <code>relatedObject</code>.
   *
   * @param relatedTypeClass
   * @param relatedObject
   * @param targetTypeClass
   * @param targetKeys
   * @return
   */
  public Object readRelatedData(Class<?> relatedTypeClass, Object relatedObject, Class<?> targetTypeClass,
      Map<String, Object> targetKeys);
}
