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
package org.jboss.switchboard.mc;

import java.util.HashMap;
import java.util.Map;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.switchboard.mc.resource.provider.ResourceProviderRegistry;
import org.jboss.switchboard.mc.spi.MCBasedResourceProvider;
import org.jboss.switchboard.spi.EnvironmentEntryType;
import org.jboss.switchboard.spi.JndiEnvironment;
import org.jboss.switchboard.spi.Resource;

/**
 * JndiEnvironmentProcessor
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class JndiEnvironmentProcessor
{

   /**
    * Logger
    */
   private static Logger logger = Logger.getLogger(JndiEnvironmentProcessor.class);
   
   private ResourceProviderRegistry registry;
    
   public JndiEnvironmentProcessor(ResourceProviderRegistry registry)
   {
      this.registry = registry;
   }
   
   public Map<String, Resource> process(DeploymentUnit unit, JndiEnvironment environment)
   {
      Map<String, Resource> resources = new HashMap<String, Resource>();
      for (EnvironmentEntryType entry : environment.getEntries())
      {
         MCBasedResourceProvider<EnvironmentEntryType> provider = this.registry.getResourceProvider((Class<EnvironmentEntryType>) entry.getClass());
         if (provider == null)
         {
            logger.debug(entry.getName() + " will not be available in ENC, of component in deployment unit: "
                        + unit + " ,because no ResourceProvider was available for type: " + entry.getClass());
            continue;
         }
         Resource resource = provider.provide(unit, entry);
         if (resource != null)
         {
            resources.put(entry.getName(), resource);
         }
      }
      return resources;
   }
}
