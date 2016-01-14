package teamdivider.dao;

import org.springframework.stereotype.Repository;

import teamdivider.bean.eo.mapping.TypeSubscriber;
import teamdivider.dao.AbstractDAO;

@Repository
public class TypeSubscriberDAO extends AbstractDAO<TypeSubscriber> {

  @Override
  protected Class<TypeSubscriber> getClazz() {
    return TypeSubscriber.class;
  }

}