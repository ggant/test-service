package si.outfit7.beans;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import si.outfit7.entity.UserEntity;
import si.outfit7.types.User;
import si.outfit7.util.Converter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Junit class implement test scenarios for testing DatabaseCDI bean.
 * @author Goran Corkovic
 */
class DatabaseCDITest {

    DatabaseCDI database;
    User firstUser;
    User secondUser;

    List<User> userList;

    UserEntity firstUserEntity;

    UserEntity secondUserEntity;

    List<UserEntity> userEntityList;

    EntityManager entityManager;

    @BeforeEach
    void setUp() {

        /**
         * Create two Users and add them into the list
         */
        firstUser = new User();
        firstUser.setUserId("123");
        firstUser.setTimezone("Europe/Ljubljana");
        firstUser.setCc("US");
        firstUser.setCounter(1);

        secondUser = new User();
        secondUser.setUserId("123");
        secondUser.setTimezone("Europe/Ljubljana");
        secondUser.setCc("US");
        secondUser.setCounter(1);

        userList = new ArrayList<>();
        userList.add(firstUser);
        userList.add(secondUser);

        /**
         * Create two JPA entities (UserEntity) and add them into the list
         */
        firstUserEntity = Converter.convertUserToUserEntity(firstUser);
        secondUserEntity = Converter.convertUserToUserEntity(secondUser);

        userEntityList = new ArrayList<>();
        userEntityList.add(firstUserEntity);
        userEntityList.add(secondUserEntity);

        entityManager = Mockito.mock(EntityManager.class);
        database = new DatabaseCDI();
        database.setEm(entityManager);

    }

    @Test
    void createUser() {

        Mockito.doAnswer(new Answer<UserEntity>() {
            @Override
            public UserEntity answer(InvocationOnMock invocationOnMock) throws Throwable {
                return firstUserEntity;
            }
        }).when(entityManager).persist(firstUserEntity);

        User userFromDb = database.createUser(firstUser);
        /**
         * Check if function call persist method on Enterprise manager and return User
         */
        Mockito.verify(entityManager, Mockito.times(1)).persist(Mockito.any(UserEntity.class));
        assertNotNull(userFromDb);
        assertEquals(userFromDb.getUserId(), firstUser.getUserId());
    }

    @Test
    void updateUser() {
        Mockito.doAnswer(new Answer<UserEntity>() {
            @Override
            public UserEntity answer(InvocationOnMock invocationOnMock) throws Throwable {
                return firstUserEntity;
            }
        }).when(entityManager).find(UserEntity.class, firstUser.getUserId());

        User userFromDb = database.updateUser(firstUser);
        /**
         * Check if function call find method on Enterprise manager and return User
         */
        Mockito.verify(entityManager, Mockito.times(1)).find(UserEntity.class, firstUser.getUserId());
        assertNotNull(userFromDb);
        assertEquals(userFromDb.getUserId(), firstUser.getUserId());
    }

    @Test
    void getAllUsers() {
        TypedQuery<UserEntity> query = Mockito.mock(TypedQuery.class);

        Mockito.when(entityManager.createNamedQuery("UserEntity.findAll")).thenReturn((TypedQuery<UserEntity>) query);
        Mockito.when(query.getResultList()).thenReturn(userEntityList);

        List<User> usersFromDb = database.getAllUsers();

        /**
         * Check if function return one User
         */
        assertNotNull(usersFromDb);
        assertEquals(2, usersFromDb.size());
        assertEquals("123", usersFromDb.get(0).getUserId());
        assertEquals("Europe/Ljubljana", usersFromDb.get(0).getTimezone());
        assertEquals("US", usersFromDb.get(0).getCc());
        assertEquals(1, usersFromDb.get(0).getCounter());
    }

    @Test
    void getUserDetails() {

        TypedQuery<UserEntity> query = Mockito.mock(TypedQuery.class);

        Mockito.when(entityManager.createNamedQuery("UserEntity.findUserByUserID", UserEntity.class)).thenReturn((TypedQuery<UserEntity>) query);
        Mockito.when(query.setParameter("userId",firstUserEntity.getUserId())).thenReturn((TypedQuery<UserEntity>) query);
        Mockito.when(query.getResultList()).thenReturn(userEntityList);

        User userFromDb = database.getUserDetails(firstUser.getUserId());

        /**
         * Check if function return one User
         */
        assertNotNull(userFromDb);
        assertEquals("123", userFromDb.getUserId());
        assertEquals("Europe/Ljubljana", userFromDb.getTimezone());
        assertEquals("US", userFromDb.getCc());
        assertEquals(1, userFromDb.getCounter());

    }

    @Test
    void getUserDetailsNoUser() {

        TypedQuery<UserEntity> query = Mockito.mock(TypedQuery.class);

        Mockito.when(entityManager.createNamedQuery("UserEntity.findUserByUserID", UserEntity.class)).thenReturn((TypedQuery<UserEntity>) query);
        Mockito.when(query.setParameter("userId",firstUserEntity.getUserId())).thenReturn((TypedQuery<UserEntity>) query);
        Mockito.when(query.getResultList()).thenReturn(null);

        User userFromDb = database.getUserDetails(secondUser.getUserId());

        /**
         * Check if function didn't find user
         */
        assertNull(userFromDb);

    }

    @Test
    void deleteUser() {
        Mockito.when(entityManager.find(UserEntity.class,firstUserEntity.getUserId())).thenReturn(firstUserEntity);
        database.deleteUser(firstUser.getUserId());
        /**
         * Check if function call remove method on Enterprise manager
         */
        Mockito.verify(entityManager, Mockito.times(1)).remove(firstUserEntity);

    }
}
