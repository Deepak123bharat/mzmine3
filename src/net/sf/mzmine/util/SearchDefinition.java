/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.mzmine.data.PeakIdentity;
import net.sf.mzmine.data.PeakListRow;

/**
 * This class defines a search condition - searching either by peak name, m/z or
 * retention time. Such search can be defined by any module and then conforming
 * peak list rows can be tested by the conforms() method.
 * 
 */
public class SearchDefinition {

	private SearchDefinitionType type;
	private Pattern nameRegex;
	private Range range;

	/**
	 * Creates a search definition by using a regular expression
	 */
	public SearchDefinition(SearchDefinitionType type, String regex)
			throws PatternSyntaxException {

		assert type == SearchDefinitionType.NAME;

		this.type = type;
		this.nameRegex = Pattern.compile(regex);

	}

	/**
	 * Creates a search definition by m/z or RT range
	 */
	public SearchDefinition(SearchDefinitionType type, Range range) {

		assert type == SearchDefinitionType.NAME;

		this.type = type;
		this.range = range;

	}

	/**
	 * Creates a search definition by using a regular expression
	 */
	public SearchDefinition(SearchDefinitionType type, String regex, Range range)
			throws PatternSyntaxException {

		this.type = type;
		this.range = range;

		// Avoid compiling the regex pattern (may cause exceptions) unless the
		// search type is set to NAME
		if (type == SearchDefinitionType.NAME) {
			this.nameRegex = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		}

	}

	/**
	 * Checks whether given peak list row conforms to this search condition.
	 */
	public boolean conforms(PeakListRow row) {
		switch (type) {
		case NAME:
			PeakIdentity identity = row.getPreferredPeakIdentity();
			if (identity == null)
				return false;
			String name = identity.getName();
			Matcher matcher = nameRegex.matcher(name);
			return matcher.find();

		case MASS:
			return range.contains(row.getAverageMZ());

		case RT:
			return range.contains(row.getAverageRT());

		}
		return false;
	}

	public String toString() {
		String text = "Search by " + type.toString();
		switch (type) {
		case NAME:
			text += ": " + nameRegex;
			break;
		case MASS:
		case RT:
			text += ": " + range;
			break;
		}
		return text;
	}

}
