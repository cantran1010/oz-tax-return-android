package au.mccann.oztaxreturn.model;

import au.mccann.oztaxreturn.database.UserEntity;

/**
 * Created by CanTran on 4/16/18.
 */
public class RegisterReponse {
    private String token;
    private UserEntity userEntity;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public String toString() {
        return "RegisterReponse{" +
                "token='" + token + '\'' +
                ", userEntity=" + userEntity +
                '}';
    }
}
