package br.com.mmf.repository;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import br.com.mmf.entity.Fruit;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class FruitRepository implements PanacheRepository<Fruit> {

	public Optional<Fruit> findById(UUID id) {
		return find("id", id).firstResultOptional();
	}
	
	public Fruit findByName(String name) {
		return find("name", name).firstResult();
	}

}
