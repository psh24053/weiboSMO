package com.psh.query.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.util.PshLogger;
import com.psh.query.service.ExcelService;

/**
 * Servlet implementation class UploadExcel
 */
//@WebServlet("/UploadExcel")
public class UploadExcel extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadExcel() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() throws ServletException {
    	// TODO Auto-generated method stub
    	super.init();
    	File file = new File(ExcelService.UploadExcelDIR);
		file.mkdirs();
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
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter out = response.getWriter();
		
		String idx = request.getParameter("idx");
		String gid = request.getParameter("gid");
		
		
		
		//获得磁盘文件条目工厂  
        DiskFileItemFactory factory = new DiskFileItemFactory();  
        //获取文件需要上传到的路径  
        String path = ExcelService.UploadExcelDIR;
          
        
        //如果没以下两行设置的话，上传大的 文件 会占用 很多内存，  
        //设置暂时存放的 存储室 , 这个存储室，可以和 最终存储文件 的目录不同  
        /** 
         * 原理 它是先存到 暂时存储室，然后在真正写到 对应目录的硬盘上，  
         * 按理来说 当上传一个文件时，其实是上传了两份，第一个是以 .tem 格式的  
         * 然后再将其真正写到 对应目录的硬盘上 
         */  
        factory.setRepository(new File(path));  
        //设置 缓存的大小，当上传文件的容量超过该缓存时，直接放到 暂时存储室  
        factory.setSizeThreshold(1024*1024) ;  
          		
        //高水平的API文件上传处理  
        ServletFileUpload upload = new ServletFileUpload(factory);  
          
          
        try {  
            //可以上传多个文件  
            List<FileItem> list = (List<FileItem>)upload.parseRequest(request);  
              
            for(FileItem item : list)  
            {  
                //获取表单的属性名字  
                String name = item.getFieldName();  
                  
                //如果获取的 表单信息是普通的 文本 信息  
                if(item.isFormField())  
                {                     
                }  
                //对传入的非 简单的字符串进行处理 ，比如说二进制的 图片，电影这些  
                else  
                {  
                      
                    //真正写到磁盘上  
                    //它抛出的异常 用exception 捕捉  
                    File f = new File(path,idx);  
                    
                    if(!f.exists()){
                    	f.createNewFile();
                    }
                    item.write( f );//第三方提供的  
                    
                    PshLogger.logger.debug("Upload Excel Complete!");
                    ExcelService excel = ExcelService.createExcel(idx, Integer.parseInt(gid));
                    
                    PshLogger.logger.debug("return onUploadExcelComplete('"+idx+"',"+gid+")");
                    responseJavaScript(out, "parent.onUploadExcelComplete('"+idx+"',"+gid+")");
                    
                    out.close();
                }  
            }  
              
              
              
        } catch (FileUploadException e) {  
        	PshLogger.logger.error(e.getMessage());
        } catch (Exception e) {  
        	PshLogger.logger.error(e.getMessage()); 
        }  
	}
	
	public void responseJavaScript(PrintWriter out, String js){
		
		out.write("<script type='text/javascript'>");
		out.write(js);
		out.write("</script>");
		out.flush();
		
	}

}
