package com.cg.mobinv.mobinventory.dataaccess.api;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "\"hkrbudgetingdb.db.dbmodel::hkrbudgeting.Material\"")
public class MaterialEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "S_Material", sequenceName = "\"hkrbudgetingdb.db::S_Material\"", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "S_Material")
  private Long id;

  @Column(name = "\"KeyFigure\"")
  private Integer keyFigure;

  @Nationalized
  @Column(name = "\"LongIdentifier\"", length = 512)
  private String longIdentifier;

  @Nationalized
  @Column(name = "\"ReasonDisputable\"", length = 1024)
  private String reasonDisputable;

  @Nationalized
  @Column(name = "\"ShortIdentifier\"", length = 256)
  private String shortIdentifier;

  public MaterialEntity() {

  }

  /**
   * @return id
   */
  public Long getId() {

    return this.id;
  }

  /**
   * @param id new value of {@link #getid}.
   */
  public void setId(Long id) {

    this.id = id;
  }

  /**
   * @return keyFigure
   */
  public Integer getKeyFigure() {

    return this.keyFigure;
  }

  /**
   * @param keyFigure new value of {@link #getkeyFigure}.
   */
  public void setKeyFigure(Integer keyFigure) {

    this.keyFigure = keyFigure;
  }

  /**
   * @return longIdentifier
   */
  public String getLongIdentifier() {

    return this.longIdentifier;
  }

  /**
   * @param longIdentifier new value of {@link #getlongIdentifier}.
   */
  public void setLongIdentifier(String longIdentifier) {

    this.longIdentifier = longIdentifier;
  }

  /**
   * @return reasonDisputable
   */
  public String getReasonDisputable() {

    return this.reasonDisputable;
  }

  /**
   * @param reasonDisputable new value of {@link #getreasonDisputable}.
   */
  public void setReasonDisputable(String reasonDisputable) {

    this.reasonDisputable = reasonDisputable;
  }

  /**
   * @return shortIdentifier
   */
  public String getShortIdentifier() {

    return this.shortIdentifier;
  }

  /**
   * @param shortIdentifier new value of {@link #getshortIdentifier}.
   */
  public void setShortIdentifier(String shortIdentifier) {

    this.shortIdentifier = shortIdentifier;
  }

}
