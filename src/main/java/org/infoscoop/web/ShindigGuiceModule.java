/* infoScoop OpenSource
 * Copyright (C) 2010 Beacon IT Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0-standalone.html>.
 */

package org.infoscoop.web;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.apache.shindig.common.PropertiesModule;
import org.apache.shindig.common.servlet.Authority;
import org.apache.shindig.common.servlet.BasicAuthority;
import org.apache.shindig.gadgets.DefaultGuiceModule;
import org.apache.shindig.gadgets.admin.GadgetAdminModule;
import org.apache.shindig.gadgets.config.DefaultConfigContributorModule;
import org.apache.shindig.gadgets.js.JsCompilerModule;
import org.apache.shindig.gadgets.js.JsServingPipelineModule;
import org.apache.shindig.gadgets.parse.ParseModule;
import org.apache.shindig.gadgets.uri.UriModule;
import org.apache.shindig.protocol.conversion.BeanConverter;
import org.apache.shindig.protocol.conversion.BeanJsonConverter;
import org.apache.shindig.protocol.conversion.BeanXStreamConverter;
import org.apache.shindig.protocol.conversion.xstream.XStreamConfiguration;
import org.apache.shindig.social.core.util.BeanXStreamAtomConverter;
import org.apache.shindig.social.core.util.xstream.XStream081Configuration;
import org.infoscoop.protocol.handler.HttpRequestHandler;

import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Creates a module to supply all of the core gadget classes.
 *
 * Instead of subclassing this consider adding features to the
 * multibindings rpc handlers.
 */
public class ShindigGuiceModule extends DefaultGuiceModule {

  /** {@inheritDoc} */
  @Override
  protected void configure() {
    bind(ExecutorService.class).to(ShindigExecutorService.class);
    bind(Executor.class).annotatedWith(Names.named("shindig.concat.executor")).to(ShindigExecutorService.class);

    bind(Authority.class).to(BasicAuthority.class);

    bindConstant().annotatedWith(Names.named("shindig.jsload.ttl-secs")).to(60 * 60); // 1 hour
    bindConstant().annotatedWith(Names.named("shindig.jsload.require-onload-with-jsload")).to(true);
    
	install(new PropertiesModule());
	
    install(new DefaultConfigContributorModule());
    install(new ParseModule());
//  install(new PreloadModule());
//  install(new RenderModule());
//  install(new RewriteModule());
//  install(new SubstituterModule());
//  install(new TemplateModule());
    install(new UriModule());
    install(new JsCompilerModule());
    install(new JsServingPipelineModule());
    
    install(new GadgetAdminModule());
    
    registerGadgetHandlers();
    registerFeatureHandlers();
    
    bind(XStreamConfiguration.class).to(XStream081Configuration.class);
    bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.xml")).to(
        BeanXStreamConverter.class);
    bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.json")).to(
        BeanJsonConverter.class);
    bind(BeanConverter.class).annotatedWith(Names.named("shindig.bean.converter.atom")).to(
        BeanXStreamAtomConverter.class);
    /*
    bind(Boolean.class)
        .annotatedWith(Names.named(AnonymousAuthenticationHandler.ALLOW_UNAUTHENTICATED))
        .toInstance(Boolean.TRUE);
    bind(new TypeLiteral<List<AuthenticationHandler>>(){}).toProvider(
            AuthenticationHandlerProvider.class);
    */
  }

  /**
   * Sets up multibinding for rpc handlers
   */
  protected void registerGadgetHandlers() {
	Multibinder<Object> handlerBinder = Multibinder.newSetBinder(binder(), Object.class, Names.named("org.infoscoop.rpc.handlers"));
	handlerBinder.addBinding().to(HttpRequestHandler.class);
  }

}
