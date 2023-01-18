package com.gv.jhipsterapp001.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Palha.
 */
@Table("palha")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Palha implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("julia")
    private String julia;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Palha id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJulia() {
        return this.julia;
    }

    public Palha julia(String julia) {
        this.setJulia(julia);
        return this;
    }

    public void setJulia(String julia) {
        this.julia = julia;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Palha)) {
            return false;
        }
        return id != null && id.equals(((Palha) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Palha{" +
            "id=" + getId() +
            ", julia='" + getJulia() + "'" +
            "}";
    }
}
