package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection;

    {
        try {
            connection = CustomDataSource.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers" +
            "  (firstname, lastname, age) VALUES " +
            " (?, ?, ?);";
    private static final String updateUserSQL = "UPDATE myusers SET firstname=? ,lastname=?, age=? WHERE id = ?;";
    private static final String deleteUser = "delete from myusers where id = ?;";
    private static final String findUserByIdSQL = "select * from myusers where id =?";
    private static final String findUserByNameSQL = "select * from myusers where firstname =?";
    private static final String findAllUserSQL = "select * from myusers";

    public Long createUser(User user) {
        try (PreparedStatement pstmt = connection.prepareStatement(createUserSQL)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setInt(3, user.getAge());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return findUserByName(user.getFirstName()).getId();
    }

    public User findUserById(Long userId) {
        User user = new User();
        try (PreparedStatement pstmt = connection.prepareStatement(findUserByIdSQL)) {
            pstmt.setLong(1, userId);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName(resultSet.getString("lastname"));
                user.setAge(resultSet.getInt("age"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = new User();
        try (PreparedStatement pstmt = connection.prepareStatement(findUserByNameSQL)) {
            pstmt.setString(1, userName);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName(resultSet.getString("lastname"));
                user.setAge(resultSet.getInt("age"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(findAllUserSQL)) {
            try (ResultSet resultSet = pstmt.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setFirstName(resultSet.getString("firstname"));
                    user.setLastName(resultSet.getString("lastname"));
                    user.setAge(resultSet.getInt("age"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public User updateUser(User user) {
        try (PreparedStatement pstmt = connection.prepareStatement(updateUserSQL)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setInt(3, user.getAge());
            pstmt.setLong(4, user.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public void deleteUser(Long userId) {
        try (PreparedStatement pstmt = connection.prepareStatement(deleteUser)) {
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}