package xyz.wjsay.mywebchat.dao;

import org.apache.ibatis.annotations.*;
import xyz.wjsay.mywebchat.domain.User;

@Mapper
public interface UserDao {
    @Select("select username, password, gender, nickname, last_login_time lastLoginTime from user where username = #{username}")
    User getUserByUsername(String username);

    @Insert("insert into user (username, password, nickname, gender, last_login_time, login_count)"
            + " values(#{username}, #{password}, #{nickname}, #{gender}, #{lastLoginTime}, 0)")
    int addUser(User user);

    @Update("update user set last_login_time = #{lastLoginTime}, login_count = login_count + 1 where username = #{username}")
    void recordLogin(User user);
}
