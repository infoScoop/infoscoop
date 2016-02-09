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

package org.infoscoop.api.rest.v1.response.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.util.List;
import java.util.Map;

public class Provisioning{

	@NotBlank
	@Size(min = 1, max = 150)
	@Pattern(regexp = "^[a-zA-Z0-9\\-_\\~\\.@]+$")
	public String uid;

	@NotBlank
	@Size(min = 8, max = 32)
	@Pattern(regexp = "^[a-zA-Z0-9!#\\$%&'\\-\\+\\*_\\?]+$")
	public String password;

	@NotBlank
	@Email
	@Size(min = 1, max = 150)
	public String email;

	@JsonProperty("given_name")
	@NotBlank
	@Size(max = 128)
	public String givenName;

	@JsonProperty("family_name")
	@NotBlank
	@Size(max = 128)
	public String familyName;

	@Size(max = 255)
	public String name;

	@JsonProperty("default_square_id")
	@Size(max = 64)
	public String defaultSquareId;

	@JsonProperty("belong_square")
	public List<Map<String, String>> belongSquare;

	public List<Map<String, String>> attrs;

}