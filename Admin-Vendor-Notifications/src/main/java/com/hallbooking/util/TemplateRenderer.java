package com.hallbooking.util;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.StringWriter;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateRenderer {
    private final TemplateEngine thymeleafEngine;

    @Autowired
    private Configuration freemarkerConfig;

    public String renderEmailTemplate(String contentTemplate, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        context.setVariable("contentTemplate", contentTemplate);
        context.setVariable("subject", "Your OTP Code");
        return thymeleafEngine.process("layout/base-email", context);
    }

    public String renderOtpTemplate(String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);
        return thymeleafEngine.process("otp", context);
    }

    public String renderConfirmationTemplate(String name, String bookingId) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("bookingId", bookingId);
        return thymeleafEngine.process("confirmation", context);
    }

    public String renderFreeMarker(String templateName, Map<String, Object> model) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter out = new StringWriter();
            template.process(model, out);
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to render FreeMarker template: " + e.getMessage());
        }
    }

    public String renderSmsTemplate(String templateName, Map<String, Object> model) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to render SMS template: " + e.getMessage(), e);
        }
    }
}