package br.com.mmf.security;

import java.security.Principal;
import java.util.Objects;
import java.util.UUID;


public class UserPrincipal implements Principal {

	private UUID id;

    private String login;

    private UserIndexable user;

    public UserPrincipal(UserIndexable user) {
        this.id = user.getId();
        this.login = user.getLogin();
		this.user = user;
	}

	public static UserPrincipal create(UserIndexable user) {
        return new UserPrincipal(user);
    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
    
    public UserIndexable getUser() {
		return user;
	}
	@Override
	public String getName() {
		return login;
	}
}
