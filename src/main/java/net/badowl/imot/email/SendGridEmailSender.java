package net.badowl.imot.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import net.badowl.imot.PropertyEmailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class SendGridEmailSender {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${sendgrid.apiKey}")
    private String sendgridApiKey;

    @Value("${sendgrid.sender}")
    private String emailSender;

    @Value("${sendgrid.receivers}")
    private String emailReceivers;

    @Value("${sendgrid.teamplate.newProperties}")
    private String templateNewProperties;

    public void send(final List<PropertyEmailData> templateData) throws IOException {
        try {
            final Personalization personalization = new Personalization();
            personalization.addDynamicTemplateData("properties", objectMapper.writeValueAsString(templateData));
            Arrays.stream(emailReceivers.split(",")).map(Email::new).forEach(personalization::addTo);

            final Mail mail = new Mail();
            mail.setFrom(new Email(emailSender));
            mail.setTemplateId(templateNewProperties);
            mail.addPersonalization(personalization);

            final SendGrid sg = new SendGrid(sendgridApiKey);
            final Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }
}