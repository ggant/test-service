package si.outfit7.entity;

import jakarta.persistence.*;

@NamedQuery(name="UserEntity.findAll", query="SELECT u FROM UserEntity u")
@NamedQuery(name="UserEntity.findUserByUserID", query="SELECT u FROM UserEntity u where u.userId = :userId")
@NamedQuery(name="UserEntity.deleteUser", query="DELETE FROM UserEntity u where u.userId = :userId")
@Entity
@Table(name = "TEST_USER")
public class UserEntity {

    @Id
    @Column(name = "USERID", length = 8)
    private String userId;

    @Column(name = "TIMEZONE", length = 50)
    private String timezone;

    @Column(name = "CC", length = 2)
    private String cc;

    @Column(name = "COUNTER", length = 2)
    private int counter;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
