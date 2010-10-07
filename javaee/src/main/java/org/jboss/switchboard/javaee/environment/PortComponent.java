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

/**
 * PortComponent
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public interface PortComponent
{

   /**
    * Returns the fully qualified class name of the endpoint interface
    * @return
    */
   String getEndpointInterface();
   
   /**
    * Returns true if MTOM is enabled. Else returns false.
    * @return
    */
   boolean isMtomEnabled();
   
   /**
    * 
    * @return
    */
   Integer getMtomThreshold();
   
   /**
    * 
    * @return
    */
   Address getAddress();
   
   /**
    * Returns true if binding is respected. Else returns false
    * @return
    */
   boolean isBindingRespected();
   
   /**
    * Returns the port-component-link
    * @return
    */
   String getLink();
}
