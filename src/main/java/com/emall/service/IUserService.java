package com.emall.service;

import com.emall.common.ServerResponse;
import com.emall.pojo.User;

public interface IUserService {
    public ServerResponse<User> login(String username, String password);
    public ServerResponse<String> register(User user);
    public ServerResponse<String> checkValid(String value, String type);
    public ServerResponse<String> forgetGetQuestion(String username);
    public ServerResponse<String> checkAnswer(String username, String question, String answer);
    public ServerResponse<String> forgetResetPassword(String username, String password, String token);
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);
    public ServerResponse<User> updateInfo(User user);
    public ServerResponse<User> getInformation(Integer userId);
    public ServerResponse<String> checkAdminRole(User user);
}
