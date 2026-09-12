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
@Table(name = "tip_dogadjaja")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipDogadjaja extends MyEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipDogadjaja;

    private String naziv;
}
