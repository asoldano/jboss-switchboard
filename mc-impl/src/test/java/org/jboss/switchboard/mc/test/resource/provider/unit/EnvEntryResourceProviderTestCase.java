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
package org.jboss.switchboard.mc.test.resource.provider.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.switchboard.javaee.environment.SimpleEnvironmentEntryType;
import org.jboss.switchboard.mc.resource.provider.EnvEntryResourceProvider;
import org.jboss.switchboard.mc.test.common.EnvEntry;
import org.jboss.switchboard.spi.Resource;
import org.junit.Test;

/**
 * Tests {@link EnvEntryResourceProvider}
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class EnvEntryResourceProviderTestCase
{

   /**
    * Test env-entry references of type String
    */
   @Test
   public void testStringEnvEntryType()
   {
      String value = "~!@#$%^&*()_+=-0987654321`abcdefghijklmnopqrstuvwxyz:;.<>?/|{}[]";
      SimpleEnvironmentEntryType envEntry = new EnvEntry("testString", String.class.getName(), value);

      DeploymentUnit mockDU = this.getMockDU();

      EnvEntryResourceProvider provider = new EnvEntryResourceProvider();
      Resource resource = provider.provide(mockDU, envEntry);

      this.assertResource(envEntry, resource, String.class, value);
   }

   /**
    * Test env-entry references of type int/Integer
    */
   @Test
   public void testIntEnvEntryType()
   {
      int primitive = 3456;
      SimpleEnvironmentEntryType envEntry = new EnvEntry("testPrimitiveInt", "int", String.valueOf(primitive));

      DeploymentUnit mockDU = this.getMockDU();

      EnvEntryResourceProvider provider = new EnvEntryResourceProvider();
      Resource resource = provider.provide(mockDU, envEntry);
      // test
      this.assertResource(envEntry, resource, Integer.class, primitive);
      
      Integer wrapperInt = 2302;
      SimpleEnvironmentEntryType wrapperIntEnvEntry = new EnvEntry("testWrapperInt", Integer.class.getName(), String.valueOf(wrapperInt));
      resource = provider.provide(mockDU, wrapperIntEnvEntry);
      // test
      this.assertResource(wrapperIntEnvEntry, resource, Integer.class, wrapperInt);
   }
   
   /**
    * Test env-entry references of type enum
    */
   @Test
   public void testEnumEnvEntryType()
   {
      TimeUnit timeUnit = TimeUnit.NANOSECONDS;
      SimpleEnvironmentEntryType envEntry = new EnvEntry("testEnum", TimeUnit.class.getName(), timeUnit.name());

      DeploymentUnit mockDU = this.getMockDU();

      EnvEntryResourceProvider provider = new EnvEntryResourceProvider();
      Resource resource = provider.provide(mockDU, envEntry);
      // test
      this.assertResource(envEntry, resource, TimeUnit.class, timeUnit);
      
   }
   
   /**
    * Test env-entry references of type java.lang.Class
    */
   @Test
   public void testClassEnvEntryType()
   {
      Class<?> klass = this.getClass();
      SimpleEnvironmentEntryType envEntry = new EnvEntry("testClass", Class.class.getName(), klass.getName());

      DeploymentUnit mockDU = this.getMockDU();

      EnvEntryResourceProvider provider = new EnvEntryResourceProvider();
      Resource resource = provider.provide(mockDU, envEntry);
      // test
      this.assertResource(envEntry, resource, Class.class, klass);
      
   }

   /**
    * Returns a mock {@link DeploymentUnit}
    * @return
    */
   private DeploymentUnit getMockDU()
   {
      // setup a mock DU
      DeploymentUnit mockDU = mock(DeploymentUnit.class);
      when(mockDU.getClassLoader()).thenReturn(Thread.currentThread().getContextClassLoader());
      return mockDU;
   }
   
   /**
    * Tests that the {@link Resource} generated for a {@link SimpleEnvironmentEntryType} is of the expected
    * target type and value
    * 
    * @param envEntry
    * @param resource
    * @param expectedTargetType
    * @param expectedValue
    */
   private void assertResource(SimpleEnvironmentEntryType envEntry, Resource resource, Class<?> expectedTargetType, Object expectedValue)
   {
      Assert.assertNotNull("Resource created for env-entry: " + envEntry.getName() + " was null", resource);
      Object target = resource.getTarget();
      Assert.assertNotNull("Resource target for env-entry: " + envEntry.getName() + " was null", target);
      Assert.assertTrue("Unexpected target type: " + target.getClass() + " for env-entry: " + envEntry.getName(), expectedTargetType.isInstance(target));
      Assert.assertEquals("Unexpected value for env-entry: " + envEntry.getName(), expectedValue, target);
   }
}
