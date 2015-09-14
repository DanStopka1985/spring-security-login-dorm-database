package com.mkyong.web.controller;

import com.mkyong.web.controller.entities.Test;
import org.eclipse.birt.report.engine.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Controller
public class MainController {

	private IReportEngine birtReportEngine = null;

	@Autowired
	TestDAO testDao;

	@RequestMapping(value = { "/", "/welcome**" }, method = RequestMethod.GET)
	public ModelAndView defaultPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Login Form - Database Authentication");
		model.addObject("message", "This is default page!");
		model.setViewName("hello");
		return model;

	}

	@RequestMapping(value = "/admin**", method = RequestMethod.GET)
	public ModelAndView adminPage() {

		ModelAndView model = new ModelAndView();
		model.addObject("title", "Spring Security Login Form - Database Authentication");
		model.addObject("message", "This page is for ROLE_ADMIN only!");
		model.setViewName("admin");

		return model;

	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Invalid username and password!");
		}

		if (logout != null) {
			model.addObject("msg", "You've been logged out successfully.");
		}
		model.setViewName("login");

		return model;

	}

	//for 403 access denied page
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView accesssDenied() {

		ModelAndView model = new ModelAndView();

		//check if user is login
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!(auth instanceof AnonymousAuthenticationToken)) {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();
			System.out.println(userDetail);

			model.addObject("username", userDetail.getUsername());

		}

		model.setViewName("403");
		return model;

	}

	@ResponseBody
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public ArrayList<Test> getRisList (Model model, HttpServletResponse response, HttpServletRequest request) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String user = null;
		if (auth instanceof AnonymousAuthenticationToken) {
			System.out.println("no user");
			return new ArrayList<Test>();
		} else {
			UserDetails userDetail = (UserDetails) auth.getPrincipal();
			user = userDetail.getUsername();
			System.out.println(userDetail.getUsername());
			return testDao.getTempList(user);
		}


	}

//	@RequestMapping(value = "/x", method = RequestMethod.GET)
//	public ModelAndView test(HttpServletResponse response) {
//
//
//
//		//RedirectView redirectView = new RedirectView("/birt/run?__report=masterreport.rptdesign");
//		RedirectView redirectView = new RedirectView("/birt/run?__report=application_session_integrationBF.rptdesign");
//		//RedirectView r1 = new RedirectView()
//
//
//		redirectView.addStaticAttribute("user", "mkyong1");
//		Properties p = new Properties();
//		p.setProperty("asd", "123");
//		redirectView.setAttributes(p);
//
//
////		response.addCookie(new Cookie("COOKIENAME", "The cookie's value"));
//
//
////		response.addHeader("AAAAAA", "AAAAAAA");
//
//		//redirectView.setRequestContextAttribute("00000000000000000");
//
////		ModelAndView mav = new ModelAndView();
////
////		redirectView
//
//		return new ModelAndView(redirectView);
//
//
//	}


	@ResponseBody
	@RequestMapping(value="/pdf", method=RequestMethod.GET)
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		//get report name and launch the engine
		//resp.setContentType("text/html");
		resp.setContentType( "application/pdf" );
		//resp.setHeader ("Content-Disposition","inline; filename=test.pdf");
		String reportName = req.getParameter("ReportName");
		ServletContext sc = req.getSession().getServletContext();
		this.birtReportEngine = BirtEngine.getBirtEngine(sc);



		IReportRunnable design;
		try
		{
			//Open report design
			//design = birtReportEngine.openReportDesign( sc.getRealPath("/Reports")+"/"+"aaa.rptdesign" );
			//design = birtReportEngine.openReportDesign(Thread.currentThread().getContextClassLoader().getResource("Reports/aaa.rptdesign").getPath());
			design = birtReportEngine.openReportDesign("d:\\Reports\\aaa1.rptdesign");


			//create task to run and render report

			//Thread.currentThread().getContextClassLoader().getResource("Reports/aaa.rptdesign")

			IRunAndRenderTask task = birtReportEngine.createRunAndRenderTask( design );
			task.getAppContext().put("BIRT_VIEWER_HTTPSERVLET_REQUEST", req );

			//set output options
			/*HTMLRenderOption options = new HTMLRenderOption();
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
			options.setOutputStream(resp.getOutputStream());
			options.setImageHandler(new HTMLServerImageHandler());
			options.setBaseImageURL(req.getContextPath()+"/images");
			options.setImageDirectory(sc.getRealPath("/images"));
			*/

			PDFRenderOption options = new PDFRenderOption();
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
			resp.setHeader(	"Content-Disposition", "inline; filename=\"test.pdf\"" );
			options.setOutputStream(resp.getOutputStream());
			task.setRenderOption(options);


			//run report
			task.run();
			task.close();
		}catch (Exception e){

			e.printStackTrace();
			throw new ServletException( e );
		}
	}

	@ResponseBody
	@RequestMapping(value="/xls", method=RequestMethod.GET)
	public void getXLS(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		//get report name and launch the engine
		//resp.setContentType("text/html");
		resp.setContentType( "application/pdf" );
		//resp.setHeader ("Content-Disposition","inline; filename=test.pdf");
		String reportName = req.getParameter("ReportName");
		ServletContext sc = req.getSession().getServletContext();
		this.birtReportEngine = BirtEngine.getBirtEngine(sc);



		IReportRunnable design;
		try
		{
			//Open report design
			//design = birtReportEngine.openReportDesign( sc.getRealPath("/Reports")+"/"+"aaa.rptdesign" );

			//design = birtReportEngine.openReportDesign(Thread.currentThread().getContextClassLoader().getResource("Reports/aaa.rptdesign").getPath());
			design = birtReportEngine.openReportDesign("d:\\Reports\\aaa.rptdesign");


			//create task to run and render report



			IRunAndRenderTask task = birtReportEngine.createRunAndRenderTask( design );
			task.getAppContext().put("BIRT_VIEWER_HTTPSERVLET_REQUEST", req );

			//set output options
			/*HTMLRenderOption options = new HTMLRenderOption();
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
			options.setOutputStream(resp.getOutputStream());
			options.setImageHandler(new HTMLServerImageHandler());
			options.setBaseImageURL(req.getContextPath()+"/images");
			options.setImageDirectory(sc.getRealPath("/images"));
			*/

			PDFRenderOption options = new PDFRenderOption();
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
			resp.setHeader(	"Content-Disposition", "inline; filename=\"test.pdf\"" );
			options.setOutputStream(resp.getOutputStream());
			task.setRenderOption(options);


			//run report
			task.run();
			task.close();
		}catch (Exception e){

			e.printStackTrace();
			throw new ServletException( e );
		}
	}



}