package cn.edu.hunnu.acm.service;

import cn.edu.hunnu.acm.model.User;

public interface EmailService {
    void sendMailSimple(User user);
}
