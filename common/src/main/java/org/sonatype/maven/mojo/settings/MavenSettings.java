/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2013 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */

package org.sonatype.maven.mojo.settings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.StringTokenizer;

import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

public class MavenSettings
{
  private MavenSettings() {
  }

  public static Server selectServer(final Settings settings, final String serverId) {
    final Server server = settings.getServer(serverId);
    if (server != null) {
      return Clone.copy(server);
    }
    return null;
  }

  public static Proxy selectProxy(final Settings settings, final String serverUrl)
      throws MalformedURLException
  {
    URL url = new URL(serverUrl);
    String host = url.getHost();

    Proxy httpProxy = null;
    Proxy httpsProxy = null;
    Collection<Proxy> proxies = settings.getProxies();
    for (Proxy proxy : proxies) {
      if (proxy.isActive() && !isNonProxyHosts(host, proxy.getNonProxyHosts())) {
        if ("http".equalsIgnoreCase(proxy.getProtocol()) && httpProxy == null) {
          httpProxy = proxy;
        }
        else if ("https".equalsIgnoreCase(proxy.getProtocol()) && httpsProxy == null) {
          httpsProxy = proxy;
        }
      }
    }

    Proxy proxy = httpProxy;
    if ("https".equalsIgnoreCase(url.getProtocol()) && httpsProxy != null) {
      proxy = httpsProxy;
    }

    return proxy;
  }

  public static Server decrypt(final SecDispatcher secDispatcher, final Server server)
      throws SecDispatcherException
  {
    final Server result = Clone.copy(server);

    synchronized (secDispatcher) {
      result.setUsername(decrypt(secDispatcher, server.getUsername()));
      result.setPassword(decrypt(secDispatcher, server.getPassword()));
    }

    return result;
  }

  public static Proxy decrypt(final SecDispatcher secDispatcher, final Proxy server)
      throws SecDispatcherException
  {
    final Proxy result = Clone.copy(server);

    synchronized (secDispatcher) {
      result.setUsername(decrypt(secDispatcher, server.getUsername()));
      result.setPassword(decrypt(secDispatcher, server.getPassword()));
    }

    return result;
  }

  // ==

  private static boolean isNonProxyHosts(String host, String nonProxyHosts) {
    if (host != null && nonProxyHosts != null && nonProxyHosts.length() > 0) {
      for (StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|"); tokenizer.hasMoreTokens(); ) {
        String pattern = tokenizer.nextToken();
        pattern = pattern.replace(".", "\\.").replace("*", ".*");
        if (host.matches(pattern)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Invokes decrypt on {@link SecDispatcher} if not {@code null}. As the sec dispatcher is not thread safe, in
   * case of concurrent access, this method should be called from protected region only!
   */
  private static String decrypt(final SecDispatcher secDispatcher, final String str)
      throws SecDispatcherException
  {
    if (secDispatcher == null) {
      return str;
    }

    return secDispatcher.decrypt(str);
  }
}
