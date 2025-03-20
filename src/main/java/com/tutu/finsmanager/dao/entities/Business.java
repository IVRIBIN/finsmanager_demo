package com.tutu.finsmanager.dao.entities;

import com.tutu.finsmanager.appuser.AppUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class Business {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;
    @Column(name = "name")
    public String name;
    @Column(name = "description")
    public String description;
    @Column(name = "created", columnDefinition = "TIMESTAMP DEFAULT NOW()")
    public String created;
    @Column(name = "main_user")
    public Long main_user;

    /*
    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User_Business> userBusinessList = new ArrayList<>();
    public void addUserBusiness(User_Business userBusiness){
        this.userBusinessList.add(userBusiness);
        userBusiness.setBusiness(this);
    }
    public void removeBusiness(User_Business userBusiness){
        this.userBusinessList.remove(userBusiness);
        userBusiness.setBusiness(null);
    }
     */
}
