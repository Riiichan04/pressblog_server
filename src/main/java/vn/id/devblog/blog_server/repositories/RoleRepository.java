package vn.id.devblog.blog_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.id.devblog.blog_server.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
