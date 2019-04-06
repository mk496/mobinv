package com.cg.mobinv.mobinventory.common.mappingbean;

        import org.dozer.DozerBeanMapper;
        import org.dozer.Mapper;

/**
 * @author manjinsi
 *
 */
public class DozerMappingSingleton {

  private static Mapper mapper = null;

  public static Mapper getInstance() {

    if (mapper == null) {
      mapper = new DozerBeanMapper();
    }

    return mapper;
  }

}
