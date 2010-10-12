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
package org.jboss.switchboard.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.switchboard.spi.Resource;
import org.jboss.util.naming.Util;

/**
 * ENCOperator
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ENCOperator
{

   private String id;
   
   /** 
    * {@link JavaEEComponent} on which this {@link ENCOperator} operates
    */
   private JavaEEComponent component;

   /** 
    * The list of {@link Resource}s which this {@link ENCOperator} has
    * to bind/unbind into/from the ENC of the {@link JavaEEComponent}
    */
   private Map<String, Resource> encBindings = new HashMap<String, Resource>();

   /**
    * Constructs a {@link Resource} with the passed {@link Resource}s
    * @param barrierId 
    * @param bindings The {@link Resource}s which this {@link ENCOperator} is responsible for binding/unbinding
    *                   from ENC
    */
   public ENCOperator(Map<String, Resource> bindings)
   {
      
      this.encBindings = bindings;
   }
   
   /**
    * Constructs a {@link ENCOperator} for a {@link JavaEEComponent}
    * 
    * @param barrierId
    * @param component The {@link JavaEEComponent} into whose {@link Context}, this {@link ENCOperator} will
    *                   bind/unbind the {@link ENCBinding}s
    * @param bindings The {@link ENCBinding}s which this {@link ENCOperator} is responsible for binding/unbinding
    *                   from ENC
    */
   public ENCOperator(JavaEEComponent component, Map<String, Resource> bindings)
   {
      this(bindings);
      this.component = component;
   }

//   @Override
//   public String getId()
//   {
//      return this.id;
//   }
   
   public void bind() throws NamingException
   {
      Context enc = this.getContext();
      for (Map.Entry<String, Resource> binding : this.encBindings.entrySet())
      {
         String jndiName = binding.getKey();
         Object jndiObject = binding.getValue().getTarget();
         Util.bind(enc, jndiName, jndiObject);
      }
   }

   public void unbind() throws NamingException
   {
      Context enc = this.getContext();
      for (Map.Entry<String, Resource> binding : this.encBindings.entrySet())
      {
         String jndiName = binding.getKey();
         enc.unbind(jndiName);
      }
   }
   
   public void setJavaEEComponent(JavaEEComponent component)
   {
      if (this.component != null)
      {
         throw new IllegalStateException(JavaEEComponent.class + " is already set on ENCOperator: " + this);
      }
      this.component = component;
   }
   
   public Map<String, Resource> getENCBindings()
   {
      return Collections.unmodifiableMap(this.encBindings);
   }
   
   private Context getContext()
   {
      if (this.component == null)
      {
         throw new IllegalStateException(JavaEEComponent.class + " is not set, cannot get context");
      }
      return this.component.getContext();
   }
   

}
