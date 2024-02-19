package ro.bogdanmierloiu.Oauth2APIFlow.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.bogdanmierloiu.Oauth2APIFlow.entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
}
