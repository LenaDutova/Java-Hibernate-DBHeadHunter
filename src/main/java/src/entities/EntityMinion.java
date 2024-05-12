package src.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "minion")
public class EntityMinion {

    @Id
    @Column(insertable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "minionIdSequence")
    @SequenceGenerator(name = "minionIdSequence", sequenceName = "minion_id_seq", allocationSize = 1)
    public Integer id;

    @Column(nullable = false)
    public String name;

    @Column(name = "eyes_count", insertable = false)
    public int eyesCount;

    @ManyToMany
    @JoinTable(
            name = "contract",
            joinColumns = @JoinColumn(name="id_minion"),
            inverseJoinColumns = @JoinColumn(name = "id_villain"))
    public Set<EntityVillain> villains = new HashSet<>();

    @Override
    public String toString() {
        return "id=" + id + "\t| eyes=" + eyesCount + "\t| name='" + name + "\'\t| villains count=" + villains.size();
    }
}
