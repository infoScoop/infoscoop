package org.infoscoop.addon;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IAddonService {
	public void execute(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
