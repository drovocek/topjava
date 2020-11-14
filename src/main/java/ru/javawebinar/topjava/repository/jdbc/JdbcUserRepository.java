package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
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

import java.util.*;

import static ru.javawebinar.topjava.util.ValidationUtil.jdbcEntityValidation;

@Transactional(readOnly = true)
@Repository
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

    @Transactional
    @Override
    public User save(User user) {
        jdbcEntityValidation(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (
                namedParameterJdbcTemplate.update("""
                           UPDATE users SET name=:name, email=:email, password=:password, 
                           registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                        """, parameterSource) == 0
        ) {
            return null;
        } else {
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id = ?", user.getId());
        }
        insertBatchRole(user);
        return user;
    }

    @Transactional
    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", USER_ROW_MAPPER, id);
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            addRoles(user);
            return user;
        }
        return null;
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", USER_ROW_MAPPER, email);
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            addRoles(user);
            return user;
        }
        return null;
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
                    Collection<Role> roles = rolePartitioningMap.computeIfAbsent(key, (k) -> new HashSet<>());
                    roles.add(role);
                }
        );
        users.forEach(u -> u.setRoles(rolePartitioningMap.get(u.getId())));

        return users;
    }

    private void insertBatchRole(User user) {
        final List<Role> roles = List.copyOf(user.getRoles());

        jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles (user_id, role) VALUES(?, ?)", roles, roles.size(),
                (ps, role) -> {
                    ps.setInt(1, user.getId());
                    ps.setString(2, role.name());
                });
    }

    private void addRoles(User user) {
        List<Role> roles = jdbcTemplate.query("SELECT * FROM user_roles WHERE user_id=?", ROLE_ROW_MAPPER, user.getId());
        user.setRoles(roles);
    }
}
