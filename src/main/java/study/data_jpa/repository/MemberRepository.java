package study.data_jpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor
{

  List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

  List<Member> findByUsername(@Param("username") String username);

  @Query("select m from Member m where m.username = :username and m.age = :age")
  List<Member> findUser(@Param("username") String username, @Param("age") int age);

  @Query("select m.username from Member m")
  List<String> findUsernameList();

  @Query("select new study.data_jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
  List<MemberDto> findMemberDto();

  @Query("select m from Member m where m.username in :names")
  List<Member> findMyNames(@Param("names") List<String> names);

  List<Member> findListByUsername(String username); // 컬렉션
  Member findMemberByUsername(String username); // 단건
  Optional<Member> findOptionalByUsername(String username); // 단건 Optional

  Page<Member> findByAge(int age, PageRequest pageRequest);

  @Modifying(clearAutomatically = true)
  @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
  int bulkAgePlus(@Param("age") int age);

  @Query("select m from Member m left join fetch m.team")
  List<Member> findMemberFetchJoin();

  @Override
  @EntityGraph(attributePaths = {"team"})
  List<Member> findAll();

  @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
  Member findReadOnlyByUsername(String username);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  List<Member> findLockByUsername(String username);

  List<NestedClosedProjections> findProjectionsByUsername(@Param("username") String username);

  @Query(value = "select * from member where username = ?", nativeQuery = true)
  Member findByNativeQuery(String username);

  @Query(value = "select m.member_id as id, m.username, t.name as teamName from member m left join team t", countQuery = "select count(*) from member", nativeQuery = true)
  Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
