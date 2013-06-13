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

package biz.janux.payment;

import java.util.List;

import org.janux.bus.search.SearchCriteria;


public interface SettlementItemHotelService <T extends SettlementItemHotel>{

	public List<T> findAll(SearchCriteria searchCriteria);
	
	public T find(SearchCriteria searchCriteria);
	
	public T load(T settlementItem);

	public T saveOrUpdate(T settlementItem);

	public T findByUuid(String uuid);

	public T delete(String uuid);
	
}