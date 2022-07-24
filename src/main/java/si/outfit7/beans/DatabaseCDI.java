package si.outfit7.beans;


import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import si.outfit7.entity.UserEntity;
import si.outfit7.types.User;
import si.outfit7.util.Converter;

import java.util.ArrayList;
import java.util.List;


/**
 * CDI bean that incorporate logic for communication with database
 * @author Goran Corkovic
 */
@RequestScoped
public class DatabaseCDI {

	@PersistenceContext(unitName = "test-service-jpa")
	private EntityManager em;

	@Transactional
	public User createUser(User user) {
		if (user == null)
			return null;

		/*
		  Convert User in UserEntity and persist in database
		 */
		UserEntity userEntity = Converter.convertUserToUserEntity(user);
		em.persist(userEntity);

		return Converter.convertUserEntityToUser(userEntity);
	}
	@Transactional
	public User updateUser(User user) {
		if (user == null && user.getUserId() == null)
			return null;

		/*
		  Find user in database and persist changes
		 */
		UserEntity userEntity = em.find(UserEntity.class, user.getUserId());
		userEntity.setCc(user.getCc());
		userEntity.setCounter(user.getCounter());
		userEntity.setTimezone(user.getTimezone());

		return Converter.convertUserEntityToUser(userEntity);

	}
	@Transactional
	public List<User> getAllUsers() {
		/*
		  Find all users in database
		 */
		List<UserEntity> listUserEntity = em.createNamedQuery("UserEntity.findAll").getResultList();
		if(listUserEntity == null || listUserEntity.isEmpty())
			return new ArrayList<>();

		/*
		  Convert UserEntity in User
		 */
		List<User> listUser= new ArrayList<>();
		listUserEntity.stream().forEach(user-> {
			listUser.add(Converter.convertUserEntityToUser(user));
		});

		return listUser;
	}
	@Transactional
	public User getUserDetails(String userId) {
		if(userId == null || userId.isEmpty())
			return null;
		/*
		  Find user by userId and convert result in User type
		 */
		List<UserEntity> userEntity = em.createNamedQuery("UserEntity.findUserByUserID", UserEntity.class).setParameter("userId", userId).getResultList();
		if(userEntity != null && !userEntity.isEmpty())
			return Converter.convertUserEntityToUser(userEntity.get(0));
		else
			return null;
	}
	@Transactional
	public boolean deleteUser(String userId) {
		if(userId == null || userId.isEmpty())
			return false;

		/*
		  Find UserEntity by userId and delete them. Return true.
		  If UserEntity doesn't exists return false
		 */
		UserEntity user = em.find(UserEntity.class, userId);
		if(user != null) {
			em.remove(user);
			return true;
		}
		return false;
	}


}
