package main;

import domain.Address;
import domain.Member;
import domain.Team;

import javax.persistence.*;
import java.util.List;


public class jpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("entityManager");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("selab");
            em.persist(team);

            Member member = new Member();
            member.setName("park");
            member.setAge(26);
            member.setAddress( Address.createAddress("수원", "파크로", "111-111"));

            member.changeTeam(team);

            em.persist(member);
//            기본문법(em);
//            기본문법테스트(em);
            String innerJoin = "select m from Member m inner join m.team t";
            String outerJoin = "select m from Member m left join m.team t";
            String setaJoin = "select m from Member m, Team t where m.name = t.name";

            String leftOnJoin = "select m from Member m left join m.team t on t.name='selab'";
            String setaOnJoin = "select m from Member m left join m.team t on m.name = t.name";

            List<Member> resultList = em.createQuery(setaOnJoin, Member.class).getResultList();

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void 기본문법테스트(EntityManager em) {
        Member member1 = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", "park")
                .getSingleResult();

        System.out.println("member1 = " + member1.getAge());
    }

    private static void 기본문법(EntityManager em) {
        //타입이 명확할때
        TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
        TypedQuery<String> query2 = em.createQuery("select m.name from Member m", String.class);
        //타입이 명확하지 않을때
        Query query3 = em.createQuery("select m.name, m.age from Member m");

        //결과를 컬렉션
        List<Member> resultList = query1.getResultList();
    }


}
