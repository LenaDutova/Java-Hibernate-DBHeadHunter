package src.entities;

import jakarta.persistence.*;

@Entity
@Table
public class Villain {

    @Id
    @Column(insertable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "villainIdSequence")
    @SequenceGenerator(name = "villainSequence", sequenceName = "villain_id_seq", allocationSize = 1)
    public Integer id;

    @Column (nullable = false)
    public String name;

    @Column
    public String nickname;

    @Override
    public String toString() {
        return "id=" + id + "\t| name='" + name + '\'' + "\t| nickname='" + nickname + "\'";
    }
}
