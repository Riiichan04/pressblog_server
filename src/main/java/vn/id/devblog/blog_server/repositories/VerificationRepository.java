package vn.id.devblog.blog_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.id.devblog.blog_server.common.enums.VerificationType;
import vn.id.devblog.blog_server.models.Verification;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {
    Verification getByEmail(String email);
    Verification findByEmailAndType(String email, VerificationType type);
    Verification getByEmailAndCode(String email, String code);
    Verification getByEmailAndType(String email, VerificationType type);
}
