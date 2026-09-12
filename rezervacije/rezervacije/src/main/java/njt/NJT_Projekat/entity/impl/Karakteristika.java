package njt.NJT_Projekat.entity.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import njt.NJT_Projekat.entity.MyEntity;

@Entity
@Table(name = "karakteristika")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Karakteristika extends MyEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKarakteristike;

    private String naziv;
}