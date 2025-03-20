package com.tutu.finsmanager.appuser;

import com.tutu.finsmanager.dao.entities.Business;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Data
@Entity
public class AppUser implements UserDetails {
    @SequenceGenerator(
            name = "app_user_sequence",
            sequenceName = "app_user_sequence",
            allocationSize = 1
    )

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "id") public Integer id;


    //private Long id;
    @Transient
    private Date date = new Date();
    @Transient
    static SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.mm.yyyy hh:mm:ss");

    private Long parent_id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String position;
    private String email;
    private String password;
    private String newpassword;
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    private Boolean locked = false;
    private Boolean enabled = false;
    private String access_dt = formatForDateNow.format(date);
    private String access_status = "demo";
    @Column(name = "created", columnDefinition = "TIMESTAMP DEFAULT NOW()")
    private Date created;
    private String controlResp;
    private String articleResp;
    private String analyticResp;
    private String cAgentResp;

    public AppUser( Long parent_id,
                    String firstName,
                    String lastName,
                    String middleName,
                    String phone,
                    String email,
                    String password,
                    String newpassword,
                    Date created,
                    AppUserRole appUserRole,
                    String controlResp,
                    String articleResp,
                    String analyticResp,
                    String cAgentResp) {
        this.parent_id = parent_id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.newpassword = newpassword;
        this.created = created;
        this.appUserRole = appUserRole;
        this.controlResp = controlResp;
        this.articleResp = articleResp;
        this.analyticResp = analyticResp;
        this.cAgentResp = cAgentResp;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(appUserRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public String getMiddleName() { return middleName; }
    public Long getParent_id() {return parent_id;}
    public String getPhone() { return phone; }
    public String getPosition() { return position; }
    public AppUserRole getRole() { return appUserRole; }

    public void setParent_id(Long parent_id) {this.parent_id = parent_id;}

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getAccess_dt() {return access_dt; }
    public void setAccess_dt(String access_dt) {this.access_dt = access_dt; }

    public Date getCreated() { return created; }
    public void setCreated(Date created) { this.created = created; }

    public String getAccess_status() {
        String strResult = access_status;
        switch (access_status) {
            case "demo": {
                strResult = "Пробный период";
            }break;
        }
        return strResult;
    }
    public void setAccess_status(String access_status) {this.access_status = access_status;}

    public void Decode(){
        String strResult = access_status;
        switch (access_status) {
            case "demo": {
                strResult = "Пробный период";
            }break;
        }
        access_status = strResult;
    }
}
