package com.psh.query.action;

import com.psh.base.common.PshAction;
import com.psh.base.common.RequestMessageParser;
import com.psh.base.common.ResponseMessageGenerator;
import com.psh.base.common.ErrorCode;
import com.psh.base.json.JSONException;
import com.psh.base.json.JSONObject;
import com.psh.base.util.PshLogger;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.model.CityModel;
import com.psh.query.model.GetFirstQueryPageNumber;
import com.psh.query.model.GetFirstQueryUser;
import com.psh.query.model.ProvModel;
import com.psh.query.model.QueryTaskModel;
import com.psh.query.model.UserQueryTaskModel;


public class QueryAction extends PshAction{
	
	public QueryAction(){
		
		super.code = 2000;
		super.name = "QueryAction";
		
	}
	
	@Override
	public ResponseMessageGenerator handleAction(RequestMessageParser parser) {
		// TODO Auto-generated method stub
		ResponseMessageGenerator generator = new ResponseMessageGenerator();
		
		JSONObject parameter = parser.getParameterJsonObject();
		
		if (parameter == null) {
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		// Start to retrieve required parameters from request
		String nickName = "";
		
		try {
			nickName = parameter.getString("nickName");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"nickName\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String tag = "";
		
		try {
			tag = parameter.getString("tag");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"tag\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String school = "";
		
		try {
			school = parameter.getString("school");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"school\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String company = "";
		
		try {
			company = parameter.getString("company");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"company\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String userType = "all";
		
		try {
			userType = parameter.getString("userType");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"userType\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String prov = "0";
		
		try {
			prov = parameter.getString("prov");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"prov\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String city = "1000";
		
		try {
			city = parameter.getString("city");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"city\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String age = "all";
		
		try {
			age = parameter.getString("age");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing: \"age\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		String sex = "";
		
		try {
			sex = parameter.getString("sex");	
		} catch (JSONException e) {
			PshLogger.logger.error("Missing:\"sex\"" );
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE);
		}
		
		//将一个搜索任务放入数据库
//		QueryTaskBean query = new QueryTaskBean();
		
//		String url = "http://s.weibo.com/user/";
//		if(nickName != null && !nickName.equals("")){
//			
//			url += "&nickname=" + nickName;
//			query.setQnck(nickName);
//			
//		}else{
//			query.setQnck("");
//		}
//		
//		if(tag != null && !tag.equals("")){
//			
//			url += "&tag=" + tag;
//			query.setQtag(tag);
//		}else{
//			query.setQtag("");
//		}
//		
//		if(school != null && !school.equals("")){
//			
//			url += "&school" + school;
//			query.setQsch(school);
//		}else{
//			query.setQsch("");
//		}
//		
//		if(company != null && !company.equals("")){
//			
//			url += "&work" + company;
//			query.setQcom(company);
//		}else{
//			query.setQcom("");
//		}
//		
//		if(userType != null && !userType.equals("") && !userType.equals("all")){
//			
//			url += "&auth" + userType;
//			query.setQutype(userType);
//		}else{
//			query.setQutype("");
//		}
//		
//		if(!prov.equals("0")){
//			
//			ProvModel provModel = new ProvModel();
//			String provName = provModel.getProvNameByID(Integer.parseInt(prov));
//			
//			if(!city.equals(1000)){
//				
//				url += "&region=custom:" + prov + ":" + city;
//				
//				CityModel cityModel = new CityModel();
//				String cityName = cityModel.getProvNameByID(Integer.parseInt(city), Integer.parseInt(prov));
//				
//				query.setQprov(provName);
//				query.setQcity(cityName);
//			}else{
//				
//				url += "&region=custom:" + prov + ":1000";
//				query.setQprov(provName);
//				query.setQcity("");
//				
//			}
//			
//		}else{
//			
//			query.setQprov("");
//			query.setQcity("");
//			
//		}
//		
//		if(age != null && !age.equals("") && !age.equals("all")){
//			
//			url += "&age=" + age;
//			query.setQage(age);
//		}else{
//			query.setQage("");
//		}
//		
//		if(sex != null && !sex.equals("")){
//			
//			url += "&gender=" + sex;
//			query.setQsex(sex);
//		}else{
//			query.setQsex("");
//		}
//		
//		QueryTaskModel queryTask = new QueryTaskModel();
//		
//		query.setQdate(System.currentTimeMillis());
//		//添加一条搜索记录任务
//		int queryTaskID = queryTask.addQueryTask(query);
		
		//获得首次查询的页数
		GetFirstQueryPageNumber firstPageNumber = new GetFirstQueryPageNumber();
		int totalPageNumber = firstPageNumber.getFirstPageNumber("http://m.weibo.cn/searchs/user?q=%E6%B5%99%E6%B1%9F&page=1");
		System.out.println("------------0级共" + totalPageNumber + "页-----------");
		for(int i = 0 ; i < totalPageNumber ; i++){
			
			GetFirstQueryUser firstQueryUser = new GetFirstQueryUser("http://m.weibo.cn/searchs/user?q=%E6%B5%99%E6%B1%9F&page=",(i + 1));
			//查找第一次搜索结果
			firstQueryUser.start();
				
		}
		
		int totalPageNumber_2 = firstPageNumber.getFirstPageNumber("http://m.weibo.cn/searchs/user?q=%E9%99%95%E8%A5%BF&page=1");
		for(int j = 0 ; j < totalPageNumber_2 ; j++){
			
			GetFirstQueryUser firstQueryUser = new GetFirstQueryUser("http://m.weibo.cn/searchs/user?q=%E9%99%95%E8%A5%BF&page=",(j + 1));
			//查找第一次搜索结果
			firstQueryUser.start();
				
		}
		
		int totalPageNumber_3 = firstPageNumber.getFirstPageNumber("http://m.weibo.cn/searchs/user?q=%E5%8F%B0%E6%B9%BE&page=1");
		for(int x = 0 ; x < totalPageNumber_3 ; x++){
			
			GetFirstQueryUser firstQueryUser = new GetFirstQueryUser("http://m.weibo.cn/searchs/user?q=%E5%8F%B0%E6%B9%BE&page=",(x + 1));
			//查找第一次搜索结果
			firstQueryUser.start();
				
		}
		
//		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
//		connectionManager.setMaxTotal(2000);
//		connectionManager.setDefaultMaxPerRoute(1000);
//		
//		// 访问 http://weibo.com/signup/mobile.php;
//		HttpClient httpClient = new DefaultHttpClient(connectionManager);
//		
//		
//		//伪装成Firefox 5, 
//		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
//		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // 一定要有，否则会生成多个Cookie header送给web server 
//		httpClient.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true); //
//		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
//		httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
//		List<BasicHeader> headerList = new ArrayList<BasicHeader>(); 
//		
//		headerList.add(new BasicHeader("Accept", "*/*")); 
//		headerList.add(new BasicHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
//		headerList.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
//		headerList.add(new BasicHeader("Cache-Control", "no-cache")); 
//		headerList.add(new BasicHeader("Connection", "keep-alive"));
//		headerList.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
//		headerList.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
//		headerList.add(new BasicHeader("Cookie", "UOR=www.3lian.com,widget.weibo.com,#weibo.com; SINAGLOBAL=5961333485506.268.1359464488255; ULV=1359464488258:6:6:4:5961333485506.268.1359464488255:1359373331074; myuid=2885381611; un=287540517@qq.com; SinaRot/u/2885381611%3Fwvr%3D5%26wvr%3D5%26lf%3Dreg=51; USRUG=usrmdins1540_26; USRHAWB=usrmdins40_86; _s_tentry=login.sina.com.cn; Apache=5961333485506.268.1359464488255; SinaRot/u/2885381611%3Fwvr%3D5%26=6; v5reg=usrmdins1031; appkey=; NSC_wjq_xfjcp.dpn_w3.6_w4=ffffffff0941137645525d5f4f58455e445a4a423660; SUS=SID-2885381611-1359471338-XD-nfkc6-851dfbb9e5fe539c82534782dc22be71; SUE=es%3D2381556ad4cbf970d94947ce4a96cbfc%26ev%3Dv1%26es2%3Dbc7833a06087dc776a3cfcf9feb18eb7%26rs0%3DWDQ6v%252F6Qz7TJjmXgjBhgxiZ00ltAR6HdfclYyVhNTqebQla4WlWhZMIWEH9fMB5rvjEiTWIcMyQpwZN1dG1u%252BMjdsGthS27esssdHXIW9pIEudiKt%252Blzb%252BBEJKkC00kKX2R%252BNKNUhfokcjryIw6euR45lL6GRsN%252BAb3Ln1iwoSg%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1359471339%26et%3D1359557739%26d%3Dc909%26i%3Db1d0%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D4%26uid%3D2885381611%26user%3D287540517%2540qq.com%26ag%3D4%26name%3D287540517%2540qq.com%26nick%3D287540517%26fmp%3D%26lcp%3D; ALF=1360076135; SSOLoginState=1359471339; v=5; wvr=5"));
//		
//		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(300000)); 
//		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(300000) ); 
//		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(300000)); // second;
//		//伪装成Firefox
//		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
//		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("109.232.32.199", 8080));
		
//		HttpClient httpClient_follow = new DefaultHttpClient(connectionManager);
//		
//		//伪装成Firefox 5, 
//		httpClient_follow.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
//		httpClient_follow.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // 一定要有，否则会生成多个Cookie header送给web server 
//		httpClient_follow.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true); //
//		httpClient_follow.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
//		httpClient_follow.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
//		List<BasicHeader> headerList_follow = new ArrayList<BasicHeader>(); 
//		
//		headerList_follow.add(new BasicHeader("Accept", "*/*")); 
//		headerList_follow.add(new BasicHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
//		headerList_follow.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
//		headerList_follow.add(new BasicHeader("Cache-Control", "no-cache")); 
//		headerList_follow.add(new BasicHeader("Connection", "keep-alive"));
//		headerList_follow.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
//		headerList_follow.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
//		headerList_follow.add(new BasicHeader("Cookie", "UOR=www.3lian.com,widget.weibo.com,#weibo.com; SINAGLOBAL=5961333485506.268.1359464488255; ULV=1359464488258:6:6:4:5961333485506.268.1359464488255:1359373331074; myuid=2885381611; un=287540517@qq.com; SinaRot/u/2885381611%3Fwvr%3D5%26wvr%3D5%26lf%3Dreg=51; USRUG=usrmdins1540_26; USRHAWB=usrmdins40_86; _s_tentry=login.sina.com.cn; Apache=5961333485506.268.1359464488255; SinaRot/u/2885381611%3Fwvr%3D5%26=6; v5reg=usrmdins1031; appkey=; NSC_wjq_xfjcp.dpn_w3.6_w4=ffffffff0941137645525d5f4f58455e445a4a423660; SUS=SID-2885381611-1359471338-XD-nfkc6-851dfbb9e5fe539c82534782dc22be71; SUE=es%3D2381556ad4cbf970d94947ce4a96cbfc%26ev%3Dv1%26es2%3Dbc7833a06087dc776a3cfcf9feb18eb7%26rs0%3DWDQ6v%252F6Qz7TJjmXgjBhgxiZ00ltAR6HdfclYyVhNTqebQla4WlWhZMIWEH9fMB5rvjEiTWIcMyQpwZN1dG1u%252BMjdsGthS27esssdHXIW9pIEudiKt%252Blzb%252BBEJKkC00kKX2R%252BNKNUhfokcjryIw6euR45lL6GRsN%252BAb3Ln1iwoSg%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1359471339%26et%3D1359557739%26d%3Dc909%26i%3Db1d0%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D4%26uid%3D2885381611%26user%3D287540517%2540qq.com%26ag%3D4%26name%3D287540517%2540qq.com%26nick%3D287540517%26fmp%3D%26lcp%3D; ALF=1360076135; SSOLoginState=1359471339; v=5; wvr=5"));
//		
//		httpClient_follow.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(300000)); 
//		httpClient_follow.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(300000) ); 
//		httpClient_follow.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(300000)); // second;
//		//伪装成Firefox
//		httpClient_follow.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
//		
//		
//		headerList_follow.add(new BasicHeader("Host", "weibo.com"));
//		headerList_follow.add(new BasicHeader("Origin", "weibo.com"));
		
		
		//0级遍历多少页
//		for(int j = 0 ; j < 1 ; j++){
//			
//			String newUrl = url + "&page=" + (j + 1);
//			
//			System.out.println(newUrl);
//			
//			headerList.add(new BasicHeader("Host", "s.weibo.com"));
//			headerList.add(new BasicHeader("Origin", "s.weibo.com"));
//			headerList.add(new BasicHeader("Referer", newUrl));
//			httpClient.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList);
			//连接超时、sockete超时和从connectionmanager中获取connection的超时设置，计算单位都是微秒；
			
			//设置代理对象 ip/代理名称,端口     
//        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("190.90.36.8", 8000));
			
//			HttpPost httpPost = new HttpPost(newUrl);
//			
//			HttpResponse httpResponse = null;
//			try {
//				httpResponse = httpClient.execute(httpPost);
//			} catch (ClientProtocolException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			HttpEntity httpEntity = httpResponse.getEntity();
//			
//			BufferedReader in = null;
//			try {
//				in = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
//			} catch (UnsupportedEncodingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (IllegalStateException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			
//			String result = "";
//			String item = "";
//			try {
//				while((item = in.readLine()) != null){
//					result += item.trim()+"\n";
//				}
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			
//			try {
//				while((item = in.readLine()) != null){
//					if(item.trim().indexOf("search_feed") != -1 && item.trim().indexOf("person_list_feed") != -1){
//						result = item.trim();
//					}
//				}
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}finally{
//				try {
//					in.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//			if(result.equals("")){
//				
//				System.out.println("新浪微博提示行为异常");
//				return generator.toError(parser, 
//						ErrorCode.ERROR_CODE, 
//						"新浪微博提示行为异常");
//
//			}
//			
//			System.out.println(result);
//			System.out.println("已获取到 html");
//			result = result.substring(result.indexOf("<div"),result.lastIndexOf("/div>") + 5);
//			int indexScript = result.indexOf("<script");
//			result = result.substring(indexScript, result.length());
//			int indexScriptEnd = 0;
//			for(int i = 0 ; i < 7 ; i++){
//				indexScriptEnd = result.indexOf("</script>");
//				result = result.substring(indexScriptEnd + 10, result.length());
//			}
//			result = result.substring(result.indexOf("<script>") + 8, result.indexOf("</script>"));
//			result = result.substring(result.indexOf("html") + 7, result.indexOf("\"})"));
//			result = result.replace('\\','`');
//			result = result.replaceAll("`n", "");
//			result = result.replaceAll("`t", "");
//			result = result.replaceAll("`r", "");
//			result = result.replaceAll("`", "");
//			result = "<html><body>" + result + "</body></html>";
//			if(j == 25){
//				
//				System.out.println(result);
//			}
			
//			Document doc = Jsoup.parse(result);
//			
//			Elements elements = doc.getElementsByAttribute("uid");
//			System.out.println(elements.size());
//			
//			//遍历每页的用户
//			for(int i = 0 ; i < elements.size() ; i ++){
//				String parentClassName = elements.get(i).parent().attr("class");
//				if(parentClassName.equals("person_name")){
//					String uid = elements.get(i).attr("uid");
					//找该用户的关注
					
//					String url_follow = "http://weibo.com/" + uid + "/follow";
//					System.out.println(url_follow);
//					
////					HttpClient httpClient_follow = new DefaultHttpClient(connectionManager);
////					
////					
////					//伪装成Firefox 5, 
////					httpClient_follow.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1); 
////					httpClient_follow.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY); // 一定要有，否则会生成多个Cookie header送给web server 
////					httpClient_follow.getParams().setParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true); //
////					httpClient_follow.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"UTF-8"); //这个是和目标网站的编码有关；
////					httpClient_follow.getParams().setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET,"UTF-8"); 
////					List<BasicHeader> headerList_follow = new ArrayList<BasicHeader>(); 
////					
////					headerList_follow.add(new BasicHeader("Accept", "*/*")); 
////					headerList_follow.add(new BasicHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3"));
////					headerList_follow.add(new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8"));
////					headerList_follow.add(new BasicHeader("Cache-Control", "no-cache")); 
////					headerList_follow.add(new BasicHeader("Connection", "keep-alive"));
////					headerList_follow.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
////					headerList_follow.add(new BasicHeader("X-Requested-With", "XMLHttpRequest"));
////					headerList_follow.add(new BasicHeader("Cookie", "UOR=www.3lian.com,widget.weibo.com,#weibo.com; SINAGLOBAL=5961333485506.268.1359464488255; ULV=1359464488258:6:6:4:5961333485506.268.1359464488255:1359373331074; myuid=2885381611; un=287540517@qq.com; SinaRot/u/2885381611%3Fwvr%3D5%26wvr%3D5%26lf%3Dreg=51; USRUG=usrmdins1540_26; USRHAWB=usrmdins40_86; _s_tentry=login.sina.com.cn; Apache=5961333485506.268.1359464488255; SinaRot/u/2885381611%3Fwvr%3D5%26=6; v5reg=usrmdins1031; appkey=; NSC_wjq_xfjcp.dpn_w3.6_w4=ffffffff0941137645525d5f4f58455e445a4a423660; SUS=SID-2885381611-1359471338-XD-nfkc6-851dfbb9e5fe539c82534782dc22be71; SUE=es%3D2381556ad4cbf970d94947ce4a96cbfc%26ev%3Dv1%26es2%3Dbc7833a06087dc776a3cfcf9feb18eb7%26rs0%3DWDQ6v%252F6Qz7TJjmXgjBhgxiZ00ltAR6HdfclYyVhNTqebQla4WlWhZMIWEH9fMB5rvjEiTWIcMyQpwZN1dG1u%252BMjdsGthS27esssdHXIW9pIEudiKt%252Blzb%252BBEJKkC00kKX2R%252BNKNUhfokcjryIw6euR45lL6GRsN%252BAb3Ln1iwoSg%253D%26rv%3D0; SUP=cv%3D1%26bt%3D1359471339%26et%3D1359557739%26d%3Dc909%26i%3Db1d0%26us%3D1%26vf%3D0%26vt%3D0%26ac%3D4%26uid%3D2885381611%26user%3D287540517%2540qq.com%26ag%3D4%26name%3D287540517%2540qq.com%26nick%3D287540517%26fmp%3D%26lcp%3D; ALF=1360076135; SSOLoginState=1359471339; v=5; wvr=5"));
////					
////					httpClient_follow.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, new Integer(300000)); 
////					httpClient_follow.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  new Integer(300000) ); 
////					httpClient_follow.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, new Long(300000)); // second;
////					//伪装成Firefox
////					httpClient_follow.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:17.0) Gecko/17.0 Firefox/17.0");
//					
//					
////					headerList_follow.add(new BasicHeader("Host", "weibo.com"));
////					headerList_follow.add(new BasicHeader("Origin", "weibo.com"));
//					headerList_follow.add(new BasicHeader("Referer", url_follow));
//					httpClient_follow.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headerList_follow);
//					
//					HttpPost httpPost_follow = new HttpPost(url_follow);
//					
//					HttpResponse httpResponseFollow = null;
//					try {
//						httpResponseFollow = httpClient_follow.execute(httpPost_follow);
//					} catch (ClientProtocolException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					
//					HttpEntity httpEntityFollow = httpResponseFollow.getEntity();
//					
//					BufferedReader in_follow = null;
//					try {
//						in_follow = new BufferedReader(new InputStreamReader(httpEntityFollow.getContent(), "UTF-8"));
//					} catch (UnsupportedEncodingException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					} catch (IllegalStateException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					
//					String result_follow = "";
//					String item_follow = "";
////					try {
////						while((item = in.readLine()) != null){
////							result += item.trim()+"\n";
////						}
////					} catch (IOException e1) {
////						// TODO Auto-generated catch block
////						e1.printStackTrace();
////					}
//					
//					try {
//						while((item_follow = in_follow.readLine()) != null){
//							System.out.println(item_follow.trim());
//							if(item_follow.trim().indexOf("cnfList") != -1){
//								result_follow = item_follow.trim();
//							}
//						}
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}finally{
//						try {
//							in_follow.close();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					
//					if(result_follow.equals("")){
//						
//						System.out.println("新浪微博提示行为异常follow");
//						return generator.toError(parser, 
//								ErrorCode.ERROR_CODE, 
//								"新浪微博提示行为异常follow");
//
//					}
//					
//					System.out.println(result_follow);
//					System.out.println("已获取到 html");
//					
//					result_follow = result_follow.substring(result_follow.indexOf("<div"),result_follow.lastIndexOf("/div>") + 5);
//					result_follow = result_follow.replace('\\','`');
//					result_follow = result_follow.replaceAll("`n", "");
//					result_follow = result_follow.replaceAll("`t", "");
//					result_follow = result_follow.replaceAll("`r", "");
//					result_follow = result_follow.replaceAll("`", "");
//					result_follow = "<html><body>" + result_follow + "</body></html>";
//					
//					System.out.println(result_follow);
//					
//					Document doc_follow = Jsoup.parse(result_follow);
//					
//					Elements elements_follow = doc_follow.getElementsByAttributeValue("class", "W_f14 S_func1");
//					System.out.println(elements_follow.size());
//					
//					for(int x = 0 ; x < elements_follow.size() ; x ++){
//						String uidString = elements_follow.get(x).attr("usercard");
//						System.out.println("uridString------"+uidString);
//						String uid_follow = uidString.substring(3);
//						System.out.println(uid_follow);
//						array.put(uid_follow);
//					}
//				}
//				
//			}
			
			
//		}
		
		JSONObject payload = new JSONObject();
		//查找该次搜索的数据源数量
		int count = 0;
		
		UserQueryTaskModel userQuery = new UserQueryTaskModel();
//		count = userQuery.getCountByQueryID(queryTaskID);
		
		try {
			payload.put("count", count);
//			payload.put("array", array);
		} catch (JSONException e) {
			PshLogger.logger.error("JSONException failed.");
			PshLogger.logger.error(e.getMessage());
			return generator.toError(parser, 
					ErrorCode.ERROR_CODE, 
					"JSONException error, reason: " + e.getMessage());
		}
		
		return generator.toSuccess(parser, payload);
	}
	
	
}
