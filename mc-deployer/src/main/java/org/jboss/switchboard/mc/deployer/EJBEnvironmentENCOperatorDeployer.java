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
package org.jboss.switchboard.mc.deployer;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.ejb.jboss.JBossEnterpriseBeanMetaData;
import org.jboss.metadata.ejb.jboss.JBossMetaData;
import org.jboss.metadata.ejb.spec.InterceptorMetaData;
import org.jboss.metadata.ejb.spec.InterceptorsMetaData;
import org.jboss.metadata.javaee.spec.Environment;
import org.jboss.reloaded.naming.deployers.javaee.JavaEEComponentInformer;

/**
 * EJBEnvironmentENCOperatorDeployer
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class EJBEnvironmentENCOperatorDeployer extends AbstractENCOperatorDeployer
{

   public EJBEnvironmentENCOperatorDeployer(JavaEEComponentInformer informer)
   {
      super(informer);
      setComponentsOnly(true);
      setInput(JBossEnterpriseBeanMetaData.class);
      setOutput(BeanMetaData.class);
   }

   @Override
   protected void internalDeploy(DeploymentUnit unit) throws DeploymentException
   {
      JBossEnterpriseBeanMetaData beanMetaData = unit.getAttachment(JBossEnterpriseBeanMetaData.class);
      if (beanMetaData == null)
      {
         return;
      }
      
      // We only work for EJB 3.x session beans
      if (!beanMetaData.getJBossMetaData().isEJB3x())
      {
         return;
      }
      Collection<Environment> environments = new ArrayList<Environment>();
      environments.add(beanMetaData);
      
      // interceptors
      InterceptorsMetaData interceptors = JBossMetaData.getInterceptors(beanMetaData.getEjbName(), beanMetaData.getJBossMetaData());
      if (interceptors != null)
      {
         for (InterceptorMetaData interceptor : interceptors)
         {
            environments.add(interceptor);
         }
      }
      this.process(unit, environments);
   }
   
}
