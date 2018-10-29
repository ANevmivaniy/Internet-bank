package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import exceptions.RolesNotFoundException;
import model.CBLogin;
import model.CBUser;

public class UserDaoImpl implements UserDao {
	private final static Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);
	
	//рудимент для возможного использования в будущем
	@SuppressWarnings("unused")
	private DataSource dataSource;
	
	private JdbcTemplate jdbcTemplate;
	
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	@Transactional
	public void register(CBUser user) {
		try {
			final String INSERT_USER = "INSERT INTO users (username, password, firstname, lastname, email, address, phone) VALUES(?,?,?,?,?,?,?)";
		    jdbcTemplate.update(INSERT_USER, new Object[] { user.getUserName(), user.getPassword(), user.getFirstName(),
		    user.getLastName(), user.getEmail(), user.getAddress(), user.getPhoneNumber() });
		    
		    final String SET_ROLE_FOR_NEW_USER = "INSERT INTO users_role (username, role) VALUES(?,?)";
		    jdbcTemplate.update(SET_ROLE_FOR_NEW_USER, new Object[] {user.getUserName(), "USER"});
		}catch(DataAccessException e) {
			LOGGER.fatal(e.getMessage());
		}
	}

	@Override
	public CBUser validateUserForLogin(CBLogin login) throws UsernameNotFoundException{
		//sql inject
		try {
			final String sql = "select * from users where username='" + login.getUserName() + "' and password='" + login.getPassword()
		    + "'";
		    List<CBUser> users = jdbcTemplate.query(sql, new UserMapper());
		    if(users.size() == 0) throw new UsernameNotFoundException("Пользователь с таким именем в системе не найден");
			return  users.get(0);
		}catch(DataAccessException e) {
			LOGGER.fatal(e.getMessage());
			return null;
		}
	}
	
	@Override
	public String getRole(String username) throws RolesNotFoundException{
		try {
			final String roleSQLQuery = "SELECT * FROM users_role WHERE username='" + username +"'";
			List<String> roles = jdbcTemplate.query(roleSQLQuery, (rs, arg1) ->{
				final String role = rs.getString("role");
				return role;
			});
			if(roles.size() == 0) throw new RolesNotFoundException("Данный пользователь отсутствует в системе либо ему не присоена роль.");
			return roles.get(0);
		}catch(DataAccessException e) {
			LOGGER.fatal(e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public CBUser loadUserByName(String username) throws UsernameNotFoundException {
		try {
			final String forUsernameSQLQuery = "select * from users where username='" + username + "'";
			List<CBUser> users = jdbcTemplate.query(forUsernameSQLQuery, new UserMapper());
			if(users.size() == 0) throw new UsernameNotFoundException("Пользователь с таким именем в системе не найден");
			return  users.get(0) ;	
		}catch(Exception e) {
			LOGGER.fatal(e.getClass() + "{}: "   + e.getMessage());
			return null;
		}
	}

}
class UserMapper implements RowMapper<CBUser> {
	
	 public CBUser mapRow(ResultSet rs, int arg1) throws SQLException {
		 
	    CBUser user = new CBUser();
	    user.setUserName(rs.getString("username"));
	    user.setPassword(rs.getString("password"));
	    user.setFirstName(rs.getString("firstname"));
	    user.setLastName(rs.getString("lastname"));
	    user.setEmail(rs.getString("email"));
	    user.setAddress(rs.getString("address"));
	    user.setPhoneNumber(rs.getString("phone"));
	    return user;
	  }
}
