package com.emall.service.impl;

import com.emall.common.CONST;
import com.emall.common.ServerResponse;
import com.emall.common.TokenCache;
import com.emall.dao.UserMapper;
import com.emall.pojo.User;
import com.emall.service.IUserService;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class userServiceImplement implements IUserService {

    @Autowired
    private UserMapper userMapper;





    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){
            return ServerResponse.responseByError("The username does not exist.");
        }

        password = DigestUtils.md5Hex(password);
        User user = userMapper.loginCheck(username, password);
        if(user == null){
            return ServerResponse.responseByError("Password is wrong");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.responseBySuccess("Login Success", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> response = this.checkValid(user.getUsername(), CONST.USERNAME);
        if(!response.isSuccess()){
            return response;
        }
        response = this.checkValid(user.getEmail(), CONST.EMAIL);
        if(!response.isSuccess()){
            return response;
        }

        user.setRole(CONST.ROLE.CUSTOMER);
        //md5hash using apache codec
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.responseByError("Register fail. Please try again.");
        }
        return ServerResponse.responseBySuccessMessage("Register success.");
    }

    @Override
    public ServerResponse<String> checkValid(String value, String type){
        if(CONST.USERNAME.equals(type)){
            int resultCount = userMapper.checkUsername(value);
            if(resultCount>0){
                return ServerResponse.responseByError("The Username exists.");
            }
        }
        if(CONST.EMAIL.equals(type)){
            int resultCount = userMapper.checkUserEmail(value);
            if(resultCount>0){
                return ServerResponse.responseByError("The email exists.");
            }
        }
        return ServerResponse.responseBySuccess();
    }

    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {
        //check if username exist in database first, not really necessary
        ServerResponse<String> nameNotExist = this.checkValid(username, CONST.USERNAME);
        if(nameNotExist.isSuccess()){
            return ServerResponse.responseByError("This username is not in database.");
        }
        String question = userMapper.selectQuestion(username);
        if(question.equals(null)){
            return ServerResponse.responseByError("The question is null");
        }
        return ServerResponse.responseBySuccess(question);

    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0){
            String token = UUID.randomUUID().toString();
            TokenCache.setKey("token_"+username, token);
            return ServerResponse.responseBySuccess(token);
        }
        return ServerResponse.responseByError("Answer is not quite right, try again.");

    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String password, String token) {
        //some null checks
        if(StringUtils.isBlank(token)){
            return ServerResponse.responseByError("Please answer the question again to get a new token.");
        }
        ServerResponse<String> nameNotExist = this.checkValid(username, CONST.USERNAME);
        if(nameNotExist.isSuccess()){
            return ServerResponse.responseByError("This username is not in database.");
        }

        //check token with local token
        String localToken = TokenCache.getKey("token_"+username);
        if(StringUtils.isBlank(localToken)){
            return ServerResponse.responseByError("Token expires. Please reset again.");
        }
        //use equals of stringUtils for stablity, receive null argus
        if(StringUtils.equals(token, localToken)){
            //remove token in local
            TokenCache.removeKey("token_"+username);
            String md5Password = DigestUtils.md5Hex(password);
            int updateResultCount = userMapper.updatePasswordByUsername(username, md5Password);
            if(updateResultCount>0){
                return ServerResponse.responseBySuccessMessage("Update success");
            }else{
                return ServerResponse.responseByError("Update exception occurs.");
            }
        }else{
            return ServerResponse.responseByError("Incorrect token.");
        }

    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        int userId = user.getId();
        int resultCount = userMapper.checkPassword(DigestUtils.md5Hex(passwordOld), userId);
        if(resultCount > 0 ){
            int updateCount = userMapper.resetPasswordById(DigestUtils.md5Hex(passwordNew), userId);
            if(updateCount > 0){
                return ServerResponse.responseBySuccessMessage("Update success.");
            }else{
                return ServerResponse.responseByError("Update fail.");
            }
        }else{
            return ServerResponse.responseByError("Old password is wrong.");
        }
    }

    @Override
    public ServerResponse<User> updateInfo(User user) {
        int selectCount = userMapper.checkEmailById(user.getEmail(), user.getId());
        if(selectCount > 0){
            return ServerResponse.responseByError("Email already used in another account.");
        }

        User newUser = userMapper.selectByPrimaryKey(user.getId());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone());
        newUser.setQuestion(user.getQuestion());
        newUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(newUser);
        if(updateCount > 0){
            newUser = userMapper.selectByPrimaryKey(newUser.getId());
            return ServerResponse.responseBySuccess("Update success", newUser);
        }else{
            return ServerResponse.responseByError("Update fail.Try again.");
        }
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.responseByError("User account Id does not exist.");
        }
        return ServerResponse.responseBySuccess(user);
    }


    //check admin role
    public ServerResponse<String> checkAdminRole(User user){
        User selectedUser = userMapper.selectByPrimaryKey(user.getId());
        if(selectedUser.getRole() == CONST.ROLE.ADMIN){
            return ServerResponse.responseBySuccess();
        }
        return ServerResponse.responseByError("Not an admin.");
    }
}
