package com.gmail.vyazankin.pavel
/**
 * @author pvyazankin
 * @since 14/08/2011
 */

import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Multipart
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class HostcommMailSender {

  public static void main(String[] args) {

    if (args.length ==0 || args[0].equals("help")) {
      println("HostcommMailSender usage:\nHostcommMailSender Tos subject body [attachmentPath]")
      System.exit(0)
    }

    def tos = args[0]
    def subject = args[1]
    def body = args[2]
    def attachmentPath
    if (args.length > 3) {
      attachmentPath = args[3]
    }

    Properties props = new Properties();

    def host = "mail.hostcomm.ru"
    def user = "pvyazankin@hostcomm.ru"
    def password = "killa11"
    def port = 465

    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);
    props.put("mail.smtp.user", user);

    props.put("mail.smtp.auth", "true");
    props.put("mail.smtps.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
//    props.put("mail.smtp.debug", "false");

    props.put("mail.smtp.socketFactory.port", port);
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.put("mail.smtp.socketFactory.fallback", "false");

    Session session = Session.getDefaultInstance(props, null);
    MimeMessage mimeMessage = new MimeMessage(session);

    //tokenize out the recipients in case they came in as a list
    StringTokenizer tok = new StringTokenizer(tos, ";");
    ArrayList emailTos = new ArrayList();

    while (tok.hasMoreElements()) {
      emailTos.add(new InternetAddress(tok.nextElement().toString()));
    }

    InternetAddress[] to = new InternetAddress[emailTos.size()];
    to = (InternetAddress[]) emailTos.toArray(to);
    mimeMessage.setRecipients(MimeMessage.RecipientType.TO, to);
    mimeMessage.setFrom(new InternetAddress(user));
    mimeMessage.setSubject(subject);
    mimeMessage.setText(body)

    if (attachmentPath) {
      // create the second message part
      MimeBodyPart mimeBodyPart = new MimeBodyPart();

      // attach the file to the message
      FileDataSource fileDataSource = new FileDataSource(attachmentPath);
      mimeBodyPart.setDataHandler(new DataHandler(fileDataSource));
      mimeBodyPart.setFileName(fileDataSource.getName());

      // create the Multipart and add its parts to it
      Multipart multipart = new MimeMultipart();
      MimeBodyPart textPart = new MimeBodyPart()
      textPart.setText(body)

      multipart.addBodyPart(textPart)
      multipart.addBodyPart(mimeBodyPart);

      // add the Multipart to the message
      mimeMessage.setContent(multipart);
    }

    Transport transport = session.getTransport("smtps");
    transport.connect(host, user, password);
    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
    transport.close()
  }
}

//GroovyMailSender.main()

