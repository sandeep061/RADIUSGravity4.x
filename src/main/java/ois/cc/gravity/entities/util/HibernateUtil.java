package ois.cc.gravity.entities.util;

import java.util.Collection;
import java.util.Iterator;

import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

	public HibernateUtil() {
		// TODO Auto-generated constructor stub
	}

	private final static Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

	public static <T> T unproxy(T entity) {
		T en = entity;
		if (entity == null) {
			return null;
		}

		if (entity instanceof HibernateProxy) {
			logger.info("Entity is a HibernateProxy : " + entity);
			en = (T) Hibernate.unproxy(entity);
		}

		if (entity instanceof PersistentCollection)
		{
			logger.info("Entity is a PersistentCollection : " + entity);
			((PersistentCollection) en).forceInitialization();
		}
		else if (entity instanceof Collection)
		{
			for (Iterator<?> iterator = ((Collection<?>) en).iterator(); iterator.hasNext();)
			{
				unproxy(iterator.next());
			}
		}

		return en;
	}
}
