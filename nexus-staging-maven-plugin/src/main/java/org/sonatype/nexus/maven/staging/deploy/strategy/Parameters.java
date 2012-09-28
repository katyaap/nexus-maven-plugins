/**
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2012 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.maven.staging.deploy.strategy;

import java.io.File;
import java.util.Map;

/**
 * Execution parameters, mostly coming from Mojo parameters.
 * 
 * @author cstamas
 */
public interface Parameters
{
    String getPluginGav();

    String getNexusUrl();

    String getServerId();

    File getStagingDirectoryRoot();

    boolean isKeepStagingRepositoryOnCloseRuleFailure();

    boolean isKeepStagingRepositoryOnFailure();

    boolean isSkipStagingRepositoryClose();

    String getStagingProfileId();

    String getStagingRepositoryId();

    String getUserDescriptionOfAction();

    Map<String, String> getTags();

    String getDefaultedUserDescriptionOfAction( String action );
}