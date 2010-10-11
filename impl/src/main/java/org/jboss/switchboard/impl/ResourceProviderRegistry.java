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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
         this.registerProvider(provider);
      }
   }

   public void registerProvider(ResourceProvider<C, ? extends EnvironmentEntryType> provider)
   {
      if (provider == null)
      {
         throw new IllegalArgumentException("ENCBindingProvider cannot be null during registration");
      }
      Class<?> type = this.getProviderEnvEntryType(provider);
      this.bindingProviders.put(type, provider);
   }

   public ResourceProvider<C, ? extends EnvironmentEntryType> getResourceProvider(Class<? extends EnvironmentEntryType> type)
   {
      return this.bindingProviders.get(type);
   }
   
   /**
    * Determine the Processor<T, ?> T generic processorType class.
    * 
    * @param processor
    * @return The Class for the T parameter type.
    */
   private Class<?> getProviderEnvEntryType(ResourceProvider<C, ? extends EnvironmentEntryType> provider)
   {
      // Find the ResourceProvider<C, T extends EnvironmentEntryType> interface
      Type[] interfaces = provider.getClass().getGenericInterfaces();
      Type resourceProviderIntf = null;
      for(Type t : interfaces)
      {
         ParameterizedType pt = (ParameterizedType) t;
         Type rawType = pt.getRawType();
         if((rawType instanceof Class) && ((Class<?>)rawType).getName().equals(ResourceProvider.class.getName()))
         {
            resourceProviderIntf = t;
            break;
         }
      }
      if(resourceProviderIntf == null)
         throw new IllegalStateException("No generic Processor interface found on: "+provider);

      // Get the type of the T parameter
      ParameterizedType pt = (ParameterizedType) resourceProviderIntf;
      Type envEntryTypeParameter = pt.getActualTypeArguments()[1];
      Class<?> evnEntryType = null;
      if(envEntryTypeParameter instanceof Class)
         evnEntryType = (Class<?>) envEntryTypeParameter;
      else if(envEntryTypeParameter instanceof TypeVariable)
      {
         TypeVariable tv = (TypeVariable) envEntryTypeParameter;
         evnEntryType = (Class<?>)tv.getBounds()[0];
      }
      
      return evnEntryType;
   }

}
