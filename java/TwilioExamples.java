// import com.sendgrid.*;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.io.IOException;

// get twilio jar from https://repo1.maven.org/maven2/com/twilio/sdk/twilio/9.2.4/twilio-9.2.4-jar-with-dependencies.jar

public class TwilioExamples {

    private static final String ACCOUNT_SID = "";
    private static final String AUTH_TOKEN = "";

    public static void main(String[] args) throws IOException {

        Twilio.init(TwilioExamples.ACCOUNT_SID, TwilioExamples.AUTH_TOKEN);
        TwilioExamples.sendText();
    }

    public static void sendMail(String from, String to, String subject, String message) {
        // Email fromEmail = new Email(from);
        // Email toEmail = new Email(to);
        // Content content = new Content("text/plain", message);
        // Mail mail = new Mail(fromEmail, subject, toEmail, content);

        // SendGrid sg = new SendGrid();
        // Request request = new Request();

        // try {
        //     request.setMethod(Method.POST);
        //     request.setEndpoint("mail/send");
        //     request.setBody(mail.build());
        //     Response response = sg.api(request);
        //     System.out.println(response.getStatusCode());
        //     System.out.println(response.getBody());
        //     System.out.println(response.getHeaders());
        // } catch (IOException ioe) {
        //     throw ioe;
        // }
    }

    public static void sendText() {

        Message message = Message.creator(new PhoneNumber("+19087237673"),
                                            new PhoneNumber("+18337940467"), 
                                            "test mssg").create();

        System.out.println(message.getSid());
  
    }


}