package src;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import static org.hibernate.cfg.AvailableSettings.*;

import src.entities.*;

public class HibernateRunner {
    public static void main(String[] args) {
        var factory = new Configuration() // configure("hibernate.properties") as default is exists
                // TODO add ORM classes
                .addAnnotatedClass(Villain.class)
                .addAnnotatedClass(Minion.class)
                .addAnnotatedClass(Contract.class)
                .addAnnotatedClass(EntityVillain.class)
                .addAnnotatedClass(EntityMinion.class)
//                // use database from URL, with user/password
//                .setProperty(URL, "jdbc:postgresql://localhost:5432/rut_head_hunter")
//                .setProperty(USER, "postgres")
//                .setProperty(PASS, "postgres")
//                // display SQL in console
//                .setProperty(SHOW_SQL, true)
//                .setProperty(FORMAT_SQL, true)
//                .setProperty(HIGHLIGHT_SQL, true)
                .buildSessionFactory();

        //TODO show all tables
        getVillains(factory); System.out.println();
        getMinions(factory); System.out.println();
        getContracts(factory); System.out.println();

        // TODO show with param "Грю Фелониус Мексон"
        getVillainNamed(factory, "Грю", false); System.out.println(); // инъекция в запрос
        getVillainNamed(factory, "Грю", true); System.out.println();  // конфигурация запроса
        getVillainMinions(factory, "Грю", false); System.out.println(); // через join
        getVillainMinions(factory, "Грю", true); System.out.println();  // сущности, скрывающие join

//        // TODO correction
        int id = addMinion(factory, "Карл", 10); System.out.println();
        correctMinion(factory, id, 4); System.out.println();
        removeMinion(factory, id); System.out.println();
    }

    // region // SELECT-запросы без параметров в одной таблице

    private static void getVillains(SessionFactory factory){
        Session session = factory.openSession(); // Open Session
        // query data using HQL + result data format
        session.createNativeQuery("select * from villain", Villain.class)
                .getResultList().forEach(System.out::println);
        session.close(); // Must close Session
    }

    private static void getMinions(SessionFactory factory){
        // AutoCloseable: query data using HQL + result data format
        factory.inSession(session -> {
            session.createQuery("select m from Minion m", Minion.class)
                    .getResultList().forEach(System.out::println);
        });
    }

    private static void getContracts (SessionFactory factory){
        // AutoCloseable: query data using HQL + result data format
        factory.inSession(session -> {
            session.createSelectionQuery("from Contract c", Contract.class)
                    .getResultList().forEach(System.out::println);
        });
    }

    // endregion

    // region // SELECT-запросы с параметрами и объединением таблиц

    private static void getVillainNamed (SessionFactory factory, String name, Boolean useCriteriaBuilder) {
        if (name == null || name.isBlank()) return;

        if (useCriteriaBuilder) getVillainNamed (factory, name);
        else {
            // AutoCloseable: query data using HQL
            factory.inSession(session -> {
                String nameParam = '%' + name + '%';
                session.createSelectionQuery("where name ilike :name", Villain.class) // без учета регистра
                        .setParameter("name", nameParam) // add parameter from it :name or to it position ?1, ?2
                        .getResultList()    // взять все предоставленные ответы как список
                        .forEach(System.out::println);
            });
        }
    }

    private static void getVillainNamed (SessionFactory factory, String name) {
        if (name == null || name.isBlank()) return;

        // AutoCloseable: query data criteria API + result data format
        factory.inSession(session -> {
            String nameParam = '%' + name + '%';

            var builder = factory.getCriteriaBuilder();
            var query = builder.createQuery(Villain.class);
            var villain = query.from(Villain.class);
            query.select(villain).where(
                    builder.like(villain.get("name"), nameParam));
            session.createQuery(query).getResultList().forEach(System.out::println);
        });
    }

    private static void getVillainMinions (SessionFactory factory, String name, boolean hiddenJoin){
        if (name == null || name.isBlank()) return;

        if (hiddenJoin) getVillainMinions(factory, name);
        else {
            factory.inSession(session -> {
                //            session.createSelectionQuery("from Contract c join c.minion m join c.villain v where v.name ilike ?1", Contract.class)
                session.createSelectionQuery("from Contract c where c.villain.name ilike ?1", Contract.class)
                        .setParameter(1, '%' + name + '%') // add parameter to it position
                        .getResultList()
                        .forEach(contract -> {
                            System.out.println(contract.villain.toString() + "\t| " + contract.minion.toString() + "\t| " + contract.payment);
                        });
            });
        }
    }

    private static void getVillainMinions (SessionFactory factory, String name){
        factory.inSession(session -> {
            session.createSelectionQuery("from EntityVillain where name ilike ?1", EntityVillain.class)
                    .setParameter(1, '%' + name + '%') // add parameter to it position
                    .getResultList().forEach(System.out::println);
        });
    }

    // endregion

    // region // CUD-запросы на добавление, изменение и удаление записей

    private static int addMinion (SessionFactory factory, String name, int eyesCount) {
        if (name == null || name.isBlank() || eyesCount < 0) return -1;

        Minion minion = new Minion();
        minion.name = name;
        minion.eyesCount = eyesCount; // описать объект

        // действия коррекции внутри транзакций
        factory.inTransaction(session -> {
            session.persist(minion); // если надо добавит или обновит
        });

        System.out.println("INSERTed minion with id = " + minion.id);
        getMinions(factory);
        return minion.id;
    }

    private static void correctMinion(SessionFactory factory, int id, int eyesCount) {
        if (id < 0 || eyesCount < 0) return;

        // действия коррекции внутри транзакций
        factory.inTransaction(session -> {
            Minion minion =
                    session.get(Minion.class, id); // если известен идентификатор сущности
            minion.eyesCount = eyesCount;
            session.persist(minion); // если надо добавит или обновит
        });

        System.out.println("UPDATEd minion with id = " + id);
        getMinions(factory);
    }

    private static void removeMinion (SessionFactory factory, int id) {
        if (id < 0) return;

        // действия коррекции внутри транзакций
        factory.inTransaction(session -> {
            Minion minion =
                    session.get(Minion.class, id); // если известен идентификатор сущности
            session.remove(minion); // если надо добавит или обновит
        });

        System.out.println("REMOVEd minion with id = " + id);
        getMinions(factory);
    }

    // endregion
}
