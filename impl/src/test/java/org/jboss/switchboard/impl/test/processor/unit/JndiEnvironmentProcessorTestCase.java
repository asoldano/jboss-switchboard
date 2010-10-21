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
package org.jboss.switchboard.impl.test.processor.unit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import junit.framework.Assert;

import org.jboss.switchboard.impl.JndiEnvironmentProcessor;
import org.jboss.switchboard.impl.ResourceProviderRegistry;
import org.jboss.switchboard.impl.test.common.DummyEJBReferenceProvider;
import org.jboss.switchboard.impl.test.common.DummyEJBReferenceType;
import org.jboss.switchboard.impl.test.common.DummyJndiEnvironment;
import org.jboss.switchboard.impl.test.common.DummyPersistenceContextProvider;
import org.jboss.switchboard.impl.test.common.DummyPersistenceContextType;
import org.jboss.switchboard.spi.EnvironmentEntryType;
import org.jboss.switchboard.spi.JndiEnvironment;
import org.jboss.switchboard.spi.Resource;
import org.jboss.switchboard.spi.ResourceProvider;
import org.junit.Test;

/**
 * Test {@link JndiEnvironmentProcessor}
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JndiEnvironmentProcessorTestCase
{

   /**
    * Test {@link JndiEnvironmentProcessor#process(Object, JndiEnvironment)}
    */
   @Test
   public void testProcessor()
   {
      ResourceProviderRegistry registry = new ResourceProviderRegistry();
      // register EJB ref provider
      ResourceProvider<String, ? extends EnvironmentEntryType> ejbRefProvider = new DummyEJBReferenceProvider<String>();
      registry.registerProvider(ejbRefProvider);
      // register PC ref provider
      ResourceProvider<String, ? extends EnvironmentEntryType> pcRefProvider = new DummyPersistenceContextProvider<String>();
      registry.registerProvider(pcRefProvider);
      
      // create the env entries
      String ejbRefEncJNDIName = "ejbRef";
      EnvironmentEntryType ejbRef = new DummyEJBReferenceType(ejbRefEncJNDIName);
      String pcRefEncJNDIName = "pcRef";
      EnvironmentEntryType pcRef = new DummyPersistenceContextType(pcRefEncJNDIName);
      Collection<EnvironmentEntryType> entries = new HashSet<EnvironmentEntryType>();
      entries.add(ejbRef);
      entries.add(pcRef);
      // create the environment
      JndiEnvironment environment = new DummyJndiEnvironment(entries);

      // create the processor
      JndiEnvironmentProcessor processor =  new JndiEnvironmentProcessor(registry);
      // now process and create the resource(s)
      Map<String, Resource> resources = processor.process(new String("Dummy context"), environment);
      
      // test the resources
      Assert.assertNotNull("No resources were created", resources);
      Assert.assertEquals("Unexpected number of resources created", 2, resources.size());
      
      // make sure ejb ref has the corresponding resource
      Resource ejbRefResource = resources.get(ejbRefEncJNDIName);
      Assert.assertNotNull("No resource created for enc jndi name: " + ejbRefEncJNDIName, ejbRefResource);
      // make sure pc ref has the corresponding resource      
      Resource pcRefResource = resources.get(pcRefEncJNDIName);
      Assert.assertNotNull("No resource created for enc jndi name: " + pcRefEncJNDIName, pcRefResource);
   }
}
