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
package org.jboss.switchboard.jbmeta.javaee.environment;

import java.util.Properties;

import javax.persistence.PersistenceContextType;

import org.jboss.metadata.javaee.spec.PersistenceContextReferenceMetaData;
import org.jboss.metadata.javaee.spec.PropertiesMetaData;
import org.jboss.metadata.javaee.spec.PropertyMetaData;
import org.jboss.switchboard.javaee.environment.PersistenceContextRefType;

/**
 * PersistenceContextReference
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class PersistenceContextReference extends JavaEEResource implements PersistenceContextRefType
{

   private PersistenceContextReferenceMetaData delegate;

   public PersistenceContextReference(PersistenceContextReferenceMetaData delegate)
   {
      super(delegate.getLookupName(), delegate.getMappedName(), InjectionTargetConverter.convert(delegate
            .getInjectionTargets()));
      this.delegate = delegate;
   }

   @Override
   public Properties getPersistenceProperties()
   {
      PropertiesMetaData propertiesMetaData = this.delegate.getProperties();
      if (propertiesMetaData == null)
      {
         return null;
      }
      Properties properties = new Properties();
      for (PropertyMetaData propertyMetaData : propertiesMetaData)
      {
         properties.put(propertyMetaData.getName(), propertyMetaData.getValue());
      }
      return properties;
   }

   @Override
   public String getPersistenceUnitName()
   {
      return this.delegate.getPersistenceUnitName();
   }

   @Override
   public String getName()
   {
      return this.delegate.getPersistenceContextRefName();
   }

   @Override
   public PersistenceContextType getPersistenceContextType()
   {
      return this.delegate.getPersistenceContextType();

   }
}
