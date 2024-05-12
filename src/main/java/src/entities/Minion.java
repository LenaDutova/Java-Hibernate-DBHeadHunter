package src.entities;

import jakarta.persistence.*;

@Entity
@Table
public class Minion {

    @Id
    @Column(insertable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "minionIdSequence")
    @SequenceGenerator(name = "minionIdSequence", sequenceName = "minion_id_seq", allocationSize = 1)
    public Integer id;

    @Column(nullable = false)
    public String name;

    @Column(name = "eyes_count", insertable = false)
    public int eyesCount;

    @Override
    public String toString() {
        return "id=" + id + "\t| eyes=" + eyesCount + "\t| name='" + name + "\'";
    }
}
