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
 * A {@link ResourceProvider} is responsible for resolving a {@link Resource}
 * from a given <code>context</code> and a <code>type</code>
 * 
 *  <p>
 *  Typically
 *
 * @param C The context which will be passed to the {@link ResourceProvider} during
 *          {@link Resource} resolution. Typically, the context is a deployment unit
 *          
 * @param T The type of resource being resolved. An example of a resource type is a EJB local reference
 *          or even a EJB remote reference.
 *                   
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public interface ResourceProvider<C, T>
{
   
   /**
    * Returns a {@link Resource} for the passed context and type
    * 
    * @param context The context
    * @param type The type of resource reference which is being resolved (For example: A ejb-local-ref type)
    * @return
    */
   Resource provide(C context, T type);

   /**
    * Returns the type of resource reference, this {@link ResourceProvider} can handle.
    * 
    * @return
    */
   Class<T> getType();
}
