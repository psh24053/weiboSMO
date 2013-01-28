package cn.panshihao.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.GenericMailet;
import org.apache.mailet.Mail;

public class WeiboMailet extends GenericMailet{

	@Override
	public void service(Mail mail) throws MessagingException {
		// TODO Auto-generated method stub
		
		MimeMessage message = mail.getMessage();
		
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(message.getInputStream(), "utf-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String temp = "";
		String result = "";
		
		try {
			while((temp = in.readLine()) != null){
				result += temp.trim() + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		System.out.println(result);
		System.out.println(mail.getRecipients().toArray());
		
		
	}

}
