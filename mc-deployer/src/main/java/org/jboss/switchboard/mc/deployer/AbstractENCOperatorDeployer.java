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
import java.util.UUID;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.plugins.AbstractDependencyMetaData;
import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.deployer.helpers.AbstractRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.logging.Logger;
import org.jboss.metadata.javaee.spec.Environment;
import org.jboss.reloaded.naming.deployers.javaee.JavaEEComponentInformer;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.switchboard.impl.ENCOperator;
import org.jboss.switchboard.impl.JndiEnvironmentProcessor;
import org.jboss.switchboard.jbmeta.javaee.environment.JndiEnvironmentMetadata;
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
   
   private JavaEEComponentInformer informer;

   private JndiEnvironmentProcessor<DeploymentUnit> jndiEnvProcessor;
   
   public AbstractENCOperatorDeployer(JavaEEComponentInformer informer)
   {
      this.informer = informer;
      setInputs(informer.getRequiredAttachments());
   }

   @Inject
   public void setJNDIEnvironmentProcessor(JndiEnvironmentProcessor<DeploymentUnit> processor)
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
      ENCOperator encOperator = new ENCOperator(resources);
      this.createAndAttachBMD(unit, encOperator);
   }

   private JndiEnvironment convert(Environment environment)
   {
      return new JndiEnvironmentMetadata(environment);
   }
   
   private void createAndAttachBMD(DeploymentUnit unit, ENCOperator encOperator)
   {
      String mcBeanName = this.getENCOperatorMCBeanName(unit, encOperator);
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder(mcBeanName, ENCOperator.class.getName());

      builder.setConstructorValue(encOperator);

      String javaeeCompMCBeanName = this.getJavaEEComponentMCBeanName(unit); 
      AbstractInjectionValueMetaData javaeeComponentInjection = new AbstractInjectionValueMetaData(javaeeCompMCBeanName);
      builder.addPropertyMetaData("javaEEComponent", javaeeComponentInjection);
      
      logger.debug("Installing ENCOperator: " + mcBeanName + " for unit: " + unit.getSimpleName() + " with dependencies: ");
      for (Resource encBinding : encOperator.getENCBindings().values())
      {
         Object dependency = encBinding.getDependency();
         if (dependency != null)
         {
            logger.debug(dependency);
            AbstractDependencyMetaData mcDependency = new AbstractDependencyMetaData(dependency);
            builder.addDependency(mcDependency);
         }
      }
      unit.addAttachment(BeanMetaData.class + ":" + mcBeanName, builder.getBeanMetaData());

   }

   private String getENCOperatorMCBeanName(DeploymentUnit unit, ENCOperator encOperator)
   {
      StringBuilder sb = new StringBuilder("jboss-switchboard-encoperator:");
      String appName = this.getApplicationName(unit);
      if (appName != null)
      {
         sb.append("appName=");
         sb.append(appName);
      }
      String moduleName = this.getModuleName(unit);
      sb.append("module=");
      sb.append(moduleName);
      String componentName = this.getComponentName(unit);
      sb.append("component=");
      sb.append(componentName);
      // there can be multiple ENCOperators for a single component. For example,
      // for EJBs and servlets deployed in a single .war (component), there will be 
      // 2 ENCOperators - one for the EJBs and one for the servlets - both operating on
      // the same (JavaEE)component. Hence, a UUID to uniquely distinguish the ENCOperator 
      sb.append("id=");
      sb.append(UUID.randomUUID().toString());

      return sb.toString();
   }

   /**
    * Returns the {@link JavaEEComponent} MC bean name
    * @param deploymentUnit
    * @return
    */
   private String getJavaEEComponentMCBeanName(DeploymentUnit deploymentUnit)
   {
      String applicationName = this.getApplicationName(deploymentUnit);
      String moduleName = this.getModuleName(deploymentUnit);
      String componentName = this.getComponentName(deploymentUnit);

      final StringBuilder builder = new StringBuilder("jboss.naming:");
      if (applicationName != null)
      {
         builder.append("application=").append(applicationName).append(",");
      }
      builder.append("module=").append(moduleName);
      if (componentName != null)
      {
         builder.append(",component=").append(componentName);
      }
      return builder.toString();
   }
}
