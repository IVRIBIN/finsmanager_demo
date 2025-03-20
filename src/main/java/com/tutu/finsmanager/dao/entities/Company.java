package com.tutu.finsmanager.dao.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;
    @Column(name = "name", length = 50)
    public String name;
    @Column(name = "description", length = 250)
    public String description;
    @Column(name = "created", columnDefinition = "TIMESTAMP DEFAULT NOW()")
    public String created;
    @Column(name = "main_user")
    public Long main_user;
    @Column(name = "inn", length = 12)
    public String inn;
    @Column(name = "kpp", length = 9)
    public String kpp;
    @Column(name = "account", length = 20)
    public String account;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            nullable = false,
            name = "business_id"
    )
    private Business business;

    public Company(Long id,String name,String description,String created,Long main_user,String inn,String kpp,String account,Business business){
        this.id = id;
        this.name = name;
        this.description = description;
        this.created = created;
        this.main_user = main_user;
        this.inn = inn;
        this.kpp = kpp;
        this.account = account;
        this.business = business;
    }
}
