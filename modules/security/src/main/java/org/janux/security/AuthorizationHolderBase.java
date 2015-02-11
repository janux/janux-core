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

package org.janux.security;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

import org.janux.util.Chronometer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 ***************************************************************************************************
 * Implementation convenience class that factors in one class the functions
 * that derive the permissions that a Role or Account may have based on its
 * associated roles and permissions granted 
 * 
 * @author  <a href="mailto:philippe.paravicini@janux.org">Philippe Paravicini</a>
 * @version $Revision: 1.8 $ - $Date: 2007-12-27 00:51:17 $
 ***************************************************************************************************
 */
public class AuthorizationHolderBase implements AuthorizationHolder, Serializable
{
	private static final long serialVersionUID = 20070617L;

	protected transient Logger log = LoggerFactory.getLogger(this.getClass());

	protected String name;
	protected List<Role> roles;
	protected boolean isSuper;

	protected Map<String, PermissionContext> permissionContexts;

	/** this is declared as protected simply for testing purposes */
	protected Map<PermissionGrantedKey, Long> permissionsGranted;

	private Map<String, Long> permsUnionMap;

	public AuthorizationHolderBase() {}

	public AuthorizationHolderBase(String name) {
		this(name, null, null);
	}

	public AuthorizationHolderBase(String name, List<Role> roles, Map<PermissionGrantedKey, Long> permissionsGranted) 
	{
		this.name = name;
		this.roles = roles;
		this.permissionsGranted = permissionsGranted;
	}


	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}


	public List<Role> getRoles() {
		if (this.roles == null) { this.roles = new ArrayList<Role>(); }
		return this.roles;
	}
	
	public void setRoles(List<Role> aggrRoles) {
		this.roles = aggrRoles;
	}



	public Map<String, PermissionContext> getPermissionContexts()
	{
		if (this.permissionContexts == null)
		{
			this.permissionContexts = new HashMap<String, PermissionContext>();

			for (Role aggrRole : this.getRoles()) {
				//if (log.isDebugEnabled()) log.debug(this.name + ": adding all permission contexts inherited from aggregated Role " + aggrRole.getName() + ": " + aggrRole.getPermissionContexts());
				this.permissionContexts.putAll(aggrRole.getPermissionContexts());
			}

			for (PermissionGrantedKey permGrantedKey : this.getPermissionsGranted().keySet()) {
					//if (log.isDebugEnabled()) log.debug(this.name + ": adding native permission context: " + permGranted.getPermissionContext().getName());
					this.permissionContexts.put(permGrantedKey.getPermissionContext().getName(), permGrantedKey.getPermissionContext());
			}
		}

		return permissionContexts;
	}

	public boolean can(long requiredPerms, String permContextName)
	{ 
		// TODO: improve this so that the denied permissions are applied on 'top' of
		// a super user: i.e. grant All Perms except A,B,C.
		if (this.isSuper()) {
			return true;
		} else {
			if (requiredPerms < 1) 
			{
				// String msg = this.name + ".hasPermissions(" + permContextName + ", " + requiredPerms + "): when querying for permissions you must provide a permission strictly greater than 0. ";
				String msg = String.format("%s.can(%s,%s): when querying for permissions you must provide a permission strictly greater than 0.", this.name, permContextName, requiredPerms);
				log.error(msg);
				throw new IllegalArgumentException(msg);
			}
				
			return (this.getPermissionsValue(permContextName) & requiredPerms) == requiredPerms;
		}
	}

	public boolean hasPermissions(String permContextName, long requiredPerms) {
		return this.can(requiredPerms, permContextName);
	}


	public boolean can(String[] permNames, String permContextName)
	{
		long requiredPerms = 0;
		PermissionContext permContext = this.getPermissionContexts().get(permContextName);

		if (permContext == null) { // no permissions have been granted in that context
			return false;
		} else {
			requiredPerms = permContext.getPermissionsAsNumber(permNames);
			return this.can(requiredPerms, permContextName);
		}
	}


	public boolean hasPermissions(String permContextName, String[] permNames) {
		return this.can(permNames, permContextName);
	}


	public boolean can(String permissionName, String permContextName)
	{
		return this.can(new String[] {permissionName}, permContextName);
	}


	public boolean hasPermission(String permContextName, String permissionName) {
		return this.can(permissionName, permContextName);
	}


	public String[] getPermissions(String permContextName)
	{
		Set<String> permGrantedNames = new LinkedHashSet<String>();
		PermissionContext permContext = this.getPermissionContexts().get(permContextName);
		
		if (permContext != null)
		{
			for ( PermissionBit permBit : permContext.getPermissionBits() )
			{
				// if ( this.hasPermissions(permContextName, permBit.getValue()) )
				if ( this.can( permBit.getValue(), permContextName ) )
					permGrantedNames.add(permBit.getName());
			}
		}
		
		return permGrantedNames.toArray(new String[permGrantedNames.size()]);
	}


	/*
	public void grantPermissions(String permContextName, String[] permNames)
	{
		PermissionContext permContext = this.getPermissionContexts().get(permContextName);
		PermissionGranted permGranted = this.getPermsGrantedMap().get(permContext);

		if ( permGranted == null ) {
			permGranted = new PermissionGranted(permContext);
			this.getPermissionsGranted().add(permGranted);

			// force recalculation of the map on next invocation of getPermsGrantedMap
			this.permsGrantedMap = null; 
		}

		permGranted.setPermissions(permGranted.getPermissions() | permContext.getValue(permNames));
	}
	*/


	/*
	public void setPermissionsGranted(PermissionContext permContext, String[] permNames)
	{
		//PermissionContext permContext = this.getPermissionContexts().get(permContextName);
		PermissionGranted permGranted = this.getPermsGrantedMap().get(permContext);

		// if we are revoking the permissions, remove the permissionsGranted from the list
		if (permNames == null || permNames.length = 0) {}


		if ( permGranted == null ) {
			permGranted = new PermissionGranted(permContext);
			this.getPermissionsGranted().add(permGranted);
			// force recalculation of the map on next invocation of getPermsGrantedMap
			this.permsGrantedMap = null; 
		}

		permGranted.setPermissions(permContext.getValue(permNames));
	}
	*/


	/*
	public void setPermissionsDenied(String permContextName, String[] permNames)
	{
		PermissionContext permContext = this.getPermissionContexts().get(permContextName);

		permGranted = new PermissionGranted(permContext);
		permGranted.setPermissions(permContext.getValue(permNames));
		permGranted.setDeny(true);
		this.getPermissionsGranted().add(permGranted);

	}
	*/



	public long getPermissionsValue(String permContextName)
	{
		Long perms = this.getPermsUnionMap().get(permContextName);
		return (perms != null) ? perms : 0;
	}


	public void grantPermissions(PermissionContext permContext, long permsGranted)
	{
		this.validatePermissions(permContext, permsGranted);

		// if the permission granted is 0, remove the record; note that this will
		// not revoke all permissions for this PermissionContext, as there may be
		// other permissions in this PermissionContext that are inherited from the
		// Roles of this entity
		if (log.isInfoEnabled())
			log.info("granting permissions '" + permsGranted + "' in PermissionContext '" + permContext.getName() + "' to entity '" + this.name + "'");

		if (permsGranted == 0)
			this.getPermissionsGranted().remove(new PermissionGrantedKey(permContext, false));
		else 
			this.setPermissionGranted(permContext, false, permsGranted); 

		// recompute union of inherited and granted permissions
		this.permsUnionMap = null;
	}


	public void grantPermissions(String[] permsGranted, PermissionContext permContext) {
		this.grantPermissions(permContext, permContext.getValue(permsGranted));
	}

	public void grantPermission(String permGranted, PermissionContext permContext) {
		this.grantPermissions(new String[] {permGranted}, permContext);
	}


	public void denyPermissions(PermissionContext permContext, long permsDenied)
	{
		this.validatePermissions(permContext, permsDenied);
		// if the permission denied is 0, remove the corresponding isDeny record; 
		if (log.isInfoEnabled())
			log.info("denying permissions " + permsDenied + " in PermissionContext '" + permContext.getName() + "' from entity '" + this.name + "'");

		if (permsDenied == 0)
			this.getPermissionsGranted().remove(new PermissionGrantedKey(permContext, true));
		else 
			this.setPermissionGranted(permContext, true, permsDenied); 
	}

	public void denyPermissions(String[] permsDenied, PermissionContext permContext)
	{
		this.denyPermissions(permContext, permContext.getPermissionsAsNumber(permsDenied));
	}

	public void denyPermission(String permDenied, PermissionContext permContext)
	{
		this.denyPermissions(new String[] {permDenied}, permContext);
	}


	/** @return the permissions granted directly to this AuthorizationHolder, as a Long */
	private Long getPermissionGranted(PermissionContext permContext, boolean isDeny)
	{
		PermissionGrantedKey permGrantedKey = new PermissionGrantedKey(permContext, isDeny);
		return this.getPermissionsGranted().get(permGrantedKey);
	}


	/** Grant a permission directly to this Role */
	private void setPermissionGranted(PermissionContext permContext, boolean isDeny, Long value)
	{
		PermissionGrantedKey permGrantedKey = new PermissionGrantedKey(permContext, isDeny);
		this.getPermissionsGranted().put(permGrantedKey, value);
	}


	/** 
	 * @return a Map of the permissions held directly by this AuthorizationHolder, 
	 * rather than those inherited from a Role
	 */
	protected Map<PermissionGrantedKey, Long> getPermissionsGranted()
	{
		if (this.permissionsGranted == null)
			this.permissionsGranted = new HashMap<PermissionGrantedKey, Long>();

		return this.permissionsGranted;
	}


	/** 
	 * validates that the perms provided are between 0 and permContext.getMaxValue(),
	 * @throws IllegalArgumentException the perms provided are not between 0 and permContext.getMaxValue(),
	 */
	private void validatePermissions(PermissionContext permContext, long perms)
	{
		String msg = null;

		if (permContext == null)
			msg = "Attempting to assign permissions to entity '" + this.name + "' with null PermissionContext";

		else if ( perms < 0 ) 
			msg = "Attempting to assign a negative permission bitmask in the context of PermissionContext '" + permContext.getName() + "' to entity '" + this.name + "'";

		else if ( perms > permContext.getMaxValue() ) 
			msg = "The permission bitmask that you are trying to assign: '" + perms 
				+ "' has a value greater than the maximum '" + permContext.getMaxValue() 
				+ "' that can be assigned in the context of PermissionContext '" + permContext.getName() 
				+ "' to entity '" + this.name + "'";

		if (msg != null) 
		{
			log.error(msg);
			throw new IllegalArgumentException(msg);
		}
	}


	/*
	public Long getPermissionsGranted(String permContextName) 
	{
		throw new UnsupportedOperationException(
				"getPermissionsGranted(permContextName) has not yet been implemented");
		//return this.getPermissionsGranted().get(new PermissionContext(permContextName));
	}
	*/
	

	/*
	public void setPermissionsGranted(String permContextName, Long permissions) 
	{
		throw new UnsupportedOperationException(
				"setPermissionsGranted(permContextName, permissions) has not yet been implemented");
	}
	*/

	

	/** 
	 * Iterates over all aggregated Roles and, for each PermissionContext, 
	 * calculates the union of all Permissions granted; 
	 * this is where the heavy lifting of this bitmask-based implementation occurs
	 */
	private Map<String, Long> getPermsUnionMap()
	{
		if (permsUnionMap == null)
		{
			Chronometer timer = new Chronometer(true);
			permsUnionMap = new HashMap<String, Long>();

			// flatten permissions of all aggregated Roles, if any
			if (!this.getRoles().isEmpty())
			{
				if (log.isDebugEnabled()) 
				{
					// pre-eventively gather all permission context names to yield cleaner debug logs
					log.debug(this.name + ": aggregating permissions from aggregated Roles for permission contexts: " + this.getPermissionContexts().keySet());
				}

				for (Role aggrRole : this.getRoles())
				{
					//if (log.isDebugEnabled()) log.debug("begin iterating over " + aggrRole.getName());
					for (String context : aggrRole.getPermissionContexts().keySet())
					{
						long permsUnion = (permsUnionMap.get(context) != null) ? permsUnionMap.get(context).longValue() : 0;
						long aggrRolePerms = aggrRole.getPermissionsValue(context);

						if (log.isDebugEnabled()) {
							log.debug(this.name + "[" + context + "]: permsUnion(" + permsUnion + ") + aggrRolePerms(" + aggrRolePerms + ") =  newPermsUnion(" + (permsUnion | aggrRolePerms) + ")");
						}

						permsUnionMap.put(context, new Long(permsUnion | aggrRolePerms));
					}
					//if (log.isDebugEnabled()) log.debug("end iterating over " + aggrRole.getName());
				}
			}


			if (!this.getPermissionsGranted().isEmpty())
			{
				if (log.isDebugEnabled()) log.debug(this.name + ": aggregating native permissions: " + this.getPermissionsGranted());

				for (PermissionGrantedKey permGrantedKey : this.getPermissionsGranted().keySet())
				{
					String context = permGrantedKey.getPermissionContext().getName();

					long inheritedPerms = (permsUnionMap.get(context) != null) ? permsUnionMap.get(context).longValue() : 0;
					//long permGranted    = permissionsGranted.get(permGrantedKey).getPermissions();
					long permGranted    = this.getPermissionsGranted().get(permGrantedKey);

					// substract or add the permission
					long permsUnion = permGrantedKey.isDeny() ?  (inheritedPerms - (permGranted & inheritedPerms)) : inheritedPerms | permGranted;

					if (log.isDebugEnabled()) 
					{
						String op = permGrantedKey.isDeny() ? "-" : "+";
						log.debug(this.name + "[" + context + "]: inheritedPerms(" + inheritedPerms + ") " + op + " permGranted(" + permGranted + ") =  permsUnion(" + permsUnion + ")");
					}

					permsUnionMap.put(context, permsUnion);
				}
			}
			if (log.isDebugEnabled()) log.debug(this.name + ": calculated permissions for role in " + timer.printElapsedTime());
		}

		return this.permsUnionMap;
	}

	
	public boolean isSuper() {
		return this.isSuper;
	}

	public void setSuper(boolean isSuper) {
		log.warn(String.format("Setting %s to Super User!", this.name));
		this.isSuper = isSuper;
	}


	public boolean isAlmighty() {
		return this.isSuper();
	}

	public void setAlmighty(boolean isAlmighty) {
		this.setSuper(isAlmighty);
	}


} // end class

