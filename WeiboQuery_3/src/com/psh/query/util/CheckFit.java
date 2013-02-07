package com.psh.query.util;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.psh.base.util.PshLogger;
import com.psh.query.bean.QueryTaskBean;
import com.psh.query.bean.UserBean;
import com.psh.query.model.CityModel;
import com.psh.query.model.ProvModel;

public class CheckFit {

	// 判断用户是否符合一个指定的搜索条件
	public boolean checkIsFitCondition(UserBean user, QueryTaskBean queryTask) {
		System.out.println("开始匹配条件");
		System.out.println("开始匹配2");
		if(queryTask == null){
			System.out.println("qurey都是null");
		}
		if(queryTask.getQprov() == null){
			System.out.println("不可能打印出我");
		}
		boolean isFit = true;

		// 检查昵称是否匹配
		if (queryTask.getQnck() != null && !(queryTask.getQnck()).equals("")) {
			System.out.println("查询的昵称" + queryTask.getQnck());
			int index = user.getUck().indexOf(queryTask.getQnck());
			if (index == -1) {
				isFit = false;
				return isFit;
			}
			
			
		}else if(queryTask.getQtag() != null && !(queryTask.getQtag()).equals("")){
			System.out.println("查询的标签" + queryTask.getQtag());
			//检查标签是否匹配
			String[] userTags = user.getTag().trim().split(" ");
			String newQueryTag = queryTask.getQtag().replaceAll(",", "");
			for(int i = 0 ; i < userTags.length ; i++){
				
				int index = newQueryTag.indexOf(userTags[i]);
				if(index == -1){
					isFit = false;
					return isFit;
				}
				
			}
			
		}else if(queryTask.getQprov() != null && !(queryTask.getQprov()).equals("")){
			System.out.println("查询地名ID" + queryTask.getQprov());
			if(user.getProv() != null && !(user.getProv()).equals("")){
				
				if(queryTask.getQprov().equals(user.getProv())){
					//如果省名匹配
					
					
					//查看查询条件是否有城市名
					if(queryTask.getQcity() != null && !(queryTask.getQcity()).equals("") && !(queryTask.getQcity()).equals("不限")){
						
						//如果不为空,判断用户是否有城市名
						if(user.getCity() != null && !(user.getCity()).equals("")){
							
							//用户有城市名
							
							//判断两城市名是否相等
							if(!(queryTask.getQcity().equals(user.getCity()))){
								//不相等
								isFit = false;
								return isFit;
								
							}
							
							
						}
						
					}
					
				}else{
					//省名不匹配
					isFit = false;
					return isFit;
				}
				
			}
			
		}else if(queryTask.getQage() == null && !(queryTask.getQage()).equals("")){
			System.out.println("查询年龄" + queryTask.getQage());
			int userAge = -1;
			
			if(user.getDate() != null && !(user.getDate()).equals("")){
				
				String datenow= new SimpleDateFormat("yyyy年MM月dd日").format(new Date());
				userAge = (Integer.parseInt(datenow.substring(0, 4)) - Integer.parseInt(user.getDate().substring(0, 4)));
				
				boolean isFitAge = false;
				
				//判断年龄是否在查找范围
				if(queryTask.getQage().equals("18岁以下")){
					
					if(userAge <= 18){
						isFitAge = true;
					}
					
				}else if(queryTask.getQage().equals("19~22岁")){
					
					if(userAge > 18 && userAge < 23){
						isFitAge = true;
					}
					
				}else if(queryTask.getQage().equals("23~29岁")){
					
					if(userAge >= 23 && userAge < 30){
						isFitAge = true;
					}
					
				}else if(queryTask.getQage().equals("30~39岁")){
					
					if(userAge >= 30 && userAge < 40){
						isFitAge = true;
					}
					
				}else if(queryTask.getQage().equals("40岁以上")){
					
					if(userAge >= 40){
						isFitAge = true;
					}
					
				}
				
				
				if(!isFitAge){
					isFit = false;
					return isFit;
				}
				
			}
			
		}else if(queryTask.getQsex() != null && !(queryTask.getQsex()).equals("")){
	
			System.out.println("查询性别" + queryTask.getQsex());
			
			//判断性别是否匹配
			if(user.getSex() != null && !(user.getSex()).equals("")){
				
				if(!(queryTask.getQsex().equals(user.getSex()))){
					
					isFit = false;
					
				}
				
			}
			
		}
		System.out.println("匹配条件结束");
		return isFit;
	}
}
