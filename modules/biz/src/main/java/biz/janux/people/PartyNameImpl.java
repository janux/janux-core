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

package biz.janux.people;


//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.Log;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 ***************************************************************************************************
 * Represents the name of an Party
 *
 * @author  <a href="mailto:philippe.paravicini@janux.org">Philippe Paravicini</a>
 * @version $Revision: 1.5 $ - $Date: 2006-11-14 01:27:50 $
 ***************************************************************************************************
 */
public class PartyNameImpl implements PartyName
{
	private static final long serialVersionUID = 325627768491877386L;
	private String shortName;
	private String longName;


	/** plain vanilla constructor */
	public PartyNameImpl() {}


	public String getShort() {
		return this.shortName;
	}
	
	public void setShort(String s) {
		this.shortName = s;
	}


	public String getLong() {
		return this.longName;
	}
	
	public void setLong(String s) {
		this.longName = s;
	}

	public String toString() 
	{
		return new ToStringBuilder(this)
			.append("short", getShort())
			.append("long",  getLong())
			.toString();
	}

	public Object clone() 
	{
	    try 
	    {
	        PartyNameImpl result = (PartyNameImpl) super.clone();
	    
	        return result;
	    } 
	    catch (CloneNotSupportedException e) 
	    {
	        return null;
	    }
	}

	
} // end class PartyNameImpl
