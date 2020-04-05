package com.project.controller.restcontroller;

import com.project.mail.MailService;
import com.project.model.FeedbackRequest;
import com.project.service.FeedbackRequestService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/feedback-request")
@PropertySource("classpath:application.properties")
public class FeedbackRequestController {
    private final static Logger LOGGER = LoggerFactory.getLogger(FeedbackRequestController.class.getName());
    private final FeedbackRequestService feedbackRequestService;
    private final MailService mailService;
    private final Environment env;

    @PostMapping
    public FeedbackRequest sendNewFeedBackRequest(@RequestBody FeedbackRequest feedbackRequest) {
        LOGGER.debug("POST request '/feedback-request' with {}", feedbackRequest);
        feedbackRequest.setId(null);
        feedbackRequest.setReplied(false);
        feedbackRequest.setSenderName(HtmlUtils.htmlEscape(feedbackRequest
                .getSenderName()
                .replaceAll("'", "")));
        feedbackRequest.setContent(HtmlUtils.htmlEscape(feedbackRequest
                .getContent()
                .replaceAll("'", "")));
        feedbackRequest.setSenderEmail(HtmlUtils.htmlEscape(feedbackRequest
                .getSenderEmail()
                .replaceAll("'", "")));
        return feedbackRequestService.save(feedbackRequest);
    }

    @SuppressWarnings("all")
    @PostMapping("/reply/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void send(@PathVariable Long id, @RequestBody SimpleMailMessage simpleMailMessage) {
        FeedbackRequest feedbackRequest = feedbackRequestService.getById(id);
        simpleMailMessage.setTo(feedbackRequest.getSenderEmail());
        simpleMailMessage.setFrom(env.getProperty("spring.mail.username"));
        feedbackRequest.setReplied(true);
        feedbackRequestService.save(feedbackRequest);
        LOGGER.debug("POST request '/feedback-request/reply/{}' with {}", id, simpleMailMessage);
        mailService.sendEmail(simpleMailMessage);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<FeedbackRequest> getAll() {
        return feedbackRequestService.findAll();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("{id}")
    public FeedbackRequest getById(@PathVariable Long id) {
        return feedbackRequestService.getById(id);
    }
}
