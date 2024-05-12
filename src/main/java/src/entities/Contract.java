package src.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Это сущность @Entity, которая соответствует
 * таблице @Table c именем "contract". В случае,
 * когда название таблицы и класса совпадают,
 * можно не прописывать свойство "name"
 */
@Entity
@Table
public class Contract {
    /*
        Этот атрибут еще и ключ @Id, значение которого
        генерируется базой данных @GeneratedValue по
        определенному в СУБД правилу с именем "contract_id_seq"
        за счет явно прописанного свойства strategy;
        передавать его при создании не требуется
        за счет свойства insertable = false
     */
    @Id
    @Column(insertable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contractIdSequence")
    @SequenceGenerator(name = "contractIdSequence", sequenceName = "contract_id_seq", allocationSize = 1)
    public Integer id;

    /*
        Атрибут таблицы с nullable = false
        не допускает пустых значений
     */
    @Column (nullable = false)
    public String payment;

    /*
        Если название столбца в таблице и свойства в коде различны,
        то требуется подсказка в аннотации - свойство name.
        Если регистр важен, то значение свойства name
        должно быть в экранированных кавычках.
        Например: (name = "\"startDate\"")
     */
    @Column (name = "start_date", insertable = false)
    public LocalDate startDate;

    @ManyToOne
    @JoinColumn(name = "id_villain", nullable = false)
    public Villain villain;

    @ManyToOne
    @JoinColumn(name = "id_minion", nullable = false)
    public Minion minion;

    /*
        Если требуется валидация параметров или защита данных,
        то в пару к приватным полям, потребуются публичные
        сеттеры, геттеры и конструктор без параметров (явно)
     */

    @Override
    public String toString() {
        return
                "id=" + id +
                        "\t| villain='" + villain.id + '\''+
                        "\t| minion='" + minion.id + '\''+
                        "\t| startDate=" + startDate +
                        "\t| payment='" + payment + '\'';
    }

}
