package com.hq.heroes.common.service;

import com.hq.heroes.common.dto.EmailMessage;


public interface EmailService {

    public void sendMail(EmailMessage emailMessage, String type);

    public String createCode();

}
