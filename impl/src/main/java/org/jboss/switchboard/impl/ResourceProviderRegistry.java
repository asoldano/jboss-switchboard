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
package org.jboss.switchboard.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.switchboard.spi.EnvironmentEntryType;
import org.jboss.switchboard.spi.ResourceProvider;

/**
 * EnvironmentProcessor
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ResourceProviderRegistry<C>
{

   private  Map<Class<?>, ResourceProvider<C, ? extends EnvironmentEntryType>> bindingProviders = new ConcurrentHashMap<Class<?>, ResourceProvider<C, ? extends EnvironmentEntryType>>();

   public void registerProviders(Collection<ResourceProvider<C, ? extends EnvironmentEntryType>> providers)
   {
      if (providers == null)
      {
         throw new IllegalArgumentException("ENCBindingProvider(s) cannot be null during registration");
      }
      for (ResourceProvider<C, ?> provider : providers)
      {
        // this.bindingProviders.put(provider.getType(), provider);
      }
   }

   public void registerProvider(ResourceProvider<C, ? extends EnvironmentEntryType> provider)
   {
      if (provider == null)
      {
         throw new IllegalArgumentException("ENCBindingProvider cannot be null during registration");
      }
     // this.bindingProviders.put(provider.getType(), provider);
   }

   public <T extends EnvironmentEntryType> ResourceProvider<C, T> getResourceProvider(T type)
   {
      return null;
      //return this.bindingProviders.get(type);
   }

}
