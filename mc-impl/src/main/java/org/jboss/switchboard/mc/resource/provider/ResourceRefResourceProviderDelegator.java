/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.switchboard.mc.resource.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.switchboard.impl.resource.LinkRefResource;
import org.jboss.switchboard.javaee.environment.ResourceRefType;
import org.jboss.switchboard.javaee.jboss.environment.JBossResourceRefType;
import org.jboss.switchboard.mc.spi.MCBasedResourceProvider;
import org.jboss.switchboard.spi.Resource;
import org.jboss.switchboard.spi.ResourceProvider;

/**
 * ResourceRefResourceProvider
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ResourceRefResourceProviderDelegator implements MCBasedResourceProvider<JBossResourceRefType>
{

   /**
    * Resource providers for various "types" of resource-ref entries.
    * The key of this map, is the type (for ex: javax.sql.DataSource) of the resource-ref and the value
    * is the ResourceProvider   
    */
   private Map<String, MCBasedResourceProvider<JBossResourceRefType>> typedResourceRefResourceProviders = new HashMap<String, MCBasedResourceProvider<JBossResourceRefType>>();
   
   /**
    * Resource providers, for resource-ref entries, which do not work on any specific "type"
    * of a resource-ref. For example, a managed bean resource-ref {@link ResourceProvider} 
    * could process a resource-ref of any (user defined) managed bean type.
    */
   private Collection<MCBasedResourceProvider<JBossResourceRefType>> fallbackResourceRefResourceProviders = new ArrayList<MCBasedResourceProvider<JBossResourceRefType>>();

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<JBossResourceRefType> getEnvironmentEntryType()
   {
      return JBossResourceRefType.class;
   }

   /**
    * 
    */
   @Override
   public Resource provide(DeploymentUnit deploymentUnit, JBossResourceRefType resRef)
   {
      // first check lookup name
      String lookupName = resRef.getLookupName();
      if (lookupName != null && !lookupName.trim().isEmpty())
      {
         return new LinkRefResource(lookupName);
      }

      // now check mapped name
      String mappedName = resRef.getMappedName();
      if (mappedName != null && !mappedName.trim().isEmpty())
      {
         return new LinkRefResource(mappedName);
      }
      
      // now check (JBoss specific jndi name)!
      String jndiName = resRef.getJNDIName();
      if (jndiName != null && !jndiName.trim().isEmpty())
      {
         return new LinkRefResource(jndiName);
      }
      
      // get the resource type and see if there's a resource-ref resource provider
      // available for that type
      String resourceType = this.getResourceRefType(deploymentUnit.getClassLoader(), resRef);
      if (resourceType != null)
      {
         resourceType = resourceType.trim();
      }
      MCBasedResourceProvider<JBossResourceRefType> resourceRefProvider = this.typedResourceRefResourceProviders.get(resourceType);
      // if available, process it
      if (resourceRefProvider != null)
      {
         return resourceRefProvider.provide(deploymentUnit, resRef);
      }
      // fallback on the other resource-ref resource provider(s), if any.
      for (MCBasedResourceProvider<JBossResourceRefType> provider : this.fallbackResourceRefResourceProviders)
      {
         if (provider != null)
         {
            Resource resource = provider.provide(deploymentUnit, resRef);
            if (resource != null)
            {
               return resource;
            }
         }
      }
      
      throw new RuntimeException("No ResourceProvider could process resource-ref named: " + resRef.getName()
            + " for deployment unit: " + deploymentUnit);
   }
   
   public void setTypedResourceRefResourceProviders(Map<String, MCBasedResourceProvider<JBossResourceRefType>> providers)
   {
      if (providers == null)
      {
         throw new IllegalArgumentException("Cannot set null to typed resource-ref resource providers");
      }
      this.typedResourceRefResourceProviders = providers;
   }
   
   public void setFallbackResourceRefResourceProviders(Collection<MCBasedResourceProvider<JBossResourceRefType>> providers)
   {
      if (providers == null)
      {
         throw new IllegalArgumentException("Cannot set null to resource-ref resource providers");
      }
      this.fallbackResourceRefResourceProviders = providers;
   }
   
   /**
    * Returns the res-type for the passed {@link ResourceRefType}.
    * <p>
    *   If the passed resource-ref has the res-type explicitly specified, then
    *   that value is returned. Else, this method checks for the presence of any
    *   injection targets for this resource-ref. If there's a injection target, then
    *   the res-type is deduced based on the field/method of the injection target. 
    * </p>
    * <p>
    *   This method returns null if the res-type isn't explicitly specified and 
    *   if the res-type could not be deduced from the injection targets of this
    *   resource-ref.
    * </p>
    * 
    * @param cl The {@link ClassLoader} to be used during processing the metadata
    * @param resourceRef The Java EE resource-ref
    * @return
    */
   private String getResourceRefType(ClassLoader cl, ResourceRefType resourceRef)
   {
      // first check whether the type is explicitly specified
      String explicitType = resourceRef.getResourceType();
      if (explicitType != null && !explicitType.isEmpty())
      {
         return explicitType;
      }
      Class<?> type = InjectionTargetUtil.getInjectionTargetPropertyType(cl, resourceRef);
      return type == null ? null : type.getName();
   }

}
