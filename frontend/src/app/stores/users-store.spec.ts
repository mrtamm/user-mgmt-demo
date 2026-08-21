import { TestBed } from '@angular/core/testing';
import { firstValueFrom, of } from 'rxjs';
import { UserData, UsersStore } from './users-store';
import { UserService } from '../services/user-service';

/*********
 TEST DATA
 *********/

const user1: UserData = {
  id: '1',
  firstName: 'Johnny',
  lastName: 'Smith',
  email: 'johnny@test.org',
};
const user2: UserData = {
  id: '2',
  firstName: 'James',
  lastName: 'Baker',
  email: 'james@test.org',
};
const user3: UserData = {
  id: '3',
  firstName: 'Jean',
  lastName: 'Jones',
  email: 'jean@test.org',
};

const users = [user1, user2, user3];


/**
 * SignalStore and reducer tests.
 */
describe('UsersStore', () => {
  let store: InstanceType<typeof UsersStore>;

  /**
   * Mocks UserService since the store calls it, and during the tests we need to detect the calls.
   */
  const userServiceMock = {
    fetchAllUsers: vi.fn(),
    addUser: vi.fn(),
    updateUser: vi.fn(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UsersStore,
        {
          provide: UserService,
          useValue: userServiceMock,
        },
      ],
    });

    store = TestBed.inject(UsersStore);
  });

  afterEach(() => {
    userServiceMock.fetchAllUsers.mockReset();
    userServiceMock.addUser.mockReset();
    userServiceMock.updateUser.mockReset();
  });

  it('should update state with users from UserService.fetchAllUsers()', async() => {
    // Register mock data for the service:
    userServiceMock.fetchAllUsers.mockReturnValueOnce(of(users));

    // Act
    const response = await firstValueFrom(store.fetchUsers());

    // Verify the value returned by the store method
    // Expecting same data as returned by the mock-service.
    expect(response).toEqual(users);

    // Verify service interaction
    expect(userServiceMock.fetchAllUsers).toHaveBeenCalled();

    // Verify resulting state: the store contains same data as returned by the mock-service
    expect(store.users()).toEqual(users);
  });

  it('should update state with new user and call UserService.addUser(data)', async() => {
    // Register mock data for the service:
    userServiceMock.addUser.mockReturnValueOnce(of(user1));

    // New user data (based on user1):
    const testUser = { firstName: user1.firstName, lastName: user1.lastName, email: user1.email };

    // Act
    const response = await firstValueFrom(store.persist(testUser));

    // Verify the value returned by the store method
    // Expecting the same data as returned by the service
    expect(response).toEqual(user1);

    // Verify service interaction
    expect(userServiceMock.addUser).toHaveBeenCalledWith(testUser);

    // Verify resulting state: the store contains the new user
    expect(store.users()).toEqual([user1]);
  });

  it('should update a user in state and call UserService.updateUser(id, data)', async() => {
    // The target values for updating user2:
    const updatedUser = {
      id: user2.id,
      firstName: user2.firstName + "-updated",
      lastName: user2.lastName + "-updated",
      email: user2.email + "-updated",
    };

    // Register mock data for the service:
    userServiceMock.fetchAllUsers.mockReturnValueOnce(of(users));
    userServiceMock.updateUser.mockReturnValueOnce(of(updatedUser));

    // Populate Store with users:
    const initialUsers = await firstValueFrom(store.fetchUsers());
    expect(initialUsers).toEqual(users);

    // Act
    const response = await firstValueFrom(store.persist(updatedUser));

    // Verify the value returned by the store method
    // Expecting the same data as returned by the service
    expect(response).toEqual(updatedUser);

    // Verify service interaction
    expect(userServiceMock.updateUser).toHaveBeenCalledWith(updatedUser);

    // Verify resulting state: the second user has updated data
    expect(store.users()).toEqual([user1, updatedUser, user3]);
  });

});
