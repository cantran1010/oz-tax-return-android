package au.mccann.oztaxreturn.model;

import au.mccann.oztaxreturn.database.UserEntity;

/**
 * Created by CanTran on 4/16/18.
 */
public class UserReponse {
    private String token;
    private UserEntity user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserReponse{" +
                "token='" + token + '\'' +
                ", user=" + user +
                '}';
    }
}

