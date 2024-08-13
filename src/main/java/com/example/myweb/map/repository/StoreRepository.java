package com.example.myweb.map.repository;

<<<<<<< HEAD
public class StoreRepository {

}
=======
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.myweb.map.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
>>>>>>> ce34d97 (Initial commit)
