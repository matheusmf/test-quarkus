package br.com.mmf.service;

import java.util.List;
import java.util.UUID;

import br.com.mmf.entity.Fruit;

public interface FruitService {

	List<Fruit> getAll();

	Fruit save(Fruit fruit);

	Fruit update(UUID id, Fruit fruit);

}
