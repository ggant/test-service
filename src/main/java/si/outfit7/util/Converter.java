package si.outfit7.util;

import si.outfit7.entity.UserEntity;
import si.outfit7.types.User;

public class Converter {

    public static User convertUserEntityToUser(UserEntity userEntity) {
        if(userEntity == null)
            return null;

        User user = new User();
        user.setUserId(userEntity.getUserId());
        user.setCc(userEntity.getCc());
        user.setCounter(userEntity.getCounter());
        user.setTimezone(userEntity.getTimezone());

        return user;
    }

    public static UserEntity convertUserToUserEntity(User user) {
        if(user == null)
            return null;

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(user.getUserId());
        userEntity.setCc(user.getCc());
        userEntity.setCounter(user.getCounter());
        userEntity.setTimezone(user.getTimezone());

        return userEntity;
    }
}
