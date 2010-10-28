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
package org.jboss.switchboard.mc.test.registry.unit;

import junit.framework.Assert;

import org.jboss.switchboard.mc.resource.provider.ResourceProviderRegistry;
import org.jboss.switchboard.mc.spi.MCBasedResourceProvider;
import org.jboss.switchboard.mc.test.common.DummyEJBReferenceProvider;
import org.jboss.switchboard.mc.test.common.DummyEJBReferenceType;
import org.jboss.switchboard.mc.test.common.DummyPersistenceContextProvider;
import org.jboss.switchboard.mc.test.common.DummyPersistenceContextType;
import org.jboss.switchboard.spi.EnvironmentEntryType;
import org.jboss.switchboard.spi.ResourceProvider;
import org.junit.Test;

/**
 * Tests {@link ResourceProviderRegistry}
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ResourceProviderRegistryTestCase
{

   
   /**
    * Test that the {@link ResourceProviderRegistry#getResourceProvider(Class)} returns the correct
    * (registered) {@link ResourceProvider}
    */
   @Test
   public void testRegister()
   {
     ResourceProviderRegistry registry = new ResourceProviderRegistry();

      // register EJB ref provider
      MCBasedResourceProvider<? extends EnvironmentEntryType> ejbRefProvider = new DummyEJBReferenceProvider();
      registry.registerProvider(ejbRefProvider);
      
      // register PC ref provider
      MCBasedResourceProvider<? extends EnvironmentEntryType> pcRefProvider = new DummyPersistenceContextProvider();
      registry.registerProvider(pcRefProvider);
      
      // test EJB ref provider
      MCBasedResourceProvider<EnvironmentEntryType> provider = registry.getResourceProvider(DummyEJBReferenceType.class);
      Assert.assertNotNull("Could not find a provider for " +  DummyEJBReferenceType.class, provider);
      Assert.assertEquals("Unexpected resource provider for " + DummyEJBReferenceType.class, ejbRefProvider, provider);
      
      // test PC ref provider
      MCBasedResourceProvider<EnvironmentEntryType> anotherProvider = registry.getResourceProvider(DummyPersistenceContextType.class);
      Assert.assertNotNull("Could not find a provider for " +  DummyPersistenceContextType.class, anotherProvider);
      Assert.assertEquals("Unexpected resource provider for " + DummyPersistenceContextType.class, pcRefProvider, anotherProvider);
      
      // test a non-existent provider type
      MCBasedResourceProvider<EnvironmentEntryType> nonExistentProvider = registry.getResourceProvider(EnvironmentEntryType.class);
      Assert.assertNull("(Unexpectedly) found a provider for type " +  EnvironmentEntryType.class, nonExistentProvider);
      
      
   }
   
   
}
