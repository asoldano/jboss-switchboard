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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.LinkRef;
import javax.naming.NamingException;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.switchboard.javaee.environment.ResourceEnvRefType;
import org.jboss.switchboard.mc.resource.LinkRefResource;
import org.jboss.switchboard.mc.spi.MCBasedResourceProvider;
import org.jboss.switchboard.spi.Resource;

/**
 * ResourceEnvRefProvider
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ResourceEnvRefProvider implements MCBasedResourceProvider<ResourceEnvRefType>
{

   @Override
   public Class<ResourceEnvRefType> getEnvironmentEntryType()
   {
      return ResourceEnvRefType.class;
   }

   @Override
   public Resource provide(DeploymentUnit deploymentUnit, ResourceEnvRefType resEnvRef)
   {
      // first check lookup name
      String lookupName = resEnvRef.getLookupName();
      if (lookupName != null && !lookupName.trim().isEmpty())
      {
         return this.getLinkRefResource(this.getContext(deploymentUnit), lookupName);
      }
      
      // now check mapped name
      String mappedName = resEnvRef.getMappedName();
      if (mappedName != null && !mappedName.trim().isEmpty())
      {
         return this.getLinkRefResource(this.getContext(deploymentUnit), mappedName);
      }
      
      // Without a mapped-name or a lookup-name, we can't resolve a
      // resource-env-ref. So throw an exception
      throw new RuntimeException("Either lookup-name or mapped-name has to be specified for resource-env-ref named "
            + resEnvRef.getName() + " of type " + resEnvRef.getResourceType());
   }

   private LinkRefResource getLinkRefResource(Context ctx, String jndiName)
   {
      return new LinkRefResource(ctx, new LinkRef(jndiName));
   }

   private Context getContext(DeploymentUnit deploymentUnit)
   {
      // TODO: We somehow have to get a JavaEEModule out of the DU.
      // The JavaEEModuleInformer just provides a MC bean name of the JavaEEModule,
      // which really isn't sufficient. We need an instance of the JavaEEModule, even
      // if the JavaEEModule isn't fully initialized.
      
      // This is temporary
      try
      {
         return new InitialContext();
      }
      catch (NamingException ne)
      {
         throw new RuntimeException(ne);
      }
   }
}
