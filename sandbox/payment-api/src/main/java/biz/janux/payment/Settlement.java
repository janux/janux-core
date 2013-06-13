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

import biz.janux.payment.MerchantAccount;

/**
 * Represents a Settlement Transaction
 * 
 * It is a batch that groups a collection of applied payments {@link SettlementItem} 
 * for a {@link MerchantAccount} 
 * 
 * @author Nilesh
 * @author albertobuffagni@gmail.com 
 * 
 */
public interface Settlement extends Transaction<SettlementResponse> {

	public List<SettlementItem> getSettlementItems();
	public void setSettlementItems(List<SettlementItem> settlementItems);
	
	/**
	 * This field contains a 3-character numeric batch sequence number generated by the POS 
	 * device. The batch number must be a number within the range of 001-999. If the POS device
	 * is automatically incrementing the batch number, it should automatically advance the number 
	 * from 999 to 001 (000 is invalid). Batch Numbers must not be reused or repeated within five 
	 * consecutive days. This field should be configurable as a parameter.
	 */
	public int getBatchNumber();
	public void setBatchNumber(int batchNumber);
	
	public boolean isEnabled();
	public void setEnabled(boolean enabled);
	
	
}