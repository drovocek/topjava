package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.Valid;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static ru.javawebinar.topjava.util.ValidationUtil.jdbcEntityValidation;

@Repository
@Transactional
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> USER_ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);
    private static final RowMapper<Role> ROLE_ROW_MAPPER = (rs, rowNum) -> Role.valueOf(rs.getString("role"));

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public User save(@Valid User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        List<Role> roles = List.copyOf(user.getRoles());
        jdbcEntityValidation(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            insertBatchRole(roles, user.getId());
        } else if (
                namedParameterJdbcTemplate.update("""
                           UPDATE users SET name=:name, email=:email, password=:password, 
                           registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                        """, parameterSource) == 0 |
                        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", user.getId()) == 0 |
                        insertBatchRole(roles, user.getId()).length == 0
        ) {
            return null;
        }

        return user;
    }

    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", USER_ROW_MAPPER, id);
        User user = DataAccessUtils.singleResult(users);
        List<Role> roles = jdbcTemplate.query("SELECT * FROM user_roles WHERE user_id=?", ROLE_ROW_MAPPER, id);
        if (user != null) {
            user.setRoles(Set.copyOf(roles));
        }
        return user;
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", USER_ROW_MAPPER, email);
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            List<Role> roles = jdbcTemplate.query("SELECT * FROM user_roles WHERE user_id=?", ROLE_ROW_MAPPER, user.getId());
            user.setRoles(Set.copyOf(roles));
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", USER_ROW_MAPPER);
        List<Map<String, Object>> roleMap = jdbcTemplate.queryForList("SELECT user_id, role FROM user_roles");

        Map<Integer, Collection<Role>> rolePartitioningMap = new HashMap<>();
        roleMap.forEach(
                m -> {
                    Integer key = (Integer) m.get("user_id");
                    Role role = Role.valueOf((String) m.get("role"));
                    rolePartitioningMap.putIfAbsent(key, new HashSet<>());
                    rolePartitioningMap.get(key).add(role);
                }
        );
        users.forEach(u -> u.setRoles(rolePartitioningMap.get(u.getId())));

        return users;
    }

    private int[] insertBatchRole(final List<Role> roles, int userId) {
        return jdbcTemplate.batchUpdate("INSERT INTO user_roles VALUES(?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, userId);
                        ps.setString(2, roles.get(i).name());
                    }

                    @Override
                    public int getBatchSize() {
                        return roles.size();
                    }
                });
    }
}
