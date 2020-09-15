package br.com.mmf.entity;

import java.util.UUID;

import javax.json.bind.annotation.JsonbCreator;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

@Entity
public class Fruit {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Type(type = "pg-uuid")
	private UUID id;
	
	private String name;
	
	private UUID createdBy;
	
	private UUID lastModifiedBy;

    public Fruit() {
    }

    public Fruit(String name) {
        this.name = name;
    }
    
    @JsonbCreator
    public static Fruit of(String name) {
        return new Fruit(name);
    }
    
    @Override
    public String toString() {
    	return String.format("Fruta: {\"id\": \"%s\", \"nome\": \"%s\"}", id.toString(), name);
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UUID createdBy) {
		this.createdBy = createdBy;
	}

	public UUID getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(UUID lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
    
}
