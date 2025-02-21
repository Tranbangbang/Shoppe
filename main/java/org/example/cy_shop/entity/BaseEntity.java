package org.example.cy_shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Date;

@MappedSuperclass
public class BaseEntity {
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "create_By")
    private String createBy;
    @Column(name = "modifier_date")
    private LocalDateTime modifierDate;

    @Column(name = "modifier_by")
    private String modifierBy;

    @PrePersist
    public void prePersist(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            this.createBy = userDetails.getUsername();
            this.modifierBy = userDetails.getUsername();

            this.createDate = UtilsFunction.getVietNameTimeNow();
            this.modifierDate = UtilsFunction.getVietNameTimeNow();
        }catch (Exception e){
            this.createBy ="Unknow User";
            this.modifierBy ="Unknow User";
            this.createDate = UtilsFunction.getVietNameTimeNow();
            this.modifierDate = UtilsFunction.getVietNameTimeNow();
        }

    }

    @PreUpdate
    public void preUpdate(){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            this.modifierBy = userDetails.getUsername();
            this.modifierDate = UtilsFunction.getVietNameTimeNow();
        }catch (Exception e){
            this.modifierBy ="Unknow User";
            this.modifierDate = UtilsFunction.getVietNameTimeNow();
        }
    }


    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getModifierDate() {
        return modifierDate;
    }

    public void setModifierDate(LocalDateTime modifierDate) {
        this.modifierDate = modifierDate;
    }

    public String getModifierBy() {
        return modifierBy;
    }

    public void setModifierBy(String modifierBy) {
        this.modifierBy = modifierBy;
    }
}
