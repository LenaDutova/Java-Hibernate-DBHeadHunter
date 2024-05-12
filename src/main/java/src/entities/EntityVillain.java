package src.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table (name = "villain")
public class EntityVillain {

    @Id
    @Column(insertable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "villainIdSequence")
    @SequenceGenerator(name = "villainSequence", sequenceName = "villain_id_seq", allocationSize = 1)
    public Integer id;

    @Column (nullable = false)
    public String name;

    @Column
    public String nickname;

    @ManyToMany
    @JoinTable(
            name = "contract",
            joinColumns = @JoinColumn(name="id_villain"),
            inverseJoinColumns = @JoinColumn(name = "id_minion"))
    public Set<EntityMinion> minions = new HashSet<>();

    @Override
    public String toString() {
        String str = "";
        for (EntityMinion minion: minions) {
            str += "id=" + id + "\t| name='" + name + '\'' + "\t| nickname='" + nickname + "\'\t| " + minion.toString() + "\n";
        }
        return str;
    }
}
