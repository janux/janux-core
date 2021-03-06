/* Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Copyright 2005-2012 janux.org */

package biz.janux.geography;

import java.util.List;

import org.hibernate.Query;
import org.janux.bus.persistence.GenericDaoHibImpl;
import org.janux.bus.search.SearchCriteria;
import org.springframework.dao.DataAccessException;

/**
 ***************************************************************************************************
 * Dao used to read and write PostalAddress instances
 * 
 * @author  <a href="mailto:philippe.paravicini@janux.org">Philippe Paravicini</a>
 * @since 0.4
 ***************************************************************************************************
 */
public class PostalAddressDaoHibImplGeneric 
	extends GenericDaoHibImpl<PostalAddressImpl, Integer, SearchCriteria> 
	implements PostalAddressDaoGeneric<PostalAddressImpl>
{
	public int countByCountryAsString()
	{
		List l = getHibernateTemplate().find("SELECT COUNT(address) FROM biz.janux.geography.PostalAddress address WHERE address.countryAsString is not null");
		return ((Long)firstResult(l)).intValue();
	}


	public List<PostalAddress> findByCountryAsString()
	{
		return this.findByCountryAsString(null, null);
	}


	@SuppressWarnings("unchecked")
	public List<PostalAddress> findByCountryAsString(final Integer numRecords, final Integer offset) 
	{
		Query query = this.getSession().createQuery("FROM biz.janux.geography.PostalAddress as address WHERE address.countryAsString is not null ORDER BY address.id");
		if (numRecords instanceof Integer) query.setMaxResults(numRecords);
		if (offset instanceof Integer) query.setFirstResult(offset);

		return (List<PostalAddress>)query.list();
	}


	public int countByCountryAsString(String country) 
	{
		Object[] parms = {country};
		List l = getHibernateTemplate().find("SELECT COUNT(address) FROM biz.janux.geography.PostalAddress address WHERE address.countryAsString=?", parms);
		return ((Long)firstResult(l)).intValue();
	}


	@SuppressWarnings("unchecked")
	public List<PostalAddress> findByCountryAsString(final String country, final Integer numRecords, final Integer offset) 
	{
		Query query = this.getSession().createQuery("FROM biz.janux.geography.PostalAddress as address WHERE address.countryAsString = ? ORDER BY address.id").setString(0, country);
		if (numRecords instanceof Integer) query.setMaxResults(numRecords);
		if (offset instanceof Integer) query.setFirstResult(offset);

		return (List<PostalAddress>)query.list();
		// we could also use a HibernateCallback to do this - I suppose that these
		// are no longer necessary since in hibernate3 hibernate exceptions are unchecked
		/*
		return (List<PostalAddress>)this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session hibSession) throws HibernateException 
			{
				Object[] params = {country};
				Query query = hibSession.createQuery("SELECT from PostalAddress address WHERE address.country = ?").setString(0, country);
				if (numRecords instanceof Integer) query.setMaxResults(numRecords);
				if (offset instanceof Integer) query.setMaxResults(numRecords);

				return query.list();
			}
		});
		*/
	}

	public List<PostalAddress> findByCountryAsString(String country)
	{
		return this.findByCountryAsString(country, null, null);
	}

}
