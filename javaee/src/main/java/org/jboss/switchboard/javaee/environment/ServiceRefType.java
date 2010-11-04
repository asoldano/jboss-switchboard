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
package org.jboss.switchboard.javaee.environment;

import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.jboss.switchboard.spi.EnvironmentEntryType;

/**
 * ServiceRefType
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public interface ServiceRefType extends JavaEEResourceType, EnvironmentEntryType
{

   /**
    * Returns the fully qualified class name of the service interface
    * @return
    */
   String getServiceInterface();
   
   
   /**
    * Returns the fully qualified class name of the service class or the
    * fully qualified class name of the service endpoint interface class
    * @return
    */
   String getType();
   
   /**
    * Returns the relative (to the module) location of the wsdl file URI of the service
    * @return
    */
   String getWsdlFile();
   
   /**
    * Returns the name of the file used for JAX-RPC mapping between the Java interaces used by
    * the application and the WSDL description in the wsdl-file. The file name is relative path within
    * the module
    * 
    * @return
    */
   String getMappingFile();
   
   /**
    * 
    * @return
    */
   QName getQName();
   
   /**
    * 
    * @return
    */
   Collection<PortComponent> getPortComponents();

   /**
    * 
    * @return
    */
   Collection<Handler> getHandlers();
   
   /**
    * 
    * @return
    */
   List<HandlerChainType> getHandlerChains();
   
   /**
    * 
    * @return
    */
   String getHandlerChain();
}
