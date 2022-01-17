package com.softline.dossier.be.domain;

import com.softline.dossier.be.Tools.Functions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SQLDelete(sql = "UPDATE check_sheet SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
public class CheckSheet extends BaseEntity implements Attachment {
    @Transient
    Functions.UnsafeRunnable afterCreate;
    private String storageName;
    private String contentType;
    private String realName;
    @OneToMany(mappedBy = "checkSheet")
    private List<CheckItem> invalidItems;

    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    private FileTask fileTask;

    public CheckSheet(FileTask fileTask, List<CheckItem> invalidItems) {
        this.fileTask = fileTask;
        this.invalidItems = invalidItems;
    }

    @Override
    public String toString() {
        return "FicheControl{" +
                "id=" + id +
                ", invalidItems=" + invalidItems +
                '}';
    }
}
