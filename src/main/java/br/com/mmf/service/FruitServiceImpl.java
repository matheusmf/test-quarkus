package br.com.mmf.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import br.com.mmf.entity.Fruit;
import br.com.mmf.repository.FruitRepository;

@ApplicationScoped
public class FruitServiceImpl implements FruitService {
	
	@Inject
	private FruitRepository fruitRepository;
	
	@Inject
	@Channel("fruits") // Emit on the channel 'fruits'
	private Emitter<Fruit> fruitsEmitter;
	
	@Override
	public List<Fruit> getAll() {
		return fruitRepository.listAll();
	}

	@Transactional
	@Override
	public Fruit save(Fruit fruit) {
		fruitRepository.persist(fruit);
		fruitsEmitter.send(fruit);
		return fruit;
	}

	@Transactional
	@Override
	public Fruit update(UUID id, Fruit fruit) {
		Optional<Fruit> opFruit = fruitRepository.findById(id);
        if(opFruit.isPresent()) {
			Fruit fruitEntity = opFruit.get();
        	fruitEntity.setName(fruit.getName());
        	return fruitEntity;
        } else {
        	throw new WebApplicationException("Fruit with id of " + id.toString() + " does not exist.", Response.Status.NOT_FOUND);
        }
	}

	

}
