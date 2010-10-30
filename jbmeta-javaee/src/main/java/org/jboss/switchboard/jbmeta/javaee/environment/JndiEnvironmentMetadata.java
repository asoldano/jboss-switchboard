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

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.metadata.javaee.spec.AnnotatedEJBReferenceMetaData;
import org.jboss.metadata.javaee.spec.AnnotatedEJBReferencesMetaData;
import org.jboss.metadata.javaee.spec.EJBLocalReferenceMetaData;
import org.jboss.metadata.javaee.spec.EJBLocalReferencesMetaData;
import org.jboss.metadata.javaee.spec.EJBReferenceMetaData;
import org.jboss.metadata.javaee.spec.EJBReferencesMetaData;
import org.jboss.metadata.javaee.spec.Environment;
import org.jboss.metadata.javaee.spec.EnvironmentEntriesMetaData;
import org.jboss.metadata.javaee.spec.EnvironmentEntryMetaData;
import org.jboss.metadata.javaee.spec.MessageDestinationReferenceMetaData;
import org.jboss.metadata.javaee.spec.MessageDestinationReferencesMetaData;
import org.jboss.metadata.javaee.spec.PersistenceContextReferenceMetaData;
import org.jboss.metadata.javaee.spec.PersistenceContextReferencesMetaData;
import org.jboss.metadata.javaee.spec.PersistenceUnitReferenceMetaData;
import org.jboss.metadata.javaee.spec.PersistenceUnitReferencesMetaData;
import org.jboss.metadata.javaee.spec.ResourceEnvironmentReferenceMetaData;
import org.jboss.metadata.javaee.spec.ResourceEnvironmentReferencesMetaData;
import org.jboss.metadata.javaee.spec.ResourceReferenceMetaData;
import org.jboss.metadata.javaee.spec.ResourceReferencesMetaData;
import org.jboss.metadata.javaee.spec.ServiceReferenceMetaData;
import org.jboss.metadata.javaee.spec.ServiceReferencesMetaData;
import org.jboss.switchboard.spi.EnvironmentEntryType;
import org.jboss.switchboard.spi.JndiEnvironment;

/**
 * JndiEnvironmentMetadata
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JndiEnvironmentMetadata implements JndiEnvironment
{

   private Environment delegate;

   private Collection<EnvironmentEntryType> environmentEntries;

   public JndiEnvironmentMetadata(Environment environment)
   {
      this.delegate = environment;
      this.initEnvironmentReferences();
   }

   @Override
   public Collection<EnvironmentEntryType> getEntries()
   {
      return this.environmentEntries;
   }

   @Override
   public void addEntry(EnvironmentEntryType entry)
   {
      if (entry == null)
      {
         throw new IllegalArgumentException("Cannot add null environment entry");
      }
      
      if (this.environmentEntries == null)
      {
         this.environmentEntries = new ArrayList<EnvironmentEntryType>();
      }
      this.environmentEntries.add(entry);
   }
   
   private void initEnvironmentReferences()
   {
      this.environmentEntries = new ArrayList<EnvironmentEntryType>();

      // simple env entry references
      EnvironmentEntriesMetaData envEntries = this.delegate.getEnvironmentEntries();
      if (envEntries != null)
      {
         for (EnvironmentEntryMetaData envEntry : envEntries)
         {
            this.environmentEntries.add(new EnvEntry(envEntry));
         }
      }
      
      // annotated ejb references
      AnnotatedEJBReferencesMetaData annotatedEjbRefs = this.delegate.getAnnotatedEjbReferences();
      if (annotatedEjbRefs != null)
      {
         for (AnnotatedEJBReferenceMetaData annotatedEjbRef : annotatedEjbRefs)
         {
            this.environmentEntries.add(new AnnotatedEJBReference(annotatedEjbRef));
         }
      }

      // ejb local references
      EJBLocalReferencesMetaData ejbLocalRefs = this.delegate.getEjbLocalReferences();
      if (ejbLocalRefs != null)
      {
         for (EJBLocalReferenceMetaData ejbLocalRef : ejbLocalRefs)
         {
            this.environmentEntries.add(new EJBLocalReference(ejbLocalRef));
         }
      }

      // ejb reference
      EJBReferencesMetaData ejbRefs = this.delegate.getEjbReferences();
      if (ejbRefs != null)
      {
         for (EJBReferenceMetaData ejbRef : ejbRefs)
         {
            this.environmentEntries.add(new EJBReference(ejbRef));
         }
      }

      // persistence unit reference
      PersistenceUnitReferencesMetaData persistenceUnitRefs = this.delegate.getPersistenceUnitRefs();
      if (persistenceUnitRefs != null)
      {
         for (PersistenceUnitReferenceMetaData persistenceUnitRef : persistenceUnitRefs)
         {
            this.environmentEntries.add(new PersistenceUnitReference(persistenceUnitRef));
         }
      }

      // persistence context reference
      PersistenceContextReferencesMetaData persistenceCtxRefs = this.delegate.getPersistenceContextRefs();
      if (persistenceCtxRefs != null)
      {
         for (PersistenceContextReferenceMetaData persistenceCtxRef : persistenceCtxRefs)
         {
            this.environmentEntries.add(new PersistenceContextReference(persistenceCtxRef));
         }
      }

      // resource env reference
      ResourceEnvironmentReferencesMetaData resourceEnvRefs = this.delegate.getResourceEnvironmentReferences();
      if (resourceEnvRefs != null)
      {
         for (ResourceEnvironmentReferenceMetaData resourceEnvRef : resourceEnvRefs)
         {
            this.environmentEntries.add(new ResourceEnvReference(resourceEnvRef));
         }
      }
      
      // resource reference
      ResourceReferencesMetaData resourceRefs = this.delegate.getResourceReferences();
      if (resourceRefs != null)
      {
         for (ResourceReferenceMetaData resourceRef : resourceRefs)
         {
            this.environmentEntries.add(new JBossResourceReference(resourceRef));
         }
      }
      
      // message destination references
      MessageDestinationReferencesMetaData messageDestRefs = this.delegate.getMessageDestinationReferences();
      if (messageDestRefs != null)
      {
         for (MessageDestinationReferenceMetaData messageDestRef : messageDestRefs)
         {
            this.environmentEntries.add(new MessageDestinationReference(messageDestRef));
         }
      }
      
      // service references
      ServiceReferencesMetaData serviceRefs = this.delegate.getServiceReferences();
      if (serviceRefs != null)
      {
         for (ServiceReferenceMetaData serviceRef : serviceRefs)
         {
            this.environmentEntries.add(new ServiceReference(serviceRef));
         }
      }
   }

   
}
