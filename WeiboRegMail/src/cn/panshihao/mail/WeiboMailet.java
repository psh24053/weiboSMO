package cn.panshihao.mail;

import javax.mail.MessagingException;

import org.apache.mailet.GenericMailet;
import org.apache.mailet.Mail;

public class WeiboMailet extends GenericMailet{

	@Override
	public void service(Mail mail) throws MessagingException {
		// TODO Auto-generated method stub
		System.out.println("WeiboMailet -> "+mail.getName());
		System.out.println("WeiboMailet -> "+mail.getSender());
	}

}
