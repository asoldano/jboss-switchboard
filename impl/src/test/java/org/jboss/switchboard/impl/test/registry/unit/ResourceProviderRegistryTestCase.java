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
package org.jboss.switchboard.impl.test.registry.unit;

import junit.framework.Assert;

import org.jboss.switchboard.impl.ResourceProviderRegistry;
import org.jboss.switchboard.impl.test.common.DummyEJBReferenceProvider;
import org.jboss.switchboard.impl.test.common.DummyEJBReferenceType;
import org.jboss.switchboard.impl.test.common.DummyPersistenceContextProvider;
import org.jboss.switchboard.impl.test.common.DummyPersistenceContextType;
import org.jboss.switchboard.impl.test.common.EjbRefType;
import org.jboss.switchboard.spi.EnvironmentEntryType;
import org.jboss.switchboard.spi.ResourceProvider;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link ResourceProviderRegistry}
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ResourceProviderRegistryTestCase
{

   private ResourceProviderRegistry<String> registry;
   
   @Before
   public void beforeTest()
   {
      this.registry = new ResourceProviderRegistry<String>();
   }
   
   /**
    * Test that the {@link ResourceProviderRegistry#getResourceProvider(Class)} returns the correct
    * (registered) {@link ResourceProvider}
    */
   @Test
   public void testRegister()
   {
      // register EJB ref provider
      ResourceProvider<String, ? extends EnvironmentEntryType> ejbRefProvider = new DummyEJBReferenceProvider<String>();
      registry.registerProvider(ejbRefProvider);
      
      // register PC ref provider
      ResourceProvider<String, ? extends EnvironmentEntryType> pcRefProvider = new DummyPersistenceContextProvider<String>();
      registry.registerProvider(pcRefProvider);
      
      // test EJB ref provider
      ResourceProvider<String, ? extends EnvironmentEntryType> provider = this.registry.getResourceProvider(DummyEJBReferenceType.class);
      Assert.assertNotNull("Could not find a provider for " +  DummyEJBReferenceType.class, provider);
      Assert.assertEquals("Unexpected resource provider for " + DummyEJBReferenceType.class, ejbRefProvider, provider);
      
      // test PC ref provider
      ResourceProvider<String, ? extends EnvironmentEntryType> anotherProvider = this.registry.getResourceProvider(DummyPersistenceContextType.class);
      Assert.assertNotNull("Could not find a provider for " +  DummyPersistenceContextType.class, anotherProvider);
      Assert.assertEquals("Unexpected resource provider for " + DummyPersistenceContextType.class, pcRefProvider, anotherProvider);
      
      // test a non-existent provider type
      ResourceProvider<String, ? extends EnvironmentEntryType> nonExistentProvider = this.registry.getResourceProvider(EnvironmentEntryType.class);
      Assert.assertNull("(Unexpectedly) found a provider for type " +  EnvironmentEntryType.class, nonExistentProvider);
      
      
   }
   
   
   
}
