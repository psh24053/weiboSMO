package cn.panshihao.weiboregserver.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.panshihao.weiboregserver.RegManager;

/**
 * Servlet implementation class emailService
 */
@WebServlet("/emailService")
public class emailService extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public emailService() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		/*
		 * 接收 type参数，如果type为Null或者不是数字，则停止执行
		 */
		int type = 0;
		String stype = request.getParameter("type");
		
		if(stype == null){
			return;
		}
		if(stype.matches("\\d+")){
			type = Integer.parseInt(stype);
		}else{
			return;
		}
		
		String email = request.getParameter("email");
		
		if(email == null){
			return;
		}
		
		switch (type) {
		case 0:
			// 代表是激活邮件
			
			String url = request.getParameter("url");
			if(url == null){
				return;
			}
			
			
			
			break;
		case 1:
			// 代表是成功开通邮件
			
			break;
		default:
			break;
		}
		
		
		
		
		
//		RegManager regManager = new RegManager();
		
//		regManager.activationWeibo(email, uid)
		
		
	}

}
