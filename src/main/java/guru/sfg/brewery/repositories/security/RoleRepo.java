package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Integer> {
}
