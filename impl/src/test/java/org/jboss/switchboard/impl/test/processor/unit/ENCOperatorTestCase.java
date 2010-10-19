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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.jboss.switchboard.impl.ENCOperator;
import org.jboss.switchboard.impl.test.common.DummyResource;
import org.jboss.switchboard.spi.Resource;
import org.jnp.interfaces.LocalOnlyContextFactory;
import org.jnp.server.SingletonNamingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link ENCOperator}
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ENCOperatorTestCase
{

   private static SingletonNamingServer namingServer;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      // start the naming server
      namingServer = new SingletonNamingServer();
   }

   @AfterClass
   public static void afterClass()
   {
      // stop the server
      if (namingServer != null)
      {
         namingServer.destroy();
      }
   }

   /**
    * Test the {@link ENCOperator#bind()} and {@link ENCOperator#unbind()} operations
    * 
    * @throws Exception
    */
   @Test
   public void testBindings() throws Exception
   {
      // create a jndi context
      Context jndiCtx = this.getLocalContext();

      // create the resources to be bound
      Map<String, Resource> resources = new HashMap<String, Resource>();
      Object ejbProxy = new String("Dummy ejb proxy");
      Resource dummyEJBResource = new DummyResource(null, ejbProxy);
      resources.put("ejb", dummyEJBResource);

      Object pcProxy = new String("Dummy pc proxy");
      Resource dummyPersistenceContextResource = new DummyResource(null, pcProxy);
      resources.put("pc", dummyPersistenceContextResource);

      // bind the resources
      ENCOperator operator = new ENCOperator(jndiCtx, resources);
      operator.bind();

      // now lookup the resources and make sure they are available
      Object ejbResource = jndiCtx.lookup("ejb");
      Assert.assertNotNull("Null EJB resource bound by ENCOperator", ejbResource);
      Assert.assertEquals("Unexpected binding for EJB proxy", ejbProxy, ejbResource);
      
      Object pcResource = jndiCtx.lookup("pc");
      Assert.assertNotNull("Null Persistence context resource bound by ENCOperator", pcResource);
      Assert.assertEquals("Unexpected binding for persistence context proxy", pcProxy, pcResource);
      
      // now unbind and test
      operator.unbind();
      
      try
      {
         Object ref = jndiCtx.lookup("ejb");
         Assert.fail("EJB reference " + ref + " not unbound from jndi");
      }
      catch(NameNotFoundException nnfe)
      {
         //expected
      }

      try
      {
         Object ref = jndiCtx.lookup("pc");
         Assert.fail("Persistence context reference " + ref + " not unbound from jndi");
      }
      catch (NameNotFoundException nnfe)
      {
         // expected
      }
   }

   /**
    * Returns a {@link Context} backed by {@link LocalOnlyContextFactory}
    * @return
    * @throws NamingException
    */
   private Context getLocalContext() throws NamingException
   {
      Properties props = new Properties();
      props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.LocalOnlyContextFactory");
      props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
      return new InitialContext(props);

   }
}
