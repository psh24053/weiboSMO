package cn.panshihao.mail;

import java.io.IOException;
import java.util.Collection;

import javax.mail.MessagingException;

import org.apache.mailet.GenericMatcher;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;

public class WeiboMatcher extends GenericMatcher {

	@Override
	public Collection<MailAddress> match(Mail mail) throws MessagingException {
		// TODO Auto-generated method stub
		
		
		System.out.println("weiboMatcher -> "+mail.getName());
		System.out.println("weiboMatcher -> "+mail.getSender());
		try {
			System.out.println("weiboMatcher -> "+mail.getMessage().getContent());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
