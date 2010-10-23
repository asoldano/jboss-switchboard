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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.DependencyMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.deployer.helpers.AbstractRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.metadata.javaee.spec.Environment;
import org.jboss.reloaded.naming.deployers.javaee.JavaEEComponentInformer;
import org.jboss.switchboard.impl.ENCOperator;
import org.jboss.switchboard.impl.JndiEnvironmentProcessor;
import org.jboss.switchboard.jbmeta.javaee.environment.JndiEnvironmentMetadata;
import org.jboss.switchboard.mc.SwitchBoardImpl;
import org.jboss.switchboard.spi.Barrier;
import org.jboss.switchboard.spi.JndiEnvironment;
import org.jboss.switchboard.spi.Resource;

/**
 * AbstractJavaEEComponentDeployer
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public abstract class AbstractENCOperatorDeployer extends AbstractRealDeployer
{
   /** Logger */
   private static Logger logger = Logger.getLogger(AbstractENCOperatorDeployer.class);

   protected JavaEEComponentInformer informer;

   protected JndiEnvironmentProcessor jndiEnvProcessor;

   public AbstractENCOperatorDeployer(JavaEEComponentInformer informer)
   {
      this.informer = informer;
      setInputs(informer.getRequiredAttachments());
      setOutput(BeanMetaData.class);
      addOutput(Barrier.class);
   }

   @Inject
   public void setJNDIEnvironmentProcessor(JndiEnvironmentProcessor processor)
   {
      this.jndiEnvProcessor = processor;
   }

   protected String getApplicationName(DeploymentUnit deploymentUnit)
   {
      return informer.getApplicationName(deploymentUnit);
   }

   protected String getComponentName(DeploymentUnit deploymentUnit)
   {
      return informer.getComponentName(deploymentUnit);
   }

   protected String getModuleName(DeploymentUnit deploymentUnit)
   {
      return informer.getModuleName(deploymentUnit);
   }

   protected void process(DeploymentUnit unit, Collection<Environment> environments)
   {
      Map<String, Resource> resources = new HashMap<String, Resource>();
      for (Environment env : environments)
      {
         JndiEnvironment jndiEnv = this.convert(env);
         resources.putAll(this.jndiEnvProcessor.process(unit, jndiEnv));
      }
      if (resources.isEmpty())
      {
         return;
      }
      // A ENCOperator might already exist for the unit. For example,
      // for EJBs and servlets deployed in a single .war (component), there will be 
      // a ENCOperator which might have been created and added when processing JBossMetaData (for EJBs)
      // and now we might be processing JBossWebMetaData (for servlets), which both share the same
      // (JavaEE)component.  
      Barrier barrier = unit.getAttachment(Barrier.class);
      if (barrier == null)
      {
         // create a new one and attach it to unit
         ENCOperator encOperator = new ENCOperator(resources);
         SwitchBoardImpl switchBoard = new SwitchBoardImpl(this.getSwitchBoardMCBeanName(unit), encOperator);
         this.attachBarrier(unit, switchBoard);
         
         BeanMetaData switchBoardBMD = this.createSwitchBoardBMD(unit, switchBoard);
         this.attachSwitchBoardBMD(unit, switchBoardBMD);
      }
      else
      {
         // HACK! 
         // TODO: Think about a better way of doing this (maybe making available the methods in Barrier)
         if (barrier instanceof SwitchBoardImpl)
         {
            SwitchBoardImpl switchboard = (SwitchBoardImpl) barrier;
            switchboard.addENCBinding(resources);
            
            // recreate and attach SwitchBoard BMD, since the 
            // new Resource binding might have additional dependencies
            BeanMetaData switchBoardBMD = this.createSwitchBoardBMD(unit, switchboard);
            this.attachSwitchBoardBMD(unit, switchBoardBMD);

         }
      }
   }

   private JndiEnvironment convert(Environment environment)
   {
      return new JndiEnvironmentMetadata(environment);
   }

   protected BeanMetaData createSwitchBoardBMD(DeploymentUnit unit, SwitchBoardImpl switchBoard)
   {
      String mcBeanName = switchBoard.getId();
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder(mcBeanName, switchBoard.getClass().getName());
      builder.setConstructorValue(switchBoard);

      AbstractInjectionValueMetaData namingContextInjection = this.getNamingContextInjectionMetaData(unit);
      builder.addPropertyMetaData("context", namingContextInjection);

      logger.debug("Installing SwitchBoard: " + mcBeanName + " for unit: " + unit.getSimpleName()
            + " with dependencies: ");
      for (DependencyMetaData dependency : switchBoard.getDependencies())
      {
            logger.debug(dependency);
            builder.addDependency(dependency);
         
      }
      return builder.getBeanMetaData();
   }
   

   protected String getSwitchBoardMCBeanName(DeploymentUnit unit)
   {
      StringBuilder sb = new StringBuilder("jboss-switchboard:");
      String appName = this.getApplicationName(unit);
      if (appName != null)
      {
         sb.append("appName=");
         sb.append(appName);
         sb.append(",");
      }
      String moduleName = this.getModuleName(unit);
      sb.append("module=");
      sb.append(moduleName);
      if (this.informer.isJavaEEComponent(unit))
      {
         String componentName = this.getComponentName(unit);
         sb.append(",name=");
         sb.append(componentName);
      }

      return sb.toString();
   }
   
   protected abstract void attachSwitchBoardBMD(DeploymentUnit deploymentUnit, BeanMetaData switchBoardBMD);
   
   protected abstract void attachBarrier(DeploymentUnit deploymentUnit, Barrier switchBoard);
   
   protected abstract AbstractInjectionValueMetaData getNamingContextInjectionMetaData(DeploymentUnit deploymentUnit);

}
