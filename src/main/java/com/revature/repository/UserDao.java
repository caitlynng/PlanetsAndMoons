package com.revature.repository;

import com.revature.models.User;
import com.revature.models.UsernamePasswordAuthentication;
import com.revature.utilities.ConnectionUtil;

import java.sql.*;

public class UserDao {

    public User getUserByUsername(String username) {
        try (Connection connection = ConnectionUtil.createConnection()) {
            String sql = "select * from users where username = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User foundUser = new User();
                foundUser.setId(rs.getInt("id"));
                foundUser.setUsername(rs.getString("username"));
                foundUser.setPassword(rs.getString("password"));
                return foundUser;
            }
            return null;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }


    public User createUser(UsernamePasswordAuthentication registerRequest) {
        try (Connection connection = ConnectionUtil.createConnection()) {
//            System.out.println("Creating user with username: " + registerRequest.getUsername());
            String sql = "insert into users (username, password) values (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, registerRequest.getUsername());
            ps.setString(2, registerRequest.getPassword());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                User createdUser = new User();
                createdUser.setId(rs.getInt(1));
                createdUser.setUsername(registerRequest.getUsername());
                createdUser.setPassword(registerRequest.getPassword());
                return createdUser;
            }
            return new User();
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

}
