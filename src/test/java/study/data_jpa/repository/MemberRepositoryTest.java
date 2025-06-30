package study.data_jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;


@SpringBootTest
@Transactional
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;
  @Autowired
  TeamRepository teamRepository;
  @PersistenceContext
  EntityManager em;

  @Test
  public void testMember() {
    Member member = new Member("memberA");
    Member savedMember = memberRepository.save(member);

    Member findMember = memberRepository.findById(savedMember.getId()).get();

    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member);
  }

  @Test
  public void basicCRUD() {
    Member member1 = new Member("member1");
    Member member2 = new Member("member2");
    memberRepository.save(member1);
    memberRepository.save(member2);

    // 단건 조회
    Member findMember1 = memberRepository.findById(member1.getId()).get();
    Member findMember2 = memberRepository.findById(member2.getId()).get();
    assertThat(findMember1).isEqualTo(member1);
    assertThat(findMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberRepository.findAll();
    assertThat(all.size()).isEqualTo(2);

    // 카운트 검증
    long count = memberRepository.count();
    assertThat(count).isEqualTo(2);

    // 삭제 검증
    memberRepository.delete(member1);
    memberRepository.delete(member2);

    long deletedCount = memberRepository.count();
    assertThat(deletedCount).isEqualTo(0);
  }

  @Test
  public void findByUsernameAndAgeGreaterThen() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("AAA", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

    assertThat(result.get(0).getUsername()).isEqualTo("AAA");
    assertThat(result.get(0).getAge()).isEqualTo(20);
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void testNamedQuery() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    List<Member> result = memberRepository.findByUsername("AAA");
    Member findMember = result.get(0);
    assertThat(findMember).isEqualTo(m1);
  }

  @Test
  public void testQuery() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    List<Member> result = memberRepository.findUser("AAA", 10);
    assertThat(result.get(0)).isEqualTo(m1);
  }

  @Test
  public void findUsernameList() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    List<String> usernameList = memberRepository.findUsernameList();
    for (String string : usernameList) {
      System.out.println("string = " + string);
    }
  }

  @Test
  public void findMemberDtoList() {
    Team team = new Team("teamA");
    teamRepository.save(team);

    Member m1 = new Member("AAA", 10);
    m1.setTeam(team);
    memberRepository.save(m1);

    List<MemberDto> memberDto = memberRepository.findMemberDto();
    for (MemberDto dto : memberDto) {
      System.out.println("dto = " + dto);
    }
  }

  @Test
  public void findMyNames() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    List<Member> result = memberRepository.findMyNames(Arrays.asList("AAA", "BBB"));
    for (Member member : result) {
      System.out.println("member = " + member);
    }
  }

  @Test
  public void returnType() {
    Member m1 = new Member("AAA", 10);
    Member m2 = new Member("BBB", 20);
    memberRepository.save(m1);
    memberRepository.save(m2);

    Optional<Member> aaa = memberRepository.findOptionalByUsername("AAA");
  }

  @Test
  public void paging() {
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

    Page<Member> page = memberRepository.findByAge(age, pageRequest);

    List<Member> content = page.getContent();
    long totalElements = page.getTotalElements();

    assertThat(content.size()).isEqualTo(3);
    assertThat(page.getTotalElements()).isEqualTo(5);
    assertThat(page.getNumber()).isEqualTo(0);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.isFirst()).isTrue();
    assertThat(page.hasNext()).isTrue();

    for (Member member : content) {
      System.out.println("member = " + member);
    }

    System.out.println("totalElements = " + totalElements);

  }

  @Test
  public void bulkUpdate() {
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 19));
    memberRepository.save(new Member("member3", 20));
    memberRepository.save(new Member("member4", 21));
    memberRepository.save(new Member("member5", 40));

    int resultCount = memberRepository.bulkAgePlus(20);

    List<Member> result = memberRepository.findByUsername("member5");
    Member member5 = result.get(0);
    System.out.println("member5 = " + member5.getAge());

    assertThat(resultCount).isEqualTo(3);
  }

  @Test
  public void findMemberLazy() {
    // given
    // member1 -> teamA
    // member2 -> teamB

    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");

    teamRepository.save(teamA);
    teamRepository.save(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 10, teamB);

    memberRepository.save(member1);
    memberRepository.save(member2);

    em.flush();
    em.clear();

    // when
    List<Member> members = memberRepository.findAll();

    for (Member member : members) {
      System.out.println("member = " + member.getUsername());
      System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }
  }

  @Test
  public void queryHint() {
    Member member1 = new Member("member1", 10);

    memberRepository.save(member1);
    em.flush();
    em.clear();

    Member findMember = memberRepository.findReadOnlyByUsername("member1");
    findMember.setUsername("member2");

    em.flush();
  }

  @Test
  public void lock() {
    Member member1 = new Member("member1", 10);

    memberRepository.save(member1);
    em.flush();
    em.clear();

    List<Member> findMember = memberRepository.findLockByUsername("member1");

    em.flush();
  }

  @Test
  public void callCustom() {
    List<Member> result = memberRepository.findMemberCustom();
  }

  @Test
  public void specBasic() {
    // given
    Team teamA = new Team("teamA");
    em.persist(teamA);

    Member m1 = new Member("m1", 0, teamA);
    Member m2 = new Member("m2", 0, teamA);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    // when
    Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
    List result = memberRepository.findAll(spec);

    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void queryByExample() {
    // given
    Team teamA = new Team("teamA");
    em.persist(teamA);

    Member m1 = new Member("m1", 0, teamA);
    Member m2 = new Member("m2", 0, teamA);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    // when
    // Probe
    Member member = new Member("m1");

    ExampleMatcher matcher = ExampleMatcher.matching()
        .withIgnorePaths("age");

    Example<Member> example = Example.of(member, matcher);

    List<Member> result = memberRepository.findAll(example);

    assertThat(result.get(0).getUsername()).isEqualTo("m1");
  }
}