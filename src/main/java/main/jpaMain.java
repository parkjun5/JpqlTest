package main;

import domain.Address;
import domain.Member;
import domain.MemberType;
import domain.Team;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;


public class jpaMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("entityManager");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA= new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Team teamC = new Team();
            teamC.setName("teamC");
            em.persist(teamC);

            Member member = new Member();
            member.setName("member1");
            member.setAge(26);
            member.setAddress(Address.createAddress("수원", "파크로", "111-111"));
            member.setTeam(teamA);
            teamA.getMembers().add(member);
            em.persist(member);

            Member m2 = new Member();
            m2.setName("member2");
            m2.setAge(30);
            m2.setAddress(Address.createAddress("수원", "d", "b"));
            m2.changeTeam(teamA);
            teamA.getMembers().add(m2);
            em.persist(m2);

            Member member3 = new Member();
            member3.setName("park");
            member3.setAge(26);
            member3.setAddress(Address.createAddress("수원", "파크로", "111-111"));
            member3.setTeam(teamB);
            teamB.getMembers().add(member3);
            em.persist(member3);

            Member member4 = new Member();
            member4.setName("park");
            member4.setAge(26);
            member4.setAddress(Address.createAddress("수원", "파크로", "111-111"));
            member4.setTeam(teamC);
            teamC.getMembers().add(member4);
            em.persist(member4);


            System.out.println("========== 기 본 설 정 ============");
//            기본문법(em);
//            기본문법테스트(em);
//            조인예제(em);

//            String teamButInnerJoin = "select m.team from Member m";
//            String innerJoin = "select m from Member m inner join m.team t";
//            String outerJoin = "select m from Member m left join m.team t";
//            String setaJoin = "select m from Member m, Team t where m.name = t.name";
//            String leftOnJoin = "select m from Member m left join m.team t on t.name='selab'";
//            List<Member> resultList = em.createQuery(setaOnJoin, Member.class).getResultList();

            em.flush();
            em.clear();

            String selectMemberWithTeam = "select m from Member m join fetch m.team";
            String selectTeamWithMembers = "select distinct t from Team t join fetch t.members";
//            String query2 = "select m.name, 'HELLO?', TRUE from Member m" +
//                    " where m.memberType = domain.MemberType.ADMIN";
            List<Team> resultList = em.createQuery(selectTeamWithMembers, Team.class)
                    .getResultList();

            for (Team team : resultList) {
//                System.out.println("member1.getName() +\", \" + member1.getTeam().getName() = " + member1.getName() +", " + member1.getTeam().getName());
                System.out.println("team.getName() = " + team.getName() + "|| team.getMembers() = " + team.getMembers().size());
                for (Member teamMember : team.getMembers()) {
                    System.out.println("----> teamMember = " + teamMember);
                }
            
            }


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void 조인예제(EntityManager em) {
        String innerJoin = "select m from Member m inner join m.team t";
        String outerJoin = "select m from Member m left join m.team t";
        String setaJoin = "select m from Member m, Team t where m.name = t.name";

        String leftOnJoin = "select m from Member m left join m.team t on t.name='selab'";
        String setaOnJoin = "select m from Member m left join m.team t on m.name = t.name";

        List<Member> resultList = em.createQuery(setaOnJoin, Member.class).getResultList();
    }

    private static void 서브쿼리(EntityManager em) {
        Team team = new Team();
        team.setName("selab");
        em.persist(team);

        for (int i = 0; i < 100; i++) {
            Member member = new Member();
            member.setName("park" + i);
            member.setAge(i);
            member.setAddress(Address.createAddress("수원", "파크로", "111-111"));
            member.setTeam(team);
            team.getMembers().add(member);
            em.persist(member);
        }


        List<Member> resultList = em.createQuery("select m from Member m where m.age > (select avg(m1.age) from Member m1)", Member.class)
                .setMaxResults(20)
                .getResultList();
        for (Member member : resultList) {
            System.out.println(member.toString());
        }
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
