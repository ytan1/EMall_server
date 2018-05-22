package com.emall.dao;

import com.emall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkUserEmail(String email);

    User loginCheck(@Param("username") String username, @Param("password") String password);

    String selectQuestion(String username);

    int checkAnswer(@Param("username")String username, @Param("question")String question, @Param("answer")String answer);

    int updatePasswordByUsername(@Param("username") String username, @Param("password") String password);

    int checkPassword(@Param("passwordOld") String password, @Param("userId") int userId);

    int resetPasswordById(@Param("passwordNew") String passwordNew, @Param("userId") int userId);

    int checkEmailById(@Param("email") String email, @Param("userId") int userId);
}