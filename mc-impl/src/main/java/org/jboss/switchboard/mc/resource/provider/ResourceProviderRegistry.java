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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.switchboard.mc.spi.MCBasedResourceProvider;
import org.jboss.switchboard.spi.EnvironmentEntryType;

/**
 * ResourceProviderRegistry
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ResourceProviderRegistry
{

   private Map<Class<?>, MCBasedResourceProvider<? extends EnvironmentEntryType>> resourceProviders = new ConcurrentHashMap<Class<?>, MCBasedResourceProvider<? extends EnvironmentEntryType>>();

   public void setResourceProviders(Collection<MCBasedResourceProvider<EnvironmentEntryType>> providers)
   {
      if (providers == null)
      {
         throw new IllegalArgumentException("ResourceProvider(s) cannot be null during registration");
      }
      for (MCBasedResourceProvider<EnvironmentEntryType> provider : providers)
      {
         this.registerProvider(provider);
      }
   }

   public void registerProvider(MCBasedResourceProvider<? extends EnvironmentEntryType> provider)
   {
      if (provider == null)
      {
         throw new IllegalArgumentException("ResourceProvider cannot be null during registration");
      }
      Class<? extends EnvironmentEntryType> type = provider.getEnvironmentEntryType();
      this.resourceProviders.put(type, provider);
   }

   public MCBasedResourceProvider<EnvironmentEntryType> getResourceProvider(Class<? extends EnvironmentEntryType> type)
   {

      MCBasedResourceProvider<? extends EnvironmentEntryType> provider = this.resourceProviders.get(type);
      if (provider != null)
      {
         return (MCBasedResourceProvider<EnvironmentEntryType>) provider;
      }
      Class<?> entryType = type.getSuperclass();
      while (entryType != null && EnvironmentEntryType.class.isAssignableFrom(entryType))
      {
         provider = this.getResourceProvider((Class<EnvironmentEntryType>) entryType);
         if (provider != null)
         {
            return (MCBasedResourceProvider<EnvironmentEntryType>) provider;
         }
         entryType = entryType.getSuperclass();

      }

      Class<?>[] interfaces = type.getInterfaces();
      for (Class<?> intf : interfaces)
      {
         if (EnvironmentEntryType.class.isAssignableFrom(intf))
         {
            provider = this.getResourceProvider((Class<EnvironmentEntryType>) intf);
            if (provider != null)
            {
               return (MCBasedResourceProvider<EnvironmentEntryType>) provider;
            }
         }
      }

      return (MCBasedResourceProvider<EnvironmentEntryType>) provider;

   }

}
