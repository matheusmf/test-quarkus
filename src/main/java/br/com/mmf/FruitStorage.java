package br.com.mmf;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import br.com.mmf.entity.Fruit;

@ApplicationScoped
public class FruitStorage {

    @Incoming("fruits")
    public void store(Fruit fruit) {
        System.out.println(fruit.toString());
    }

}
