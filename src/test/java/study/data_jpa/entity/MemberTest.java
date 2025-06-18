package study.data_jpa.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.repository.MemberRepository;

@SpringBootTest
@Transactional
class MemberTest {

  @PersistenceContext
  EntityManager em;

  @Autowired MemberRepository memberRepository;

  @Test
  public void testEntity() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);

    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    // 초기화
    em.flush();
    em.clear();

    // 확인
    List<Member> members = em.createQuery("select m from Member m", Member.class)
        .getResultList();

    for (Member member : members) {
      System.out.println("member = " + member);
      System.out.println("member.getTeam() = " + member.getTeam());
    }
  }

  @Test
  public void JpaEventBaseEntity() {
    Member member = new Member("member1");
    memberRepository.save(member);

    member.setUsername("member2");
    em.flush(); // @PreUpdate
    em.clear();

    Member findMember = memberRepository.findById(member.getId()).get();
    System.out.println("getCreatedDate = " + findMember.getCreatedDate());
    System.out.println("getLastModifiedDate = " + findMember.getLastModifiedDate());
    System.out.println("getCreateBy = " + findMember.getCreateBy());
    System.out.println("getLastModifiedBy = " + findMember.getLastModifiedBy());
  }

}