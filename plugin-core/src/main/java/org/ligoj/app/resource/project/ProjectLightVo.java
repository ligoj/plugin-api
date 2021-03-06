/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.app.resource.project;

import lombok.Getter;
import lombok.Setter;

/**
 * A simple project description.
 */
@Getter
@Setter
public class ProjectLightVo extends BasicProjectVo {

	/**
	 * SID, for Hazelcast
	 */
	private static final long serialVersionUID = 1L;

	private int nbSubscriptions;

}
