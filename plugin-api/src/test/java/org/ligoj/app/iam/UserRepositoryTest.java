/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.ligoj.app.iam;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ligoj.bootstrap.core.validation.ValidationJsonException;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Test class of {@link IUserRepository}
 */
public class UserRepositoryTest {

	@Test
	public void coverage() {
		final IUserRepository repository = new IUserRepository() {

			@Override
			public void updateUser(UserOrg user) {
			}

			@Override
			public void updateMembership(Collection<String> groups, UserOrg user) {
			}

			@Override
			public void unlock(UserOrg user) {
			}

			@Override
			public String toDn(UserOrg newUser) {
				return null;
			}

			@Override
			public void setPassword(UserOrg user, String password, String newPassword) {
			}

			@Override
			public void setPassword(UserOrg user, String password) {
			}

			@Override
			public void restore(UserOrg user) {
			}

			@Override
			public void move(UserOrg user, CompanyOrg company) {
			}

			@Override
			public void lock(String principal, UserOrg user) {
			}

			@Override
			public void isolate(String principal, UserOrg user) {
			}

			@Override
			public String getToken(String id) {
				return null;
			}

			@Override
			public String getPeopleInternalBaseDn() {
				return null;
			}

			@Override
			public UserOrg findByIdNoCache(String id) {
				return null;
			}

			@Override
			public List<UserOrg> findAllBy(String attribute, String value) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Page<UserOrg> findAll(Collection<GroupOrg> requiredGroups, Set<String> companies, String criteria,
					Pageable pageable) {
				return null;
			}

			@Override
			public Map<String, UserOrg> findAll() {
				return null;
			}

			@Override
			public void delete(UserOrg user) {
			}

			@Override
			public UserOrg create(UserOrg entry) {
				return null;
			}

			@Override
			public boolean authenticate(String name, String password) {
				return false;
			}
		};
		Assertions.assertNull(repository.getCompanyRepository());
		Assertions.assertNull(repository.getGroupRepository());
	}

	@Test
	public void findAll() {
		new EmptyUserRepository().setPassword(null, null);
		new EmptyUserRepository().setPassword(null, null, null);
		Assertions.assertTrue(new EmptyUserRepository().findAll().isEmpty());
	}

	@Test
	public void findAllNoCache() {
		Assertions.assertTrue(new EmptyUserRepository().findAllNoCache(null).isEmpty());
	}

	@Test
	public void findAll2() {
		Assertions.assertTrue(new EmptyUserRepository().findAll(null, null, null, null).getContent().isEmpty());
	}

	@Test
	public void findAllBy() {
		Assertions.assertTrue(new EmptyUserRepository().findAllBy("any", "any").isEmpty());
	}

	@Test
	public void authenticate() {
		Assertions.assertTrue(new EmptyUserRepository().authenticate("any", "any"));
	}

	@Test
	public void findByIdNoCache() {
		Assertions.assertEquals("some", new EmptyUserRepository().findByIdNoCache("some").getId());
	}

	@Test
	public void getToken() {
		Assertions.assertEquals("some", new EmptyUserRepository().getToken("some"));
	}

	@Test
	public void toUser() {
		final IamConfiguration configuration = new EmptyIamProvider().getConfiguration();
		Assertions.assertNotNull(configuration);
		Assertions.assertNotNull(configuration.getUserRepository());
		Assertions.assertEquals("login", configuration.getUserRepository().toUser("login").getId());
	}

	@Test
	public void toUserNull() {
		Assertions.assertNull(new EmptyIamProvider().getConfiguration().getUserRepository().toUser(null));
	}

	@Test
	public void toUserNotExists() {
		Assertions.assertEquals("login", new MockUserRepository().toUser("login").getId());
	}

	@Test
	public void checkLockStatus() {
		// Nothing by default
		new MockUserRepository().checkLockStatus(null);
	}

	@Test
	public void findByIdExpectedNotFound() {
		Assertions.assertThrows(ValidationJsonException.class, () -> {
			new MockUserRepository().findByIdExpected("user1", "user2");
		});
	}

	@Test
	public void findByIdExpectedCompanyNotExists() {
		final EmptyUserRepository emptyUserRepository = new EmptyUserRepository();
		emptyUserRepository.setCompanyRepository(Mockito.mock(ICompanyRepository.class));
		Assertions.assertThrows(ValidationJsonException.class, () -> {
			emptyUserRepository.findByIdExpected("user1", "user2");
		});
	}

	@Test
	public void findByIdExpected() {
		final EmptyUserRepository emptyUserRepository = new EmptyUserRepository();
		final ICompanyRepository companyRepository = Mockito.mock(ICompanyRepository.class);
		Mockito.when(companyRepository.findById("user1", "company")).thenReturn(new CompanyOrg("", ""));
		emptyUserRepository.setCompanyRepository(companyRepository);
		Assertions.assertEquals("user2", emptyUserRepository.findByIdExpected("user1", "user2").getId());
	}

}
