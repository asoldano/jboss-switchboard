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
package org.jboss.switchboard.spi;


/**
 * A {@link ResourceProvider} resolves a particular metadata type into a {@link Resource}.
 * <p>
 *  The {@link Resource} is then picked up by the switchboard operator and made available
 *  in the JNDI 
 * </p>
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public interface Resource
{

   /**
    * Returns the dependency (for example, the name of the entity) which 
    * has to be fulfilled before this {@link Resource} can be made available
    * in JNDI  
    * @return
    */
   Object getDependency();
   
   /**
    * Returns the jndi name at which this {@link Resource} will be bound
    * 
    * @return
    */
   String getJNDIName();
   
   /**
    * Returns the object which needs to be bound into JNDI for this {@link Resource} 
    * @return
    */
   Object getJNDIObject();
}
