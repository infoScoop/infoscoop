package org.infoscoop.manager.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MenuController {
	@RequestMapping
	public void index() throws Exception {
	}

	@RequestMapping
	public ModelAndView data(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("test", "aeraerere");
		return model;
	}
}
