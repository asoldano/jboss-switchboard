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

import org.jboss.metadata.javaee.spec.Addressing;
import org.jboss.switchboard.javaee.environment.Address;
import org.jboss.switchboard.javaee.environment.ResponseType;

/**
 * ServiceAddress
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ServiceAddress implements Address
{

   private Addressing delegate;
   
   public ServiceAddress(Addressing delegate)
   {
      this.delegate = delegate;
   }
   
   @Override
   public ResponseType getResponseType()
   {
      String response = this.delegate.getResponses();
      if (response == null)
      {
         return null;
      }
      return ResponseType.valueOf(response);
   }

   @Override
   public boolean isEnabled()
   {
      return this.delegate.isEnabled();
   }

   @Override
   public boolean isRequired()
   {
      return this.delegate.isRequired();
   }

}
